package org.its.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.its.model.FleetParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeImagesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceTypesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsRequest;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.Filter;

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
			if (input == InstanceType.UNKNOWN_TO_SDK_VERSION)
				retVal.add("instance type is UNKNOWN_TO_SDK_VERSION");
			else {
				List<InstanceType> instTypes = ec2
						.describeInstanceTypes(DescribeInstanceTypesRequest.builder().instanceTypes(input).build())
						.instanceTypes().stream().map(inst -> inst.instanceType()).collect(Collectors.toList());

				if (instTypes.size() == 0) {
					retVal.add("instance type is invalid");
				}
			}
		}
		if (fleetParam.getSecurityGroups() != null && fleetParam.getSecurityGroups().size() > 0) {
			List<String> listWithNoDuplicates = fleetParam.getSecurityGroups().stream().distinct().collect(Collectors.toList());
			fleetParam.getSecurityGroups().removeAll(fleetParam.getSecurityGroups());
			fleetParam.getSecurityGroups().addAll(listWithNoDuplicates);
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
		if (fleetParam.getSubnets() != null && fleetParam.getSubnets().size() > 0) {
			List<String> listWithNoDuplicates = fleetParam.getSubnets().stream().distinct().collect(Collectors.toList());
			fleetParam.getSubnets().removeAll(fleetParam.getSubnets());
			fleetParam.getSubnets().addAll(listWithNoDuplicates);

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
