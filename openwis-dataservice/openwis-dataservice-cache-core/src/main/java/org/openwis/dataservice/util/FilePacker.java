package org.openwis.dataservice.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tools.ant.util.FileUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.JndiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TransactionTimeout(18000)
public class FilePacker implements ConfigurationInfo {
	
	private final Logger LOG = LoggerFactory.getLogger(FilePacker.class);
	
	private static final byte[] FORMAT_IDENTIFIER_Array = {'0','0'}; // length = 2
	private static final byte[] SOH_CR_CR_LF_Array = {0x01,0x0D,0x0D,0x0A}; // length = 4	
	private static final byte[] CR_CR_LF_Array = {0x0D,0x0D,0x0A}; // length = 3
	private static final byte[] CR_CR_LF_ETX_Array = {0x0D,0x0D,0x0A,0x03}; // length = 4
	private static final String PACKED_FILE_EXTENSION = ".b";
	private static final String TEMP_FILE_EXTENSION = ".tmp";
	private static final int LENGTH_OF_MESSAGE_NUMBER_STRING = 3; // length of nnn
	
	public final static String FEEDING_FILE_PACKER_INSTANCE = "Feeding";
	public final static String DISSEMINATION_FILE_PACKER_INSTANCE = "Dissemination";
	
	private static FilePacker feedingFilePacker = null;
	//private static FilePacker disseminationFilePacker = null;
	
	private String instanceName = null;
	private String targetDirectory = null;	
	private File packedFile = null;
	
	
	// ------------------------------
	
	public static FilePacker getFeedingFilePacker(String targetDirectory){
		if (feedingFilePacker == null){
			feedingFilePacker = new FilePacker(FEEDING_FILE_PACKER_INSTANCE,targetDirectory);			
		}
		feedingFilePacker.getDatabaseAccessor().initialize(FEEDING_FILE_PACKER_INSTANCE);
		if (feedingFilePacker.getPackedFile() == null) feedingFilePacker.createNewPackage();
		feedingFilePacker.setTargetDirectory(targetDirectory);
		return feedingFilePacker;
	}
	
	public static FilePacker getDisseminationFilePacker(String targetDirectory){
		//if (disseminationFilePacker == null){
	   FilePacker disseminationFilePacker = new FilePacker(DISSEMINATION_FILE_PACKER_INSTANCE,targetDirectory);			
		//}
		disseminationFilePacker.getDatabaseAccessor().initialize(DISSEMINATION_FILE_PACKER_INSTANCE);
		if (disseminationFilePacker.getPackedFile() == null) disseminationFilePacker.createNewPackage();
		disseminationFilePacker.setTargetDirectory(targetDirectory);
		return disseminationFilePacker;
	}		
	
	/**
	 * Constructs and initializes the FilePacker.
	 * @param instanceName The name of the FilePacker instance. For every instance there is a separate set of parameters (e.g. the package-number).
	 * @param targetDirectory The path to the directory in which the packed files will be created.
	 */
	private FilePacker(String instanceName, String targetDirectory){
		this.instanceName = instanceName;
		this.targetDirectory = targetDirectory;		
	}
		
	/**
	 * Finishes the current package.
	 */
	public synchronized void flush(){
		if (getPackedFile() != null){
			File finalizedFile = new File(getTargetDirectory(),getPackedFile().getName().replace(TEMP_FILE_EXTENSION, ""));
			try{
				FileUtils.getFileUtils().rename(getPackedFile(), finalizedFile);
			}
			catch(IOException e){
				LOG.error("--- Could not rename file " + getPackedFile().getName() + " to " + finalizedFile.getName());
			}
			setPackedFile(null);
			resetNumberOfIncludedBulletins();
		}		
	}
	
