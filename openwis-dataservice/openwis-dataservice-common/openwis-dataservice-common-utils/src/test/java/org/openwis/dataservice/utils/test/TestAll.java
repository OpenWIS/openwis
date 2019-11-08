package org.openwis.dataservice.utils.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * The class <code>TestAll</code> builds a suite that can be used to run all
 * of the tests within its package as well as within any subpackages of its
 * package.
 *
 * @generatedBy CodePro at 25/11/10 14:00
 * @author racaru
 * @version $Revision: 1.0 $
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( {TestDateUtils.class, TestHashUtils.class, TestNumberUtils.class})
public class TestAll {

   /**
    * Launch the test.
    *
    * @param args the command line arguments
    *
    * @generatedBy CodePro at 25/11/10 14:00
    */
   public static void main(String[] args) {
      JUnitCore.runClasses(new Class[] {TestAll.class});
   }
}
