package org.openwis.usermanagement;

import static org.openwis.usermanagement.util.LdapUtils.EMAILS;
import static org.openwis.usermanagement.util.LdapUtils.FTPS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.model.user.OpenWISEmail;
import org.openwis.usermanagement.model.user.OpenWISFTP;
import org.openwis.usermanagement.util.OpenWISEmailUtils;
import org.openwis.usermanagement.util.OpenWISFTPUtils;
import org.openwis.usermanagement.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;

/**
 * Implements the Dissemination Parameters management interface.
 *  The Dissemination Parameters Component is used for managed user's dissemination parameters. <P>
 * -  create / add / update / get / remove email or ftp for dissemination parameters. <P>
 */
@WebService(endpointInterface = "org.openwis.usermanagement.DisseminationParametersService", targetNamespace = "http://securityservice.openwis.org/", portName = "DisseminationParametersServicePort", serviceName = "DisseminationParametersService")
public class DisseminationParametersServiceImpl implements DisseminationParametersService {

   /** The logger */
   private final Logger logger = LoggerFactory.getLogger(DisseminationParametersServiceImpl.class);

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.DisseminationParametersService#addOrUpdateEmailForDisseminationParameters
    * (java.lang.String, org.openwis.usermanagement.model.user.OpenWISEmail)
    */
   @Override
   public void addOrUpdateEmailForDisseminationParameters(
         @WebParam(name = "username") String username, @WebParam(name = "email") OpenWISEmail email)
         throws UserManagementException {
      logger.debug("addOrUpdateEmailForDisseminationParameters : " + username);
      String dn = UserUtils.getDn(username);
      List<OpenWISEmail> emails = getEmailForDisseminationParameters(username);
      if (emails.contains(email)) {
         int index = emails.indexOf(email);
         emails.set(index, email);
      } else {
         emails.add(email);
      }
      UtilEntry.replaceParamToEntry(OpenWISEmailUtils.convertToString(emails), dn, EMAILS);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.DisseminationParametersService#removeEmailForDisseminationParameters
    * (java.lang.String, org.openwis.usermanagement.model.user.OpenWISEmail)
    */
   @Override
   public void removeEmailForDisseminationParameters(@WebParam(name = "username") String username,
         @WebParam(name = "email") OpenWISEmail emailAddress) throws UserManagementException {
      logger.debug("removeEmailForDisseminationParameters : " + username);
      String dn = UserUtils.getDn(username);
      List<OpenWISEmail> emails = getEmailForDisseminationParameters(username);
      emails.remove(emailAddress);
      UtilEntry.replaceParamToEntry(OpenWISEmailUtils.convertToString(emails), dn, EMAILS);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.DisseminationParametersService#removeFTPForDisseminationParameters
    * (java.lang.String, org.openwis.usermanagement.model.user.OpenWISFTP)
    */
   @Override
   public void removeFTPForDisseminationParameters(@WebParam(name = "username") String username,
         @WebParam(name = "ftp") OpenWISFTP ftp) throws UserManagementException {
      logger.debug("removeFTPForDisseminationParameters : " + username);
      String dn = UserUtils.getDn(username);
      List<OpenWISFTP> ftps = getFTPForDisseminationParameters(username);
      ftps.remove(ftp);
      UtilEntry.replaceParamToEntry(OpenWISFTPUtils.convertToString(ftps), dn, FTPS);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#getEmailForDisseminationParameters(java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<OpenWISEmail> getEmailForDisseminationParameters(
         @WebParam(name = "username") String username) throws UserManagementException {
      logger.debug("getEmailForDisseminationParameters : " + username);
      String dn = UserUtils.getDn(username);
      int searchScope = LDAPConnection.SCOPE_BASE;

      String[] attrs = new String[] {EMAILS};

      LDAPEntry nextEntry = UtilEntry.searchEntry(dn, searchScope, null, attrs);

      List<OpenWISEmail> openWISEmails = new ArrayList<OpenWISEmail>();

      if (nextEntry != null) {
         LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();

         Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();

         while (allAttributes.hasNext()) {
            LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
            openWISEmails = OpenWISEmailUtils.convertToOpenWISEmails(attribute.getStringValue());
         }
      }

      return openWISEmails;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.DisseminationParametersService#getFTPForDisseminationParameters(java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<OpenWISFTP> getFTPForDisseminationParameters(
         @WebParam(name = "username") String username) throws UserManagementException {
      logger.debug("getFTPForDisseminationParameters : " + username);
      String dn = UserUtils.getDn(username);
      int searchScope = LDAPConnection.SCOPE_BASE;

      String[] attrs = new String[] {FTPS};

      LDAPEntry nextEntry = UtilEntry.searchEntry(dn, searchScope, null, attrs);
      List<OpenWISFTP> ftps = new ArrayList<OpenWISFTP>();

      if (nextEntry != null) {
         LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
         Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();

         while (allAttributes.hasNext()) {
            LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
            ftps = OpenWISFTPUtils.convertToOpenWISFTPs(attribute.getStringValue());
         }
      }

      return ftps;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.DisseminationParametersService#addOrUpdateFTPForDisseminationParameters
    * (java.lang.String, org.openwis.usermanagement.model.user.OpenWISFTP)
    */
   @Override
   public void addOrUpdateFTPForDisseminationParameters(
         @WebParam(name = "username") String username, @WebParam(name = "ftp") OpenWISFTP ftp)
         throws UserManagementException {
      logger.debug("addOrUpdateFTPForDisseminationParameters : " + username);
      String dn = UserUtils.getDn(username);
      List<OpenWISFTP> ftps = getFTPForDisseminationParameters(username);
      if (ftps.contains(ftp)) {
         int index = ftps.indexOf(ftp);
         ftps.set(index, ftp);
      } else {
         ftps.add(ftp);
      }
      UtilEntry.replaceParamToEntry(OpenWISFTPUtils.convertToString(ftps), dn, FTPS);
   }
}