	/**
	 *  Starts a new package.
	 */
	public void createNewPackage(){
		String sendingCentreLocationIdentifier = getSendingCentreLocationIdentifier();
		int packageNumber = getPackageNumber();
		String filename = getNewPackedFilename(sendingCentreLocationIdentifier, packageNumber);
		filename = filename.concat(TEMP_FILE_EXTENSION);
		File packedFile = new File(getTargetDirectory(),filename); 
		setPackedFile(packedFile);
		if (!getPackedFile().exists()){
			try {
				getPackedFile().createNewFile();
			}
			catch (IOException e) {
				LOG.error("--- Could not create file " + getTargetDirectory() + "/" + filename + "; " + e.getMessage());
				return;
			}
		}
		increasePackageNumber();
		getDatabaseAccessor().setPackageName(getInstanceName(), getPackedFile().getName());
	}
	
	/**
	 * Appends a new file to the current package.
	 * @param bulletin The file to be added to the package.
	 */
	public synchronized void appendBulletinToPackedFile(File bulletin, String originalFilename){
		try{
			append(bulletin, originalFilename);
		}
		catch(TooManyBulletinsException e){
			flush();
			createNewPackage();
			try {
				append(bulletin, originalFilename);
			}
			catch (TooManyBulletinsException e1) {
				// cannot happen since we start a new package with 0 bulletins and append only 1
			}
		}
	}

	private void append(File bulletin, String originalFilename) throws TooManyBulletinsException{
		if ('A' != originalFilename.charAt(0)) {
			LOG.error("--- File " + originalFilename + " not valid for WMO FTP packing!");
			return;
		}
		
		// file setup
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(bulletin);
			fos = new FileOutputStream(getPackedFile(),true); // append
		}
		catch (FileNotFoundException e) {
			LOG.error("--- Could not find file. " + e.getMessage());
			return;
		}
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		// get AHL byte array
		WMOFNC wmofnc = null;
		try {
			wmofnc = FileNameParser.parseFileName(originalFilename);
		}
		catch (ParseException e) {
			LOG.error("--- Could not parse filename. " + e.getMessage());
			return;
		}
		char[] ahlCharArray = wmofnc.getAHL().toCharArray();
		byte[] ahlByteArray = new byte[ahlCharArray.length];
		for (int i = 0; i < ahlByteArray.length; i++) {
			ahlByteArray[i] = (byte) ahlCharArray[i];
		}
		
