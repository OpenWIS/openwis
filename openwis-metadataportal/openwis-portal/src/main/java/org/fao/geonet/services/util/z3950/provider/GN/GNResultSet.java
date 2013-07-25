//=============================================================================
//===  Copyright (C) 2009 World Meteorological Organization
//===  This program is free software; you can redistribute it and/or modify
//===  it under the terms of the GNU General Public License as published by
//===  the Free Software Foundation; either version 2 of the License, or (at
//===  your option) any later version.
//===
//===  This program is distributed in the hope that it will be useful, but
//===  WITHOUT ANY WARRANTY; without even the implied warranty of
//===  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===  General Public License for more details.
//===
//===  You should have received a copy of the GNU General Public License
//===  along with this program; if not, write to the Free Software
//===  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===  Contact: Timo Proescholdt
//===  email: tproescholdt_at_wmo.int
//==============================================================================

package org.fao.geonet.services.util.z3950.provider.GN;

import java.text.MessageFormat;
import java.util.List;
import java.util.Observer;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.SearchManagerFactory;
import org.fao.geonet.services.util.z3950.GNXMLQuery;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.DOMOutputter;
import org.jzkit.search.util.RecordModel.ExplicitRecordFormatSpecification;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.RecordModel.InformationFragmentImpl;
import org.jzkit.search.util.RecordModel.RecordFormatSpecification;
import org.jzkit.search.util.ResultSet.AbstractIRResultSet;
import org.jzkit.search.util.ResultSet.IFSNotificationTarget;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetException;
import org.jzkit.search.util.ResultSet.IRResultSetInfo;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.openwis.metadataportal.kernel.search.query.IQueryManager;
import org.openwis.metadataportal.kernel.search.query.SearchQuery;
import org.openwis.metadataportal.kernel.search.query.SearchQueryManagerFactory;
import org.openwis.metadataportal.kernel.search.query.SearchResult;

/**
 * interface between JZKit and GN. Retrieves XML content from the GN backend and
 * makes it available to JZkit
 * @author 'Timo Proescholdt <tproescholdt@wmo.int>'
 *
 */
public class GNResultSet<T extends SearchQuery> extends AbstractIRResultSet implements IRResultSet {

   /** The query. */
   private final GNXMLQuery query;

   /** The context. */
   private final ServiceContext context;

   /** The status. */
   private int status;

   /** The fragment count. */
   private int fragmentCount;

   /** The query manager factory. */
   private SearchQueryManagerFactory<T> queryManagerFactory;

   /** The search result. */
   private SearchResult searchResult;
   
   /** Search query sent to Solr */
   private T searchQuery;
   
   /** Query manager */
   private IQueryManager<T> qm;

   /**
    * Instantiates a new gN result set.
    *
    * @param query the query
    * @param userInfo the user info
    * @param observers the observers
    * @param context the srvctx
    * @throws Exception the exception
    */
   @SuppressWarnings("unchecked")
   public GNResultSet(GNXMLQuery query, Object userInfo, Observer[] observers,
         ServiceContext context) throws Exception {
      super(observers);
      this.query = query;
      this.context = context;

      try {
         GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
         ServiceConfig config = gc.getHandlerConfig();
         String appPath = context.getAppPath();
         queryManagerFactory = SearchManagerFactory.getQueryManagerFactory(config, appPath);
      } catch (Exception e) {
         Log.error(Geonet.Z3950_SERVER, "error constructing GNresult set ", e);
      }
   }

   /**
    * Evaluate.
    *
    * @param timeout the timeout
    * @return the status
    */
   public int evaluate(int timeout) {
      try {
         Log.info(Geonet.Z3950_SERVER, "INCOMING XML QUERY:\n" + query);
         // FIXME Igor: check categories restriction
         //         List<String> categories = query.getCollections();
         //         for (String category : categories) {
         //            if (!category.equals("geonetwork") && !category.equals("Default")) {
         //               request.addContent(new Element("category").setText(category));
         //            }
         //         }

         //Build Query
         this.qm = queryManagerFactory.buildIQueryManager();
         GNRemoteQueryDecoder<T> queryBuilder = new GNRemoteQueryDecoder<T>(qm,
               query.getQuerymodel(), query.getCtx());
         this.searchQuery = queryBuilder.getQuery();

         // consider only a short range of 1 record for the initial query (for the count)
         // will get the actual records in getFragment call
         this.searchQuery.setRange(0, 0);

         // search
         Log.info(Geonet.Z3950_SERVER, "Searching with query: " + searchQuery);
         this.searchResult = qm.search(searchQuery);

         // Random number of records.. Set up the result set
         setFragmentCount(this.searchResult.getCount());
         setTaskStatusCode(IRResultSetStatus.COMPLETE);

         context.getResourceManager().close();
      } catch (Throwable e) {
         Log.error(Geonet.Z3950_SERVER, "error evaluating query. Try to abort", e);
         try {
            context.getResourceManager().abort();
         } catch (Exception e2) {
            Log.error(Geonet.Z3950_SERVER, "Aborting Fail", e2);
         }
      }
      return (getStatus());
   }

