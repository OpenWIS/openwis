/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.exec;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;

import org.openwis.metadataportal.kernel.harvest.AbstractHarvester;
import org.openwis.metadataportal.kernel.harvest.csw.CSWHarvester;
import org.openwis.metadataportal.kernel.harvest.filesystem.LocalFileSystemHarvester;
import org.openwis.metadataportal.kernel.harvest.geonet20.Geonet20Harvester;
import org.openwis.metadataportal.kernel.harvest.oaipmh.OaipmhHarvester;
import org.openwis.metadataportal.kernel.harvest.webdav.WebDavHarvester;

/**
 * Creates the harvester corresponding to the given type. <P>
 * Explanation goes here. <P>
 * 
 */
public final class HarvesterFactory {
    
    public static AbstractHarvester createHarvester(String type, ServiceContext context, Dbms dbms) {
        if(type.equals(OaipmhHarvester.getType())) {
            return new OaipmhHarvester(context, dbms);
        } else if(type.equals(LocalFileSystemHarvester.getType())) {
           return new LocalFileSystemHarvester(context, dbms);
        } else if(type.equals(Geonet20Harvester.getType())) {
           return new Geonet20Harvester(context, dbms);
        } else if(type.equals(CSWHarvester.getType())) {
           return new CSWHarvester(context, dbms);
        } else if(type.equals(WebDavHarvester.getType())) {
           return new WebDavHarvester(context, dbms);
        }
        throw new UnsupportedOperationException();
    }

}
