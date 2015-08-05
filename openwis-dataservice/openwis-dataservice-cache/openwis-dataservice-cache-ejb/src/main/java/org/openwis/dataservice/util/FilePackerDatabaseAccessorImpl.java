package org.openwis.dataservice.util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.common.domain.entity.cache.FilePackerInstance;

@Stateless(name = "FilePackerDatabaseAccessor")
@TransactionTimeout(18000)
public class FilePackerDatabaseAccessorImpl implements FilePackerDatabaseAccessor {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private static final int MAXIMUM_PACKAGE_NUMBER = 99999999;
	private static final int MAXIMUM_TRANSMISSION_SEQUENCE_NUMBER = 999;
	
	// --------------------
	
	public void initialize(String instanceName){
		FilePackerInstance filePackerInstance = null;
		try{
			Query query = entityManager.createQuery("SELECT fpi FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
			filePackerInstance = (FilePackerInstance) query.getSingleResult();
		}
		catch (Exception e) {
			filePackerInstance = new FilePackerInstance();
			filePackerInstance.setName(instanceName);
			filePackerInstance.setNumberOfIncludedBulletins(Integer.valueOf(0));
			filePackerInstance.setPackageNumber(Integer.valueOf(0));
			filePackerInstance.setTransmissionSequenceNumber(Integer.valueOf(0));
			entityManager.persist(filePackerInstance);
			entityManager.flush();
		}		
	}
	
	// Transmission Sequence Number
	public int getTransmissionSequenceNumber(String instanceName){
		Query query = entityManager.createQuery("SELECT fpi.transmissionSequenceNumber FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
		Integer transmissionSequenceNumber = (Integer) query.getSingleResult();
		return transmissionSequenceNumber.intValue();
	}
	
	public void increaseTransmissionSequenceNumber(String instanceName){
		Query query = entityManager.createQuery("SELECT fpi FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
		FilePackerInstance filePackerInstance = (FilePackerInstance) query.getSingleResult();
		Integer transmissionSequenceNumber = filePackerInstance.getTransmissionSequenceNumber();
		Integer newTransmissionSequenceNumber = Integer.valueOf(transmissionSequenceNumber + 1);
		if (newTransmissionSequenceNumber > MAXIMUM_TRANSMISSION_SEQUENCE_NUMBER) newTransmissionSequenceNumber = Integer.valueOf(0);
		filePackerInstance.setTransmissionSequenceNumber(newTransmissionSequenceNumber);
		entityManager.merge(filePackerInstance);
		entityManager.flush();
	}
	
	// Package Number
	public int getPackageNumber(String instanceName){
		Query query = entityManager.createQuery("SELECT fpi.packageNumber FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
		Integer packageNumber = (Integer) query.getSingleResult();
		return packageNumber.intValue();
	}
	
	public void increasePackageNumber(String instanceName){
		Query query = entityManager.createQuery("SELECT fpi FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
		FilePackerInstance filePackerInstance = (FilePackerInstance) query.getSingleResult();
		Integer packageNumber = filePackerInstance.getPackageNumber();
		Integer newPackageNumber = Integer.valueOf(packageNumber + 1);
		if (newPackageNumber > MAXIMUM_PACKAGE_NUMBER) newPackageNumber = Integer.valueOf(0);
		filePackerInstance.setPackageNumber(newPackageNumber);
		entityManager.merge(filePackerInstance);
		entityManager.flush();
	}
	
	// Number of included Bulletins
	public int getNumberOfIncludedBulletins(String instanceName){
		Query query = entityManager.createQuery("SELECT fpi.numberOfIncludedBulletins FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
		Integer numberOfIncludedBulletins = (Integer) query.getSingleResult();
		return numberOfIncludedBulletins.intValue();
	}
	
	public void increaseeNumberOfIncludedBulletins(String instanceName){
		Query query = entityManager.createQuery("SELECT fpi FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
		FilePackerInstance filePackerInstance = (FilePackerInstance) query.getSingleResult();
		Integer numberOfIncludedBulletins = filePackerInstance.getNumberOfIncludedBulletins();
		filePackerInstance.setNumberOfIncludedBulletins(Integer.valueOf(numberOfIncludedBulletins + 1));
		entityManager.merge(filePackerInstance);
		entityManager.flush();
	}
	
	public void resetNumberOfIncludedBulletins(String instanceName){
		Query query = entityManager.createQuery("SELECT fpi FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
		FilePackerInstance filePackerInstance = (FilePackerInstance) query.getSingleResult();		
		filePackerInstance.setNumberOfIncludedBulletins(Integer.valueOf(0));
		entityManager.merge(filePackerInstance);
		entityManager.flush();
	}
	
	// Package Name
	public String getPackageName(String instanceName){
		String packageName = null;
		try{
			Query query = entityManager.createQuery("SELECT fpi.packageName FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
			packageName = (String) query.getSingleResult();
		}
		catch(Exception e){
			packageName = null;
		}
		return packageName;
	}
	
	public void setPackageName(String instanceName, String newPackageName){
		Query query = entityManager.createQuery("SELECT fpi FROM FilePackerInstance fpi WHERE fpi.name = '" + instanceName + "'");
		FilePackerInstance filePackerInstance = (FilePackerInstance) query.getSingleResult();		
		filePackerInstance.setPackageName(newPackageName);
		entityManager.merge(filePackerInstance);
		entityManager.flush();
	}
}