package org.its.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Configuration
public class AWSConfig {

	@Bean
	public Ec2Client ec2client(){	
		//Create an Ec2Client object
        Region region = Region.US_EAST_2;
        return Ec2Client.builder()
                .region(region)
                .build();
		
	}
}
