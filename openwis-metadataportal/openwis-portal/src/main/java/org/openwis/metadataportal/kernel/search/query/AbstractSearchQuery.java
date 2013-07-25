package org.openwis.metadataportal.kernel.search.query;

import jeeves.server.context.ServiceContext;

/**
 * The Class AbstractSearchQuery. <P>
 * Explanation goes here. <P>
 */
public abstract class AbstractSearchQuery implements SearchQuery {

   /** The service context. */
   private final ServiceContext serviceContext;

   /** The from. */
   private int from;

   /** The to. */
   private int to;

   /** The hits per page. */
   private int hitsPerPage;

   /**
    * Instantiates a new abstract search query.
    */
   public AbstractSearchQuery(ServiceContext serviceContext) {
      super();
      this.serviceContext = serviceContext;
   }

   /**
    * Gets the service context.
    *
    * @return the service context
    */
   protected ServiceContext getServiceContext() {
      return serviceContext;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQuery#getFrom()
    */
   @Override
   public int getFrom() {
      return from;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQuery#getTo()
    */
   @Override
   public int getTo() {
      return to;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQuery#setRange(int, int)
    */
   @Override
   public void setRange(int from, int to) {
      this.from = from;
      this.to = to;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQuery#getHitsPerPage()
    */
   @Override
   public int getHitsPerPage() {
      return hitsPerPage;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQuery#setHitsPerPage(int)
    */
   @Override
   public void setHitsPerPage(int hitsPerPage) {
      this.hitsPerPage = hitsPerPage;
   }
}
