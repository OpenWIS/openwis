package org.openwis.metadataportal.kernel.search;

import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.search.index.IIndexManager;
import org.openwis.metadataportal.kernel.search.index.IndexException;

/**
 * The Class OptimizeTask. <P>
 * Explanation goes here. <P>
 */
public class OptimizeTask implements Runnable {

   /** The index manager. */
   private final IIndexManager indexManager;

   /**
    * Instantiates a new optimize task.
    *
    * @param indexManager the index manager
    */
   public OptimizeTask(IIndexManager indexManager) {
      super();
      this.indexManager = indexManager;
   }

   /**
    * {@inheritDoc}
    * @see java.util.TimerTask#run()
    */
   @Override
   public void run() {
      try {
         Log.debug(Geonet.INDEX_ENGINE, "Optimize index task started");
         indexManager.optimize();
      } catch (IndexException e) {
         Log.error(Geonet.INDEX_ENGINE, "Optimize task failed to optimize the index", e);
      }
   }
}