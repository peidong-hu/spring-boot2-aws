package org.its.model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.ec2.model.InstanceBlockDeviceMapping;
import software.amazon.awssdk.services.ec2.model.StateReason;

public class Instance {
	/**
	 * <p>
	 * The ID of the instance.
	 * </p>
	 */
	private String instanceId;
	/**
	 * <p>
	 * Any block device mapping entries for the instance.
	 * </p>
	 */
	
	private String mountedNonRoot;
	/**
	 * <p>
	 * The current state of the instance.
	 * </p>
	 */
	private String state;
	/**
	 * <p>
	 * The ID of the AMI used to launch the instance.
	 * </p>
	 */
	private String imageId;
	

	public String getKernelId() {
		return kernelId;
	}

	public void setKernelId(String kernelId) {
		this.kernelId = kernelId;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPrivateDnsName() {
		return privateDnsName;
	}

	public void setPrivateDnsName(String privateDnsName) {
		this.privateDnsName = privateDnsName;
	}

	public String getPrivateIpAddress() {
		return privateIpAddress;
	}

	public void setPrivateIpAddress(String privateIpAddress) {
		this.privateIpAddress = privateIpAddress;
	}

	public String getPublicDnsName() {
		return publicDnsName;
	}

	public void setPublicDnsName(String publicDnsName) {
		this.publicDnsName = publicDnsName;
	}

	public String getPublicIpAddress() {
		return publicIpAddress;
	}

	public void setPublicIpAddress(String publicIpAddress) {
		this.publicIpAddress = publicIpAddress;
	}

	public String getRamdiskId() {
		return ramdiskId;
	}

	public void setRamdiskId(String ramdiskId) {
		this.ramdiskId = ramdiskId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateTransitionReason() {
		return stateTransitionReason;
	}

	public void setStateTransitionReason(String stateTransitionReason) {
		this.stateTransitionReason = stateTransitionReason;
	}

	public String getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}

	public String getVpcId() {
		return vpcId;
	}

	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}

	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	public String getMountedNonRoot() {
		return mountedNonRoot;
	}

	public void setMountedNonRoot(String mountedNonRoot) {
		this.mountedNonRoot = mountedNonRoot;
	}

	public String getClientToken() {
		return clientToken;
	}

	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

	public Boolean getEbsOptimized() {
		return ebsOptimized;
	}

	public void setEbsOptimized(Boolean ebsOptimized) {
		this.ebsOptimized = ebsOptimized;
	}

	public Boolean getEnaSupport() {
		return enaSupport;
	}

	public void setEnaSupport(Boolean enaSupport) {
		this.enaSupport = enaSupport;
	}

