package org.openwis.datasource.server.service.impl;

import java.util.Arrays;

import javax.ejb.EJB;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.bean.BlacklistInfoResult;
import org.openwis.dataservice.common.domain.entity.blacklist.BlacklistInfo;
import org.openwis.dataservice.common.domain.entity.enumeration.BlacklistStatus;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.service.BlacklistService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.openwis.management.JndiManagementServiceBeans;
import org.openwis.management.ManagementServiceBeans;

@RunWith(Arquillian.class)
public class BlacklistServiceImplIntegrationTestCase extends ArquillianDBTestCase {

	/** The Constant USER_TEST. */
	private static final String USER_TEST = "USER_TEST";

	/** The Constant URN_TEST. */
	private static final String URN_TEST = "URN_TEST";

	/** The Constant DATA_POLICY_TEST. */
	private static final String DATA_POLICY_TEST = "dp-test";

	/** The CacheIndex service. */
	@EJB
	private CacheIndex cacheIndexService;

	/** The metada srv. */
	@EJB
	private ProductMetadataService metadaSrv;

	/** The blacklist service. */
	@EJB
	private BlacklistService blacklistService;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.openwis.datasource.server.ArquillianDBTestCase#getCachedIndex()
	 */
	@Override
	protected CacheIndex getCachedIndex() {
		return cacheIndexService;
	}

	/**
	 * Initialize the test.
	 * @throws NamingException 
	 */
	@Before
	public void init() throws NamingException {
//      ManagementServiceBeans.setInstance(new JndiManagementServiceBeans(JndiManagementServiceBeans.LOCAL_JNDI_PREFIX));
	   
		ProductMetadata productMetadata = metadaSrv
				.getProductMetadataByUrn(URN_TEST);
		if (productMetadata == null) {
			productMetadata = buildProductMetadata(URN_TEST, DATA_POLICY_TEST);
			metadaSrv.createProductMetadata(productMetadata);
		}

		// clear User staging post
		clearStagingPost(USER_TEST);
	}

	/**
	 * Test is user blacklisted.
	 */
	@Test
	public void testIsUserBlacklisted() {
		String user = "user-0";
		boolean isBlacklisted;

		BlacklistInfo bli;
		// Unknown user
		bli = blacklistService.getUserBlackListInfoIfExists(user);
		Assert.assertNotNull(bli);
		isBlacklisted = blacklistService.isUserBlacklisted(user);
		Assert.assertFalse(isBlacklisted);

		// set status
		bli = new BlacklistInfo();
		bli.setUser(user);

		// Check not blacklisted status
		for (BlacklistStatus status : Arrays.asList(
				BlacklistStatus.NOT_BLACKLISTED,
				BlacklistStatus.NOT_BLACKLISTED_BY_ADMIN)) {
			bli.setStatus(status);
			blacklistService.updateUserBlackListInfo(bli);

			isBlacklisted = blacklistService.isUserBlacklisted(user);
			Assert.assertFalse(isBlacklisted);
		}

		// Check blacklisted status
		for (BlacklistStatus status : Arrays.asList(
				BlacklistStatus.BLACKLISTED_BY_ADMIN,
				BlacklistStatus.BLACKLISTED_BY_NUMBER_OF_DISSEMINATIONS,
				BlacklistStatus.BLACKLISTED_BY_VOLUME_OF_DISSEMINATIONS)) {
			bli.setStatus(status);
			blacklistService.updateUserBlackListInfo(bli);

			isBlacklisted = blacklistService.isUserBlacklisted(user);
			Assert.assertTrue(isBlacklisted);
		}
	}

	/**
	 * Test set user blacklisted.
	 */
	@Test
	public void testSetUserBlacklisted() {
		String user = "user-1";
		boolean isBlacklisted;

		BlacklistInfo bli;
		// Unknown user
		bli = blacklistService.getUserBlackListInfoIfExists(user);
		Assert.assertNotNull(bli);
		isBlacklisted = blacklistService.isUserBlacklisted(user);
		Assert.assertFalse(isBlacklisted);

		// Blacklist user
		blacklistService.setUserBlacklisted(user, true);
		Assert.assertNotNull(bli);
		isBlacklisted = blacklistService.isUserBlacklisted(user);
		Assert.assertTrue(isBlacklisted);

		// undo Blacklist user
		blacklistService.setUserBlacklisted(user, false);
		Assert.assertNotNull(bli);
		isBlacklisted = blacklistService.isUserBlacklisted(user);
		Assert.assertFalse(isBlacklisted);
	}

	/**
	 * Test update user blacklist info.
	 */
	@Test
	public void testUpdateUserBlacklistInfo() {
		String user = "user-2";

		BlacklistInfo bli;
		// Unknown user
		bli = blacklistService.getUserBlackListInfoIfExists(user);
		Assert.assertNotNull(bli);

		BlacklistStatus status = BlacklistStatus.BLACKLISTED_BY_VOLUME_OF_DISSEMINATIONS;
		int nbDisseminationWarnThreshold = 7;
		int nbDisseminationBlacklistThreshold = 10;
		int volDisseminationWarnThreshold = 100;
		int volDisseminationBlacklistThreshold = 120;

		// update
		bli = new BlacklistInfo();
		bli.setUser(user);
		bli.setStatus(status);
		bli.setNbDisseminationWarnThreshold(nbDisseminationWarnThreshold);
		bli.setNbDisseminationBlacklistThreshold(nbDisseminationBlacklistThreshold);
		bli.setVolDisseminationWarnThreshold(volDisseminationWarnThreshold);
		bli.setVolDisseminationBlacklistThreshold(volDisseminationBlacklistThreshold);
		blacklistService.updateUserBlackListInfo(bli);

		// Check
		bli = blacklistService.getUserBlackListInfoIfExists(user);
		Assert.assertEquals(status, bli.getStatus());
		Assert.assertEquals(nbDisseminationWarnThreshold,
				bli.getNbDisseminationWarnThreshold());
		Assert.assertEquals(nbDisseminationBlacklistThreshold,
				bli.getNbDisseminationBlacklistThreshold());
		Assert.assertEquals(volDisseminationWarnThreshold,
				bli.getVolDisseminationWarnThreshold());
		Assert.assertEquals(volDisseminationBlacklistThreshold,
				bli.getVolDisseminationBlacklistThreshold());
	}

