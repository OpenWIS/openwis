package org.openwis.usermanagement;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.util.LdapConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

/**
 * Utilities for add or remove Entry. <P>
 */
public final class UtilEntry {

   /**
    * The logger
    * @member: logger
    */
   private static final Logger LOGGER = LoggerFactory.getLogger(UtilEntry.class);

   /**
    * Default constructor.
    * Builds a UtilEntry.
    */
   private UtilEntry() {

   }

   /**
    * Add new entry
    * 
    * To Add an entry to the directory,
    * 
    * - Create the attributes of the entry and add them to an attribute set
    * 
    * - Specify the DN of the entry to be created
    * 
    * - Create an LDAPEntry object with the DN and the attribute set
    * 
    * - Call the LDAPConnection add method to add it to the directory
    * 
    * @param newEntry Entry to add.
    * @throws UserManagementException if an error occurs.
    */
   public static void addNewEntry(LDAPEntry newEntry) throws UserManagementException {

      try {
         LDAPConnection lc = createLDAPConnection();

         lc.add(newEntry);
      } catch (LDAPException e) {
         LOGGER.error("LDAP Exception : Error during add new entry", e);
         throw new UserManagementException();
      } catch (UnsupportedEncodingException e) {
         LOGGER.error("Unsupported EncodingException : Error during add new entry", e);
         throw new UserManagementException();
      }
   }

   /**
    * Remove entry.
    * @param deleteDN DN to delete.
    * @throws UserManagementException if an error occurs.
    */
   public static void deleteEntry(String deleteDN) throws UserManagementException {

      try {

         LDAPConnection lc = createLDAPConnection();

         // Deletes the entry from the directory
         lc.delete(deleteDN);
      } catch (LDAPException e) {
         LOGGER.error("LDAP Exception : Error during delete entry", e);
         throw new UserManagementException();
      } catch (UnsupportedEncodingException e) {
         LOGGER.error("Unsupported EncodingException : Error during delete entry", e);
         throw new UserManagementException();
      }
   }

   /**
    * Search entry.
    * @param entryDN The base distinguished name to search from.
    * @param searchScope The scope of the entries to search. The following are the valid options:
    *    - SCOPE_BASE - searches only the base DN
    *    - SCOPE_ONE - searches only entries under the base DN
    *    - SCOPE_SUB - searches the base DN and all entries within its subtree 
    * @param searchFilter Search filter specifying the search criteria
    * @param attrs Names of attributes to retrieve
    * @return result Search results.
    * @throws UserManagementException if an error occurs.
    */
   public static LDAPEntry searchEntry(String entryDN, int searchScope, String searchFilter,
         String[] attrs) throws UserManagementException {
      // connect to the server
      LDAPEntry ldapEntry = null;
      try {
         //typesOnly - false, returns the names and values for attributes found. 
         LDAPSearchResults searchResults = searchEntries(entryDN, searchScope, searchFilter, attrs);
         ldapEntry = searchResults.next();
      } catch (LDAPException e) {
         if (e.getResultCode() == LDAPException.NO_SUCH_OBJECT) {
            LOGGER.error("LDAP Exception : The object " + entryDN + "doesn't exist");
         } else {
            LOGGER.error("LDAP Exception : Error during searching entry", e);
            throw new UserManagementException();
         }
      }
      return ldapEntry;
   }

   /**
    * Search entry.
    * @param entryDN The base distinguished name to search from.
    * @param searchScope The scope of the entries to search. The following are the valid options:
    *    - SCOPE_BASE - searches only the base DN
    *    - SCOPE_ONE - searches only entries under the base DN
    *    - SCOPE_SUB - searches the base DN and all entries within its subtree 
    * @param searchFilter Search filter specifying the search criteria
    * @param attrs Names of attributes to retrieve
    * @return result Search results.
    * @throws UserManagementException if an error occurs.
    */
   public static LDAPSearchResults searchEntries(String entryDN, int searchScope,
         String searchFilter, String[] attrs) throws UserManagementException {
      // connect to the server

      try {
         LDAPConnection lc = createLDAPConnection();

         //typesOnly - false, returns the names and values for attributes found. 
         LDAPSearchResults searchResults = lc.search(entryDN, searchScope, searchFilter, attrs,
               false);
         return searchResults;

      } catch (LDAPException e) {
         LOGGER.error("LDAP Exception : Error during searching entries", e);
         throw new UserManagementException();
      } catch (UnsupportedEncodingException e) {
         LOGGER.error("Unsupported EncodingException : Error during during searching entries", e);
         throw new UserManagementException();
      }
   }
   
