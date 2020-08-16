package org.its.rest.controller;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.its.model.FleetParam;
import org.its.service.FleetService;
import org.its.service.impl.FleetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
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
		final private String fleetTerraformFolder;
		private final List<String> instancesWithAttachedVolum = new ArrayList<String>();
		public FleetState(FleetParam param, UUID uuid, String fleetTfFolder) {
			fleetParam = param;
			fleetUUID = uuid;
			fleetTerraformFolder = fleetTfFolder;
		}
		

		public UUID getFleetUUID() {
			return fleetUUID;
		}


		public FleetParam getFleetParam() {
			return fleetParam;
		}


		


		public List<String> getInstancesWithAttachedVolum() {
			return instancesWithAttachedVolum;
		}


		public String getFleetTerraformFolder() {
			return fleetTerraformFolder;
		}

		
	}

	// TODO use a simple list to store the fleets states for processing volume
	// attachments, can be replace by Reactive design using a message queues for same purpose
	public static List<FleetState> fleets = new ArrayList<FleetState>();
	@Autowired
	private FleetService fleetService;

	@PostMapping
	public ResponseEntity<String> provision(@Valid @RequestBody FleetParam fleetParam) {
		UUID fleetUUID = UUID.randomUUID();
		String fleetTerraformFolder = "/tmp/terra/" + fleetUUID + "/";
		Path sourceDirectory = Paths.get(FleetServiceImpl.TERRAFORM_TEMPLATE_FOLDER);
        Path targetDirectory = Paths.get(fleetTerraformFolder);

        //copy source to target using Files Class
        try {
			//Files.copy(sourceDirectory, targetDirectory, CopyOption);
			FileSystemUtils.copyRecursively(sourceDirectory, targetDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "text/html")
					.body(e.getLocalizedMessage());
		}
		FleetState fs = new FleetState(fleetParam, fleetUUID, fleetTerraformFolder);
		fleets.add(fs);
		return ResponseEntity.ok().header("Content-Type", "text/html")
				.body(fleetService.provision(fs));
	}

}
