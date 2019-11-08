package org.openwis.dataservice.gts.collection;

import org.openwis.dataservice.util.FileInfo;

/**
 * This is a plugin interface for validating products (files) to be ingested.
 * Every implementing class must only implement the 'validate(FileInfo) : boolean' method.
 * In this method an arbitrary test can be performed using the following attributes of the FileInfo:
 * fileURL,
 * priority,
 * metadata-URNList/-IDList,
 * checksum,
 * numberOfChecksumBytes,
 * gtsCategory
 * 
 * @author kulbatzki
 *
 */
public interface ProductValidator {

	/**
	 * This method is used to check the given file with the provided (implemented) algorithm.
	 * @param fileInfo the represented file to be tested
	 * @return {@code true} if the file (represented by the FileInfo object) passed the test, {@code false} otherwise
	 */
	public boolean validate(FileInfo fileInfo);
}