/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.product;

import org.openwis.dataservice.ProductMetadata;
import org.openwis.dataservice.UpdateFrequency;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.model.metadata.Metadata;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public interface IProductMetadataExtractor {

   /**
    * Description goes here.
    * @param metadata
    * @return
    */
   String extractFncPattern(Metadata metadata) throws Exception;

   /**
    * Description goes here.
    * @param metadata
    * @return
    */
   String extractOriginator(Metadata metadata) throws Exception;

   /**
    * Description goes here.
    * @param metadata
    * @return
    */
   String extractTitle(Metadata metadata) throws Exception;

   /**
    * Description goes here.
    * @param data
    * @return
    */
   String extractLocalDataSource(Metadata metadata) throws Exception;

   /**
    * Description goes here.
    * @param metadata
    * @return
    */
   UpdateFrequency extractUpdateFrequency(Metadata metadata) throws Exception;

   /**
    * Description goes here.
    * @param metadata
    * @return
    */
   String extractFileExtension(Metadata metadata) throws Exception;

   /**
    * Description goes here.
    * @param metadata
    * @param pm
    */
   void extractGTSCategoryGTSPriorityAndDataPolicy(Metadata metadata, ProductMetadata pm) throws Exception;

   boolean isGlobalExchange(Metadata metadata) throws Exception;
   
   /**
    * The GTS Category Additional.
    */
   static final String GTS_CATEGORY_ADDITIONAL = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.GTS_CATEGORY_ADDITIONAL_REGEXP);

   /**
    * The GTS Category Essential.
    */
   static final String GTS_CATEGORY_ESSENTIAL = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.GTS_CATEGORY_ESSENTIAL_REGEXP);
   
   /**
    * The GTS Category Unknown.
    */
   static final String GTS_CATEGORY_NONE = "Unknown";

   /**
    * The default additional data policy.
    */
   static final String DEFAULT_ADDITIONAL_DATAPOLICY = "additional-default";

   /**
    * The Public Data policy.
    */
   static final String PUBLIC_DATAPOLICY = "public";

   /**
    * The Unknown Data policy.
    */
   static final String UNKNOWN_DATAPOLICY = "unknown";
   
   /**
    * GlobalExchange flag
    */
   static final String GLOBAL_EXCHANGE = "GlobalExchange";
   
   /**
    * CoreProfile1.3NotGlobalExchange flag
    */
   static final String CORE_PROFILE_1_3_NOT_GLOBAL_EXCHANGE = "";

   
   /**
    * Default priority.
    */
   static final Integer DEFAULT_PRIORITY = 3;

   /**
    * Default originator.
    */
   static final String DEFAULT_ORIGINATOR = "RTH focal point";

   /**
    * The GTS priority.
    */
   static final String GTS_PRIORITY = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.GTS_PRIORITY_REGEXP);
   
   /**
    * The pattern applied on URN to determine if FNC Pattern should be ignored
    */
   static final String URN_PATTERN_FOR_IGNORED_FNC_PATTERN = OpenwisMetadataPortalConfig
         .getString(ConfigurationConstants.URN_PATTERN_FOR_IGNORED_FNC_PATTERN);
   
   /**
    * Max string length for Title
    */
   static final int MAX_LENGTH_TITLE = 255;
      
   /**
    * Max string length for GTS Category
    */
   static final int MAX_LENGTH_GTS_CATEGORY = 255;

   /**
    * Max string length for Data Policy
    */
   static final int MAX_LENGTH_FNC_PATTERN = 1024;

   /**
    * Max string length for Originator
    */
   static final int MAX_LENGTH_ORIGINATOR = 255;
   
   /**
    * Max string length for Originator
    */
   static final int MAX_LENGTH_LOCAL_DATASOURCE = 255;

   boolean isIsoCoreProfile1_3(Metadata metadata) throws Exception;
}
