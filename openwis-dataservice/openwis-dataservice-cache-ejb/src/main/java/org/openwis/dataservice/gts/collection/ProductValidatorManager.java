package org.openwis.dataservice.gts.collection;

import java.util.HashMap;
import java.util.Map;

import org.openwis.dataservice.util.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages all the ProductValidator plugin implementations.
 * A ProductValidator can be registered or unregistered using the static methods 'registerProductValidator(ProductValidator)' and 'unRegisterProductValidator(ProductValidator)'.
 * To check a file with all registered ProductValidators, just execute the method 'validateAll(FileInfo) : Map<String,Boolean>', which returns a map containing all pairs of
 * registered and executed ProductValidator plugins and their corresponding validation-return-value. 
 * @author kulbatzki
 *
 */
public class ProductValidatorManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductValidatorManager.class);

	/**
	 * A map of all registered ProductValidator plugins. The key to this map is the name of the implementing ProductValidator class.
	 */
	private static Map<String,ProductValidator> validators;
	
	static {
	   validators = new HashMap<String,ProductValidator>();
	   // Initialize validators
	   // Checksum Validator is not used as the Checksum is already re-computed for each collected file
	   // This is only there to illustrate how to register further validators
      // registerProductValidator(new ChecksumValidator());
	}
	
	/**
	 * Registers a plugin to this manager to be called if a file has to be validated.
	 * @param validator the plugin to register
	 */
	public static void registerProductValidator(ProductValidator validator){
		if (validator != null && validators != null && !validators.containsKey(validator.getClass().getSimpleName())){
			String className = validator.getClass().getSimpleName();
			validators.put(className, validator);
			
			LOG.info("Registering " + validator.getClass().getSimpleName());
		}
	}
	
	/**
	 * Unregisters a plugin from this manager. This plugin will no longer be taken into account when validating a file.
	 * @param validator the plugin to unregister
	 */
	public static void unregisterProductValidator(ProductValidator validator){
		if (validator != null && validators != null && validators.containsKey(validator.getClass().getSimpleName())){
			validators.remove(validator);
			
			LOG.info("Unregistering " + validator.getClass().getSimpleName());
		}
	}
	
	/**
	 * This method has to be invoked if a file has to be approven by all registered ProductValidator plugins.
	 * @param fileInfo the represented file to be tested
	 * @return a map of String-Boolean-pairs. The String is the name of a registered plugin and the Boolean has the boolen value
	 * {@code true} if the corresponding registered plugin approves of the file and {@code false} if it does NOT
	 */
	public static Map<String,Boolean> validateAll(FileInfo fileInfo){
		Map<String,Boolean> validationMap = new HashMap<String,Boolean>();
		
		for (String validatorName : validators.keySet()){
			ProductValidator validator = validators.get(validatorName);
			if (validator.validate(fileInfo)) {
				validationMap.put(validatorName, new Boolean(true));
				LOG.info(validatorName + " approved for " + fileInfo.getFileURL());
			} else {
				validationMap.put(validatorName, new Boolean(false));
				LOG.warn(validatorName + " did NOT approve of " + fileInfo.getFileURL());
			}
		}
		
		return validationMap;
	}
}