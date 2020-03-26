package org.openwis.metadataportal.kernel.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * Last login filter for testing.
 * The threshold is set to 1 minute
 */
public class TestLastLoginFilter extends LastLoginFilter{

    public TestLastLoginFilter(Long duration, TimeUnit timeUnit) {
        super(duration, timeUnit);
    }

    public TestLastLoginFilter() {
        super(1L, TimeUnit.MINUTES);
    }
}
