package org.openwis.dataservice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChecksumCalculator {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChecksumCalculator.class);
	
	/**
	 * Calculates the MD5-checksum an a byte array using the first (length) bytes.
	 */
	public static String getChecksum(final byte[] input, final int length){
	    MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
         LOG.error(e.getMessage(), e);
		}
		if (input != null){
			if (length > 0){
				if (length <= input.length){
					m.update(input,0,length);
				} else {
					m.update(input,0,input.length);
				}
			} else { 
				throw new IllegalAccessError("Length for checksum calculation was negative");
			}
		}			
		else {
			throw new NullPointerException("Input for checksum calculation was null");		
		}
	    return new BigInteger(1,m.digest()).toString(16);
	}		
	
	public static String calculateChecksumOnFile(File file, int numberOfChecksumBytes){		
		FileInputStream fis = null;
		FileChannel fc = null;
		ByteBuffer buffer = null;
		int size = 0;
		try {
			fis = new FileInputStream(file);
			fc = fis.getChannel();
			size = Math.min(fis.available(), numberOfChecksumBytes);
			buffer = ByteBuffer.allocateDirect(size);
			fc.read(buffer);
		}
		catch (FileNotFoundException e) {
         LOG.error(e.getMessage(), e);
		}
		catch (IOException e) {
         LOG.error(e.getMessage(), e);
		}
		buffer.rewind();
		byte[] data = new byte[size];
		buffer.get(data);
		buffer = null;
		
		String checksum = "";
		try {
			checksum = getChecksum(data, size); 
		}
		catch (IllegalAccessError e){
			LOG.error("Length for checksum calculation was negative.");
		}
		catch (NullPointerException e1){
			LOG.error("Input for checksum calculation was null");
		}
		return checksum;
	}
}