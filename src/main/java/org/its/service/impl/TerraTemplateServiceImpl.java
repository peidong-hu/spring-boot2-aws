package org.its.service.impl;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;


@Service
public class TerraTemplateServiceImpl {
	
	private static String BZONE_SUBNETS_TAG = "US-EAST-2B-SUBNET-IDS";
	private static String CZONE_SUBNETS_TAG = "US-EAST-2C-SUBNET-IDS";
	private static String SECURITY_GROUPS_TAG = "SG-IN-TEMPLATE";
	private static String INSTANCE_TYPE_TAG = "INS_TYPE";
	private static String VOLUME_SIZE_TAG = "";
	private static String AMI_ID_TAG = "AMI-ID";
	private static String BZONE_TOTAL_TAG = "BZONE-TOTAL";
	private static String BZONE_SPOT_SIZE_TAG = "BZONE-SPOT-COUNT";
	private static String BZONE_ONDEMAND_SIZE_TAG = "BZONE-ONDEMAND-COUNT";
	private static String CZONE_TOTAL_TAG = "CZONE-TOTAL";
	private static String CZONE_SPOT_SIZE_TAG = "CZONE-SPOT-COUNT";
	private static String CZONE_ONDEMAND_SIZE_TAG = "CZONE-ONDEMAND-COUNT";
	private static String UUID_TAG = "MULTI-ATTACH-UUID";

	// private static final String remoteUrl =
	// "https://github.com/peidong-hu/Hygieia.git";
	private int calculateEachInstanceTypeProvisionTotal(int numberOfInstanceTypes, int numberOfNodes) {
		int retVal = 0;
		if (numberOfInstanceTypes == 0) {
			retVal = numberOfNodes;
		} else if (numberOfNodes > numberOfInstanceTypes) {
			retVal = numberOfNodes/numberOfInstanceTypes;
		}
		return retVal;
	}
	private int calculateLastInstanceTypeProvisionTotal(int numberOfInstanceTypes, int numberOfNodes) {
		int retVal = 0;
		if (numberOfNodes > numberOfInstanceTypes) {
			retVal = numberOfNodes%numberOfInstanceTypes + numberOfNodes/numberOfInstanceTypes;
		}
		return retVal;
	}
	private int calculateEachInstanceTypeProvisionSpotSize(Integer eachInstanceTypeProvisionSize) {
		int retVal = 0;
		retVal = (int) Math.ceil(eachInstanceTypeProvisionSize * 0.8);
		return retVal;
	}
	private int calculateEachInstanceTypeProvisionOnDemandSize(Integer spotSize,  int numberOfNodes) {
		return numberOfNodes - spotSize;
	}
	private int calculateEachInstanceTypeProvisionSpotBzoneSize(Integer eachInstanceTypeProvisionSpotSize) {
		int retVal = 0;
		retVal = (int) Math.ceil(eachInstanceTypeProvisionSpotSize * 0.5);
		return retVal;
	}
	private int calculateEachInstanceTypeProvisionSpotCzoneSize(Integer eachInstanceTypeProvisionSpotSize, int spotBzoneSize) {
		
		return eachInstanceTypeProvisionSpotSize - spotBzoneSize;
	}
	
	private int calculateEachInstanceTypeProvisionOndemandBzoneSize(Integer eachInstanceTypeProvisionOndemandSize) {
		int retVal = 0;
		retVal = (int) Math.ceil(eachInstanceTypeProvisionOndemandSize * 0.5);
		return retVal;
	}
	private int calculateEachInstanceTypeProvisionOndemandCzoneSize(Integer eachInstanceTypeProvisionOndemandSize, int ondemandCzoneSize) {
		
		return eachInstanceTypeProvisionOndemandSize - ondemandCzoneSize;
	}
	
	private String concatStrings(List<String> values) {
		String sg = "";
		for (int i = 0; i< values.size(); i++) {
			if (i == values.size() - 1) {
				sg = sg + "\"" + values.get(i) + "\"";
			} else {
				sg = sg + "\"" + values.get(i) + "\",";
			}
		}
		return sg;
	}
	//TODO query AWS to know which subnet belongs to which AZ;
	private List<String> parseBzoneSubnets(List<String> subnets) {
		List<String> retVal = new ArrayList<String>();
		if (subnets.size() > 1) {
			for (int i = 0; i < subnets.size(); i++) {
				if (i < subnets.size() / 2)
					retVal.add(subnets.get(i));
			}
		} else {
			retVal.addAll(subnets);
		}
		return retVal;
	}
	//TODO query AWS to know which subnet belongs to which AZ;
	private List<String> parseCzoneSubnets(List<String> subnets, List<String> bZoneSubnets) {
		List<String> retVal = new ArrayList<String>();
		if (subnets.size() > 1) {
			for (int i = 0; i < subnets.size(); i++) {
				if (!bZoneSubnets.contains(subnets.get(i)))
					retVal.add(subnets.get(i));
			}
		} else {
			retVal.addAll(subnets);
		}
		return retVal;
	}

