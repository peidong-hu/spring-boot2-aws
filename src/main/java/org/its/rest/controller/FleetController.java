package org.its.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
	public static class FleetState {
		final private FleetParam fleetParam;
		final private UUID fleetUUID;
		private int numberOfVolumesAttached = 0;
		public FleetState(FleetParam param, UUID uuid) {
			fleetParam = param;
			fleetUUID = uuid;
		}
		

		public UUID getFleetUUID() {
			return fleetUUID;
		}


		public FleetParam getFleetParam() {
			return fleetParam;
		}


		public int getNumberOfVolumesAttached() {
			return numberOfVolumesAttached;
		}


		public void setNumberOfVolumesAttached(int numberOfVolumesAttached) {
			this.numberOfVolumesAttached = numberOfVolumesAttached;
		}

		
	}

	// TODO use a simple list to store the fleets states for processing volume
	// attachments, can be replace by Reactive design using a message queues for same purpose
	public static List<FleetState> fleets = new ArrayList<FleetState>();
	@Autowired
	private FleetService fleetService;

	@PostMapping
	public ResponseEntity<String> provision(@Valid @RequestBody FleetParam fleetParam) {
		FleetState fs = new FleetState(fleetParam, UUID.randomUUID());
		fleets.add(fs);
		return ResponseEntity.ok().header("Content-Type", "text/html")
				.body(fleetService.provision(fs));
	}

}
