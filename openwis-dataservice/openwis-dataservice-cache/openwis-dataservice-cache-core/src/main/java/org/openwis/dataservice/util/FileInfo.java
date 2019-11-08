/**
 *
 */
package org.openwis.dataservice.util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Defines an entity that provides information about incoming or outgoing files.
 * <p>
 * Explanation goes here.
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public class FileInfo implements Serializable{
	
   private String fileURLwithSuffix;

   private String fileURL;
	private String productFilename; // the name of the file during collection process
	private Integer priority = 4;
	private List<String> metadataURNList = new ArrayList<String>();
	private List<Long> metadataIDList = new ArrayList<Long>();
	private String checksum;
	private int numberOfChecksumBytes;
	private boolean receivedFromGTS;
	private GTScategory gtsCategory = GTScategory.LOCAL;
	
	private Date productDate;
	private Date insertionDate;
	private String originator;
	
	private long size;
	private String extension;

   public String getFileURLwithSuffix() {
      return fileURLwithSuffix;
   }

   public void setFileURLwithSuffix(String fileURLwithSuffix) {
      this.fileURLwithSuffix = fileURLwithSuffix;
   }

	public String getExtension() {
      return extension;
   }

   public void setExtension(String extension) {
      this.extension = extension;
   }

   public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public Date getProductDate() {
		return productDate;
	}

	public void setProductDate(Date productDate) {
		this.productDate = productDate;
	}

	public Date getInsertionDate() {
		return insertionDate;
	}

	public void setInsertionDate(Date insertionDate) {
		this.insertionDate = insertionDate;
	}

	public FileInfo(){		
	}
	
	public boolean shouldBePacked() {
		char pflag = new File(fileURL).getName().charAt(0);
		if ('A' == pflag) return true;
		return false;
	}
	
	public DataType getDataType() {
		String fileExtension = fileURL.substring(fileURL.indexOf('.') + 1);
		if (("tiff".equals(fileExtension))  || ("ps".equals(fileExtension))) return DataType.BINARY;
		return DataType.ALPHANUMERIC;
	}					
	
	public String getFileURL() {
		return fileURL;
	}
	
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}
	
	public long getFilesize() {
		File file = new File(fileURL);
		return file.length();		
	}		
	
	public Integer getPriority() {
		return priority;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public List<String> getMetadataURNList() {
		return metadataURNList;
	}
	
	public void setMetadataURNList(List<String> metadataURNList) {
		this.metadataURNList = metadataURNList;
	}
	
	public void addMetadataURN(String metadataURN){
		metadataURNList.add(metadataURN);
	}		
	
	public String getChecksum() {
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public boolean isReceivedFromGTS() {
		return receivedFromGTS;
	}

	public void setReceivedFromGTS(boolean receivedFromGTS) {
		this.receivedFromGTS = receivedFromGTS;
	}
	
	public GTScategory getGtsCategory() {
		return gtsCategory;
	}

	public void setGtsCategory(GTScategory gtsCategory) {
		this.gtsCategory = gtsCategory;
	}

	public List<Long> getMetadataIDList() {
		return metadataIDList;
	}

	public void addMetadataId(Long metadataId) {
		this.metadataIDList.add(metadataId);
	}

	public int getNumberOfChecksumBytes() {
		return numberOfChecksumBytes;
	}

	public void setNumberOfChecksumBytes(int numberOfChecksumBytes) {
		this.numberOfChecksumBytes = numberOfChecksumBytes;
	}

	public String getProductFilename() {
		return productFilename;
	}

	public void setProductFilename(String productFilename) {
		this.productFilename = productFilename;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setMetadataIDList(List<Long> metadataIDList) {
		this.metadataIDList = metadataIDList;
	}
}