//=============================================================================
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

package org.fao.geonet.kernel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;

//=============================================================================

/**
 * Handles the access to a metadata <P>
 */
public class AccessManager {

    /**
     * Comment for <code>dbms</code>
     */
    private Dbms dbms;
    
    /**
     * Default constructor.
     * Builds a AccessManager.
     * @param dbms
     */
    public AccessManager(Dbms dbms)  {
        super();
        this.dbms = dbms;
    }
    /**
     * Gets the dbms.
     * @return the dbms.
     */
    public Dbms getDbms() {
        return dbms;
    }

    /**
     * Sets the dbms.
     * @param dbms the dbms to set.
     */
    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

    /**
     * Returns all groups accessible by the user (a collection of Group)
     * @param dbms
     * @param us
     * @return
     * @throws SQLException
     */
    public Collection<Group> getUserGroups(UserSession us) throws SQLException {

        Collection<Group> groups = null;
        GroupManager gm = new GroupManager(getDbms());

        if (us.isAuthenticated()) {
            if (us.getProfile().equals(Geonet.Profile.ADMINISTRATOR)) {
                groups = gm.getAllGroups();
            } else {
                groups = gm.getAllUserGroups(us.getUserId());
            }
        } else {
            groups = new ArrayList<Group>();
            groups.add(new Group(-1, Geonet.Profile.GUEST));
        }
        return groups;

    }

    /** 
     * Returns true if, and only if, at least one of these conditions is
     * satisfied :
     * - The user is the metadata owner
     * - The user is an Administrator or Operator
     * - The user has edit rights over the metadata
     * 
     * @param context
     * @param id
     * @return
     * @throws Exception
     */
    public boolean canEdit(ServiceContext context, String id) throws Exception {
        return isOwner(context, id) || hasEditPermission(context, id);
    }

    /**
     * Description goes here.
     * @param context
     * @param id
     * @return
     * @throws Exception
     */
    public boolean isOwner(ServiceContext context, String id) throws Exception {

        UserSession us = context.getUserSession();

        if (!us.isAuthenticated()) {
            return false;
        }

        //--- retrieve metadata info

        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        DataManager dm = gc.getDataManager();

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        MdInfo info = dm.getMetadataInfo(dbms, id);

        //--- harvested metadata cannot be edited

        //		if (info == null || info.isHarvested)
        if (info == null)
            return false;

        //--- check if the user is an administrator

        // FIXME OPERATOR and ACCESS_ADMIN as owner or not??  
        // us.getProfile().equals(Geonet.Profile.OPERATOR)
        if (us.getProfile().equals(Geonet.Profile.ADMINISTRATOR))
            return true;

        //--- check if the user is the metadata owner
        //
        if (us.getUsername().equals(info.owner))
            return true;

        return false;
    }

    /**
     * Checks whether a user is an editor and has edit permissions.
     * @param context
     * @param id
     * @return
     * @throws Exception
     */
    private boolean hasEditPermission(ServiceContext context, String id) throws Exception {
        UserSession us = context.getUserSession();

        if (!us.isAuthenticated())
            return false;

        //--- check if the user is an editor and has edit rights over the metadata 
        //--- record 
        if (us.getProfile().equals(Geonet.Profile.EDITOR)) {
            Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
            IDataPolicyManager dpm = new DataPolicyManager(dbms);
            GroupManager gm = new GroupManager(dbms);
            List<Group> groups = gm.getAllUserGroups(us.getUserId());

            return CollectionUtils.exists(dpm.getAllOperationAllowedByMetadataId(id, groups),
                    new Predicate() {

                        @Override
                        public boolean evaluate(Object arg0) {
                            return ((Operation) arg0).getId().equals(OperationEnum.EDITING.getId());
                        }
                    });
        }

        return false;
    }
}

//=============================================================================

