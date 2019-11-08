package org.openwis.metadataportal.kernel.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.openwis.metadataportal.kernel.search.index.IndexException;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;

/**
 * The Class IndexMetadataTask. <P>
 * Explanation goes here. <P>
 */
public class IndexMetadataTask extends TimerTask implements Runnable {

   /** The to index. */
   private final Set<IndexableElement> toIndex;

   /** The search manager. */
   private final ISearchManager sm;

   /**
    * Instantiates a new index metadata task.
    *
    * @param sm the search manager
    * @param context the context
    * @param toIndex the to index
    */
   public IndexMetadataTask(ISearchManager sm, Collection<IndexableElement> toIndex) {
      super();
      this.sm = sm;
      this.toIndex = new HashSet<IndexableElement>(toIndex);
   }

   /**
    * {@inheritDoc}
    * @see java.util.TimerTask#run()
    */
   @Override
   public void run() {
      try {
         if (Log.isInfo(Geonet.INDEX_ENGINE)) {
            Log.info(Geonet.INDEX_ENGINE, "Going to index some metadata: " + toIndex);
         }
         sm.index(toIndex);
      } catch (IndexException e) {
         Log.error(Geonet.INDEX_ENGINE, "Error in indexing elements", e);
      }
   }
}