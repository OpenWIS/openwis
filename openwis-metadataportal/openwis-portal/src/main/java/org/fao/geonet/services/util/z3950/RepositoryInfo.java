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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jzkit.ServiceDirectory.CollectionDescriptionDBO;
import org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO;
import org.jzkit.configuration.api.Configuration;
import org.jzkit.configuration.api.ConfigurationException;
import org.springframework.context.ApplicationContext;

/**
 * helperclass to get a list of remote searchable collections from the
 * repositories in the JZkit configuration.
 *
 * @author 'Timo Proescholdt <tproescholdt@wmo.int>'
 * @author 'Simon Pigot'
 */
public class RepositoryInfo {

   /** The dn. */
   private final String dn;

   /** The name. */
   private final String name;

   /** The code. */
   private final String code;

   /** The classname. */
   private final String classname;

   /**
    * Instantiates a new repository info.
    *
    * @param dn the dn
    * @param name the name
    * @param code the code
    * @param classname the classname
    */
   private RepositoryInfo(String dn, String name, String code, String classname) {
      this.name = name;
      this.dn = dn;
      this.code = code;
      this.classname = classname;
   }

   /**
    * Gets the dn.
    *
    * @return the dn
    */
   public String getDn() {
      return dn;
   }

   /**
    * Gets the name.
    *
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the code.
    *
    * @return the code
    */
   public String getCode() {
      return code;
   }

   /**
    * Gets the class name.
    *
    * @return the class name
    */
   public String getClassName() {
      return classname;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return getName() + ":" + getDn() + ":" + getCode() + ":" + getClassName();
   }

   /**
    * returns the list of repositories that are configured in JZkit.
    *
    * @param srvContext the srv context
    * @return the repositories
    * @throws ConfigurationException the configuration exception
    */
   @SuppressWarnings("unchecked")
   public static Collection<RepositoryInfo> getRepositories(ServiceContext srvContext)
         throws ConfigurationException {

      GeonetContext gc = (GeonetContext) srvContext.getHandlerContext(Geonet.CONTEXT_NAME);
      ApplicationContext appContext = gc.getApplicationContext();

      Configuration conf = (Configuration) appContext.getBean("JZKitConfig");
      List<RepositoryInfo> ret = new ArrayList<RepositoryInfo>();
      Iterator<SearchServiceDescriptionDBO> it = conf.enumerateRepositories();

      while (it.hasNext()) {
         SearchServiceDescriptionDBO ssd = it.next();
         Collection<CollectionDescriptionDBO> col = ssd.getCollections();
         if (col.size() > 0) {
            Iterator<CollectionDescriptionDBO> colit = col.iterator();
            Log.debug(Geonet.Z3950, "Service " + ssd.getServiceName() + " has " + col.size()
                  + " collections " + colit.hasNext());
            while (colit.hasNext()) {
               CollectionDescriptionDBO oneCol = colit.next();
               Log.debug(Geonet.Z3950,
                     "Adding collection " + oneCol.getCode() + ":" + oneCol.getCollectionName()
                           + ":" + ssd.getCode());
               ret.add(new RepositoryInfo(oneCol.getCode(), oneCol.getCollectionName(), ssd
                     .getCode(), ssd.getClassName()));
            }
         }
      }

      return ret;
   }

}
