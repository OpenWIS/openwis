package org.openwis.dataservice.common.domain.entity.cache;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OPENWIS_FILE_PACKER_INSTANCE")
public class FilePackerInstance implements Serializable {
	
	@Id
	@Column(name = "FILE_PACKER_NAME")
	private String name;
	
	/**
	 * the next transmission sequence number
	 */
	@Column(name = "TRANSMISSION_SEQUENCE_NUMBER")
	private Integer transmissionSequenceNumber;
	
	/**
	 * the next package number
	 */
	@Column(name = "PACKAGE_NUMBER")
	private Integer packageNumber;
	
	/**
	 * the number of included bulletins of the current package
	 */
	@Column(name = "NUMBER_OF_INCLUDED_BULLETINS")
	private Integer numberOfIncludedBulletins;
	
	/**
	 * the current package name
	 */
	@Column(name = "PACKAGE_NAME")
	private String packageName;

	// -------------------------------------
	
	public FilePackerInstance(){
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getTransmissionSequenceNumber() {
		return transmissionSequenceNumber;
	}


	public void setTransmissionSequenceNumber(Integer transmissionSequenceNumber) {
		this.transmissionSequenceNumber = transmissionSequenceNumber;
	}


	public Integer getPackageNumber() {
		return packageNumber;
	}


	public void setPackageNumber(Integer packageNumber) {
		this.packageNumber = packageNumber;
	}


	public Integer getNumberOfIncludedBulletins() {
		return numberOfIncludedBulletins;
	}


	public void setNumberOfIncludedBulletins(Integer numberOfIncludedBulletins) {
		this.numberOfIncludedBulletins = numberOfIncludedBulletins;
	}


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}