	/**
	 * Test get user blacklist info.
	 */
	@Test
	public void testGetUserBlacklistInfo() {
		String user = "user-3";

		BlacklistInfo bli;
		// Unknown user
		bli = blacklistService.getUserBlackListInfoIfExists(user);
		Assert.assertNotNull(bli);

		BlacklistStatus status = BlacklistStatus.BLACKLISTED_BY_VOLUME_OF_DISSEMINATIONS;
		int nbDisseminationWarnThreshold = 7;
		int nbDisseminationBlacklistThreshold = 10;
		int volDisseminationWarnThreshold = 100;
		int volDisseminationBlacklistThreshold = 120;

		// update
		bli = new BlacklistInfo();
		bli.setUser(user);
		bli.setStatus(status);
		bli.setNbDisseminationWarnThreshold(nbDisseminationWarnThreshold);
		bli.setNbDisseminationBlacklistThreshold(nbDisseminationBlacklistThreshold);
		bli.setVolDisseminationWarnThreshold(volDisseminationWarnThreshold);
		bli.setVolDisseminationBlacklistThreshold(volDisseminationBlacklistThreshold);
		blacklistService.updateUserBlackListInfo(bli);

		// Check
		bli = blacklistService.getUserBlackListInfoIfExists(user);
		Assert.assertNotNull(bli);
	}

	/**
	 * Test get users blacklist info.
	 */
	@Test
	public void testGetUsersBlacklistInfo() {
		String[] users = { "user-4", "user-5", "aaa" };

		BlacklistInfo bli;
		for (String user : users) {
			// Unknown user
			bli = blacklistService.getUserBlackListInfoIfExists(user);
			Assert.assertNotNull(bli);

			BlacklistStatus status = BlacklistStatus.BLACKLISTED_BY_VOLUME_OF_DISSEMINATIONS;
			int nbDisseminationWarnThreshold = 7;
			int nbDisseminationBlacklistThreshold = 10;
			int volDisseminationWarnThreshold = 100;
			int volDisseminationBlacklistThreshold = 120;

			// update
			bli = new BlacklistInfo();
			bli.setUser(user);
			bli.setStatus(status);
			bli.setNbDisseminationWarnThreshold(nbDisseminationWarnThreshold);
			bli.setNbDisseminationBlacklistThreshold(nbDisseminationBlacklistThreshold);
			bli.setVolDisseminationWarnThreshold(volDisseminationWarnThreshold);
			bli.setVolDisseminationBlacklistThreshold(volDisseminationBlacklistThreshold);
			blacklistService.updateUserBlackListInfo(bli);
		}
		// Checks
		BlacklistInfoResult bls = blacklistService.getUsersBlackListInfo(0, 10,
				null, null);
		Assert.assertNotNull(bls);
		Assert.assertTrue(bls.getList().size() >= 3);
		Assert.assertTrue(bls.getCount() >= 3);

		bls = blacklistService.getUsersBlackListInfo(0, 1, null, null);
		Assert.assertNotNull(bls);
		Assert.assertEquals(1, bls.getList().size());
		Assert.assertTrue(bls.getCount() >= 3);

		bls = blacklistService.getUsersBlackListInfoByUser("aa", 0, 10, null,
				null);
		Assert.assertNotNull(bls);
		Assert.assertEquals(1, bls.getList().size());
		Assert.assertEquals(1, bls.getCount());
	}

	/**
	 * Test check and update disseminated data.
	 * Test commented because unable to get management service endpoint with arquilian
	 */
	/*@Test*/
	public void testCheckAndUpdateDisseminatedData() {
		String user = USER_TEST;
		BlacklistStatus status = BlacklistStatus.BLACKLISTED_BY_VOLUME_OF_DISSEMINATIONS;
		String email = "toto@plop.org";
		int nbFiles = Integer.MAX_VALUE;
		long totalSize = Long.MAX_VALUE;
		String date = DateTimeUtils.formatUTC(DateTimeUtils.getUTCTime());

		// Update blacklist info
		BlacklistInfo bli = new BlacklistInfo();
		bli.setUser(user);
		bli.setStatus(status);
		bli.setNbDisseminationWarnThreshold(1);
		bli.setNbDisseminationBlacklistThreshold(2);
		bli.setVolDisseminationWarnThreshold(1);
		bli.setVolDisseminationBlacklistThreshold(2);
		blacklistService.updateUserBlackListInfo(bli);

		// Check send email
		blacklistService.checkAndUpdateDisseminatedData(USER_TEST, email, date,
				nbFiles, totalSize);
	}

}
