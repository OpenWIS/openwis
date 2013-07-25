package org.openwis.factorytests.user;

import org.openwis.factorytests.OpenWisFactoryTest;

/**
 * Base for all OpenWIS factory tests on admin portal.
 */
public abstract class UserFactoryTest extends OpenWisFactoryTest {

   public static final String SECTION_METADATA_SERVICE = "Metadata Service";

   public static final String SECTION_CREATE_METADATA = "Create metadata";

   public static final String SECTION_MONITOR_CATALOG_CONTENT = "Monitor catalog content";

   public static final String SECTION_CATEGORY_MANAGEMENT = "Category management";

   public static final String SECTION_SECURITY_SERVICE = "Security Service";

   public static final String SECTION_GROUP_MANAGEMENT = "Group management";

   public static final String BTN_NEW = "New...";

   public static final String BTN_EDIT = "Edit...";

   public static final String BTN_VIEW = "View...";

   public static final String BTN_REMOVE = "Remove";

   public static final String BTN_CANCEL = "Cancel";

   public static final String BTN_OK = "OK";

   public static final String BTN_SAVE = "Save";

   public static final String BTN_YES = "Yes";

   public static final String BTN_NO = "No";

   public static final String BTN_EXPORT = "Export";

   @Override
   protected String getWebappName() {
      return getUserWebapp();
   }

   protected void openUserHomePage() {
      open("/srv/en/main.home");
   }

   protected void openMyAccountPage() {
      open("/srv/en/myaccount");
   }

}
