# OpenWIS Puppet Scripts

This repository hosts the puppet scripts used to deploy OpenWIS 3.14.X 

> Note: Currently this repository only contains an example configuration for setting up an OpenWIS Development environment based on 4 nodes.

 
* Data (exampledata.pp)

* Auth (examplesecurity.pp)

* Admin Portal (exampleadminportal.pp)

* User Portal (exmapleuserportal.pp)


## Puppet Pre-requisites

> Note: These scripts were used to build a development environment on base images provided by the Met Office (which are not available) but the pre-requisites are listed below so that a similar base image can be created.


* Data
    *  RHEL/CENTOS 6.5 or 7 with Postgres 9.2  / OpenJDK7 Installed

* Auth
    *  RHEL/CENTOS 6.5 or 7 with Postgres 9.2  / OpenJDK7 / OpenDJ 2.6 Installed 
    
> Note: There is some manual confirguration to do with OpenAM See [Manual Configuration] (docs/manual_config.md)

* Admin Portal
    *  RHEL/CENTOS 6.5 or 7 

* User Portal
    *  RHEL/CENTOS 6.5 or 7


## Building OpenWIS

**The nodes should be built in the order defined above** (Data > Auth > adminportal > userportal)


### Set up Git
First, ssh to the vm you want to build and clone this repo. Git needs to be installed on the vm but an alternative option may be to scp the repo to the vm 

 
     # Install / Configure Git on VM
     sudo yum -y install git
     git config --global user.name "<your_username>"
     git config --global user.email "<your_email>"

     # Add Key to Git
     ssh-keygen -t rsa -C "<your_email>"
     cat .ssh/id_rsa.pub
    
     # Clone the Repo and pull develop
     git clone <REPO>
     cd resources/puppet
     git checkout -b develop
     git pull origin develop
    
### Run Puppet Script

Then build an environment, from the base folder (ukmo-openwis-puppet) run the following command as below 

 
     # Run Puppet
     HNAME=$(hostname -s) && sudo puppet apply nodes/${HNAME: -5}/${HNAME}.pp --modulepath=openwis:components:modules --hiera_config=hiera.yaml --strict_variables           

> where:
  
>  - **hostname** is the name of the machine / script name in the nodes folder
>  - the puppet command is run as one line



# Other Links

* [Manual Configuration] (docs/manual_config.md)


* [Set up Your Development Environment] (docs/set_up_dev_ide.md)
