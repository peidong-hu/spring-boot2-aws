package org.its.service;

import java.util.List;
import java.util.Optional;

import org.its.rest.controller.FleetController.FleetState;


public interface FleetService {

	public String provision(FleetState fs);
	//public String provision(int numberOfNodes, List<String> subnets, List<String> securityGroups, Optional<String> instanceType, Optional<Integer> volSize, Optional<String> amiId);
	
	
}
