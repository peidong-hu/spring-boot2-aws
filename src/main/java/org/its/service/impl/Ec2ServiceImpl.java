package org.its.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.its.service.Ec2Service;
import org.jsoup.parser.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AttachVolumeRequest;
import software.amazon.awssdk.services.ec2.model.AttachVolumeResponse;
import software.amazon.awssdk.services.ec2.model.CreateVolumeRequest;
import software.amazon.awssdk.services.ec2.model.CreateVolumeResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.VolumeState;
import software.amazon.awssdk.services.ec2.model.VolumeType;

@Service
public class Ec2ServiceImpl implements Ec2Service {
	public static final String[] AVAILABLE_ZONES = { "us-east-2b", "us-east-2c" };
	public static final int DEFAULT_VOL_SIZE = 4; // TODO Cant be 3 since the min size required by aws is 4 for io1
	public static final String MULTI_ATTACH_TAG_NAME = "multiVolAttach";
	public static final String DEVICE_NAME = "/dev/sdf";
	public static final String FLEET_TAG_NAME = "aws:ec2:fleet-id";
	public static final int VOLUME_JOB_TRIGGER_THRESHOLD = 1;

	@Autowired
	private Ec2Client ec2;

	@Override
	public List<Instance> getAllRunningFleetInstances(String fleetUUID) {
		List<Instance> fleetInstances = new ArrayList<Instance>();
		DescribeInstancesRequest request;
		if (fleetUUID != null) {
			request = DescribeInstancesRequest.builder()
					.filters(Filter.builder().name("tag:" + FLEET_TAG_NAME).values("*").build(),
							Filter.builder().name("instance-state-name").values("running").build(),
							Filter.builder().name("tag:" + MULTI_ATTACH_TAG_NAME).values(fleetUUID).build())
					.build();
		} else {
			request = DescribeInstancesRequest.builder()
					.filters(Filter.builder().name("tag:" + FLEET_TAG_NAME).values("*").build(),
							Filter.builder().name("instance-state-name").values("running").build())
					.build();
		}

		DescribeInstancesResponse response = ec2.describeInstances(request);

		for (Reservation reservation : response.reservations()) {
			fleetInstances.addAll(reservation.instances());
		}
		return fleetInstances;

	}

	@Override
	public Optional<Instance> getInstance(String instanceId) {
		List<Instance> fleetInstances = new ArrayList<Instance>();
		DescribeInstancesRequest request;
		if (instanceId != null) {
			request = DescribeInstancesRequest.builder()
					.filters(Filter.builder().name("tag:" + FLEET_TAG_NAME).values("*").build(),
							Filter.builder().name("instance-state-name").values("running").build(),
							Filter.builder().name("instance-id").values(instanceId).build())
					.build();
		} else {
			return Optional.empty();
		}

		DescribeInstancesResponse response = ec2.describeInstances(request);

		for (Reservation reservation : response.reservations()) {
			fleetInstances.addAll(reservation.instances());
		}
		if (fleetInstances.size() == 1)
			return Optional.of(fleetInstances.get(0));
		else
			return Optional.empty();

	}

