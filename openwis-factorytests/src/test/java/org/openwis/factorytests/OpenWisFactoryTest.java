package org.openwis.factorytests;

import static org.junit.Assert.fail;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.DefaultOperationListener;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openwis.usermanagement.PopulateUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestBase;
import com.thoughtworks.selenium.Selenium;

/**
 * Base for all OpenWIS factory tests.
 * <p>
 * Configuration properties available:<br>
 * <ul>
 * <li>dbUrl</li>
 * <li>dbUser</li>
 * <li>dbPassword</li>
 * <li>seleniumServer</li>
 * <li>seleniumPort</li>
 * <li>browser: *chrome (firefox-mac), *iexplore (ie-win), *firefox (firefox-win)</li>
 * <li>browserUrl: base URL of the application to test</li>
 * <li>initSeleniumOnce: whether the init of selenium is done in beforeclass or in before</li>
 * <li>userWebapp: user webapp name; ex: /openwis-portal</li>
 * <li>adminWebapp: admin webapp name; ex: /openwis-portal</li>
 * <li>projectDir: dir of the factory test project, in case the test is not run from the root of the project</li>
 * </ul>
 */
public abstract class OpenWisFactoryTest {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(OpenWisFactoryTest.class);

   public static final String LOC_ADMIN_TITLE = "//div[contains(@class, 'administrationTitle1')]";

   public static final String CONFIRM_DELETE_MSG = "Do you confirm";

   public static final String BTN_OK = "OK";

   public static final String BTN_SAVE = "Save";

   public static final String BTN_YES = "Yes";

   public static final String BTN_NO = "No";

   public static final String CENTRE_NAME = "GiscA";

   protected IDatabaseTester databaseTester;

   protected LDAPConnection ldapConnection;

   protected static Selenium selenium;

   private static SeleneseTestBase stb = new SeleneseTestBase();

   private static String adminWebapp;

   private static String userWebapp;

   private static String projectDir;
   
   /** for non-selenium tests */
   private static boolean seleniumDisabled;

   protected static void initSelenium() {
      if (seleniumDisabled) {
         logger.info("Selenium disabled");
         return;
      }
      
      logger.info("System properties: {}", System.getProperties());

      String seleniumServer = System.getProperty("seleniumServer");
      if (seleniumServer == null || seleniumServer.length() == 0) {
         seleniumServer = "localhost";
      }
      String seleniumPortStr = System.getProperty("seleniumPort");
      if (seleniumPortStr == null || seleniumPortStr.length() == 0) {
         seleniumPortStr = "4445";
      }
      int seleniumPort = Integer.parseInt(seleniumPortStr);
      String browser = System.getProperty("browser");
      if (browser == null || browser.length() == 0) {
         browser = "*chrome";
      }
      String browserUrl = System.getProperty("browserUrl");
      if (browserUrl == null || browserUrl.length() == 0) {
         browserUrl = "http://localhost:8080/";
      }

      adminWebapp = System.getProperty("adminWebapp");
      if (adminWebapp == null || adminWebapp.length() == 0) {
         adminWebapp = "/openwis-portal";
      }

      userWebapp = System.getProperty("userWebapp");
      if (userWebapp == null || userWebapp.length() == 0) {
         userWebapp = "/openwis-portal";
      }

      projectDir = System.getProperty("projectDir");
      if (projectDir == null || userWebapp.length() == 0) {
         projectDir = ".";
      }
      logger.info("Using projectDir: {}", projectDir);

      selenium = new DefaultSelenium(seleniumServer, seleniumPort, browser, browserUrl);

      selenium.start();
      selenium.useXpathLibrary("javascript-xpath");
   }

   public static boolean isInitSeleniumOnce() {
      return Boolean.parseBoolean(System.getProperty("initSeleniumOnce"));
   }
   
   public static void disableSelenium() {
      seleniumDisabled = true;
   }

   public static String getAdminWebapp() {
      return adminWebapp;
   }

   public static String getUserWebapp() {
      return userWebapp;
   }

   @BeforeClass
   public static void init() {
      if (isInitSeleniumOnce()) {
         initSelenium();
      }
   }

   @Before
   public void setUp() throws Exception {
      // Init db tester
      String dbUrl = System.getProperty("dbUrl");
      String dbUser = System.getProperty("dbUser");
      String dbPassword = System.getProperty("dbPassword");

      // default
      if (dbUrl == null || dbUrl.length() == 0) {
         dbUrl = "jdbc:postgresql://localhost:5432/OpenWIS-it3";
         dbUser = "postgres";
         dbPassword = "postgres";
      }

      databaseTester = new JdbcDatabaseTester("org.postgresql.Driver", dbUrl, dbUser, dbPassword);
      //initDB(databaseTester.getConnection());

      // initialize dataset
      databaseTester.setDataSet(getDataSet());
      databaseTester.setSetUpOperation(getDatabaseOperation());
      databaseTester.setOperationListener(new DefaultOperationListener());

      // will call default setUpOperation
      databaseTester.onSetup();

      createTestUsers();

      if (!isInitSeleniumOnce()) {
         initSelenium();
      }
   }

