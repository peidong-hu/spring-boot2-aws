package org.its.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.its.model.FleetParam;
import org.its.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.ec2.model.Instance;

@RestController
@RequestMapping("/ec2Fleet")
public class FleetController {

	@Autowired
	private FleetService fleetService;

	@PostMapping
	public ResponseEntity<String> provision(@Valid @RequestBody FleetParam fleetParam){
		
		
		return ResponseEntity.ok(fleetService.provision(fleetParam.getNumberOfNodes(), null, null, null, null, null)); 
	}
	
}
