package org.openwis.metadataportal.model.user;

import org.openwis.metadataportal.kernel.user.TwoFactorAuthenticationUtils;

/**
 * Created by cosmin on 24/07/19.
 */
public class TwoFactorAuthenticationKey {

    private final String key;

    public TwoFactorAuthenticationKey() {
        this.key = TwoFactorAuthenticationUtils.generateKey();
    }

    public String getKeyBase16() {
        return TwoFactorAuthenticationUtils.encodeBase16(this.key);
    }

    public String getKeyBase32() {
        return TwoFactorAuthenticationUtils.encodeBase32(this.key);
    }

    public String getKeyUri(String contactMail) {
        return TwoFactorAuthenticationUtils.getTOPTKeyUri(contactMail, this.getKeyBase32());
    }
}
