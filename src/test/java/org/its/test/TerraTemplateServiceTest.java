package org.its.test;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.its.service.impl.TerraTemplateServiceImpl;
import org.testng.annotations.Test;

@Test
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
		
		ttsi.replaceVariables("/tmp/terra2/variables.tf", 20, subnets, securityGroups, Optional.of("eds"), null, Optional.of("abc"), UUID.randomUUID());
	}

}
