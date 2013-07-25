package org.fao.geonet.kernel.search;

/**
 * The listener interface for receiving index events.
 */
public interface IndexListener {

   /**
    * On index event.
    *
    * @param event the event
    */
   void onIndexEvent(IndexEvent event);

}
