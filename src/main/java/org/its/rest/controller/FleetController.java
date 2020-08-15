package org.its.rest.controller;
 
import java.util.ArrayList;
import java.util.Optional; 

import javax.validation.Valid;

import org.its.model.FleetParam;
import org.its.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ec2Fleet")
public class FleetController {

	@Autowired
	private FleetService fleetService;

	@PostMapping
	public ResponseEntity<String> provision(@Valid @RequestBody FleetParam fleetParam) {

		return ResponseEntity.ok().header("Content-Type", "text/html")
		        .body(fleetService.provision(fleetParam.getNumberOfNodes(), fleetParam.getSubnets() == null? new ArrayList<String>(): fleetParam.getSubnets(),
				fleetParam.getSecurityGroups() == null? new ArrayList<String>():fleetParam.getSecurityGroups(), Optional.ofNullable(fleetParam.getInstanceType()),
				Optional.ofNullable(fleetParam.getVolSize()), Optional.ofNullable(fleetParam.getAmiId())));
	}

}