   @SuppressWarnings("unused")
   private void initDB(IDatabaseConnection connection) throws Exception {
      long t1 = System.currentTimeMillis();
      executeDBScript(
            "../openwis-metadataportal/openwis-portal/src/main/webapp/WEB-INF/classes/setup/sql/create/remove-db-postgres.sql",
            connection);
      executeDBScript(
            "../openwis-metadataportal/openwis-portal/src/main/webapp/WEB-INF/classes/setup/sql/create/create-db-postgres.sql",
            connection);
      executeDBScript(
            "../openwis-metadataportal/openwis-portal/src/main/webapp/WEB-INF/classes/setup/sql/data/data-db-postgres.sql",
            connection);
      System.out.println((System.currentTimeMillis() - t1) + " ms");
   }

   public void executeDBScript(String aSQLScriptFilePath, IDatabaseConnection connection)
         throws IOException, SQLException, ClassNotFoundException {
      Statement stmt = connection.getConnection().createStatement();
      try {
         BufferedReader in = new BufferedReader(new FileReader(aSQLScriptFilePath));
         String row;
         StringBuffer sb = new StringBuffer();
         while ((row = in.readLine()) != null) {
            if (!row.toUpperCase().startsWith("REM") && !row.startsWith("--")
                  && !row.trim().equals("")) {
               sb.append(" ");
               sb.append(row);

               if (row.endsWith(";")) {
                  String sql = sb.toString();

                  sql = sql.substring(0, sql.length() - 1);

                  //System.out.println("Executing " + sql);
                  stmt.addBatch(sql);
                  //stmt.execute(sql);

                  sb = new StringBuffer();
               }
            }
         }
         //stmt.executeBatch();
         connection.getConnection().commit();

         in.close();
      } finally {
         if (stmt != null) {
            stmt.close();
         }
      }
   }

   protected abstract IDataSet getDataSet() throws Exception;

   /**
    * Operation used on setup, default to DELETE.
    * @return the {@link DatabaseOperation}
    */
   protected DatabaseOperation getDatabaseOperation() {
      return DatabaseOperation.DELETE;
   }

   @After
   public void tearDown() throws Exception {
      //selenium.stop();
      if (databaseTester != null) {
         databaseTester.onTearDown();
      }
      if (!seleniumDisabled && !isInitSeleniumOnce()) {
         selenium.stop();
      }
   }

   @AfterClass
   public static void cleanup() {
      if (!seleniumDisabled && isInitSeleniumOnce()) {
         selenium.stop();
      }
   }

   /**
    * Load file relative to resources dir.
    */
   protected IDataSet loadDataSet(String file) {
      FlatXmlDataFileLoader loader = new FlatXmlDataFileLoader();
      return loader.load(file);
   }

