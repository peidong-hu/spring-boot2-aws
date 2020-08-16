package org.its.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.its.model.FleetParam;
import org.its.service.Ec2Service;
import org.jsoup.parser.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AttachVolumeRequest;
import software.amazon.awssdk.services.ec2.model.AttachVolumeResponse;
import software.amazon.awssdk.services.ec2.model.CreateVolumeRequest;
import software.amazon.awssdk.services.ec2.model.CreateVolumeResponse;
import software.amazon.awssdk.services.ec2.model.DescribeImagesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceTypesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.InstanceTypeInfo;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.VolumeState;
import software.amazon.awssdk.services.ec2.model.VolumeType;

@Service
public class FleetParamValidationServiceImpl {

	@Autowired
	private Ec2Client ec2;

	public List<String> validate(FleetParam fleetParam) {
		List<String> retVal = new ArrayList<String>();
		if (fleetParam.getVolSize() < 4 && fleetParam.getVolSize() > 0) {
			retVal.add("Volume Size must be greater or equals to 4GB for IO1 type volume");
		}
		;
		if (fleetParam.getNumberOfNodes() < 4) {
			retVal.add("NUmber of Nodes must be greater or equals to 4");
		}
		if (fleetParam.getAmiId() != null) {
			if (ec2.describeImages(DescribeImagesRequest.builder()
					.filters(Filter.builder().name("image-id").values(fleetParam.getAmiId()).build()).build()).images()
					.isEmpty()) {
				retVal.add("ami image id is invalid");
			}
		}
		if (fleetParam.getInstanceType() != null) {
			InstanceType input = InstanceType.fromValue(fleetParam.getInstanceType());
			List<InstanceType> instTypes = ec2
					.describeInstanceTypes(
							DescribeInstanceTypesRequest.builder().instanceTypes(input).build())
					.instanceTypes().stream().map(inst -> inst.instanceType()).collect(Collectors.toList());
			
			if (instTypes.size() == 0) {
				retVal.add("instance type is invalid");
			}
		}
		if (fleetParam.getSecurityGroups().size() > 0) {
			// List<Filter> sgFilters = new ArrayList<Filter>();
			// fleetParam.getSecurityGroups().forEach(sg -> {
			// sgFilters.add(Filter.builder().name("group-id").values(sg).build());
			// });
			DescribeSecurityGroupsRequest.builder()
					.filters(Filter.builder().name("group-id").values(fleetParam.getSecurityGroups()).build()).build();
			List<String> sgs = ec2.describeSecurityGroups(DescribeSecurityGroupsRequest.builder()
					.filters(Filter.builder().name("group-id").values(fleetParam.getSecurityGroups()).build()).build())
					.securityGroups().stream().map(sgi -> sgi.groupId()).collect(Collectors.toList());
			fleetParam.getSecurityGroups().forEach(sgid -> {
				if (sgs.stream().filter(sgsid -> sgsid.equals(sgid)).count() == 0) {
					retVal.add("security group id is invalid: " + sgid);
				}
			});

		}
		if (fleetParam.getSubnets().size() > 0) {
			// List<Filter> sgFilters = new ArrayList<Filter>();
			// fleetParam.getSubnets().forEach(sg -> {
			// sgFilters.add(Filter.builder().name("subnet-id").values(sg).build());
			// });

			List<String> sgs = ec2.describeSubnets(DescribeSubnetsRequest.builder()
					.filters(Filter.builder().name("subnet-id").values(fleetParam.getSubnets()).build()).build())
					.subnets().stream().map(sgi -> sgi.subnetId()).collect(Collectors.toList());
			fleetParam.getSubnets().forEach(sgid -> {
				if (!sgs.contains(sgid)) {
					retVal.add("security subnet id is invalid: " + sgid);
				}
			});

		}
		return retVal;

	}

}