	@Override
	public List<Instance> attachVolumeToUnattachedFleetInstances(int volSize, String muiltiAttachUUID) {

		List<Instance> successAttachRequestedInstances = new ArrayList<Instance>();

		Arrays.asList(AVAILABLE_ZONES).forEach(az -> {
			List<Instance> fleetInstances = new ArrayList<Instance>();
			Filter filter = Filter.builder().name("tag:" + FLEET_TAG_NAME).values("*").build();
			List<String> azs = new ArrayList<String>() {
				{
					add((String) az);
				}
			};
			Filter filter2 = Filter.builder().name("availability-zone").values(azs).build();
			Filter filter3 = Filter.builder().name("instance-state-name").values("running").build();
			Filter filter4 = Filter.builder().name("tag:" + MULTI_ATTACH_TAG_NAME).values(muiltiAttachUUID).build();

			DescribeInstancesRequest bzoneRequest = DescribeInstancesRequest.builder()
					.filters(filter, filter2, filter3, filter4).build();

			DescribeInstancesResponse response = ec2.describeInstances(bzoneRequest);

			for (Reservation reservation : response.reservations()) {
				fleetInstances.addAll(reservation.instances().stream().filter(ins -> {
					return ins.blockDeviceMappings().stream()
							.filter(device -> device.deviceName().equalsIgnoreCase(DEVICE_NAME)).count() == 0;
				}).collect(Collectors.toList()));
			}
			// System.out.println("number of instances in zone " + az);
			// System.out.println("number of instances:" + fleetInstances.size());
			// fleetInstances.forEach(ins -> {
			// System.out.println(ins.instanceId());
			// });
			// TODO the problem description doesn't address what to do with the volume size
			// difference from user input in the case that instances out of the 16 size of
			// attach bucket for a specific call. It will leaves some instances with no
			// volumes for example for the 17th instance because each call could have
			// different
			// volume size requirement. Use 1 as the bucket size will avoid this for demo
			// purpose.
			if (fleetInstances.size() >= VOLUME_JOB_TRIGGER_THRESHOLD) {
				// TODO I have no MultiAttachedVolume suported AZ in my account, I will just use
				// regular volume to demo the code
				// String volId = this.createMultiAttachVolume((String) az, volSize == 0 ?
				// DEFAULT_VOL_SIZE : volSize);
				for (int index = 0; index < VOLUME_JOB_TRIGGER_THRESHOLD; index++) {
					Instance inst = fleetInstances.get(index);
					// if
					// (inst.tags().stream().filter(tag->tag.value().equals(muiltiAttachUUID)).count()==1)
					// {
					String volId = this.createMultiAttachVolume((String) az, volSize == 0 ? DEFAULT_VOL_SIZE : volSize);

					if (isVolumeAvailable(volId) && attachMultiAttachVolume(volId, fleetInstances.get(index))) {
						successAttachRequestedInstances.add(fleetInstances.get(index));
					}
					// }
				}
			}
		});
		return successAttachRequestedInstances;

	}

	@Override
	public List<String> getAllSubnetIdsINZone(String zone) {
		DescribeSubnetsRequest request = DescribeSubnetsRequest.builder()
				.filters(Filter.builder().name("availability-zone").values(zone).build()).build();
		DescribeSubnetsResponse res = ec2.describeSubnets(request);
		return res.subnets().stream().map(net -> net.subnetId()).collect(Collectors.toList());

	}

	private boolean attachMultiAttachVolume(String multiAttachVolumeId, Instance instance) {

		AttachVolumeResponse res = ec2.attachVolume(AttachVolumeRequest.builder().device(DEVICE_NAME)
				.instanceId(instance.instanceId()).volumeId(multiAttachVolumeId).build());

		return res.sdkHttpResponse().isSuccessful() ? true : false;

	}

	private boolean isVolumeAvailable(String volumeId) {
		// TODO this polling can be refactored to using async call to improve
		// performance.
		int timeout = 300;
		int loopCount = 0;
		while (loopCount < timeout) {
			DescribeVolumesResponse res = ec2
					.describeVolumes(DescribeVolumesRequest.builder().volumeIds(volumeId).build());
			if (res.volumes().stream().filter(vol -> {
				return vol.state().equals(VolumeState.AVAILABLE);
			}).count() > 0) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			loopCount = loopCount + 1;
		}
		if (loopCount == 300)
			return false;
		else
			return true;
	}

	private String createMultiAttachVolume(String az, int sizeInGB) {
		// TODO I have no MultiAttachedVolume suported AZ in my account, I will just use
		// regular volume to demo the code
		CreateVolumeResponse res = ec2.createVolume(
				CreateVolumeRequest.builder().multiAttachEnabled(false).availabilityZone(az).size(sizeInGB)
						.volumeType(VolumeType.IO1).iops(sizeInGB * 10 > 100 ? sizeInGB * 10 : 100).build());
		return res.volumeId();
	}

	@Override
	public List<Instance> getAllRunningFleetInstances() {
		return this.getAllRunningFleetInstances(null);
	}

}