   /**
    * Search entry.
    * @param entryDN The base distinguished name to search from.
    * @param searchScope The scope of the entries to search. The following are the valid options:
    *    - SCOPE_BASE - searches only the base DN
    *    - SCOPE_ONE - searches only entries under the base DN
    *    - SCOPE_SUB - searches the base DN and all entries within its subtree 
    * @param searchFilter Search filter specifying the search criteria
    * @param attrs Names of attributes to retrieve
    * @param constraints LDAP constraints used for the search
    * @return result Search results.
    * @throws UserManagementException if an error occurs.
    */
   public static LDAPSearchResults searchEntries(String entryDN, int searchScope,
         String searchFilter, String[] attrs, LDAPSearchConstraints constraints) throws UserManagementException {
      // connect to the server

      try {
         LDAPConnection lc = createLDAPConnection();

         //typesOnly - false, returns the names and values for attributes found. 
         LDAPSearchResults searchResults = lc.search(entryDN, searchScope, searchFilter, attrs,
               false, constraints);
         return searchResults;

      } catch (LDAPException e) {
         LOGGER.error("LDAP Exception : Error during searching entries", e);
         throw new UserManagementException();
      } catch (UnsupportedEncodingException e) {
         LOGGER.error("Unsupported EncodingException : Error during during searching entries", e);
         throw new UserManagementException();
      }
   }

   /**
    * Add param of an entry.
    * @param dn The object dn which have a parameter to add.
    * @param valueToAdd The value to add.
    * @param member The param to add.
    * @throws UserManagementException if an error occurs
    */
   public static void addParamToEntry(String valueToAdd, String dn, String member)
         throws UserManagementException {
      UtilEntry utilEntry = new UtilEntry();
      LDAPAttribute ldapMember = new LDAPAttribute(member, valueToAdd);
      utilEntry.actionParamToEntry(dn, LDAPModification.ADD, ldapMember);
   }

   /**
    * Delete param of an entry.
    * @param dn The object dn which have a parameter to delete.
    * @param valueToDelete The value to delete.
    * @param member The param to delete.
    * @throws UserManagementException if an error occurs
    */
   public static void deleteParamToEntry(String valueToDelete, String dn, String member)
         throws UserManagementException {
      UtilEntry utilEntry = new UtilEntry();
      LDAPAttribute ldapMember = new LDAPAttribute(member, valueToDelete);
      utilEntry.actionParamToEntry(dn, LDAPModification.DELETE, ldapMember);
   }

   /**
    * Replace param of an entry
    * @param dn The object dn which have a parameter to replace.
    * @param valueToReplace The value to replace.
    * @param member The param to replace.
    * @throws UserManagementException if an error occurs
    */
   public static void replaceParamToEntry(String valueToReplace, String dn, String member)
         throws UserManagementException {
      UtilEntry utilEntry = new UtilEntry();
      LDAPAttribute ldapMember = new LDAPAttribute(member, valueToReplace);
      utilEntry.actionParamToEntry(dn, LDAPModification.REPLACE, ldapMember);
   }

   /**
    * Add/Replace/Remove param of an entry
    * @param dn The object dn to add/replace/remove.
    * @param ldapModificationType The type of modification : add/replace/delete
    * @param member The param to add/update/remove
    * @throws UserManagementException if an error occurs
    */
   private void actionParamToEntry(String dn, int ldapModificationType, LDAPAttribute member)
         throws UserManagementException {
      try {

         LDAPConnection lc = createLDAPConnection();

         // modifications for group and user
         LDAPModification[] modGroup = new LDAPModification[1];

         // Add modifications to modGroup
         modGroup[0] = new LDAPModification(ldapModificationType, member);

         // Modify the group's attributes
         lc.modify(dn, modGroup);
      } catch (LDAPException e) {
         LOGGER.error("LDAP Exception : Error during action Param To Entry", e);
         throw new UserManagementException();
      } catch (UnsupportedEncodingException e) {
         LOGGER.error("Unsupported EncodingException : Error during action Param To Entry", e);
         throw new UserManagementException();
      }
   }

   /**
    * Update entry.
    * @param modList List of attributes to update
    * @param dn The object dn to update.
    * @throws UserManagementException if an error occurs
    */
   public static void updateEntry(List<LDAPModification> modList, String dn)
         throws UserManagementException {

      try {
         if (!modList.isEmpty()) {
            LDAPConnection lc = createLDAPConnection();

            LDAPModification[] mods = new LDAPModification[modList.size()];
            mods = (LDAPModification[]) modList.toArray(mods);

            lc.modify(dn, mods);
         }
      } catch (LDAPException e) {
         LOGGER.error("LDAP Exception : Error during update Entry", e);
         throw new UserManagementException();
      } catch (UnsupportedEncodingException e) {
         LOGGER.error("Unsupported EncodingException : Error during update Entry", e);
         throw new UserManagementException();
      }
   }

   /**
    * Create a LDAP Connection.
    * @return a LDAP Connection
    * @throws LDAPException if an error occurs.
    * @throws UnsupportedEncodingException if an error occurs.
    */
   protected static LDAPConnection createLDAPConnection() throws LDAPException,
         UnsupportedEncodingException {
      return LdapConnectionPool.getLDAPConnection();
   }
}
