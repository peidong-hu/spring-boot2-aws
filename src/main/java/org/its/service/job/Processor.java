package org.its.service.job;

import java.util.List;
import java.util.stream.Collectors;

import org.its.rest.controller.FleetController.FleetState;
import org.its.service.Ec2Service;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Processor implements ItemProcessor<List<FleetState>, List<FleetState>> {

	@Autowired
	private Ec2Service ec2Service;

	@Override
	public List<FleetState> process(List<FleetState> fleetStates) throws Exception {
		fleetStates.stream().filter(fs -> fs.getFleetParam().getNumberOfNodes() != fs.getInstancesWithAttachedVolum().size())
				.forEach(fs -> {
					fs.getInstancesWithAttachedVolum().addAll(
							ec2Service.attachVolumeToUnattachedFleetInstances(fs.getFleetParam().getVolSize(),
									fs.getFleetUUID().toString()).stream().map(inst->inst.instanceId()).collect(Collectors.toList()));
				});
		System.out.println("one process");
		return fleetStates;
	}

}
