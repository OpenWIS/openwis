/**
 * 
 */
package org.openwis.harness.samples.db.localdatasource.utils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileSystemFeeder. <P>
 * Create a mock FS:
 * <ul>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::EOXF42MSGG</b> one file by day at 12:00 from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX01EGRR</b> one file by hour from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX02EGRR</b> four file by day from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX03EGRR</b> one file by minutes Day from 12 dec. 2010 06:00 to 18:00</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX04EGRR</b> one file by seconds from 17 jan. 2011 12:00 to 12:15</li>
 * </ul>
 */
public class SampleDBUnitFileBuilder {

   /**
    * The main method.
    *
    * @param args the arguments
    */
   public static void main(String[] args) {
      PrintStream writer = null;
      try {
         writer = new PrintStream("src/test/resources/database.xml");
         writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
         writer.println("<dataset>");
         SampleDBUnitFileBuilder builder = new SampleDBUnitFileBuilder(writer);
         builder.createTestFileSystem();
         writer.println("</dataset>");
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } finally {
         IOUtils.closeQuietly(writer);
      }
   }

   /** The Constant CREATE_FILE_FOR. */
   private static final String CREATE_FILE_FOR = "Create file for {}";

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SampleDBUnitFileBuilder.class);

   /** The Constant METADATA_URN_PREFIX. */
   public static final String METADATA_URN_PREFIX = "urn:x-wmo:md:int.wmo.wis::";

   /** The Constant md0. */
   public static final String MD_0 = "EOXF42MSGG";

   /** The Constant md1. */
   public static final String MD_1 = "FVXX01EGRR";

   /** The Constant md2. */
   public static final String MD_2 = "FVXX02EGRR";

   /** The Constant md3. */
   public static final String MD_3 = "FVXX03EGRR";

   /** The Constant md4. */
   public static final String MD_4 = "FVXX04EGRR";

   /** The sdf1. */
   private final SimpleDateFormat sdf1;

   /** The sdf2. */
   private final SimpleDateFormat sdf2;

   /** The sdf2. */
   private final SimpleDateFormat sdf3;

   /** The md id. */
   private long mdId = 0;

   /** The p id. */
   private long pId = 0;

   /** The writer. */
   private final PrintStream writer;

   /**
    * Default constructor.
    * Builds a FileSystemFeeder.
    *
    * @param printStream the print stream
    */
   public SampleDBUnitFileBuilder(PrintStream printStream) {
      super();
      sdf1 = new SimpleDateFormat("FFHHmm", Locale.ENGLISH);
      sdf2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
      sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
      writer = printStream;
   }

   /**
    * Next metadata id.
    *
    * @return the long
    */
   private long nextMetadataId() {
      return ++mdId;
   }

   /**
    * Next product id.
    *
    * @return the long
    */
   private long nextProductId() {
      return ++pId;
   }

   /**
    * Creates the test file system.
    */
   public void createTestFileSystem() {
      long id;
      // Create metadata
      List<String> lst = new ArrayList<String>();

      Calendar from = Calendar.getInstance();
      // Start at 01 September 2010 at 00:00
      from.clear();
      from.set(2010, Calendar.SEPTEMBER, 1);

      // End at 20 January 2011
      Calendar to = Calendar.getInstance();
      to.clear();
      to.set(2011, Calendar.JANUARY, 20);

      String urn;
      // EOXF42MSGG one file / day at 12:00
      urn = MD_0;
      from.set(Calendar.HOUR, 12);
      id = createProductMetadata(urn);
      lst.addAll(createProduct(id, urn, from, to, Calendar.DAY_OF_MONTH, 1));

      // FVXX01EGRR one file / hour
      urn = MD_1;
      from.clear(Calendar.HOUR);
      logger.info(CREATE_FILE_FOR, urn);
      id = createProductMetadata(urn);
      lst.addAll(createProduct(id, urn, from, to, Calendar.HOUR, 1));

      // FVXX02EGRR files at 00:00, 06:00, 12:00, 18:00 each day
      urn = MD_2;
      logger.info(CREATE_FILE_FOR, urn);
      id = createProductMetadata(urn);
      lst.addAll(createProduct(id, urn, from, to, Calendar.HOUR, 6));

      // FVXX03EGRR files each minutes between 06:00 and 18:00 the 12st Dec 2010
      urn = MD_3;
      from.clear();
      from.set(2010, Calendar.DECEMBER, 12, 06, 00);

      to.clear();
      to.set(2010, Calendar.DECEMBER, 12, 18, 00);
      logger.info(CREATE_FILE_FOR, urn);
      id = createProductMetadata(urn);
      lst.addAll(createProduct(id, urn, from, to, Calendar.MINUTE, 1));

      // FVXX04EGRR files each seconds between 12:00 and 12:15 the 17th Jan 2011
      urn = MD_4;
      from.clear();
      from.set(2011, Calendar.JANUARY, 17, 12, 00);

      to.clear();
      to.set(2011, Calendar.JANUARY, 17, 12, 15);
      logger.info(CREATE_FILE_FOR, urn);
      id = createProductMetadata(urn);
      lst.addAll(createProduct(id, urn, from, to, Calendar.SECOND, 1));

      for (String s : lst) {
         writer.println(s);
      }
   }

   /**
    * Creates the product metadata.
    *
    * @param urn the urn
    * @return the long
    */
   private long createProductMetadata(String urn) {
      long id = nextMetadataId();
      writer.println(MessageFormat.format("<product_metadata id=\"{0}\" urn=\"{1}{2}\"/>",
            String.valueOf(id), METADATA_URN_PREFIX, urn));
      return id;
   }

   /**
    * Creates the product.
    *
    * @param id the id
    * @param mdURN the md urn
    * @param from the from
    * @param to the to
    * @param calendarField the calendar field
    * @param increment the increment
    * @return the list
    */
   private List<String> createProduct(long id, String mdURN, Calendar from, Calendar to,
         int calendarField, int increment) {
      List<String> result = new ArrayList<String>();
      Calendar current = Calendar.getInstance();
      current.clear();
      current.setTime(from.getTime());

      String urn;
      while (current.before(to)) {
         urn = MessageFormat.format("X_{0}{1}_C_{2}", mdURN, sdf1.format(current.getTime()),
               sdf2.format(current.getTime()));
         result.add(MessageFormat.format(
               "<product id=\"{0}\" urn=\"{1}\" md_id=\"{2}\" product_timestamp=\"{3}\"/>",
               String.valueOf(nextProductId()), urn, String.valueOf(id),
               sdf3.format(current.getTime())));
         current.add(calendarField, increment);
      }
      return result;
   }

}
