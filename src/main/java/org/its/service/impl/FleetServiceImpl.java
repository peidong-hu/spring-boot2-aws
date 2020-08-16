package org.its.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.its.model.FleetParam;
import org.its.rest.controller.FleetController.FleetState;
import org.its.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.terraform.TerraformClient;
import com.microsoft.terraform.TerraformOptions;

@Service
public class FleetServiceImpl implements FleetService {

	public final static String TERRAFORM_TEMPLATE_FOLDER = "/tmp/terra2/";

	private final static String TERRAFORM_GIT_URL = "git@github.com:peidong-hu/terraform-ec2-fleet.git";
	
	private final static String FLEET_UUID_KEYWORD = "Fleet_UUID_KEYWORD";

	@Autowired
	private JGitServiceImpl gitService;

	@Autowired
	private TerraTemplateServiceImpl terraTemplateService;

	private String removeColor(String input) {
		// TODO code application logic here
		return input.replace("\033[0m", "").replace("\033[31m", "").replace("\033[32m", "").replace("\033[33m", "")
				.replace("\033[34m", "").replace("\033[35m", "").replace("\033[36m", "").replace("\033[37m", "")
				.replace("\033[1m", "");

	}

	public String provision(int numberOfNodes, List<String> subnets, List<String> securityGroups,
			Optional<String> instanceTypes, Optional<Integer> volSize, Optional<String> amiId, UUID uuid,
			String terraformFolder, boolean dryRun) {

		if (gitService.getGitRepo(TERRAFORM_GIT_URL, terraformFolder).isPresent()
				&& terraTemplateService.replaceVariables(terraformFolder + "variables.tf", numberOfNodes, subnets,
						securityGroups, instanceTypes, volSize, amiId, uuid)) {

			TerraformOptions options = new TerraformOptions();

			try (TerraformClient client = new TerraformClient(options)) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);
				// IMPORTANT: Save the old System.out!
				PrintStream old = System.out;
				// Tell Java to use your special stream
				System.setOut(ps);

				client.setOutputListener(System.out::println);
				client.setErrorListener(System.err::println);

				client.setWorkingDirectory(new File(terraformFolder));

				client.plan().get();
				if (dryRun) {
					// Put things back
					System.out.flush();
					System.setOut(old);
					// Show what happened
					System.out.println("Here: " + baos.toString());
					return FLEET_UUID_KEYWORD +": (Note: dry-run/Wet-run consider different fleet having different uuid) "+ uuid + "\n" + removeColor(baos.toString());
				} else {
					client.apply().get();
					// Put things back
					System.out.flush();
					System.setOut(old);
					// Show what happened
					System.out.println("Here: " + baos.toString());
					return FLEET_UUID_KEYWORD +": (Note: dry-run/Wet-run consider different fleet having different uuid)"+ uuid + "\n"  + removeColor(baos.toString());

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return "failed";

	}

	@Override
	public String provision(FleetState fs) {
		FleetParam fleetParam = fs.getFleetParam();

		return this.provision(fleetParam.getNumberOfNodes(),
				fleetParam.getSubnets() == null ? new ArrayList<String>() : fleetParam.getSubnets(),
				fleetParam.getSecurityGroups() == null ? new ArrayList<String>() : fleetParam.getSecurityGroups(),
				Optional.ofNullable(fleetParam.getInstanceType()), Optional.ofNullable(fleetParam.getVolSize()),
				Optional.ofNullable(fleetParam.getAmiId()), fs.getFleetUUID(), fs.getFleetTerraformFolder(),
				fs.isDryRun());

	}

}
