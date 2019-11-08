//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet.kernel.search;

import java.util.List;

import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.services.util.SearchDefaults;
import org.jdom.Element;

/**
 * The Class MetaSearcher. <P>
 * Explanation goes here. <P>
 */
public abstract class MetaSearcher {
   /** The _from. */
   private int _from;

   /** The _to. */
   private int _to;

   /** The _valid. */
   private boolean _valid = false;

   //--------------------------------------------------------------------------------
   // MetaSearcher API

   /**
    * Search.
    *
    * @param srvContext the service context
    * @param request the request
    * @param config the config
    * @throws Exception the exception
    */
   public abstract void search(ServiceContext srvContext, Element request, ServiceConfig config)
         throws Exception;

   /**
    * Present.
    *
    * @param srvContext the service context
    * @param request the request
    * @param config the config
    * @return the element
    * @throws Exception the exception
    */
   public abstract Element present(ServiceContext srvContext, Element request, ServiceConfig config)
         throws Exception;

   /**
    * Gets the size.
    *
    * @return the size
    */
   public abstract int getSize();

   /**
    * Gets the summary.
    *
    * @return the summary
    * @throws Exception the exception
    */
   public abstract Element getSummary() throws Exception;

   /**
    * Close.
    */
   public abstract void close();

   /**
    * Initialize the search range.
    *
    * @param srvContext the service context
    */
   protected void initSearchRange(ServiceContext srvContext) {
      // get from and to default values
      _from = 1;
      try {
         UserSession session = null;
         if (srvContext != null) {
            session = srvContext.getUserSession();
         }
         Element defaultSearch = SearchDefaults.getDefaultSearch(session, null);
         _to = Integer.parseInt(defaultSearch.getChildText(Geonet.SearchResult.HITS_PER_PAGE));
      } catch (Exception e) {
         _to = 10;
      }
   }

   /**
    * Update search range.
    *
    * @param request the request
    */
   protected void updateSearchRange(Element request) {
      // get request parameters
      String sFrom = request.getChildText("from");
      String sTo = request.getChildText("to");
      if (sFrom != null) {
         try {
            _from = Integer.parseInt(sFrom);
         } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Bad 'from' parameter: " + sFrom);
         }
      }
      if (sTo != null) {
         try {
            _to = Integer.parseInt(sTo);
         } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Bad 'to' parameter: " + sTo);
         }
      }

      int count = getSize();
      _from = _from > count ? count : _from;
      _to = _to > count ? count : _to;
   }

   /**
    * Gets the from.
    *
    * @return the from
    */
   protected int getFrom() {
      return _from;
   }

   /**
    * Gets the to.
    *
    * @return the to
    */
   protected int getTo() {
      return _to;
   }

   /**
    * Checks if is valid.
    *
    * @return true, if is valid
    */
   protected boolean isValid() {
      return _valid;
   }

   /**
    * Gets the element.
    *
    * @param srvContext the service context
    * @param request the request
    * @param config the config
    * @return the element
    * @throws Exception the exception
    */
   public Element get(ServiceContext srvContext, Element request, ServiceConfig config)
         throws Exception {
      String id = Util.getParam(request, Params.ID);

      // save _from and _to
      int from = _from;
      int to = _to;

      // perform search
      Element req = new Element("request");
      addElement(req, "from", id);
      addElement(req, "to", id);
      Element result = present(srvContext, req, config);

      // restore _from and _to
      _from = from;
      _to = to;

      // skip summary
      for (Object o : result.getChildren()) {
         Element child = (Element) o;

         if (!child.getName().equals(Geonet.Elem.SUMMARY)) {
            return child;
         }
      }
      return null;
   }

   /**
    * Adds the element.
    *
    * @param root the root
    * @param name the name
    * @param value the value
    */
   protected static void addElement(Element root, String name, String value) {
      root.addContent(new Element(name).setText(value));
   }

   /**
    * Gets the all uuids.
    *
    * @param maxhits the maxhits
    * @return the all uuids
    * @throws SearchException the search exception
    */
   public abstract List<String> getAllUuids(int maxhits) throws Exception;
}
