package org.its.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.its.rest.controller.AwsFleetController.FleetState;
import org.its.service.Ec2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.ec2.model.Instance;

@RestController
@RequestMapping("/spring-boot2")
public class AwsInstanceController {

	@Autowired
	private Ec2Service ec2Service;

	@GetMapping(value = "/instance/{instanceId}")
	public ResponseEntity<org.its.model.Instance> getInstance(@PathVariable String instanceId) {
		if (instanceId == null)
			return ResponseEntity.badRequest().build();
		Optional<software.amazon.awssdk.services.ec2.model.Instance> inst = ec2Service.getInstance(instanceId);
		if (inst.isPresent())
			return ResponseEntity.ok(org.its.model.Instance.build(inst.get()));
		else
			return ResponseEntity.notFound().build();
	}
	//
	// @GetMapping(value="/fleetInstancesVolumesStatus")
	// public ResponseEntity<List<FleetState>>
	// getFleetInstancesVolumeAttachementStatus(String fleetUUID){
	// List<FleetState> fts =
	// AwsFleetController.fleets.stream().filter(ft->ft.getFleetUUID().toString().equalsIgnoreCase(fleetUUID)).collect(Collectors.toList());
	// if (fts.size() == 0) {
	// return ResponseEntity.ok().body(fts).notFound().build();
	// } else if (fts.size() > 1) {
	// return ResponseEntity.ok().body(fts).unprocessableEntity().build();
	// } else {
	// return ResponseEntity.ok(fts);
	// }
	// }

}