	public String getHypervisor() {
		return hypervisor;
	}

	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}

	public String getInstanceLifecycle() {
		return instanceLifecycle;
	}

	public void setInstanceLifecycle(String instanceLifecycle) {
		this.instanceLifecycle = instanceLifecycle;
	}

	public String getOutpostArn() {
		return outpostArn;
	}

	public void setOutpostArn(String outpostArn) {
		this.outpostArn = outpostArn;
	}

	public String getRootDeviceName() {
		return rootDeviceName;
	}

	public void setRootDeviceName(String rootDeviceName) {
		this.rootDeviceName = rootDeviceName;
	}

	public String getRootDeviceType() {
		return rootDeviceType;
	}

	public void setRootDeviceType(String rootDeviceType) {
		this.rootDeviceType = rootDeviceType;
	}

	public String getSpotInstanceRequestId() {
		return spotInstanceRequestId;
	}

	public void setSpotInstanceRequestId(String spotInstanceRequestId) {
		this.spotInstanceRequestId = spotInstanceRequestId;
	}

	public String getSriovNetSupport() {
		return sriovNetSupport;
	}

	public void setSriovNetSupport(String sriovNetSupport) {
		this.sriovNetSupport = sriovNetSupport;
	}

	

	public String getVirtualizationType() {
		return virtualizationType;
	}

	public void setVirtualizationType(String virtualizationType) {
		this.virtualizationType = virtualizationType;
	}

	public String getCapacityReservationId() {
		return capacityReservationId;
	}

	public void setCapacityReservationId(String capacityReservationId) {
		this.capacityReservationId = capacityReservationId;
	}

	/**
	 * <p>
	 * The instance type.
	 * </p>
	 */
	private String instanceType;
	/**
	 * <p>
	 * The kernel associated with this instance, if applicable.
	 * </p>
	 */
	private String kernelId;
	/**
	 * <p>
	 * The name of the key pair, if this instance was launched with an associated
	 * key pair.
	 * </p>
	 */
	private String keyName;
	
	/**
	 * <p>
	 * The value is <code>Windows</code> for Windows instances; otherwise blank.
	 * </p>
	 */
	private String platform;
	/**
	 * <p>
	 * (IPv4 only) The private DNS hostname name assigned to the instance. This DNS
	 * hostname can only be used inside the Amazon EC2 network. This name is not
	 * available until the instance enters the <code>running</code> state.
	 * </p>
	 * <p>
	 * [EC2-VPC] The Amazon-provided DNS server resolves Amazon-provided private DNS
	 * hostnames if you've enabled DNS resolution and DNS hostnames in your VPC. If
	 * you are not using the Amazon-provided DNS server in your VPC, your custom
	 * domain name servers must resolve the hostname as appropriate.
	 * </p>
	 */
	private String privateDnsName;
	/**
	 * <p>
	 * The private IPv4 address assigned to the instance.
	 * </p>
	 */
	private String privateIpAddress;
	/**
	 * <p>
	 * (IPv4 only) The public DNS name assigned to the instance. This name is not
	 * available until the instance enters the <code>running</code> state. For
	 * EC2-VPC, this name is only available if you've enabled DNS hostnames for your
	 * VPC.
	 * </p>
	 */
	private String publicDnsName;
	/**
	 * <p>
	 * The public IPv4 address, or the Carrier IP address assigned to the instance,
	 * if applicable.
	 * </p>
	 * <p>
	 * A Carrier IP address only applies to an instance launched in a subnet
	 * associated with a Wavelength Zone.
	 * </p>
	 */
	private String publicIpAddress;
	/**
	 * <p>
	 * The RAM disk associated with this instance, if applicable.
	 * </p>
	 */
	private String ramdiskId;
	
	/**
	 * <p>
	 * The reason for the most recent state transition. This might be an empty
	 * string.
	 * </p>
	 */
	private String stateTransitionReason;
	/**
	 * <p>
	 * [EC2-VPC] The ID of the subnet in which the instance is running.
	 * </p>
	 */
	private String subnetId;
	/**
	 * <p>
	 * [EC2-VPC] The ID of the VPC in which the instance is running.
	 * </p>
	 */
	private String vpcId;
	/**
	 * <p>
	 * The architecture of the image.
	 * </p>
	 */
	private String architecture;
	
	/**
	 * <p>
	 * The idempotency token you provided when you launched the instance, if
	 * applicable.
	 * </p>
	 */
	private String clientToken;
	/**
	 * <p>
	 * Indicates whether the instance is optimized for Amazon EBS I/O. This
	 * optimization provides dedicated throughput to Amazon EBS and an optimized
	 * configuration stack to provide optimal I/O performance. This optimization
	 * isn't available with all instance types. Additional usage charges apply when
	 * using an EBS Optimized instance.
	 * </p>
	 */
	private Boolean ebsOptimized;
	/**
	 * <p>
	 * Specifies whether enhanced networking with ENA is enabled.
	 * </p>
	 */
	private Boolean enaSupport;
	/**
	 * <p>
	 * The hypervisor type of the instance. The value <code>xen</code> is used for
	 * both Xen and Nitro hypervisors.
	 * </p>
	 */
	private String hypervisor;
	/**
	 * <p>
	 * Indicates whether this is a Spot Instance or a Scheduled Instance.
	 * </p>
	 */
	private String instanceLifecycle;
	/**
	 * <p>
	 * The Amazon Resource Name (ARN) of the Outpost.
	 * </p>
	 */
	private String outpostArn;
	/**
	 * <p>
	 * The device name of the root device volume (for example,
	 * <code>/dev/sda1</code>).
	 * </p>
	 */
	private String rootDeviceName;
	/**
	 * <p>
	 * The root device type used by the AMI. The AMI can use an EBS volume or an
	 * instance store volume.
	 * </p>
	 */
	private String rootDeviceType;
	/**
	 * <p>
	 * If the request is a Spot Instance request, the ID of the request.
	 * </p>
	 */
	private String spotInstanceRequestId;
	/**
	 * <p>
	 * Specifies whether enhanced networking with the Intel 82599 Virtual Function
	 * interface is enabled.
	 * </p>
	 */
	private String sriovNetSupport;
	
	/**
	 * <p>
	 * The virtualization type of the instance.
	 * </p>
	 */
	private String virtualizationType;
	/**
	 * <p>
	 * The ID of the Capacity Reservation.
	 * </p>
	 */
	private String capacityReservationId;

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public static final Instance build(software.amazon.awssdk.services.ec2.model.Instance awsInstance) {

		org.its.model.Instance retVal = new Instance();

		Field[] retValFields = retVal.getClass().getDeclaredFields();

		for (int index = 0; index < retValFields.length; index++) {
			try {
				retValFields[index].setAccessible(true);
				if (retValFields[index].getName().equalsIgnoreCase("mountedNonRoot")) {
					List<InstanceBlockDeviceMapping> devices = awsInstance.blockDeviceMappings().stream().filter(device->device.deviceName().contains("/sdf")).collect(Collectors.toList());
					if (devices.size() == 1) {
						retValFields[index].set(retVal, devices.get(0).deviceName());
					} else {
						retValFields[index].set(retVal, null);
					}
				} else if (retValFields[index].getName().equalsIgnoreCase("state")) {
					retValFields[index].set(retVal, awsInstance.state().toString());
					
				} else {

					Field awsField = awsInstance.getClass().getDeclaredField(retValFields[index].getName());
					awsField.setAccessible(true);

					retValFields[index].set(retVal, awsField.get(awsInstance));
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return retVal;

		

	}

}
