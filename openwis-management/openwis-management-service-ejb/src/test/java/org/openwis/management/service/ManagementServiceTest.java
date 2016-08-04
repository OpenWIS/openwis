package org.openwis.management.service;

import java.io.File;

import org.jboss.arquillian.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.openwis.management.entity.UserDisseminatedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ManagementServiceTest.
 */
public abstract class ManagementServiceTest {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ManagementServiceTest.class);

   /**
    * Creates the deployment.
    *
    * @return the archive
    */
   @Deployment
   public static Archive<?> createDeployment() {
      File dataFolder = new File("data");
      if (dataFolder.exists() && dataFolder.isDirectory() && dataFolder.canWrite()) {
         dataFolder.delete();
      }
      JavaArchive archive = ShrinkWrap
            .create(JavaArchive.class)
            .addResource("log4j.properties")
            .addResource("jndi.properties")
            // Add a class from each package requested.
            .addPackages(true, DisseminatedDataStatisticsImpl.class.getPackage(),
                  UserDisseminatedData.class.getPackage())
            .addManifestResource("test-persistence.xml", "persistence.xml");

      logger.info("Deployment/n{}", archive.toString(true));
      return archive;
   }

}
