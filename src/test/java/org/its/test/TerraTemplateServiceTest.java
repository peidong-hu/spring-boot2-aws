package org.its.test;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
		
		ttsi.replaceVariables("/tmp/terra2/variables.tf", 20, subnets, securityGroups, insTypes, null, Optional.of("abc"));
	}

}
