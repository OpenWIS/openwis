package org.openwis.usermanagement.util;

import java.io.UnsupportedEncodingException;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPJSSEStartTLSFactory;
import org.openwis.usermanagement.UserManagementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pool for LDAP Connection.
 * <p>
 * Create and share only one LDAP connection. 
 * As stated in LDAPConnection doc, multiple threads may share the same LDAPConnection.
 * </p>
 */
public class LdapConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementServiceImpl.class);
    /**
     * @member: utf8 8-bit Unicode Transformation Format
     */
    private static final String UTF8 = "UTF8";

    /** Single shared ldap connection */
    private static LDAPConnection ldapConnection;

    /**
     * Get or create an ldap connection.
     * @return an ldap connection
     * @throws LDAPException
     * @throws UnsupportedEncodingException
     */
    public static LDAPConnection getLDAPConnection() throws LDAPException,
            UnsupportedEncodingException {

        if (ldapConnection == null || !ldapConnection.isConnected()
                || !ldapConnection.isConnectionAlive()) {

            if (JNDIUtils.getInstance().isLdapSSL()) {
                ldapConnection = new LDAPConnection(new LDAPJSSESecureSocketFactory());
            } else {
                ldapConnection = new LDAPConnection();
            }

            // connect to the server
            ldapConnection.connect(JNDIUtils.getInstance().getLdapHost(), JNDIUtils
                    .getInstance().getLdapPort());

            if (ldapConnection.isTLS()) {
                logger.info("Secured connection with LDAP.");
            } else {
                logger.warn("LDAP connection is not TLS");
            }
            // authenticate to the server
            ldapConnection.bind(LDAPConnection.LDAP_V3, JNDIUtils
                    .getInstance().getLdapUser(), JNDIUtils.getInstance().getLdapPassword()
                    .getBytes(UTF8));
        }

        return ldapConnection;
    }

}
