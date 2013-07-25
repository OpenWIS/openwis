/**
 * 
 */
package org.openwis.harness.samples.fs.filter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The Class Availability Filter. <P>
 */
public class AfterDateFilter implements FileFilter {

   /** The filters. */
   private final Queue<FsFileFilter> allFilters;

   /**
    * Default constructor.
    * Builds a AvailabiltyFilter.
    *
    * @param timestamp the timestamp
    */
   public AfterDateFilter(Calendar timestamp) {
      super();
      // Create filters
      allFilters = new LinkedList<FsFileFilter>();
      allFilters.add(new AfterFieldFilter(timestamp, Calendar.YEAR));
      allFilters.add(new AfterMonthFilter(timestamp));
      allFilters.add(new AfterFieldFilter(timestamp, Calendar.DAY_OF_MONTH));
      allFilters.add(new AfterTimeFilter(timestamp));
   }

   /**
    * Accept.
    *
    * @param file the file
    * @return true, if successful
    * {@inheritDoc}
    * @see java.io.FileFilter#accept(java.io.File)
    */
   @Override
   public boolean accept(File file) {
      assert file != null;
      boolean result = false;
      // Accept file if the FS contains a product after the timestamp
      if (file.isDirectory()) {
         result = accept(file, new LinkedList<FsFileFilter>(allFilters));
      }
      return result;
   }

   /**
    * Accept.
    *
    * @param file the file
    * @param filters the filters
    * @return true, if successful
    */
   private boolean accept(File file, Queue<FsFileFilter> filters) {
      boolean result = false;
      if (filters.isEmpty()) {
         result = file.list().length > 0;
      } else {
         FsFileFilter filter = filters.remove();
         List<File> children = Arrays.asList(file.listFiles());
         for (File child : children) {
            // strict accept
            if (filter.acceptStrict(child)) {
               Queue<FsFileFilter> trueFilters = new LinkedList<FsFileFilter>();
               for (int i = 0; i < filters.size(); i++) {
                  trueFilters.add(TrueFileFilter.INSTANCE);
               }
               result = accept(child, trueFilters);
            } else if (filter.accept(child)) {
               result = accept(child, filters);
            }
            if (result) {
               break;
            }
         }
      }
      return result;
   }
}
