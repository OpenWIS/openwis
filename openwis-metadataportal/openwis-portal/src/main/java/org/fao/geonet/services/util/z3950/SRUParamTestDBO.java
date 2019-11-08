package org.fao.geonet.services.util.z3950;

import java.util.Set;

/**
 * The Class SRUParamTestDBO.
 */
public class SRUParamTestDBO {

   /** The op. */
   private final String op;

   /** The not supported. */
   private final Set<String> notSupported;

   /** The missing args. */
   private final Set<String> missingArgs;

   /** The cannot parse. */
   private final Set<String> cannotParse;

   /**
    * Instantiates a new sRU param test dbo.
    *
    * @param op the op
    * @param notSupported the not supported
    * @param missingArgs the missing args
    * @param cannotParse the cannot parse
    */
   public SRUParamTestDBO(String op, Set<String> notSupported, Set<String> missingArgs,
         Set<String> cannotParse) {

      this.op = op;
      this.notSupported = notSupported;
      this.missingArgs = missingArgs;
      this.cannotParse = cannotParse;

   }

   /**
    * Gets the op.
    *
    * @return the op
    */
   public String getOp() {
      return op;
   }

   /**
    * Gets the arg not supported.
    *
    * @return the arg not supported
    */
   public Set<String> getArgNotSupported() {
      return notSupported;
   }

   /**
    * Gets the missing args.
    *
    * @return the missing args
    */
   public Set<String> getMissingArgs() {
      return missingArgs;
   }

   /**
    * Gets the cannot parse arg.
    *
    * @return the cannot parse arg
    */
   public Set<String> getCannotParseArg() {
      return cannotParse;
   }

}
