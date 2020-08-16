package org.its.test;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.its.SpringRestApplication;
import org.its.config.SwaggerConfig;
import org.its.service.impl.TerraTemplateServiceImpl;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
//import org.testng.annotations.Test;
import org.junit.Test;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringRestApplication.class,
		SwaggerConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TerraTemplateServiceTest {

	@Test
	void templateTest() {
		TerraTemplateServiceImpl ttsi = new TerraTemplateServiceImpl();
		List<String> subnets = new ArrayList<String>() {{
		    add("Abc");
		   
		}};
		List<String> securityGroups = new ArrayList<String>() {{
		    add("Abc");
		   
		}};
		List<String> insTypes = new ArrayList<String>() {{
		    add("Abc");
		   
		}};
//		{
//			  "numberOfNodes": 10,
//			  "subnets": [
//			    "subnet-d3c3b7a9",
//			    "subnet-9ef12ed2"
//			  ],
//			  "securityGroups": [
//			    "sg-9301b6fd"
//			  ],
//			  "instanceType": "t2.micro",
//			  "volSize": 5,
//			  "amiId": "ami-007e9fbe81cfbf4fa"
//			}
		
		ttsi.replaceVariables("/tmp/terra3/variables.tf", 20, subnets, securityGroups, Optional.of("eds"), null, Optional.of("abc"), UUID.randomUUID());
	}

}
