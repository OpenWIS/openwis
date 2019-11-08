package org.openwis.datasource.server.dao.adhoc;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.openwis.dataservice.common.domain.dao.adhoc.AdHocDao;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.datasource.server.dao.JpaDao;

/**
 * Implementation of the Request DAO.
 */
@Local(AdHocDao.class)
@Stateless(name = "AdHocDao")
public class JpaAdHocDao extends JpaDao<Long, AdHoc> implements AdHocDao {
   //
}
