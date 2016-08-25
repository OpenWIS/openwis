package org.openwis.datasource.server;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openwis.datasource.server.mdb.delegate.ExtractionDelegateIntegrationTestCase;
import org.openwis.datasource.server.mdb.delegate.SubscriptionDelegateIntegrationTestCase;
import org.openwis.datasource.server.service.impl.BlacklistServiceImplIntegrationTestCase;
import org.openwis.datasource.server.service.impl.ExtractionTimerServiceImplIntegrationTestCase;
import org.openwis.datasource.server.service.impl.ProcessedRequestServiceImplIntegrationTestCase;
import org.openwis.datasource.server.service.impl.ProductMetadataServiceImplIntegrationTestCase;
import org.openwis.datasource.server.service.impl.RequestServiceImplIntegrationTestCase;
import org.openwis.datasource.server.service.impl.SubscriptionServiceImplIntegrationTestCase;
import org.openwis.datasource.server.service.impl.SubscriptionTimerServiceImplIntegrationTestCase;

/**
 * The Class TestAll.
 * <P>
 * Explanation goes here.
 * <P>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ SubscriptionDelegateIntegrationTestCase.class,
		ExtractionDelegateIntegrationTestCase.class, RequestServiceImplIntegrationTestCase.class,
		SubscriptionTimerServiceImplIntegrationTestCase.class,
		ExtractionTimerServiceImplIntegrationTestCase.class,
		BlacklistServiceImplIntegrationTestCase.class,
		ProcessedRequestServiceImplIntegrationTestCase.class,
		SubscriptionServiceImplIntegrationTestCase.class,
		ProductMetadataServiceImplIntegrationTestCase.class })
public final class TestAll {

	/**
	 * Instantiates a new test all.
	 */
	private TestAll() {
		super();
	}

	/**
	 * Launch the test.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		JUnitCore.runClasses(new Class[] { TestAll.class });
	}
}
