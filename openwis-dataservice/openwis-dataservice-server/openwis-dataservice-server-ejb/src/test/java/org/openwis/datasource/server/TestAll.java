package org.openwis.datasource.server;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openwis.datasource.server.mdb.delegate.ExtractionDelegateTestCase;
import org.openwis.datasource.server.mdb.delegate.SubscriptionDelegateTestCase;
import org.openwis.datasource.server.service.impl.BlacklistServiceImplTestCase;
import org.openwis.datasource.server.service.impl.ExtractionTimerServiceImplTestCase;
import org.openwis.datasource.server.service.impl.ProcessedRequestServiceImplTestCase;
import org.openwis.datasource.server.service.impl.ProductMetadataServiceImplTestCase;
import org.openwis.datasource.server.service.impl.RequestServiceImplTestCase;
import org.openwis.datasource.server.service.impl.SubscriptionServiceImplTestCase;
import org.openwis.datasource.server.service.impl.SubscriptionTimerServiceImplTestCase;

/**
 * The Class TestAll.
 * <P>
 * Explanation goes here.
 * <P>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ SubscriptionDelegateTestCase.class,
		ExtractionDelegateTestCase.class, RequestServiceImplTestCase.class,
		SubscriptionTimerServiceImplTestCase.class,
		ExtractionTimerServiceImplTestCase.class,
		BlacklistServiceImplTestCase.class,
		ProcessedRequestServiceImplTestCase.class,
		SubscriptionServiceImplTestCase.class,
		ProductMetadataServiceImplTestCase.class })
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
