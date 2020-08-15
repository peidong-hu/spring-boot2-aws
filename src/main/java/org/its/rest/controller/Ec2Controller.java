package org.its.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	public ResponseEntity<List<String>> getAllFleetInstances(){
		List<String> list = ec2Service.getAllFleetInstances().stream().map(Instance::instanceId).collect(Collectors.toList());
		return ResponseEntity.ok(list); 
	}
	
	@PutMapping(value="attach")
	public ResponseEntity<List<String>> attachMultiAttachVolumeToFleetInstances(int volSize){
		if (volSize < 4) return ResponseEntity.ok(new ArrayList<String>()).badRequest().build(); 
		List<String> list = ec2Service.attachVolumeToUnattachedFleetInstances(volSize).stream().map(ins -> ins.instanceId()).collect(Collectors.toList());
		return ResponseEntity.ok(list); 
	}
	
}