	public boolean replaceVariables(String variableFile, int numberOfNodes, List<String> subnets, List<String> securityGroups,
			Optional<String> instanceType, Optional<Integer> volSize, Optional<String> amiId, UUID uuid) {
		boolean retVal = true;
		//TODO at this moment, one instance type supported
		int eachInstanceTypeProvisionSize = calculateEachInstanceTypeProvisionTotal(1, numberOfNodes);
		if (eachInstanceTypeProvisionSize == 0) return false;
		
		//int lastInstanceTypeProvisionSize = calculateLastInstanceTypeProvisionTotal(instanceTypes.size(), numberOfNodes);
		
		int eachInstanceTypeProvisionSpotSize = calculateEachInstanceTypeProvisionSpotSize(eachInstanceTypeProvisionSize);
		int eachInstanceTypeProvisionOndemandSize = calculateEachInstanceTypeProvisionOnDemandSize(eachInstanceTypeProvisionSpotSize, numberOfNodes);
		
		int eachBzoneSpotsize = calculateEachInstanceTypeProvisionSpotBzoneSize(eachInstanceTypeProvisionSpotSize);
		int eachBzoneOndemandSize = calculateEachInstanceTypeProvisionOndemandBzoneSize(eachInstanceTypeProvisionOndemandSize);
		
		int eachCzoneSpotsize = calculateEachInstanceTypeProvisionSpotCzoneSize(eachInstanceTypeProvisionSpotSize, eachBzoneSpotsize);
		int eachCzoneOndemandSize = calculateEachInstanceTypeProvisionOndemandCzoneSize(eachInstanceTypeProvisionOndemandSize, eachBzoneOndemandSize);
		
		try {
	        // input the file content to the StringBuffer "input"
	        BufferedReader file = new BufferedReader(new FileReader(variableFile));
	        StringBuffer inputBuffer = new StringBuffer();
	        String line;

	        while ((line = file.readLine()) != null) {
	        	if (line.contains(BZONE_TOTAL_TAG)) {
	        		line = "  default = " + (eachBzoneSpotsize + eachBzoneOndemandSize)+ " #" + BZONE_TOTAL_TAG;
	        	} else if (line.contains(BZONE_SPOT_SIZE_TAG)) {
	        		line = "  default = " + (eachBzoneSpotsize)+ " #" + BZONE_SPOT_SIZE_TAG;
	        	} else if (line.contains(BZONE_ONDEMAND_SIZE_TAG)) {
	        		line = "  default = " + (eachBzoneOndemandSize)+ " #" + BZONE_ONDEMAND_SIZE_TAG;
	        	} else if (line.contains(CZONE_TOTAL_TAG)) {
	        		line = "  default = " + (eachCzoneSpotsize + eachCzoneOndemandSize)+ " #" + CZONE_TOTAL_TAG;
	        	} else if (line.contains(CZONE_SPOT_SIZE_TAG)) {
	        		line = "  default = " + (eachCzoneSpotsize)+ " #" + CZONE_SPOT_SIZE_TAG;
	        	} else if (line.contains(CZONE_ONDEMAND_SIZE_TAG)) {
	        		line = "  default = " + (eachCzoneOndemandSize)+ " #" + CZONE_ONDEMAND_SIZE_TAG;
	        	} else if (line.contains(AMI_ID_TAG) && amiId.isPresent()) {
	        		line = "  default = \"" + (amiId.get())+ "\" #" + AMI_ID_TAG;
	        	} else if (line.contains(SECURITY_GROUPS_TAG) && securityGroups.size()>0) {
	        		line = "  default = [" + concatStrings(securityGroups) + "] #" + SECURITY_GROUPS_TAG;
	        	} else if (line.contains(BZONE_SUBNETS_TAG) && subnets.size()>0) {
	        		
	        		line = "  default = [" + concatStrings(parseBzoneSubnets(subnets)) + "] #" + BZONE_SUBNETS_TAG;
	        		
	        	} else if (line.contains(CZONE_SUBNETS_TAG) && subnets.size()>0) {
	        		
	        		line = "default = [" + concatStrings(parseCzoneSubnets(subnets, parseBzoneSubnets(subnets))) + "] #" + CZONE_SUBNETS_TAG;
	        	} else if (line.contains(INSTANCE_TYPE_TAG) && instanceType.isPresent()) {
	        		line = "default = \"" + instanceType.get() + "\" #" + INSTANCE_TYPE_TAG;
	        	} else if (line.contains(UUID_TAG)) {
	        		line = "default = \"" + uuid.toString() + "\" #" + UUID_TAG;
	        	}
	        	
	            inputBuffer.append(line);
	            inputBuffer.append('\n');
	        }
	        file.close();
	        String inputStr = inputBuffer.toString();

	        System.out.println(inputStr); // display the original file for debugging

	        
	        // overwrite the new string with the replaced line OVER the same file
	        FileOutputStream fileOut = new FileOutputStream(variableFile, false);
	        fileOut.write(inputStr.getBytes());
	        fileOut.close();

	    } catch (Exception e) {
	        System.out.println("Problem reading file.");
	    }
		return retVal;

	}

	

}
