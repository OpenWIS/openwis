/**
 *
 */
package org.openwis.harness.samples.common.extraction;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.samples.common.Product;
import org.openwis.harness.samples.common.parameters.ParameterUtils;
import org.openwis.harness.samples.common.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExtractionRunnable. <P>
 * Background extraction for file. <P>
 * Check timestamp criteria.
 */
public abstract class ExtractionRunnable implements Runnable {

   /** The Constant INTERVAL_SEPARATOR. */
   private static final String INTERVAL_SEPARATOR = "/";

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ExtractionRunnable.class);

   /** The staging post URI. */
   private final String stagingPostURI;

   /** The request id. */
   private final long requestId;

   /** The parameters. */
   private final List<Parameter> parameters;

   /** The metadata urn. */
   private final String metadataURN;

   /** The local datasource file utils. */
   private final LocalDatasourceUtils localDatasourceFileUtils;

   /** The date values. */
   private Calendar from;

   /** The time values. */
   private Calendar to;

   /**
    * Default constructor.
    * Builds a ExtractionRunnable.
    *
    * @param localDatasourceFileUtils the local datasource file utils
    * @param metadataURN the metadata urn
    * @param parameters the parameters
    * @param requestId the request id
    * @param stagingPostURI the staging post URI
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public ExtractionRunnable(LocalDatasourceUtils localDatasourceFileUtils, String metadataURN,
         List<Parameter> parameters, long requestId, String stagingPostURI) throws IOException {
      super();
      this.localDatasourceFileUtils = localDatasourceFileUtils;
      this.metadataURN = metadataURN;
      this.parameters = parameters;
      this.requestId = requestId;
      this.stagingPostURI = stagingPostURI;

      try {
         // handle parameters
         parseParameters();

         // Create temp file
         File tempFile = localDatasourceFileUtils.getTempFile(requestId);
         FileUtils.touch(tempFile);
      } catch (ParseException e) {
         logger.error(
               MessageFormat.format("Extraction {0} - ParseException: {1}", requestId,
                     e.getMessage()), e);
         localDatasourceFileUtils.createErrorFile(requestId, e.getMessage(), e);
      }
   }

   /**
    * To string.
    *
    * @return the string
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[Extraction {0}] on {1} to {2}", requestId, metadataURN,
            stagingPostURI);
   }

   /**
    * Run.
    *
    * {@inheritDoc}
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      long start = 0;
      try {
         logger.info("Extraction {} - Started", requestId);

         start = System.currentTimeMillis();
         File tempFile = localDatasourceFileUtils.getTempFile(requestId);
         // Search file
         List<Product> matchingProducts = searchMatchingProducts();
         localDatasourceFileUtils.setNumberProductFound(requestId, matchingProducts.size());
         localDatasourceFileUtils.appendToFile(tempFile,
               MessageFormat.format("Found {0} matching product(s)\n", matchingProducts.size()));
         logger.info("Extraction {} - Found {} matching product(s)", requestId,
               matchingProducts.size());

         // Copy files to staging post
         File stagingPostFile = localDatasourceFileUtils.getStagingPostFile(stagingPostURI);
         for (Product product : matchingProducts) {
            if (matchingProducts.size() < 10) {
               localDatasourceFileUtils.appendToFile(tempFile,
                     MessageFormat.format("\t{0}\n", product));
            }
            logger.trace("Extraction {} - Write {} to {}", new Object[] {requestId, product,
                  stagingPostFile});
            writeProductToStagingPost(product, stagingPostFile);
         }

         // update temp file OK
         localDatasourceFileUtils.createOkFile(requestId);
         logger.info("Extraction {} - Done, see {}", requestId, stagingPostFile);
      } catch (IOException ioe) {
         logger.error(
               MessageFormat.format("Extraction {0} - IOException: {1} ", requestId,
                     ioe.getMessage()), ioe);
         localDatasourceFileUtils.createErrorFile(requestId, ioe.getMessage(), ioe);
      } catch (ExtractionException e) {
         logger.error(
               MessageFormat.format("Extraction {0} - ExtractionException: {1}", requestId,
                     e.getMessage()), e);
         localDatasourceFileUtils.createErrorFile(requestId, e.getMessage(), e);
      } finally {
         if (logger.isDebugEnabled()) {
            long time = System.currentTimeMillis() - start;
            DateFormat timeFormatter = new SimpleDateFormat("mm:ss.SSS");
            logger.debug("Extraction {} - Tooks {}", requestId,
                  timeFormatter.format(new Date(time)));
         }
      }
   }

   /**
    * Search matching products.
    *
    * @return the product list
    * @throws ExtractionException the extraction exception
    */
   protected abstract List<Product> searchMatchingProducts() throws ExtractionException;

   /**
    * Write product to file.
    *
    * @param product the product
    * @param stagingPostFile the staging post file
    * @throws IOException Signals that an I/O exception has occurred.
    */
   protected abstract void writeProductToStagingPost(Product product, File stagingPostFile)
         throws IOException;

   /**
    * Parses the parameters.
    *
    * @throws ParseException the parse exception
    */
   protected synchronized void parseParameters() throws ParseException {
      from = Calendar.getInstance();
      from.clear();
      to = Calendar.getInstance();
      to.clear();

      List<String> values;
      Calendar date;
      boolean hasTime = false;
      boolean hasDate = false;
      String[] dates = null;
      String[] times = null;
      for (Parameter parameter : parameters) {
         if (ParameterUtils.DATE_PARAMETER.equals(parameter.getCode())) {
            values = parameter.getValues();
            logger.debug("Extraction {} - Date parameter {}", requestId, values);
            if (values != null && values.size() > 0) {
               dates = values.get(0).split(INTERVAL_SEPARATOR);
               hasDate = true;
            }
         } else if (ParameterUtils.TIME_PARAMETER.equals(parameter.getCode())) {
            values = parameter.getValues();
            logger.debug("Extraction {} - Time parameter {}", requestId, values);
            // handle time
            if (values != null && values.size() > 0) {
               hasTime = true;
               times = values.get(0).split(INTERVAL_SEPARATOR);
            }
         } else {
            handleParameter(parameter);
         }
      }

      // handle date
      if (hasDate) {
         // From
         date = DateTimeUtils.parseDate(dates[0]);
         from.set(Calendar.YEAR, date.get(Calendar.YEAR));
         from.set(Calendar.MONTH, date.get(Calendar.MONTH));
         from.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
         // To
         date = DateTimeUtils.parseDate(dates[1]);
         to.set(Calendar.YEAR, date.get(Calendar.YEAR));
         to.set(Calendar.MONTH, date.get(Calendar.MONTH));
         to.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
      } else {
         // No date -> current date (subscription)
         from = Calendar.getInstance();
         from.clear(Calendar.MINUTE);
         from.clear(Calendar.SECOND);
         to = Calendar.getInstance();
         to.clear(Calendar.MINUTE);
         to.clear(Calendar.SECOND);
      }
      
      // Handle time
      if (hasTime) {
         int min;
         // From
         min = DateTimeUtils.getMinutes(times[0]);
         from.add(Calendar.MINUTE, min);
         
         // To
         min = DateTimeUtils.getMinutes(times[1]);
         to.add(Calendar.MINUTE, min);
      }

      // Fix to granularity
      // max granularity is minute
      to.add(Calendar.MINUTE, 1);
      to.add(Calendar.MILLISECOND, -1);
      logger.info("date between {} and {}", from.getTime(), to.getTime());
   }

   /**
    * Handle parameter.
    *
    * @param parameter the parameter
    */
   protected void handleParameter(Parameter parameter) {
      logger.debug("Extraction {} - Unhandled paramerter: {}", requestId, parameter.getCode());
   }

   /**
    * get From timestamp.
    *
    * @return the from
    * @throws ParseException the parse exception
    */
   protected Calendar getFrom() throws ParseException {
      return from;
   }

   /**
    * get to timestamp.
    *
    * @return the to
    * @throws ParseException the parse exception
    */
   protected Calendar getTo() throws ParseException {
      return to;
   }

   /**
    * Gets the metadata urn.
    *
    * @return the metadata urn
    */
   public String getMetadataURN() {
      return metadataURN;
   }

   /**
    * Gets the parameters.
    *
    * @return the parameters
    */
   public List<Parameter> getParameters() {
      return parameters;
   }

   /**
    * Gets the staging post uri.
    *
    * @return the staging post uri
    */
   public String getStagingPostURI() {
      return stagingPostURI;
   }

   /**
    * Gets the local datasource file utils.
    *
    * @return the local datasource file utils
    */
   public LocalDatasourceUtils getLocalDatasourceFileUtils() {
      return localDatasourceFileUtils;
   }

}
