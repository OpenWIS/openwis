package org.openwis.factorytests.admin;

import org.openwis.factorytests.OpenWisFactoryTest;

/**
 * Base for all OpenWIS factory tests on admin portal.
 */
public abstract class AdminFactoryTest extends OpenWisFactoryTest {

   public static final String SECTION_METADATA_SERVICE = "Metadata Service";

   public static final String SECTION_CREATE_METADATA = "Create metadata";

   public static final String SECTION_INSERT_METADATA = "Insert metadata";

   public static final String SECTION_CONFIGURE_HARVESTING = "Configure harvesting";

   public static final String SECTION_MONITOR_CATALOG_CONTENT = "Monitor catalogue content";

   public static final String SECTION_CATEGORY_MANAGEMENT = "Category management";

   public static final String SECTION_SECURITY_SERVICE = "Security Service";

   public static final String SECTION_GROUP_MANAGEMENT = "Group management";

   public static final String SECTION_USER_MANAGEMENT = "User management";

   public static final String SECTION_DATA_POLICY_MANAGEMENT = "Data policy management";

   public static final String BTN_NEW = "New...";

   public static final String BTN_EDIT = "Edit...";

   public static final String BTN_VIEW = "View...";

   public static final String BTN_REMOVE = "Remove";

   public static final String BTN_CANCEL = "Cancel";

   public static final String BTN_EXPORT = "Export";

   protected void openAdminHomePage() {
      open("/srv/en/main.home");
   }

   @Override
   protected String getWebappName() {
      return getAdminWebapp();
   }


}
