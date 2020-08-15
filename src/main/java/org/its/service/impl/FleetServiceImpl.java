package org.its.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.its.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.terraform.TerraformClient;
import com.microsoft.terraform.TerraformOptions;

import software.amazon.awssdk.services.ec2.Ec2Client;

@Service
public class FleetServiceImpl implements FleetService {
	
	private static String TERRAFORM_CODE_FOLDER = "/tmp/terra2/";
	
	private static String TERRAFORM_GIT_URL = "git@github.com:peidong-hu/terraform-ec2-fleet.git";
	
	@Autowired
	private JGitServiceImpl gitService;
	
	@Autowired
	private Ec2Client ec2;

	@Override
	public String provision(int numberOfNodes, List<String> subnets, List<String> securityGroups,
			List<String> instanceType, Optional<Integer> volSize, Optional<String> amiId) {
		
		if (gitService.getGitRepo(TERRAFORM_GIT_URL, TERRAFORM_CODE_FOLDER).isPresent()) {
			TerraformOptions options = new TerraformOptions();
			
			try (TerraformClient client = new TerraformClient(options)) {
				client.setOutputListener(System.out::println);
			    client.setErrorListener(System.err::println);

			    client.setWorkingDirectory(new File(TERRAFORM_CODE_FOLDER));
			    client.plan().get();
			    client.apply().get();
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
		
		
			return "ok";
		} else {
			return "failed";
		}
	}

}
