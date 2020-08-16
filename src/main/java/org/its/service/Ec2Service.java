package org.its.service;

import java.util.List;
import java.util.Optional;

import software.amazon.awssdk.services.ec2.model.Instance;

public interface Ec2Service {

	public List<Instance> getAllRunningFleetInstances(String fleetUUID);
	
	public List<Instance> getAllRunningFleetInstances();
	
	public Optional<Instance> getInstance(String instanceId);
	
	public List<String> getAllSubnetIdsINZone(String zone);
	
	public List<Instance> attachVolumeToUnattachedFleetInstances(int volSize, String muiltiAttachUUID);
	
	
}
