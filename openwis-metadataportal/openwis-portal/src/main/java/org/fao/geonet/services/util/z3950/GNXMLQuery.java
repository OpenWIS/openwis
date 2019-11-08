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

package org.fao.geonet.services.util.z3950;

import java.util.List;

import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.springframework.context.ApplicationContext;

/**
 * transforms a JZKit internal query into the GN XML query format.
 *
 * @author 'Timo Proescholdt <tproescholdt@wmo.int>'
 */
public class GNXMLQuery {

   /** The ctx. */
   private final ApplicationContext ctx;

   /** The querymodel. */
   private final QueryModel querymodel;

   /** The collections. */
   private final List<String> collections;

   /** The q. */
   private final IRQuery q;

   /**
    * Instantiates a new gNXML query.
    *
    * @param q the q
    * @param ctx the ctx
    */
   @SuppressWarnings("unchecked")
   public GNXMLQuery(IRQuery q, ApplicationContext ctx) {
      this.ctx = ctx;
      this.q = q;
      querymodel = q.getQueryModel();
      collections = q.getCollections();
   }

   /**
    * Gets the querymodel.
    *
    * @return the querymodel
    */
   public QueryModel getQuerymodel() {
      return querymodel;
   }

   /**
    * Gets the ctx.
    *
    * @return the ctx
    */
   public ApplicationContext getCtx() {
      return ctx;
   }

   /**
    * To string.
    *
    * @return the string
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return q.toString();
   }

   /**
    * Gets the collections.
    *
    * @return the collections
    */
   public List<String> getCollections() {
      return collections;
   }

}
