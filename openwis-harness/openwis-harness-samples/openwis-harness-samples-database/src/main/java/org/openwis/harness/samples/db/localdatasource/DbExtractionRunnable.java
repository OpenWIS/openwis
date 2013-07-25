/**
 * 
 */
package org.openwis.harness.samples.db.localdatasource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.samples.common.Product;
import org.openwis.harness.samples.common.extraction.ExtractionException;
import org.openwis.harness.samples.common.extraction.ExtractionRunnable;

/**
 * The Class ExtractionRunnable. <P>
 * Background extraction for file. <P>
 * Check timestamp criteria.
 */
public class DbExtractionRunnable extends ExtractionRunnable {

   /** The Constant TIME_PATTERN. */
   public static final Pattern TIME_PATTERN = Pattern.compile("(\\d\\d)_(\\d\\d)");

   /** The local datasource file utils. */
   private final LocalDatasourceDbUtils localDatasourceFileUtils;

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
   public DbExtractionRunnable(LocalDatasourceDbUtils localDatasourceFileUtils, String metadataURN,
         List<Parameter> parameters, long requestId, String stagingPostURI) throws IOException {
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
         Connection c = null;
         PreparedStatement ps = null;
         ResultSet resultSet = null;
         try {
            c = localDatasourceFileUtils.getConnection();
            if (getFrom() != null && getTo() != null) {
               Timestamp fromTS = new Timestamp(getFrom().getTimeInMillis());
               Timestamp toTS = new Timestamp(getTo().getTimeInMillis());

               ps = c.prepareStatement(SqlRequest.EXTRACT_BY_TIMESTAMP);
               ps.setString(1, getMetadataURN());
               ps.setTimestamp(2, fromTS);
               ps.setTimestamp(3, toTS);
            } else {
               ps = c.prepareStatement(SqlRequest.EXTRACT_ALL);
               ps.setString(1, getMetadataURN());
            }
            Product product;
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
               product = buildProduct(resultSet);
               result.add(product);
            }
         } catch (SQLException e) {
            throw new ExtractionException(e);
         } finally {
            localDatasourceFileUtils.close(c, ps, resultSet);
         }
      } catch (ParseException e) {
         throw new ExtractionException(e);
      }
      return result;
   }

   /**
    * Builds the product.
    *
    * @param resultSet the result set
    * @return the product
    * @throws SQLException the SQL exception
    */
   private Product buildProduct(ResultSet resultSet) throws SQLException {
      long id = resultSet.getLong(1);
      String urn = resultSet.getString(2);
      Timestamp ts = resultSet.getTimestamp(3);
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(ts.getTime());
      return new ProductDB(id, getMetadataURN(), urn, cal);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.harness.samples.common.extraction.ExtractionRunnable#
    * writeProductToFile(org.openwis.harness.samples.common.Product, java.io.File)
    */
   @Override
   protected void writeProductToStagingPost(Product product, File stagingPostFile)
         throws IOException {
      String dataFileName = product.getURN();
      File urnTestFile = new File(stagingPostFile, dataFileName);
      FileUtils.touch(urnTestFile);
   }
}
