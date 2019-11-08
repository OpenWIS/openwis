/**
 * 
 */
package org.openwis.datasource.server.dao.subscription;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.openwis.dataservice.common.domain.dao.subscription.SubscriptionDao;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.datasource.server.dao.JpaDao;

/**
 * Implementation of the Subscription DAO.
 */
@Local(SubscriptionDao.class)
@Stateless(name = "SubscriptionDao")
public class JpaSubscriptionDao extends JpaDao<Long, Subscription> implements SubscriptionDao {
   //
}
