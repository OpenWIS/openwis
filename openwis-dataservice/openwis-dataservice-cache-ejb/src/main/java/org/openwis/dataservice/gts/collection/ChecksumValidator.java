package org.openwis.dataservice.gts.collection;

import java.io.File;

import org.openwis.dataservice.util.ChecksumCalculator;
import org.openwis.dataservice.util.FileInfo;

public class ChecksumValidator implements ProductValidator {

	@Override
	public boolean validate(FileInfo fileInfo) {
		File productFile = new File(fileInfo.getFileURL());
		String checksum = fileInfo.getChecksum();
		int numberOfChecksumBytes = fileInfo.getNumberOfChecksumBytes();
		
		boolean valid = false;
		
		String newChecksum = ChecksumCalculator.calculateChecksumOnFile(productFile,numberOfChecksumBytes);		
		if (checksum != null && newChecksum != null && newChecksum.equals(checksum)) valid = true;
		
		return valid;
	}
}