		try{
			// get length of input file as byte array
			byte[] lengthArray = new byte[8];
			for (int i = 0; i < lengthArray.length; i++) {
				lengthArray[i] = '0';
			}
			int dataLength = bis.available();
			int messageLength = dataLength + SOH_CR_CR_LF_Array.length + LENGTH_OF_MESSAGE_NUMBER_STRING + CR_CR_LF_Array.length + ahlByteArray.length + CR_CR_LF_Array.length + CR_CR_LF_ETX_Array.length;
			char[] lengthCharArray = Integer.valueOf(messageLength).toString().toCharArray();
			for (int i = 0; i < lengthCharArray.length; i++) {
				lengthArray[i + 8 - lengthCharArray.length] = (byte) lengthCharArray[i];
			}
			
			// read data from input file			
			byte[] dataArray = new byte[dataLength];
			bis.read(dataArray);
			bis.close();							
			
			// only a predefined (see JNDI properties) number of messages must go into one package
			if (getNumberOfIncludedBulletins() >= getMaximumNumberOfIncludedBulletins()){
				bos.close();
				fos.close();
				throw new TooManyBulletinsException();
			}
			// get the messageNumber as byte array			
			byte[] transmissionSequenceNumberByteArray = getTransmissionSequenceNumberAsByteArray();			
			increaseTransmissionSequenceNumber();
			increaseNumberOfIncludedBulletins();
			
			// write packed file
			bos.write(lengthArray);
			bos.write(FORMAT_IDENTIFIER_Array);
			bos.write(SOH_CR_CR_LF_Array); // 4
			bos.write(transmissionSequenceNumberByteArray); // 5
			bos.write(CR_CR_LF_Array); // 3
			bos.write(ahlByteArray); // variable length
			bos.write(CR_CR_LF_Array); // 3
			bos.write(dataArray); // variable length
			bos.write(CR_CR_LF_ETX_Array); // 4
			bos.flush();
			bos.close();
			
			fis.close();
			fos.close();
			bis.close();
		}
		catch (IOException e){
			LOG.error("--- Error while accessing file. " + e.getMessage());
			return;
		}		
	}
	
	public String getTargetDirectory(){
		return targetDirectory;
	}
	
	public synchronized void setTargetDirectory(String directory){
		targetDirectory = directory;
	}
	
	public String getInstanceName(){
		return instanceName;
	}
	
	public File getPackedFile(){
		String packageName = getDatabaseAccessor().getPackageName(getInstanceName());
		if (packageName != null){
			if (packedFile == null || !packedFile.getName().equals(packageName)) {
				packedFile = new File(getTargetDirectory(),packageName);
			}
		}
		return packedFile;
	}
	
	private void setPackedFile(File file){
		String newPackageName = null;
		if (file != null){
			newPackageName = file.getName();
		}
		getDatabaseAccessor().setPackageName(getInstanceName(), newPackageName);
		packedFile = file;
	}
	
	public int getNumberOfIncludedBulletins(){
		return getDatabaseAccessor().getNumberOfIncludedBulletins(getInstanceName());
	}
	
	private void increaseNumberOfIncludedBulletins(){
		getDatabaseAccessor().increaseeNumberOfIncludedBulletins(getInstanceName());
	}
	
	private void resetNumberOfIncludedBulletins(){
		getDatabaseAccessor().resetNumberOfIncludedBulletins(getInstanceName());
	}
	
	public int getMaximumNumberOfIncludedBulletins(){
		return ConfigServiceFacade.getInstance().getInt(MAXIMUM_MESSAGE_COUNT_KEY);
	}
	
	private int getTransmissionSequenceNumber(){
		return getDatabaseAccessor().getTransmissionSequenceNumber(getInstanceName());
	}
	
	private byte[] getTransmissionSequenceNumberAsByteArray(){
		String transmissionSequenceNumberString = String.valueOf(getTransmissionSequenceNumber());
		while (transmissionSequenceNumberString.length() < LENGTH_OF_MESSAGE_NUMBER_STRING){
			transmissionSequenceNumberString = "0".concat(transmissionSequenceNumberString);
		}
		char[] transmissionSequenceNumberCharArray = transmissionSequenceNumberString.toCharArray();
		byte[] transmissionSequenceNumberByteArray = new byte[LENGTH_OF_MESSAGE_NUMBER_STRING];
		for (int i = 0; i < transmissionSequenceNumberCharArray.length; i++) {
			char c = transmissionSequenceNumberCharArray[i];	
			transmissionSequenceNumberByteArray[i] = (byte) c;
		}
		return transmissionSequenceNumberByteArray;
	}
	
	private void increaseTransmissionSequenceNumber(){
		getDatabaseAccessor().increaseTransmissionSequenceNumber(getInstanceName());
	}
	
	private int getPackageNumber(){
		return getDatabaseAccessor().getPackageNumber(getInstanceName());
	}
	
	private void increasePackageNumber(){
		getDatabaseAccessor().increasePackageNumber(getInstanceName());
	}
	
	private String getSendingCentreLocationIdentifier(){
		return ConfigServiceFacade.getInstance().getString(SENDING_CENTRE_LOCATION_IDENTIFIER);
	}

	private String getNewPackedFilename(String sendingCentre, int packageNumber){
		String filename = sendingCentre;
		String packageNumberString = String.valueOf(packageNumber);
		while (packageNumberString.length() < 8){
			packageNumberString = "0".concat(packageNumberString);
		}
		return filename.concat(packageNumberString).concat(PACKED_FILE_EXTENSION);
	}
	
	private FilePackerDatabaseAccessor getDatabaseAccessor(){
		FilePackerDatabaseAccessor databaseAccessor = null;
		try {
			InitialContext context = new InitialContext();
			databaseAccessor = (FilePackerDatabaseAccessor) context.lookup(ConfigServiceFacade.getInstance().getString(FILE_PACKER_DATABASE_ACCESSOR_URL_KEY));
		}
		catch (NamingException e) {
			LOG.error("--- Could not resolve name " + FILE_PACKER_DATABASE_ACCESSOR_URL_KEY);
		}
		return databaseAccessor;
	}
}