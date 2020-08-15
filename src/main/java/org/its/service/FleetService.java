package org.its.service;

import java.util.List;
import java.util.Optional;


public interface FleetService {

	public String provision(int numberOfNodes, List<String> subnets, List<String> securityGroups, Optional<String> instanceType, Optional<Integer> volSize, Optional<String> amiId);
	
	
}
