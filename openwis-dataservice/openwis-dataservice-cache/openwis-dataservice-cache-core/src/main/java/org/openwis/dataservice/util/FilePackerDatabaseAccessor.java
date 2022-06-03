package org.openwis.dataservice.util;

import javax.ejb.Local;
import javax.ejb.Remote;

//Zhan
//the following change is for openWIS issue #281
//@Local
@Remote
public interface FilePackerDatabaseAccessor {
	
	public void initialize(String instanceName);
	
	// Transmission Sequence Number
	public int getTransmissionSequenceNumber(String instanceName);
	
	public void increaseTransmissionSequenceNumber(String instanceName);
	
	// Package Number
	public int getPackageNumber(String instanceName);
	
	public void increasePackageNumber(String instanceName);
	
	// Number of included Bulletins
	public int getNumberOfIncludedBulletins(String instanceName);
	
	public void increaseeNumberOfIncludedBulletins(String instanceName);
	
	public void resetNumberOfIncludedBulletins(String instanceName);
	
	// Package Name
	public String getPackageName(String instanceName);
	
	public void setPackageName(String instanceName, String newPackageName);
}