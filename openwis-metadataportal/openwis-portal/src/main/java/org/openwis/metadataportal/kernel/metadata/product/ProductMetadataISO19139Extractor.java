/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.product;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.dataservice.RecurrentScale;
import org.openwis.dataservice.RecurrentUpdateFrequency;
import org.openwis.dataservice.UpdateFrequency;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.model.metadata.Metadata;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class ProductMetadataISO19139Extractor implements IProductMetadataExtractor {

   /**
    * Comment for <code>namespace_GMD</code>
    */
   private static final Namespace NAMESPACE_GMD = Namespace.getNamespace("gmd",
         "http://www.isotc211.org/2005/gmd");

   /**
    * Comment for <code>namespace_GCO</code>
    */
   private static final Namespace NAMESPACE_GCO = Namespace.getNamespace("gco",
         "http://www.isotc211.org/2005/gco");

   /**
    * Comment for <code>namespaceGMX</code>
    */
   private static final Namespace NAMESPACE_GMX = Namespace.getNamespace("gmx",
         "http://www.isotc211.org/2005/gmx");

   /**
    * Comment for <code>nsListGMDGCO</code>
    */
   private static List<Namespace> nsListGMDGCO = new ArrayList<Namespace>();

   /**
    * Comment for <code>nsListGMDGMX</code>
    */
   private static List<Namespace> nsListGMDGMX = new ArrayList<Namespace>();

   private static final List<String> ACCEPTED_FILE_EXTENSIONS = OpenwisMetadataPortalConfig
         .getList(ConfigurationConstants.ACCEPTED_FILE_EXTENSIONS);

   private static final List<String> TEXT_EXTENSION = Arrays.asList("1", "B", "C", "D", "F", "G",
         "K", "N", "S", "U", "V", "W", "X");

   private static final List<String> GRIB_EXTENSION = Arrays.asList("H", "O", "Y");

   private static final List<String> BUFR_EXTENSION = Arrays.asList("I", "J");

   private static final List<String> IMG_EXTENSION = Arrays.asList("P", "Q");

   private static final List<String> SATIMG_EXTENSION = Arrays.asList("E");

   private static final List<String> SATDATA_EXTENSION = Arrays.asList("T");

   /**
    * Default constructor.
    * Builds a ProductMetadataISO19139ExtractManager.
    */
   public ProductMetadataISO19139Extractor() {
      super();
      nsListGMDGCO = new ArrayList<Namespace>();
      nsListGMDGCO.add(NAMESPACE_GMD);
      nsListGMDGCO.add(NAMESPACE_GCO);

      nsListGMDGMX = new ArrayList<Namespace>();
      nsListGMDGMX.add(NAMESPACE_GMD);
      nsListGMDGMX.add(NAMESPACE_GMX);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractFncPattern(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractFncPattern(Metadata metadata) throws JDOMException {
      String fncPattern;
      final String xpath = "gmd:describes/gmx:MX_DataSet/gmx:dataFile/gmx:MX_DataFile/gmx:fileName/gmx:FileName";
      fncPattern = Xml.selectString(metadata.getData(), xpath, nsListGMDGMX);
      fncPattern = StringUtils.abbreviate(fncPattern, MAX_LENGTH_FNC_PATTERN);
      Log.info(Geonet.EXTRACT_PRODUCT_METADATA, "Extracted FNC Pattern: " + fncPattern);
      return StringUtils.isBlank(fncPattern) ? null : fncPattern;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractOriginator(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractOriginator(Metadata metadata) throws JDOMException {
      final String xpath = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString";

      String originator = Xml.selectString(metadata.getData(), xpath, nsListGMDGCO);
      originator = StringUtils.abbreviate(originator, MAX_LENGTH_ORIGINATOR);
      Log.info(Geonet.EXTRACT_PRODUCT_METADATA,
            MessageFormat.format("Extracted Originator: {0}", originator));

      return originator;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractTitle(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractTitle(Metadata metadata) throws JDOMException {
      final String xpath = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/"
            + "gmd:CI_Citation/gmd:title/gco:CharacterString";
      String title = Xml.selectString(metadata.getData(), xpath, nsListGMDGCO);
      title = StringUtils.abbreviate(title, MAX_LENGTH_TITLE);
      Log.info(Geonet.EXTRACT_PRODUCT_METADATA, MessageFormat.format("Extracted Title: {0}", title));
      return title;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractLocalDataSource(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractLocalDataSource(Metadata metadata) throws JDOMException {
      final String xpath = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:environmentDescription/gco:CharacterString";
      String localDS = Xml.selectString(metadata.getData(), xpath, nsListGMDGCO);
      localDS = StringUtils.abbreviate(localDS, MAX_LENGTH_LOCAL_DATASOURCE);
      Log.info(Geonet.EXTRACT_PRODUCT_METADATA,
            MessageFormat.format("Extracted Local Data Source: {0}", localDS));
      return localDS;
   }

   /**
    * !! In the current implementation update frequency is ignored !!
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractUpdateFrequency(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public UpdateFrequency extractUpdateFrequency(Metadata metadata) throws JDOMException {
      String updateFrequency;
      final String xpath = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceMaintenance/"
            + "gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/"
            + "gmd:MD_MaintenanceFrequencyCode/@codeListValue";

      // FIXME create an update frequency object ...
      updateFrequency = Xml.selectString(metadata.getData(), xpath, nsListGMDGCO);
      Log.debug(Geonet.EXTRACT_PRODUCT_METADATA, "Extracted update frequency (ignored): "
            + updateFrequency);

      // FIXME the recurrentUpdateFrequency should have a period also ... 
      RecurrentUpdateFrequency recurrentUpdateFrequency = new RecurrentUpdateFrequency();
      recurrentUpdateFrequency.setRecurrentScale(RecurrentScale.HOUR);
      recurrentUpdateFrequency.setRecurrentPeriod(1);

      return recurrentUpdateFrequency;
   }

   @Override
   public boolean isGlobalExchange(Metadata metadata) throws Exception {
      final String xpath = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword[gco:CharacterString/text() = 'GlobalExchange']";
      Element keywordElt = Xml.selectElement(metadata.getData(), xpath, nsListGMDGCO);
      if (keywordElt != null) {
         Log.info(Geonet.EXTRACT_PRODUCT_METADATA, "Found GlobalExchange Keyword");
         return true;
      }
      return false;
   }

   @Override
   public boolean isIsoCoreProfile1_3(Metadata metadata) throws Exception {
      String xpathStandardName = "gmd:metadataStandardName/gco:CharacterString";
      String standardName = Xml.selectString(metadata.getData(), xpathStandardName, nsListGMDGCO);
      if (standardName != null && standardName.startsWith("WMO Core Metadata Profile")) {
         String xpathStandardVersion = "gmd:metadataStandardVersion/gco:CharacterString";
         String standardVersion = Xml.selectString(metadata.getData(), xpathStandardVersion, nsListGMDGCO);
         //metadata is wmo core profile 1.3 or higher
         int resultCmp = standardVersion.compareTo("1.3");
         if (resultCmp>0 || resultCmp == 0){
            Log.info(Geonet.EXTRACT_PRODUCT_METADATA, "Found wmo core profile 1.3 or higher");
            return true;
         }
      }
      return false;
   }
   /**
    * !! FNC Pattern and URN must be set before processing GTS Category.
    * This allows to ignore patterns in some cases.
    * @see #checkFNCPattern(ProductMetadata, boolean) 
    * !!
    * 
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractGTSCategoryGTSPriorityAndDataPolicy(org.openwis.metadataportal.model.metadata.Metadata, org.openwis.dataservice.ProductMetadata)
    */
   @Override
   @SuppressWarnings("unchecked")
   public void extractGTSCategoryGTSPriorityAndDataPolicy(Metadata metadata, ProductMetadata pm)
         throws Exception {
      List<String> xpathList = OpenwisMetadataPortalConfig
            .getList(ConfigurationConstants.EXTRACT_XPATH);
      List<Element> useLimitationElts = new ArrayList<Element>();
      for (String xpath : xpathList) {
         useLimitationElts.addAll((List<Element>) Xml.selectNodes(metadata.getData(), xpath,
               nsListGMDGCO));
      }

      if (useLimitationElts.isEmpty()) {
         assignUnkownDataPolicy(pm);
         return;
      }

      String otherDP = null; // track other dp (than essential and additional)
      boolean isGlobal = false;
      for (Element useLimitationEl : useLimitationElts) {
         String useLimitationStr = useLimitationEl.getText();

         // Try to get GTS category
         if (Pattern.matches(GTS_CATEGORY_ESSENTIAL, useLimitationStr)
               || Pattern.matches(GTS_CATEGORY_ADDITIONAL, useLimitationStr)) {
            pm.setGtsCategory(useLimitationStr);
            // Extract the datapolicy
            pm.setDataPolicy(extractDatapolicy(useLimitationStr, metadata.getData()));

            Log.info(Geonet.EXTRACT_PRODUCT_METADATA, MessageFormat.format(
                  "Extracted GTS Category: {0} - Data Policy: {1}", useLimitationStr,
                  pm.getDataPolicy()));
            isGlobal = true;
         } else if (Pattern.matches(GTS_PRIORITY, useLimitationStr)) {
            pm.setPriority(extractGtsPriority(useLimitationStr));
         } else {
            // will keep the last other dp...
            otherDP = useLimitationStr;
         }
      }

      if (pm.getGtsCategory() != null
            && Pattern.matches(GTS_CATEGORY_ADDITIONAL, pm.getGtsCategory()) && otherDP != null) {
         // Try to apply another DP for additional product
         Log.info(Geonet.EXTRACT_PRODUCT_METADATA,
               MessageFormat.format("Possible Data Policy for Additional product: {0}", otherDP));
         pm.setDataPolicy(otherDP);
      } else if (StringUtils.isBlank(pm.getGtsCategory())) {
         // Custom GTS category / data policy name
         if (otherDP == null) {
            // no other dp specified (only GTS priority was specified)
            assignUnkownDataPolicy(pm);
         } else {
            otherDP = StringUtils.abbreviate(otherDP, MAX_LENGTH_GTS_CATEGORY);
            Log.info(Geonet.EXTRACT_PRODUCT_METADATA, MessageFormat.format(
                  "Possible value for GTS Category and Data Policy: {0}", otherDP));
            pm.setDataPolicy(otherDP);
            pm.setGtsCategory(otherDP);
         }
      }

      checkFNCPattern(pm, metadata, isGlobal);
   }

   /**
    * Check if FNC Pattern should be ignored, which occurs in the following cases:
    * - the md is not Global (including globalExchange)
    * - the md URN matches a given regexp
    * @throws Exception 
    */
   private void checkFNCPattern(ProductMetadata pm, Metadata m, boolean isGlobal) throws Exception {
      if (pm.getFncPattern() == null) {
         return;
      }
      if (!isGlobal && !isGlobalExchange(m)) {
         Log.info(Geonet.EXTRACT_PRODUCT_METADATA, "FNC Pattern ignored for non Global product");
         pm.setFncPattern(null);
      } else {
         // Check URN matches exclude pattern
         if (Pattern.matches(URN_PATTERN_FOR_IGNORED_FNC_PATTERN, pm.getUrn())) {
            Log.info(Geonet.EXTRACT_PRODUCT_METADATA,
                  "FNC Pattern ignored because of URN exclude pattern");
            pm.setFncPattern(null);
         }
      }
   }

   /**
    * Assign Unknown data policy and category (in case no proper information was found)
    * 
    * @param pm the {@link ProductMetadata}
    */
   private void assignUnkownDataPolicy(ProductMetadata pm) {
      Log.warning(Geonet.EXTRACT_PRODUCT_METADATA,
            "Unable to extract the GTS category (and Data Policy)");
      Log.info(Geonet.EXTRACT_PRODUCT_METADATA, MessageFormat
            .format("Assigning GTS Category: {0} - Data Policy: {1}", GTS_CATEGORY_NONE,
                  UNKNOWN_DATAPOLICY));
      pm.setGtsCategory(GTS_CATEGORY_NONE);
      pm.setDataPolicy(UNKNOWN_DATAPOLICY);
   }

   /**
    * Extract the data policy.
    * 
    * @param useLimitationStr
    * @param data
    * @throws JDOMException 
    */
   private String extractDatapolicy(String useLimitationStr, Element data) throws JDOMException {
      if (Pattern.matches(GTS_CATEGORY_ESSENTIAL, useLimitationStr)) {
         return PUBLIC_DATAPOLICY;
      }

      if (Pattern.matches(GTS_CATEGORY_ADDITIONAL, useLimitationStr)) {
         return DEFAULT_ADDITIONAL_DATAPOLICY;
      }
      return null;
   }

   /**
    * Extract the priority from the GTS priority string.
    * 
    * @param useLimitation
    * @return the priority or null
    */
   private Integer extractGtsPriority(String useLimitation) {
      Integer priority = null;
      Matcher matcher = Pattern.compile(GTS_PRIORITY).matcher(useLimitation);
      if (matcher.find()) {
         String priorityStr = matcher.group(1);
         if (StringUtils.isNumericSpace(priorityStr)) {
            priority = Integer.parseInt(priorityStr.trim());
         }
         Log.info(Geonet.EXTRACT_PRODUCT_METADATA,
               MessageFormat.format("Extracted GTS Priority: {0}", priority));
      }

      return priority;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractFileExtension(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractFileExtension(Metadata metadata) throws JDOMException {
      String result = null;

      // Try to guess file extension from T1 of TTAAiiCCCC
      String urn = metadata.getUrn();
      String ttaaii = urn.substring(urn.lastIndexOf("::") + 2);
      String t1 = ttaaii.substring(0, 1).toUpperCase();

      if (TEXT_EXTENSION.contains(t1)) {
         //text
         result = "txt";
      } else if (GRIB_EXTENSION.contains(t1)) {
         //grib
         result = "grib";
      } else if (BUFR_EXTENSION.contains(t1)) {
         //bufr
         result = "bufr";
      } else if (IMG_EXTENSION.contains(t1)) {
         //img
         result = null;
      } else if (SATIMG_EXTENSION.contains(t1)) {
         //sat Img
         result = null;
      } else if (SATDATA_EXTENSION.contains(t1)) {
         //Sat Data
         result = null;
      }

      // If no result, try to extract info from metadata
      if (result == null) {
         String xpath = "gmd:distributionInfo/gmd:MD_Distribution/gmd:distributionFormat/gmd:MD_Format/gmd:name/gco:CharacterString";
         String distributionFormat = Xml.selectString(metadata.getData(), xpath, nsListGMDGCO);
         if (StringUtils.isNotBlank(distributionFormat)) {
            result = distributionFormat;
         }
      }

      // In any case, filter file extensions to keep only valid FNC extension
      if (result != null && !ACCEPTED_FILE_EXTENSIONS.contains(result)) {
         Log.info(Geonet.EXTRACT_PRODUCT_METADATA, "Ignored file extension: " + result);
         result = null;
      }

      return result;
   }
   
}
