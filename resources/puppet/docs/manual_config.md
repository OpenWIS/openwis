# Manual Configuration

There are a few Manual Configuration steps required for OpenWIS

Once the servers have been built the OpenWIS software components are deployed using a mixture of Puppet (for tasks that can be automated) and manual intervention (for tasks that cannot).

## Security Service (Create IDP, Login TimeOut, Create Remote SP)
After running Puppet to deploy the Security Service including Openam, the following steps need to be done
 (See https://github.com/OpenWIS/openwis/blob/develop/docs/IG-OpenWIS-1-v02%209-MigrationOpenAM-v3.doc?raw=true)


* step **3.3.1** to set up the Identity Providers (This should match value used in puppet yaml "idp_name")

* step **3.2.2** (Only the Login Page timeout configuration at end of this section needs to be done)

* **Deploy Portals** using puppet

* and finally run step **3.3.4.2 (with 3.3.5)** to add the Remote Service Providers and assertion properties

## Portals

### Cache Configuration

Under OpenWIS Admin Portal Select Cache Configuration and disable Feeding (This is only needed for replication)

### Set up Default Templates

2 templates needed to be added to OpenWIS following installation

* Log onto the Admin Portal

* Navigate to Metadata Service > Templates

* Click **'Add default'** button

* Add **'Template for Stop-Gap metadata'** and **'Template for OpenWIS'** by Selecting Default button and adding iso **19139**

### Customizing Login Page

* Login to OpenAM console as the amAdmin user

* Navigate to Configuration | Authentication | Core | Global Attributes

* Uncheck **"XUI Interface"**

* Save he change and logout

