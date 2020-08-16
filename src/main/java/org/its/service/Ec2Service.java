package org.its.service;

import java.util.List;


import software.amazon.awssdk.services.ec2.model.Instance;

public interface Ec2Service {

	public List<Instance> getAllRunningFleetInstances();
	
	public List<String> getAllSubnetIdsINZone(String zone);
	
	public List<Instance> attachVolumeToUnattachedFleetInstances(int volSize, String muiltiAttachUUID);
	
	
}
