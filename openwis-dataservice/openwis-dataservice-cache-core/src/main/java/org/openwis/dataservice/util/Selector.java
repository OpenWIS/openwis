/**
 *
 */
package org.openwis.dataservice.util;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Encapsulates the attributes for the directory scanner matching task.
 * <p>
 * The {@code Selector} entity defines properties to serialise the matching criteria for the
 * collection of GTS products.
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public class Selector implements Cloneable, Serializable {

   // -------------------------------------------------------------------------
   // Bound Properties
   // -------------------------------------------------------------------------

   // Resource Location
   private String pathname;

   // Filter settings
   private boolean caseSensitive;
   private List<String> includePatterns;
   private List<String> excludePatterns;

   // Scheduling options
   public static final long DEFAULT_PERIOD = 20;
   public static final long DEFAULT_INITIAL_DELAY = 5;
   public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

   // -------------------------------------------------------------------------
   // Instance Creation
   // -------------------------------------------------------------------------

   /**
    * Creates a new Selector initialised with the default attributes.
    */
   public Selector() {
      // init: set defaults
      caseSensitive = Boolean.TRUE;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#clone()
    */
   @Override
   public Object clone() {
      Selector selector = null;
      try {
         selector = (Selector) super.clone();
      }
      catch (Exception e) {
         // this should not happen
         selector = new Selector();
         selector.setPathname(getPathname());

         selector.setCaseSensitive(isCaseSensitive());
         selector.setIncludePatterns(getIncludePatterns());
         selector.setExcludePatterns(getExcludePatterns());
      }
      return selector;
   }

   // -------------------------------------------------------------------------
   // Resource Location
   // -------------------------------------------------------------------------

   /**
    * Gets the pathname.
    *
    * @return the pathname.
    */
   public String getPathname() {
      return pathname;
   }

   /**
    * Sets the pathname.
    *
    * @param pathname the pathname to set.
    */
   public void setPathname(final String pathname) {
      this.pathname = pathname;
   }

   // -------------------------------------------------------------------------
   // Matching Criteria
   // -------------------------------------------------------------------------

   /**
    * Specifies whether include exclude patterns are matched in a case sensitive way.
    *
    * @return whether or not the scanning is case sensitive.
    */
   public boolean isCaseSensitive() {
      return caseSensitive;
   }

   /**
    * Set whether or not include and exclude patterns are matched in a case sensitive way.
    *
    * @param sensitive whether or not the file system should be regarded as a case sensitive one.
    */
   public void setCaseSensitive(final boolean sensitive) {
      caseSensitive = sensitive;
   }

   /**
    * Gets the includePatterns.
    *
    * @return the includePatterns.
    */
   public List<String> getIncludePatterns() {
      return includePatterns;
   }

   /**
    * Sets the includePatterns.
    *
    * @param includePatterns the includePatterns to set.
    */
   public void setIncludePatterns(final List<String> includePatterns) {
      this.includePatterns = includePatterns;
   }

   /**
    * Gets the excludePatterns.
    *
    * @return the excludePatterns.
    */
   public List<String> getExcludePatterns() {
      return excludePatterns;
   }

   /**
    * Sets the excludePatterns.
    *
    * @param excludePatterns the excludePatterns to set.
    */
   public void setExcludePatterns(final List<String> excludePatterns) {
      this.excludePatterns = excludePatterns;
   }

}
