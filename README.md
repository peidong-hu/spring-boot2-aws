# Restful API with Swagger-UI to Manage AWS Ec2 Fleet

## Features

##### Integrate with Terraform to use EC2 fleets as the way of provisioning VMs for the deployment

##### Provisioned EC2 fleets have 80% spot and 20% on-demand instances

##### Provisioned EC2 fleets span at least 2 AZ, 'us-east-2b' and 'us-east-2c'

##### Background batch job runs every 30 seconds to provision a non-multi-attach IO1 vol and attach it to instances. (Multi-attach IO1 vol is not supported in 'us-east-2b' and 'us-east-2c', and multi-attach volmes need special type of instances to test. Currently not implemented in the code.

##### The bucket of 16 instances for each batch job processing will possibly leave some instances never been volume attached if the user gives volume size in api call which requires this user's instances can only attached with certain size volume. For this requirement of the feature needs to be give more specific instructions for how to deal with this case. Right now, the bucket size is set as 1, so all the instances will be guaranteed attached with a volume. 

##### The restful api tool accepts the following parameters through a json object, 

#### num_nodes: (required); Integer. Used to specify the total number of nodes needed.

#### subnets: (optional); Array. Used to specify the subnet ids to be used with a minimum of 2 subnets. Default value is ["subnet-d3c3b7a9", "subnet-9ef12ed2"]

#### security_groups: (optional); Array. Used to specify the security groups applied to the provisioned ec2 instance, with a minimum of 1 security group. Default value is ["sg-9301b6fd"]

#### instances_types: (optional); Array. Used to to specify the ec2 instances types to use. Defaults to t3.micro.

#### multi_attach_vol_size: (optional); Integer. Used to specify the size in GB of the shared IO1 vol. Defaults to 4GB! (AWS access min 4GB for IO1 volume, can't be set to 3GB as default)

#### ami_id: (optional); String. Used to specify the AMI used for the nodes. Defaults to Ubuntu 18.04 server

## Build Project

##### To build the project, Run following command
	mvn clean install

## Running the Project

##### To run the project, Run following command
	mvn spring-boot:run

##### Testing the API using swagger
Click on the link to access live demo running on AWS cloud, ask Peidong for the basic auth username/password

[Swagger-UI](https://aws-fleet.peidong.eco-perf.cloud/swagger-ui.html "Swagger-UI") https://aws-fleet.peidong.eco-perf.cloud/swagger-ui.html

Click on the link to access the swagger ui when run locally

[Swagger-UI](http://localhost:7772/swagger-ui.html "Swagger-UI") http://localhost:7772/swagger-ui.html

### Creating static API Documentation
To create static document in pdf and html format, Run following command

	mvn test
	
Three files will be created in **\src\main\resources\asciidoc** directory

1.  api-doc.adoc

2.  api-doc.html

3.  api-doc.pdf


* PDF Sample : 
[Generated pdf document](https://github.com/AtulRanjan/spring-boot2/tree/master/src/main/resources/asciidoc/api-doc.pdf "Generated pdf document") 


* HTML Sample : [Generated html document](https://github.com/AtulRanjan/spring-boot2/tree/master/src/main/resources/asciidoc/api-doc.html "Generated html document") | [Preview](http://htmlpreview.github.io/?https://github.com/AtulRanjan/spring-boot2/blob/master/src/main/resources/asciidoc/api-doc.html "View doc")