   protected void assertFilteredTable(IDataSet expectedDataSet, ITable actualTable, String tableName)
         throws Exception {
      ITable expectedTable = expectedDataSet.getTable(tableName);

      ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable, expectedTable
            .getTableMetaData().getColumns());
      Assertion.assertEquals(expectedTable, filteredTable);
   }

   /**
    * Compare all tables of expectedData to the actualDataSet
    */
   protected void assertFilteredDataSet(IDataSet expectedDataSet, IDataSet actualDataSet)
         throws Exception {
      ITableIterator tableIterator = expectedDataSet.iterator();
      while (tableIterator.next()) {
         ITable expectedTable = tableIterator.getTable();
         ITable actualTable = actualDataSet.getTable(expectedTable.getTableMetaData()
               .getTableName());

         ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable, expectedTable
               .getTableMetaData().getColumns());
         Assertion.assertEquals(expectedTable, filteredTable);
      }
   }

   /**
    * Assert filtered table equality between data set got from the given xml file
    * and the current db, filtered by the tables contained in the expected dataset.
    */
   protected void assertCurrentDataSet(String expectedDataSetFile) throws Exception {
      IDataSet expectedDataSet = loadDataSet(expectedDataSetFile);
      IDataSet actualDataSet = databaseTester.getConnection().createDataSet(
            expectedDataSet.getTableNames());
      assertFilteredDataSet(expectedDataSet, actualDataSet);
   }

   protected void createTestUsers() throws Exception {
      IDataSet dataSet = loadDataSet("/init-dataset-users.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);
   }

   protected void selectAdminMenu(String section, String subsection) {
      selenium.mouseDownAt("//a/span[text()='" + section + "']", "");
      selenium.mouseDownAt("//a[text()='" + subsection + "']", "");

      waitForElementPresence(LOC_ADMIN_TITLE, true, 10);
   }

   protected abstract String getWebappName();

   protected void open(String service) {
      selenium.open(getWebappName() + service);
   }

   protected void openWindow(String service) {
      selenium.openWindow(getWebappName() + service, "id1");
   }

   protected void loginAs(String user) throws Exception {
      openWindow("/srv/xml.user.login?username=" + user + "&password=admin");
      pause(1000);
   }

   protected void loginAsAdmin() throws Exception {
      loginAs("myadmin");
   }

   protected void loginAsOperator() throws Exception {
      loginAs("myoperator");
   }

   protected void loginAsAccessAdmin() throws Exception {
      loginAs("myaccessadmin");
   }

   protected void logout() throws Exception {
      openWindow("/srv/xml.user.logout");
      pause(1000);
   }

   public void addDefaultTemplates() {
      openWindow("/srv/xml.template.addDefault?jsonData={\"content\":\"iso19139\"}");
      pause(1000);
   }

   protected void rebuildIndex() {
      // clean index
      openWindow("/srv/metadata.admin.index.rebuild");
      pause(1000);
   }

   /**
    * Get the absolute path, considering the project dir property.
    */
   protected String getAbsolutePath(String relativeDir) {
      File fDir = new File(projectDir, relativeDir);
      return fDir.getAbsolutePath();
   }

   protected void loadMetadata(String dir) throws Exception {
      String absoluteDir = getAbsolutePath(dir);
      openWindow("/srv/en/xml.metadata.batchimport?dir=" + URLEncoder.encode(absoluteDir, "UTF-8")
            + "&file_type=single&rest=true&category=datasets");
   }

   /** Sleeps for the specified number of milliseconds */
   public void pause(int millisecs) {
      stb.pause(millisecs);
   }

   protected void setTextFieldValue(String textFieldLabel, String value) {
      selenium.type("//label[text()='" + textFieldLabel + "']/following-sibling::div/input", value);
   }

   protected void setComboValue(String comboLabel, String value) throws Exception {
      // click the down arrow image on the right of the ComboBox and assumes
      // that there is a label before the component
      selenium
            .click("//label[text()='"
                  + comboLabel
                  + "']/following-sibling::div/descendant::img[contains(@class, 'x-form-arrow-trigger')]");

      // wait for a drop down list of options to be visible
      // Doesn't' work on windows
      //      waitForElementPresence(
      //            "//div[contains(@class, 'x-combo-list') and contains(@style, 'visibility: visible')]",
      //            true, 10);
      pause(1000);

      // click the required drop down item based on the text of the target
      // item
      selenium
            .click("//div[contains(@class, 'x-combo-list')]/descendant::div[contains(@class, 'x-combo-list-item')][text()='"
                  + value + "']");

      // wait for the drop down list of options to be no longer visible
      //      waitForElementPresence(
      //            "//div[contains(@class, 'x-combo-list') and contains(@style, 'visibility: visible')]",
      //            false, 10);
      pause(1000);
   }

   protected void setCheckboxValue(String checkboxLabel, boolean value) throws Exception {
      String locator = "//label[text()='" + checkboxLabel + "']/following-sibling::div//input";
      if (value) {
         selenium.check(locator);
      } else {
         selenium.uncheck(locator);
      }
   }

   protected void waitForElementPresence(String locator, boolean present, int timeoutSeconds) {
      for (int second = 0;; second++) {
         if (second >= timeoutSeconds) {
            fail("timeout for element presence + " + locator + ", test=" + present);
         }
         try {
            boolean isPresent = selenium.isElementPresent(locator);
            if (isPresent == present) {
               break;
            }
            Thread.sleep(1000);
         } catch (Exception e) {
         }
      }
   }

   protected void waitForTextPresent(String text, int timeoutSeconds) {
      for (int second = 0;; second++) {
         if (second >= timeoutSeconds) {
            fail("timeout for text presence: " + text);
         }
         try {
            if (selenium.isTextPresent(text)) {
               break;
            }
            Thread.sleep(1000);
         } catch (Exception e) {
         }
      }
   }

   protected void waitForTextDisappear(String text, int timeoutSeconds) {
      for (int second = 0;; second++) {
         if (second >= timeoutSeconds) {
            fail("timeout for text disappear: " + text);
         }
         try {
            if (!selenium.isTextPresent(text)) {
               break;
            }
            Thread.sleep(1000);
         } catch (Exception e) {
         }
      }
      // ensure wait message has really vanished !
      pause(500);
   }

   protected void waitForPleaseWaitVanish(int timeoutSeconds) {
      waitForTextDisappear("Loading...", timeoutSeconds);
   }

   protected void waitForLoadingVanish(int timeoutSeconds) {
      waitForTextDisappear("Loading...", timeoutSeconds);
   }

   protected void waitForMetadataViewer(String title, int timeoutSeconds) {
      waitForElementPresence("//span[contains(@class, 'x-window-header-text')][text()='" + title
            + "']", true, timeoutSeconds);
   }

   protected void closeMetadataViewer(boolean editMode) {
      selenium
            .click("//div[contains(@class, 'x-window') and contains(@style, 'visible')]//div[contains(@class, 'x-window-header')]/div[contains(@class, 'x-tool-close')]");
      pause(3000);
      if (editMode) {
         clickOnButton(BTN_NO);
         pause(3000);
      }
   }

   protected void clickOnButton(String buttonName) {
      selenium.click("css=button:contains('" + buttonName + "')");
   }

   protected boolean isButtonEnabled(String buttonName) {
      // ensure first button is present
      if (!selenium.isElementPresent("//table[contains(@class, 'x-btn')]//button[text()='"
            + buttonName + "']")) {
         throw new IllegalArgumentException("Button " + buttonName + " not found");
      }
      return !selenium
            .isElementPresent("//table[contains(@class, 'x-btn') and contains(@class, 'x-item-disabled')]//button[text()='"
                  + buttonName + "']");
   }

   protected int countGridRows(String gridId) {
      return selenium.getXpathCount(
            "//div[@id='" + gridId + "']//div[contains(@class, 'x-grid3-row')]").intValue();
   }

   protected String getGridCellText(String gridId, int row, int col) {
      return selenium.getText("//div[@id='" + gridId + "']//div[contains(@class, 'x-grid3-row')]["
            + row + "]//td[contains(@class, 'x-grid3-col')][" + col + "]/div");
   }

   protected boolean isGridCellChecked(String gridId, int row, int col) {
      return selenium.isElementPresent("//div[@id='" + gridId
            + "']//div[contains(@class, 'x-grid3-row')][" + row
            + "]//td[contains(@class, 'x-grid3-col')][" + col
            + "]/div/div[contains(@class, 'x-grid3-check-col-on')]");
   }

   protected void selectGridCell(String gridId, int row, int col) {
      selenium.mouseDownAt("//div[@id='" + gridId + "']//div[contains(@class, 'x-grid3-row')]["
            + row + "]//td[contains(@class, 'x-grid3-col')][" + col + "]/div", "");
   }

   protected void selectGridCells(String gridId, List<Point> cells) {
      selenium.controlKeyDown();
      for (Point point : cells) {
         selectGridCell(gridId, point.y, point.x);
      }
      selenium.controlKeyUp();
   }

   protected void initLDAP(String file) {
      // default config for security mgt
      if (System.getProperty("userManagementServiceWsdl") == null) {
         System.setProperty(
               "userManagementServiceWsdl",
               "http://localhost:8180/openwis-securityservice-openwis-securityservice-usermanagement-server-ejb-1.0-SNAPSHOT/UserManagementService?wsdl");
      }
      if (System.getProperty("groupManagementServiceWsdl") == null) {
         System.setProperty(
               "groupManagementServiceWsdl",
               "http://localhost:8180/openwis-securityservice-openwis-securityservice-usermanagement-server-ejb-1.0-SNAPSHOT/GroupManagementService?wsdl");
      }

      PopulateUser populateUser = new PopulateUser();
      populateUser.resetUsers();
      populateUser.resetGroups();
      URL fileUrl = getClass().getResource(file);
      populateUser.populate(fileUrl.getFile(), CENTRE_NAME);
   }

   protected void initLDAPConnection() throws LDAPException, UnsupportedEncodingException {
      ldapConnection = new LDAPConnection();

      String ldapHost = System.getProperty("ldapHost");
      if (ldapHost == null || ldapHost.length() == 0) {
         ldapHost = "localhost";
      }
      String ldapPortStr = System.getProperty("ldapPort");
      if (ldapPortStr == null || ldapPortStr.length() == 0) {
         ldapPortStr = "1389";
      }
      int ldapPort = Integer.parseInt(ldapPortStr);
      String ldapUser = System.getProperty("ldapUser");
      if (ldapUser == null || ldapUser.length() == 0) {
         ldapUser = "cn=Directory Manager";
      }
      String ldapPassword = System.getProperty("ldapPassword");
      if (ldapPassword == null || ldapPassword.length() == 0) {
         ldapPassword = "toulouse";
      }

      // connect to the server
      ldapConnection.connect(ldapHost, ldapPort);

      // authenticate to the server
      ldapConnection.bind(LDAPConnection.LDAP_V3, ldapUser, ldapPassword.getBytes("UTF8"));
   }

   protected void synchronizeGroupsWithLDAP() {
      openWindow("/srv/xml.group.synchronize?force=true");
      pause(1000);
   }

}
