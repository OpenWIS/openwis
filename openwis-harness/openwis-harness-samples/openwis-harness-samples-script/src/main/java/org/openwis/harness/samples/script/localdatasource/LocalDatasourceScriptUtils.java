package org.openwis.harness.samples.script.localdatasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.openwis.harness.samples.common.extraction.LocalDatasourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LocalDatasourceScriptUtils.
 */
public class LocalDatasourceScriptUtils extends LocalDatasourceUtils {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(LocalDatasourceScriptUtils.class);

   /**
    * Instantiates a new local datasource script utils.
    *
    * @param props the props
    */
   public LocalDatasourceScriptUtils(Properties props) {
      super(props);
   }

   /**
    * Run and wait result.
    *
    * @param cmds script and arguments
    * @return the result
    */
   public int runAndWaitResult(String... cmds) {
      int result = -1;
      ProcessBuilder pb = new ProcessBuilder(cmds);
      try {
         pb.redirectErrorStream(true);

         Process process = pb.start();
         result = process.waitFor();
         redirect(process);
      } catch (InterruptedException e) {
         logger.error("Error in execution !", e);
      } catch (IOException e) {
         logger.error("Error when creating process", e);
      }
      return result;
   }

   /**
    * Redirect.
    *
    * @param process the process
    * @throws IOException Signals that an I/O exception has occurred.
    */
   private void redirect(Process process) throws IOException {
      InputStream in = process.getInputStream();
      IOUtils.copy(in, System.out);
      IOUtils.closeQuietly(in);
   }

   /**
    * Run in background.
    *
    * @param cmds script and arguments
    */
   public void runInBackground(String... cmds) {
      ProcessBuilder pb = new ProcessBuilder(cmds);
      try {
         pb.redirectErrorStream(true);
         Process process = pb.start();
         redirect(process);
      } catch (IOException e) {
         logger.error("Error when creating process", e);
      }
   }
}
