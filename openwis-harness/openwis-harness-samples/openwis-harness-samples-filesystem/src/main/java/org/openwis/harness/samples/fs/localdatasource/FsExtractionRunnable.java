/**
 * 
 */
package org.openwis.harness.samples.fs.localdatasource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.samples.common.Product;
import org.openwis.harness.samples.common.extraction.ExtractionException;
import org.openwis.harness.samples.common.extraction.ExtractionRunnable;
import org.openwis.harness.samples.common.time.DateTimeUtils;
import org.openwis.harness.samples.fs.filter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExtractionRunnable. <P>
 * Background extraction for file. <P>
 * Check timestamp criteria.
 */
public class FsExtractionRunnable extends ExtractionRunnable {

   /** The Constant INVALID_FILE_NAME. */
   private static final String INVALID_FILE_NAME = "Invalid file name: ";

   /** The Constant TIME_PATTERN. */
   public static final Pattern TIME_PATTERN = Pattern.compile("(\\d\\d)_(\\d\\d)");

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(FsExtractionRunnable.class);

   /** The local datasource file utils. */
   private final FsLocalDatasourceFileUtils localDatasourceFileUtils;

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
   public FsExtractionRunnable(FsLocalDatasourceFileUtils localDatasourceFileUtils,
         String metadataURN, List<Parameter> parameters, long requestId, String stagingPostURI)
         throws IOException {
      super(localDatasourceFileUtils, metadataURN, parameters, requestId, stagingPostURI);
      this.localDatasourceFileUtils = localDatasourceFileUtils;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.harness.samples.common.extraction.ExtractionRunnable#searchMatchingProducts()
    */
   @Override
   protected List<Product> searchMatchingProducts() throws ExtractionException {
      List<Product> result = new ArrayList<Product>();
      try {
         File mdFolder = localDatasourceFileUtils.getMetadataFile(getMetadataURN());
         processYear(getFrom(), getTo(), result, mdFolder);
      } catch (ParseException e) {
         throw new ExtractionException(e);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.harness.samples.common.extraction.ExtractionRunnable#
    * writeProductToFile(org.openwis.harness.samples.common.Product, java.io.File)
    */
   @Override
   protected void writeProductToStagingPost(Product product, File stagingPostFile)
         throws IOException {
      if (product instanceof ProductFile) {
         ProductFile pf = (ProductFile) product;
         FileUtils.copyFileToDirectory(pf.getFile(), stagingPostFile);
      }
   }

   /**
    * Process year.
    *
    * @param from the from
    * @param to the to
    * @param result the result
    * @param mdFolder the md folder
    */
   private void processYear(Calendar from, Calendar to, List<Product> result, File mdFolder) {
      Calendar fromTS = DateTimeUtils.getCopy(from, Calendar.YEAR);
      Calendar toTS = DateTimeUtils.getCopy(to, Calendar.YEAR);

      Calendar timestamp;
      int year;
      for (File yearFolder : mdFolder.listFiles()) {
         try {
            year = Integer.parseInt(yearFolder.getName());
            timestamp = DateTimeUtils.getCopy(DateTimeUtils.getCalendarUTC());
            timestamp.set(Calendar.YEAR, year);
            if (!checkDateInInterval(fromTS, toTS, timestamp)) {
               // skip this year
               continue;
            }

            processMonth(from, to, result, timestamp, yearFolder);
         } catch (NumberFormatException e) {
            logger.warn(INVALID_FILE_NAME + yearFolder, e);
         }
      }

   }

   /**
    * Process month.
    *
    * @param from the from
    * @param to the to
    * @param result the result
    * @param ts the timestamp
    * @param yearFolder the year folder
    */
   private void processMonth(Calendar from, Calendar to, List<Product> result, Calendar ts,
         File yearFolder) {
      Calendar fromTS = DateTimeUtils.getCopy(from, Calendar.YEAR, Calendar.MONTH);
      Calendar toTS = DateTimeUtils.getCopy(to, Calendar.YEAR, Calendar.MONTH);

      Calendar timestamp;
      int month;
      for (File monthFolder : yearFolder.listFiles()) {
         try {
            month = Integer.parseInt(monthFolder.getName());
            timestamp = DateTimeUtils.getCopy(ts, Calendar.YEAR);
            timestamp.set(Calendar.MONTH, month - 1); // January is 0 for Calendar.MONTH
            if (!checkDateInInterval(fromTS, toTS, timestamp)) {
               // skip this month
               continue;
            }
            processDay(from, to, result, timestamp, monthFolder);
         } catch (NumberFormatException e) {
            logger.warn(INVALID_FILE_NAME + monthFolder, e);
         }
      }

   }

   /**
    * Process day.
    *
    * @param from the from
    * @param to the to
    * @param result the result
    * @param ts the timestamp
    * @param monthFolder the month folder
    */
   private void processDay(Calendar from, Calendar to, List<Product> result, Calendar ts,
         File monthFolder) {
      Calendar fromTS = DateTimeUtils.getCopy(from, Calendar.YEAR, Calendar.MONTH,
            Calendar.DAY_OF_MONTH);
      Calendar toTS = DateTimeUtils.getCopy(to, Calendar.YEAR, Calendar.MONTH,
            Calendar.DAY_OF_MONTH);

      Calendar timestamp;
      int day;
      for (File dayFolder : monthFolder.listFiles()) {
         try {
            day = Integer.parseInt(dayFolder.getName());
            timestamp = DateTimeUtils.getCopy(ts, Calendar.YEAR, Calendar.MONTH);
            timestamp.set(Calendar.DAY_OF_MONTH, day);
            if (!checkDateInInterval(fromTS, toTS, timestamp)) {
               // skip this day
               continue;
            }

            processTime(from, to, result, timestamp, dayFolder);
         } catch (NumberFormatException e) {
            logger.warn(INVALID_FILE_NAME + dayFolder, e);
         }
      }
   }

   /**
    * Process time.
    *
    * @param from the from
    * @param to the to
    * @param result the result
    * @param ts the timestamp
    * @param dayFolder the day folder
    */
   private void processTime(Calendar from, Calendar to, List<Product> result, Calendar ts,
         File dayFolder) {
      Calendar timestamp;
      int hour;
      int min;
      for (File timeFolder : dayFolder.listFiles()) {
         Matcher matcher = TIME_PATTERN.matcher(timeFolder.getName());
         if (!matcher.matches()) {
            // skip this time
            continue;
         }
         hour = Integer.parseInt(matcher.group(1));
         min = Integer.parseInt(matcher.group(2));

         timestamp = DateTimeUtils
               .getCopy(ts, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
         timestamp.set(Calendar.HOUR_OF_DAY, hour);
         timestamp.set(Calendar.MINUTE, min);
         if (checkDateInInterval(from, to, timestamp)) {
            Product product;
            for (File file : timeFolder.listFiles(getOthersParametersFilter())) {
               product = new ProductFile(getMetadataURN(), file.getName(), timestamp, file);
               result.add(product);
            }
         }
      }
   }

   /**
    * Gets the others parameters filter.
    *
    * @return the others parameters filter
    */
   protected FileFilter getOthersParametersFilter() {
      // TODO Create here the FileFilter to match others request parameters
      FileFilter filter = TrueFileFilter.INSTANCE;
      return filter;
   }

   /**
    * Check date in interval.
    *
    * @param from the from
    * @param to the to
    * @param timestamp the timestamp
    * @return true, if successful
    */
   private boolean checkDateInInterval(Calendar from, Calendar to, Calendar timestamp) {
      return ((from == null) || (timestamp.compareTo(from) >= 0))
            && ((to == null) || (timestamp.compareTo(to) <= 0));
   }

}
