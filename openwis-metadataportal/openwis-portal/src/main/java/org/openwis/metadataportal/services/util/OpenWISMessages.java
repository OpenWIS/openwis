/**
 * 
 */
package org.openwis.metadataportal.services.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * OpenWIS Message class. <P>
 * 
 */
public class OpenWISMessages {

   /**
    * Comment for <code>BUNDLE_NAME</code>
    * @member: BUNDLE_NAME
    */
   private static final String BUNDLE_NAME = "openwisMessage";

   /**
    * Default constructor.
    * Builds a OpenWISMessages.
    */
   private OpenWISMessages() {
   }

   /**
    * Get the string.
    * @param key the key
    * @param lang the language (default if <code>null</code>)
    * @return the string
    */
   public static String getString(String key, String lang) {
      try {
         ResourceBundle resourceBundle;
         if (lang == null) {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
         } else {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale(lang));
         }
         return resourceBundle.getString(key);
      } catch (MissingResourceException e) {
         return '!' + key + '!';
      }
   }

   /**
    * Format the message.
    * @param key the key
    * @param args the arguments
    * @return the custom message
    */
   public static String format(String key, String lang, Object... args) {
      String pattern = OpenWISMessages.getString(key, lang);
      return MessageFormat.format(pattern, args);
   }
}
