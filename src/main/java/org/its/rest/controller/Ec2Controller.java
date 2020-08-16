package org.its.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.its.rest.controller.FleetController.FleetState;
import org.its.service.Ec2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.ec2.model.Instance;

@RestController
@RequestMapping("/ec2FleetInstances")
public class Ec2Controller {

	@Autowired
	private Ec2Service ec2Service;

	@GetMapping
	public ResponseEntity<List<String>> getAllRunningFleetInstances(){
		List<String> list = ec2Service.getAllRunningFleetInstances().stream().map(Instance::instanceId).collect(Collectors.toList());
		return ResponseEntity.ok(list); 
	}
	
	@GetMapping(value="getFleetInstancesVolumeAttachmentStatus")
	public ResponseEntity<List<FleetState>> getFleetInstancesVolumeAttachementStatus(String fleetUUID){
		List<FleetState> fts = FleetController.fleets.stream().filter(ft->ft.getFleetUUID().toString().equalsIgnoreCase(fleetUUID)).collect(Collectors.toList());
		if (fts.size() == 0) {
			return ResponseEntity.ok().body(fts).notFound().build();
		} else if (fts.size() > 1) {
			return ResponseEntity.ok().body(fts).unprocessableEntity().build();
		} else {
			return ResponseEntity.ok(fts);
		}
	}
	
}
