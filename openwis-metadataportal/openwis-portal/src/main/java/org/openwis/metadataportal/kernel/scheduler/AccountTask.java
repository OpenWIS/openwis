package org.openwis.metadataportal.kernel.scheduler;

/**
 * This task do some action on user accounts
 */
public class AccountTask implements Runnable {

    private final AccountAction action;

    public AccountTask(AccountAction action) {
        this.action = action;
    }
    @Override
    public void run() {
       this.action.doAction();
    }
}
