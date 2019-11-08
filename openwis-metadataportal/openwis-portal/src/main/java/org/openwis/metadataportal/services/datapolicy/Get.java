/**
 * 
 */
package org.openwis.metadataportal.services.datapolicy;

import java.util.ArrayList;
import java.util.Collection;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.datapolicy.DataPolicyGroupPrivileges;
import org.openwis.metadataportal.model.datapolicy.DataPolicyOperationsPerGroup;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.datapolicy.dto.DataPolicyGetDTO;

/**
 * A service that returns all informations on a data policy.
 * <P>
 * Explanation goes here.
 * <P>
 * 
 */
public class Get implements Service {

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        DataPolicy dataPolicy = JeevesJsonWrapper.read(params, DataPolicy.class);

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        //Get managers.
        GroupManager gm = new GroupManager(dbms);
        IDataPolicyManager dpm = new DataPolicyManager(dbms);

        //Get all groups and operations.
        Collection<Group> allGroups = gm.getAllGroups();
        Collection<Operation> allOperations = dpm.getAllOperations();

        //If opening an existing one, get it fully from database.
        if (StringUtils.isNotBlank(dataPolicy.getName())) {
            dataPolicy = dpm.getDataPolicyByName(dataPolicy.getName(), true, true);
        }

        //Populate with all group and all privileges (defaults to not authorized).
        populateGroupsAndPrivileges(dataPolicy, allGroups, allOperations);

        // -- The DTO Containing the operations and the permissions for each group.
        DataPolicyGetDTO dto = new DataPolicyGetDTO();
        dto.setDataPolicy(dataPolicy);
        dto.setOperations(allOperations);
        return JeevesJsonWrapper.send(dto);
    }

    /**
     * Populate the data policy with missing groups and operations.
     * @param dataPolicy the data policy.
     * @param allGroups all groups of portal.
     * @param allOperations all operations of portal.
     */
    @SuppressWarnings("unchecked")
    private static void populateGroupsAndPrivileges(DataPolicy dataPolicy,
            Collection<Group> allGroups, Collection<Operation> allOperations) {

        /* Process groups with some privileges defined. */
        for (DataPolicyOperationsPerGroup opPerGroup : dataPolicy.getDpOpPerGroup()) {
            Collection<Operation> operationNotAllowed = null;

            if (!opPerGroup.getPrivilegesPerOp().isEmpty()) {
                Collection<Operation> operationsAllowed = CollectionUtils.collect(
                        opPerGroup.getPrivilegesPerOp(), new Transformer() {

                            @Override
                            public Object transform(Object arg0) {
                                return ((DataPolicyGroupPrivileges) arg0)
                                        .getOperation();
                            }
                        });
                operationNotAllowed = CollectionUtils.subtract(allOperations, operationsAllowed);
            } else {
                operationNotAllowed = new ArrayList<Operation>(allOperations);
            }

            //Populate with not allowed operations and default authorization is "NO"
            for (Operation op : operationNotAllowed) {
                opPerGroup.getPrivilegesPerOp().add(
                        new DataPolicyGroupPrivileges(op, false));
            }
        }

        /* Process groups with no privileges defined. */

        //Get groups with no privileges defined.
        Collection<Group> groupsWithNoPrivileges = null;
        if (!dataPolicy.getDpOpPerGroup().isEmpty()) {
            Collection<Group> groupsWithPrivilegesAllowed = CollectionUtils.collect(
                    dataPolicy.getDpOpPerGroup(), new Transformer() {

                        @Override
                        public Object transform(Object arg0) {
                            return ((DataPolicyOperationsPerGroup) arg0).getGroup();
                        }
                    });
            groupsWithNoPrivileges = CollectionUtils.subtract(allGroups,
                    groupsWithPrivilegesAllowed);
        } else {
            groupsWithNoPrivileges = new ArrayList<Group>(allGroups);
        }

        //Populate with all operations and default authorization is "NO"
        for (Group group : groupsWithNoPrivileges) {
            DataPolicyOperationsPerGroup opPerGroup = new DataPolicyOperationsPerGroup();
            opPerGroup.setGroup(group);
            for (Operation operation : allOperations) {
                opPerGroup.getPrivilegesPerOp().add(
                        new DataPolicyGroupPrivileges(operation, false));
            }
            dataPolicy.getDpOpPerGroup().add(opPerGroup);
        }

    }
}
