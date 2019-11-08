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

import java.util.Map;
import java.util.Observer;

import jeeves.utils.Log;

import org.fao.geonet.ContextContainer;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.services.util.z3950.GNXMLQuery;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.iface.Searchable;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

//import org.fao.geonet.services.util.z3950.GNSearchTask;

/**
 * The Class GNSearchable.
 *
 * @author 'Timo Proescholdt <tproescholdt@wmo.int>'
 * interface between JZkit and GN. not currently used
 */
@SuppressWarnings("rawtypes")
public class GNSearchable implements Searchable {

   /** The record archetypes. */
   private Map recordArchetypes;

   /** The timeout. */
   private int timeout;

   /** The ctx. */
   private ApplicationContext ctx;

   /**
    * Instantiates a new gN searchable.
    */
   public GNSearchable() {
      Log.debug(Geonet.Z3950_SERVER, "creating GNSearchable");
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.provider.iface.Searchable#close()
    */
   @Override
   public void close() {

   }

   /**
    * Sets the timeout.
    *
    * @param i the new timeout
    */
   public void setTimeout(int i) {
      timeout = i;
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.provider.iface.Searchable#evaluate(org.jzkit.search.provider.iface.IRQuery)
    */
   @Override
   public IRResultSet evaluate(IRQuery q) {

      return this.evaluate(q, null, null);

   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.provider.iface.Searchable#evaluate(org.jzkit.search.provider.iface.IRQuery, java.lang.Object)
    */
   @Override
   public IRResultSet evaluate(IRQuery q, Object userInfo) {
      return this.evaluate(q, userInfo, null);
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.provider.iface.Searchable#evaluate(org.jzkit.search.provider.iface.IRQuery, java.lang.Object, java.util.Observer[])
    */
   @Override
   public IRResultSet evaluate(IRQuery q, Object userInfo, Observer[] observers) {

      Log.debug(Geonet.Z3950_SERVER, "evaluating...");

      ContextContainer cnt = (ContextContainer) ctx.getBean("ContextGateway");

      GNResultSet result = null;

      try {
         result = new GNResultSet(new GNXMLQuery(q, ctx), userInfo, observers, cnt.getSrvctx()); //       SRUResultSet(observers, base_url, getCQLString(q), code);
         result.evaluate(timeout);
         result.setStatus(IRResultSetStatus.COMPLETE);
      } catch (Exception e) {
         if (result != null) {
            result.setStatus(IRResultSetStatus.FAILURE);
         }
         Log.error(Geonet.Z3950_SERVER, "Fail", e);
      }

      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.provider.iface.Searchable#getRecordArchetypes()
    */
   @Override
   public Map getRecordArchetypes() {
      return recordArchetypes;
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.provider.iface.Searchable#setRecordArchetypes(java.util.Map)
    */
   @Override
   public void setRecordArchetypes(Map recordSyntaxArchetypes) {
      recordArchetypes = recordSyntaxArchetypes;

   }

   /**
    * {@inheritDoc}
    * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
    */
   @Override
   public void setApplicationContext(ApplicationContext ctx) throws BeansException {
      this.ctx = ctx;

   }

}
