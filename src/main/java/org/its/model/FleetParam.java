package org.its.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class FleetParam {
	@ApiModelProperty(required = true)
	private Integer numberOfNodes;
	
	@ApiModelProperty(required = false)
	private List<String> subnets;
	@ApiModelProperty( required = false)
	private List<String> securityGroups;
	@ApiModelProperty( required = false)
	private List<String> instanceTypes;
	@ApiModelProperty( required = false)
	private int volSize;
	@ApiModelProperty(required = false)
	private String amiId;
	
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

	public List<String> getInstanceTypes() {
		return instanceTypes;
	}

	public void setInstanceTypes(List<String> instanceTypes) {
		this.instanceTypes = instanceTypes;
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
