package org.fao.geonet.services.util.z3950;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fao.geonet.services.main.SRUSearch;

/**
 * The Class SRUParamTester.
 */
public class SRUParamTester {

   /** The explain args. */
   private final Set<String> explainArgs;

   /** The explain mandatory args. */
   private final Set<String> explainMandatoryArgs;

   /** The search retrieve args. */
   private final Set<String> searchRetrieveArgs;

   /** The search retrieve mandatory args. */
   private final Set<String> searchRetrieveMandatoryArgs;

   /** The param types. */
   private final Map<String, String> paramTypes;

   /**
    * Instantiates a new sRU param tester.
    */
   public SRUParamTester() {

      explainMandatoryArgs = new HashSet<String>();

      explainMandatoryArgs.add("version");
      explainMandatoryArgs.add("operation");

      explainArgs = new HashSet<String>();

      explainArgs.add("recordpacking");
      explainArgs.add("stylesheet");
      explainArgs.add("extrarequestdata");

      searchRetrieveMandatoryArgs = new HashSet<String>();

      searchRetrieveMandatoryArgs.add("operation");
      searchRetrieveMandatoryArgs.add("version");
      searchRetrieveMandatoryArgs.add("query");

      searchRetrieveArgs = new HashSet<String>();

      searchRetrieveArgs.add("startrecord");
      searchRetrieveArgs.add("maximumrecords");
      searchRetrieveArgs.add("recordpacking");
      searchRetrieveArgs.add("recordschema");
      searchRetrieveArgs.add("recordxpath ");
      searchRetrieveArgs.add("resultsetttl");
      searchRetrieveArgs.add("sortkeys");
      searchRetrieveArgs.add("stylesheet");
      searchRetrieveArgs.add("extrarequestdata");

      paramTypes = new HashMap<String, String>();

      paramTypes.put(SRUSearch.OP_SR_QUERY, "string");
      paramTypes.put(SRUSearch.OP_SR_VERSION, "string");
      paramTypes.put(SRUSearch.OP_SR_STYLESH, "string");
      paramTypes.put(SRUSearch.OP_SR_STARTREC, "int");
      paramTypes.put(SRUSearch.OP_SR_MAXREC, "int");
      paramTypes.put(SRUSearch.OP_SR_RECPACK, "string");
      paramTypes.put(SRUSearch.OP_SR_RECSCHEMA, "string");
      paramTypes.put(SRUSearch.OP_SR_RECXPATH, "string");
      paramTypes.put(SRUSearch.OP_SR_SORTKEYS, "string");
      paramTypes.put(SRUSearch.OP_SR_EXTRADATA, "string");

   }

   /**
    * Test params.
    *
    * @param op the op
    * @param params the params
    * @return the sRU param test dbo
    */
   public SRUParamTestDBO testParams(String op, Map<String, String> params) {

      Set<String> notSupported = testNotSupported(op, params);
      Set<String> missingArgs = testMissingArgs(op, params);
      Set<String> cannotParse = testCannotParse(op, params);

      SRUParamTestDBO ret = new SRUParamTestDBO(op, notSupported, missingArgs, cannotParse);

      return ret;
   }

   /**
    * Test not supported.
    *
    * @param op the op
    * @param params the params
    * @return the sets the
    */
   private Set<String> testNotSupported(String op, Map<String, String> params) {

      Set<String> temp = new HashSet<String>();
      Set<String> ret = new HashSet<String>();

      if (op.equals("explain")) {
         temp.addAll(explainArgs);
         temp.addAll(explainMandatoryArgs);
      } else if (op.equals("searchretrieve")) {
         temp.addAll(searchRetrieveArgs);
         temp.addAll(searchRetrieveMandatoryArgs);
      } else {
         return ret;
      }

      for (String param : params.keySet()) {
         if (!temp.contains(param)) {
            ret.add(param);
         }
      }

      return ret;
   }

   /**
    * Test missing args.
    *
    * @param op the op
    * @param params the params
    * @return the sets the
    */
   private Set<String> testMissingArgs(String op, Map<String, String> params) {

      Set<String> temp = new HashSet<String>();
      Set<String> ret = new HashSet<String>();

      if (op.equals("explain")) {
         temp.addAll(explainMandatoryArgs);
      }
      if (op.equals("searchretrieve")) {
         temp.addAll(searchRetrieveMandatoryArgs);
      }

      for (String param : temp) {
         if (!params.containsKey(param)) {
            ret.add(param);
         }
      }

      return ret;
   }

   /**
    * Test cannot parse.
    *
    * @param op the op
    * @param params the params
    * @return the sets the
    */
   private Set<String> testCannotParse(String op, Map<String, String> params) {

      Set<String> ret = new HashSet<String>();

      if (op.equals("searchretrieve")) {

         for (String key : params.keySet()) {
            String val = params.get(key);

            if (paramTypes.containsKey(key) && paramTypes.get(key).equals("int")) {
               try {
                  Integer.parseInt(val);
               } catch (NumberFormatException e) {
                  ret.add(key);
               }
            }
         }

      }

      return ret;

   }

}