   /**
    * Gets the fragment.
    *
    * @param startingFragment the starting fragment
    * @param count the count
    * @param spec the spec
    * @return the fragment
    * @throws IRResultSetException the iR result set exception
    * {@inheritDoc}
    * @see org.jzkit.search.util.ResultSet.IRResultSet#getFragment(int, int, org.jzkit.search.util.RecordModel.RecordFormatSpecification)
    */
   @SuppressWarnings("unchecked")
   @Override
   public InformationFragment[] getFragment(int startingFragment, int count,
         RecordFormatSpecification spec) throws IRResultSetException {
      Log.debug(Geonet.Z3950_SERVER, MessageFormat.format(
            "Request for fragment start:{0}, count:{1}", startingFragment, count));

      InformationFragment fragment[] = new InformationFragment[count];
      ExplicitRecordFormatSpecification recSpec = new ExplicitRecordFormatSpecification("xml",
            null, "f");

      try {
         // build fragment data
         // Ranges in SolR are 0-based as opposed to SRU search
         int from = startingFragment - 1;
         int to = from + count - 1;
         
         // adapt query with new range
         this.searchQuery.setRange(from, to);

         // search
         Log.info(Geonet.Z3950_SERVER, "Searching with query: " + searchQuery + ", from: " + from
               + " to: " + to);
         this.searchResult = this.qm.search(searchQuery);
         
         // get result set
         Element result = this.searchResult.toPresent();
         if (Log.isDebug(Geonet.Z3950_SERVER)) {
            Log.debug(Geonet.Z3950_SERVER, "Search result:\n" + Xml.getString(result));
         }

         // remove summary
         result.removeChildren("summary");
         List<Element> list = result.getChildren();

         if (Log.isDebug(Geonet.Z3950_SERVER)) {
            Log.debug(Geonet.Z3950_SERVER, "Set name asked:" + spec);
         }

         // save other records to fragment
         for (int i = 0; i < count; i++) {
            Element md = list.get(0);
            md.detach();

            if (Log.isDebug(Geonet.Z3950_SERVER)) {
               Log.debug(Geonet.Z3950_SERVER, "Returning fragment:\n" + Xml.getString(md));
            }

            // add metadata
            //fragment[i] = new DOMTree("geonetwork", "geonetwork", null,   getRecord(md),rec_spec );
            //fragment[i].setHitNo(startingFragment+i);

            DOMOutputter outputter = new DOMOutputter();
            Document doc = new Document(md);
            org.w3c.dom.Document doc2 = outputter.output(doc);

            fragment[i] = new InformationFragmentImpl(startingFragment + i, "geonetwork",
                  "geonetwork", null, doc2, recSpec);
            //fragment[i] = new InformationFragmentImpl(startingFragment+i,"geonetwork","geonetwork",null,doc,rec_spec);

         }
         context.getResourceManager().close();
         Log.debug(Geonet.Z3950_SERVER, "Fragment returned");
      } catch (Throwable e) {
         Log.error(Geonet.Z3950_SERVER, "Fail, try to abort", e);
         try {
            context.getResourceManager().abort();
         } catch (Exception e2) {
            e2.printStackTrace();
            Log.error(Geonet.Z3950_SERVER, "Aborting fail", e2);
         }
      }

      return fragment;
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.util.ResultSet.AbstractIRResultSet#asyncGetFragment(int, int, org.jzkit.search.util.RecordModel.RecordFormatSpecification, org.jzkit.search.util.ResultSet.IFSNotificationTarget)
    */
   @Override
   public void asyncGetFragment(int starting_fragment, int count, RecordFormatSpecification spec,
         IFSNotificationTarget target) throws IRResultSetException {
      InformationFragment[] result = getFragment(starting_fragment, count, spec);
      target.notifyRecords(result);
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.util.ResultSet.AbstractIRResultSet#close()
    */
   @Override
   public void close() {
      // clean
      this.searchResult = null;
   }

   /**
    * Sets the task status code.
    *
    * @param i the new task status code
    */
   private void setTaskStatusCode(int i) {
      status = i;
   }

   /**
    * Sets the fragment count.
    *
    * @param i the new fragment count
    */
   private void setFragmentCount(int i) {
      fragmentCount = i;
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.util.ResultSet.AbstractIRResultSet#getFragmentCount()
    */
   @Override
   public int getFragmentCount() {
      return fragmentCount;
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.util.ResultSet.AbstractIRResultSet#getRecordAvailableHWM()
    */
   @Override
   public int getRecordAvailableHWM() {
      return getFragmentCount();
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.util.ResultSet.AbstractIRResultSet#getResultSetInfo()
    */
   @Override
   public IRResultSetInfo getResultSetInfo() {

      return new IRResultSetInfo("GNDefault", fragmentCount, status);

   }

}
