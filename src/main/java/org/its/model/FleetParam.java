package org.its.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class FleetParam {
	@ApiModelProperty(position = 1, required = true)
	private Integer numberOfNodes;
	
	@ApiModelProperty(position = 2, required = false)
	private List<String> subnets;
	@ApiModelProperty(position = 3, required = false)
	private List<String> securityGroups;
	@ApiModelProperty(position = 4, required = false)
	private String instanceType;
	@ApiModelProperty(position = 5, required = false)
	private int volSize;
	@ApiModelProperty(position = 6,required = false)
	private String amiId;
	
	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	
	
	public Integer getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(Integer numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	public List<String> getSubnets() {
		return subnets;
	}

	public void setSubnets(List<String> subnets) {
		this.subnets = subnets;
	}

	public List<String> getSecurityGroups() {
		return securityGroups;
	}

	public void setSecurityGroups(List<String> securityGroups) {
		this.securityGroups = securityGroups;
	}

	 

	public int getVolSize() {
		return volSize;
	}

	public void setVolSize(int volSize) {
		this.volSize = volSize;
	}

	public String getAmiId() {
		return amiId;
	}

	public void setAmiId(String amiId) {
		this.amiId = amiId;
	}
	
}
