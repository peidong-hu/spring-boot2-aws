package org.its.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.its.service.Ec2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Filter;

@Service
public class Ec2ServiceImpl implements Ec2Service {

	@Autowired
	private Ec2Client ec2;

	@Override
	public List<Instance> getFleetInstances() {
		List<Instance> fleetInstances = new ArrayList<Instance>();
		
		DescribeInstancesRequest request = DescribeInstancesRequest.builder().filters(Filter.builder().name("tag:aws:ec2:fleet-id").values("*").build()).build();
		//DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).build();
				
		DescribeInstancesResponse response = ec2.describeInstances(request);

		for (Reservation reservation : response.reservations()) {
			fleetInstances.addAll(reservation.instances());
			//for (Instance instance : reservation.instances()) {
//				if (instance.tags().stream().filter(tag -> {
//					return tag.key().equalsIgnoreCase("aws:ec2-fleet-id")
//							&& tag.value().equalsIgnoreCase("fleet-eb9c097d-e722-416f-a024-58c667c01c1b");
//				}).count() > 0) {
//					fleetInstances.add(instance);
//				}
			//}

		}
		return fleetInstances;

	}

}
