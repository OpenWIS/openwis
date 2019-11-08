package org.fao.geonet.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.IndexField;
import org.openwis.metadataportal.kernel.search.query.SearchException;

/**
 * These are all extension methods for calling from xsl docs.  Note:  All
 * params are objects because it is hard to determine what is passed in from XSLT.
 * Most are converted to string by calling tostring.
 *
 * @author jesse
 */
public final class XslUtil {

   /** The Constant TS_DEFAULT. */
   private static final char TS_DEFAULT = ' ';

   /** The Constant CS_DEFAULT. */
   private static final char CS_DEFAULT = ',';

   /** The Constant TS_WKT. */
   private static final char TS_WKT = ',';

   /** The Constant CS_WKT. */
   private static final char CS_WKT = ' ';

   /** The search manager. */
   public static ISearchManager searchManager = null;

   /**
    * clean the src of ' and <>.
    *
    * @param src the src
    * @return the string
    */
   public static String clean(Object src) {
      String result = src.toString().replaceAll("'", "\'").replaceAll("[><\n\r]", " ");
      return result;
   }

   /**
    * Returns 'true' if the pattern matches the src.
    *
    * @param src the src
    * @param pattern the pattern
    * @return the string
    */
   public static String countryMatch(Object src, Object pattern) {
      if (src.toString().trim().length() == 0) {
         return "false";
      }
      boolean result = src.toString().toLowerCase().contains(pattern.toString().toLowerCase());
      return String.valueOf(result);
   }

   /**
    * Replace the pattern with the substitution.
    *
    * @param src the src
    * @param pattern the pattern
    * @param substitution the substitution
    * @return the string
    */
   public static String replace(Object src, Object pattern, Object substitution) {
      String result = src.toString().replaceAll(pattern.toString(), substitution.toString());
      return result;
   }

   /**
    * Takes the characters until the pattern is matched.
    *
    * @param src the src
    * @param pattern the pattern
    * @return the string
    */
   public static String takeUntil(Object src, Object pattern) {
      String src2 = src.toString();
      Matcher matcher = Pattern.compile(pattern.toString()).matcher(src2);

      if (!matcher.find())
         return src2;

      int index = matcher.start();

      if (index == -1) {
         return src2;
      }
      return src2.substring(0, index);
   }

   /**
    * Converts the seperators of the coords to the WKT from ts and cs.
    *
    * @param coords the coords string to convert
    * @param ts the separator that separates 2 coordinates
    * @param cs the separator between 2 numbers in a coordinate
    * @return the string
    */
   public static String toWktCoords(Object coords, Object ts, Object cs) {
      String coordsString = coords.toString();
      char tsString;
      if (ts == null || ts.toString().length() == 0) {
         tsString = TS_DEFAULT;
      } else {
         tsString = ts.toString().charAt(0);
      }
      char csString;
      if (cs == null || cs.toString().length() == 0) {
         csString = CS_DEFAULT;
      } else {
         csString = cs.toString().charAt(0);
      }

      if (tsString == TS_WKT && csString == CS_WKT) {
         return coordsString;
      }

      if (tsString == CS_WKT) {
         tsString = ';';
         coordsString = coordsString.replace(CS_WKT, tsString);
      }
      coordsString = coordsString.replace(csString, CS_WKT);
      String result = coordsString.replace(tsString, TS_WKT);
      char lastChar = result.charAt(result.length() - 1);
      if (result.charAt(result.length() - 1) == TS_WKT || lastChar == CS_WKT) {
         result = result.substring(0, result.length() - 1);
      }
      return result;
   }

   /**
    * Pos list to wkt coords.
    *
    * @param coords the coords
    * @param dim the dim
    * @return the string
    */
   public static String posListToWktCoords(Object coords, Object dim) {
      String[] coordsString = coords.toString().split(" ");

      int dimension;
      if (dim == null) {
         dimension = 2;
      } else {
         try {
            dimension = Integer.parseInt(dim.toString());
         } catch (NumberFormatException e) {
            dimension = 2;
         }
      }
      StringBuilder results = new StringBuilder();

      for (int i = 0; i < coordsString.length; i++) {
         if (i > 0 && i % dimension == 0) {
            results.append(',');
         } else if (i > 0) {
            results.append(' ');
         }
         results.append(coordsString[i]);
      }

      return results.toString();
   }

   /**
    * Gets the index field.
    *
    * @param uuid the uuid
    * @param fieldName the field name
    * @return the index field
    */
   public static String getIndexField(Object uuid, Object fieldName) {
      String result = null;
      IndexField field = IndexField.getField((String) fieldName);
      if (searchManager == null) {
         Log.warning(Geonet.SEARCH_ENGINE, "Search Engine not set in XslUtil");
         result = "?";
      } else if (field == null) {
         Log.warning(Geonet.SEARCH_ENGINE, "Not a valid field in XslUtil");
         result = "?";
      } else {
         try {
            result = searchManager.getField((String) uuid, field);
         } catch (SearchException e) {
            Log.warning(Geonet.SEARCH_ENGINE, "Cannot retrieve value", e);
         }
      }
      return result;
   }
}
