package org.openwis.dataservice.util;

import javax.ejb.Local;

@Local
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