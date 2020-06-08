/**
 *
 */
package org.openwis.metadataportal.kernel.user;

import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.dataservice.BlacklistInfo;
import org.openwis.dataservice.BlacklistService;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.model.user.Address;
import org.openwis.metadataportal.model.user.BackUp;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.securityservice.OpenWISAddress;
import org.openwis.securityservice.OpenWISGroup;
import org.openwis.securityservice.OpenWISUserUpdateLog;
import org.openwis.securityservice.OpenWISUser;
import org.openwis.securityservice.InetUserStatus;
import org.openwis.securityservice.UserManagementException_Exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class UserManager extends AbstractManager {

    //----------------------------------------------------------------------- Constructors.

    /**
     * Default constructor.
     * Builds a UserManager.
     * @param dbms Database connection
     */
    public UserManager(Dbms dbms) {
        super(dbms);
    }

    //----------------------------------------------------------------------- Public methods.

    private static final String LDAP_PWD_DATE_FORMAT="yyyyMMddHHmmss.SSS'Z'";
    /**
     * Get all users.
     * @return all users.
     * @throws UserManagementException_Exception if an error occurs.
     */
    public List<User> getAllUsers() throws UserManagementException_Exception {
        List<User> allUsers = new ArrayList<User>();

        List<OpenWISUser> openWISUsers = SecurityServiceProvider.getGroupManagementService()
                .getAllUsersByGroup(
                        OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));

        for (OpenWISUser openWISUser : openWISUsers) {
            allUsers.add(buildUserFromOpenWisUser(openWISUser));
        }

        return allUsers;
    }

    /**
     * Gets a user by its username.
     * @param userName the user Name.
     * @return the user.
     * @throws UserManagementException_Exception if an error occurs.
     */
    public User getUserByUserName(String userName) throws UserManagementException_Exception {
        OpenWISUser openWISUser = SecurityServiceProvider.getUserManagementService().getUserInfo(
                userName);
        return buildUserFromOpenWisUser(openWISUser);
    }

    //   /**
    //    * Gets a user by its id.
    //    * @param id the id of the user.
    //    * @return the user.
    //    * @throws SQLException if an error occurs.
    //    */
    //   @SuppressWarnings("unchecked")
    //   public User getUserById(int id) throws SQLException {
    //      User user = null;
    //      String query = "SELECT * FROM Users WHERE id=?";
    //      List<Element> records = getDbms().select(query, id).getChildren();
    //      if (!records.isEmpty()) {
    //         user = buildUserFromElement(records.get(0));
    //      }
    //      return user;
    //   }

    /**
     * Creates the user.
     * @param user the user to create.
     * @throws Exception  if an error occurs.
     */
    public void createUser(User user) throws Exception {
        // check user rules
        checkUser(user);

        //Check LDAP AND create into LDAP.
        OpenWISUser openWISUser = buildOpenWisUserFromUser(user);
        SecurityServiceProvider.getUserManagementService().createUser(openWISUser,
                OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));

        // Create a record in the blacklist table to allow an admin to modify the user threshold
        BlacklistService blacklistService = DataServiceProvider.getBlacklistService();
        BlacklistInfo blacklistInfo = blacklistService.getUserBlackListInfoIfExists(user.getUsername());
        if (blacklistInfo.getId() == null) {
            blacklistService.updateUserBlackListInfo(blacklistInfo);
        }
    }

    /**
     * Updates the user..
     * @param user the user to update.
     * @throws Exception  if an error occurs.
     */
    public List<OpenWISUserUpdateLog> updateUser(User user) throws Exception {
        OpenWISUser openWISUser = buildOpenWisUserFromUser(user);

        boolean updatePersoInfo = openWISUser.getBackUps().size() == 0 && openWISUser.getGroups().size() == 0
                && StringUtils.isBlank(openWISUser.getProfile()) && openWISUser.getClassOfService() == null;
        if (! updatePersoInfo) {
            retrieveUserGroupsOfOtherCentres(openWISUser);
        }

        //Check LDAP AND update LDAP.
        return SecurityServiceProvider.getUserManagementService().updateUser(openWISUser);
    }

    private void retrieveUserGroupsOfOtherCentres(OpenWISUser openWISUser) throws Exception {
        // Retrieve user groups to keep local groups of other centres
        List<OpenWISGroup> ldapGroups = SecurityServiceProvider.getUserManagementService().getUserGroups(openWISUser.getUserName());
        for (OpenWISGroup openWISGroup : ldapGroups) {
            if (!OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME).equals(openWISGroup.getCentreName())
                    && !openWISGroup.isIsGlobal()) {
                openWISGroup.getGroupIds().remove(LoginConstants.DEFAULT);
                if (openWISGroup.getGroupIds().size() > 0) {
                    // add the external local group to the user groups
                    openWISUser.getGroups().add(openWISGroup);
                }
            }
        }
    }

    /**
     * Deletes a user.
     * @param username the user name to delete.
     * @throws Exception  if an error occurs.
     */
    public void removeUser(String username) throws Exception {
        SecurityServiceProvider.getUserManagementService().removeUser(username,
                OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));
    }

    public void lockUser(String username, Boolean lock) throws Exception {
       if (lock) {
           SecurityServiceProvider.getUserManagementService().lockUser(username);
       } else {
           SecurityServiceProvider.getUserManagementService().unlockUser(username);
       }
    }

    /**
     * Gets all users of the given group.
     * @param group the group name.
     * @return all users linked to this group.
     * @throws UserManagementException_Exception  if an exception occurs.
     */
    public List<User> getAllUsersByGroup(Group group) throws UserManagementException_Exception {
        List<User> allUsers = new ArrayList<User>();
        List<OpenWISUser> openWISUsers = new ArrayList<OpenWISUser>();
        if (group.isGlobal()) {
            openWISUsers = SecurityServiceProvider.getGroupManagementService()
                    .getAllUsersByGlobalGroupId(group.getName());
        } else {
            openWISUsers = SecurityServiceProvider.getGroupManagementService()
                    .getAllUsersByLocalGroupId(
                            OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME),
                            group.getName());
        }

        for (OpenWISUser openWISUser : openWISUsers) {
            User user = buildUserFromOpenWisUser(openWISUser);
            allUsers.add(user);
        }

        return allUsers;
    }

    //----------------------------------------------------------------------- Private methods.

    /**
     * Builds a user from an openWis User.
     * @param openWISUser the openWis User.
     * @return the user.
     */
    public static User buildUserFromOpenWisUser(OpenWISUser openWISUser) {
        User user = new User();
        user.setUsername(openWISUser.getUserName());
        user.setName(openWISUser.getName());
        user.setSurname(openWISUser.getSurName());
        user.setPassword(openWISUser.getPassword());
        if (openWISUser.getAddress() != null) {
            Address address = new Address();
            address.setAddress(openWISUser.getAddress().getAddress());
            address.setCity(openWISUser.getAddress().getCity());
            address.setCountry(openWISUser.getAddress().getCountry());
            address.setState(openWISUser.getAddress().getState());
            address.setZip(openWISUser.getAddress().getZip());
            user.setAddress(address);
        }

        user.setEmailContact(openWISUser.getEmailContact());
        user.setProfile(openWISUser.getProfile());
        for (OpenWISGroup openWISGroup : openWISUser.getGroups()) {
            user.getGroups().addAll(GroupManager.buildGroupFromOpenWisGroup(openWISGroup));
        }
        user.setClassOfService(openWISUser.getClassOfService());
        user.setInetUserStatus(openWISUser.getInetUserStatus());
        user.setNeedUserAccount(openWISUser.isNeedUserAccount());
        for (String backUpName : openWISUser.getBackUps()) {
            BackUp backUp = new BackUp();
            backUp.setName(backUpName);
            user.getBackUps().add(backUp);
        }
        user.setSecretKey(openWISUser.getSecretKey());

        if (openWISUser.getLastLoginTime() != null) {
            user.setLastLogin(LocalDateTime.parse(openWISUser.getLastLoginTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        user.setPwdChangedTime(LocalDateTime.parse(openWISUser.getPwdChangedTime(), DateTimeFormatter.ofPattern(LDAP_PWD_DATE_FORMAT)));

        /**
         * Fix: DO NOT USE getPwdExpireTime
         * Password expiration is computed from pwdChangedTime + the period of password valability
         * Password expiration
         * If you set up password expiration, the warning gets triggered when the user authenticates during the password expiration warning interval
         * and the ds-pwp-warned-time attribute is set. If the user does not authenticate before the password expiry time,
         * the ds-pwp-password-expiration-time value will keep increasing until the user password is changed and the expiry time is reset.
         * For more details see: https://backstage.forgerock.com/knowledge/kb/article/a40016497
         */
        int period = 365;
        ChronoUnit periodTimeUnit;
        try {
            period = OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.ACCOUNT_PASSWORD_EXPIRE_PERIOD);
            switch (OpenwisMetadataPortalConfig.getString(ConfigurationConstants.ACCOUNT_TASK_TIME_UNIT.toLowerCase())) {
                case "minutes":
                    periodTimeUnit = ChronoUnit.MINUTES;
                    break;
                case "hours":
                    periodTimeUnit = ChronoUnit.HOURS;
                    break;
                default:
                    periodTimeUnit = ChronoUnit.DAYS;
            }
            user.setPwdExpireTime(user.getPwdChangedTime().plus(period, periodTimeUnit));
        } catch (NumberFormatException ex) {
            Log.error(Log.WEBAPP, "Not a number: " + ConfigurationConstants.ACCOUNT_PASSWORD_EXPIRE_PERIOD);
        }

        user.getEmails().addAll(openWISUser.getEmails());
        user.getFtps().addAll(openWISUser.getFtps());

        return  user;
    }

    /**
     * Builds an openWis User from a user.
     * @param user the user.
     * @return the openWis User.
     */
    public static OpenWISUser buildOpenWisUserFromUser(User user) {
        OpenWISUser openWisUser = new OpenWISUser();
        openWisUser.setUserName(user.getUsername());
        openWisUser.setName(user.getName());
        openWisUser.setSurName(user.getSurname());
        openWisUser.setPassword(user.getPassword());

        OpenWISAddress openWISAddress = new OpenWISAddress();
        openWISAddress.setAddress(user.getAddress().getAddress());
        openWISAddress.setCity(user.getAddress().getCity());
        openWISAddress.setCountry(user.getAddress().getCountry());
        openWISAddress.setState(user.getAddress().getState());
        openWISAddress.setZip(user.getAddress().getZip());
        openWisUser.setAddress(openWISAddress);

        openWisUser.setEmailContact(user.getEmailContact());
        openWisUser.setProfile(user.getProfile());
        for (Group group : user.getGroups()) {
            openWisUser.getGroups().add(GroupManager.buildOpenWisGroupFromGroup(group));
        }

        openWisUser.setClassOfService(user.getClassOfService());
        openWisUser.setInetUserStatus(InetUserStatus.ACTIVE);
        openWisUser.setNeedUserAccount(user.isNeedUserAccount());
        for (BackUp backUp : user.getBackUps()) {
            openWisUser.getBackUps().add(backUp.getName());
        }

        openWisUser.setPwdReset(user.getPwdReset());
        openWisUser.setSecretKey(user.getSecretKey());
        openWisUser.getEmails().addAll(user.getEmails());
        openWisUser.getFtps().addAll(user.getFtps());

        return openWisUser;
    }

    /**
     * Check if the user exists.
     * @param user The user to check.
     * @throws Exception if an error occurs.
     */
    private void checkUser(User user) throws Exception {
        boolean userExists = SecurityServiceProvider.getUserManagementService().checkUserNameExists(
                user.getUsername());
        if (userExists) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
    }

    /**
     * Import the user list.
     * @param userNames the user name list to import.
     * @throws Exception
     */
    public void importUser(List<String> userNames) throws Exception {
        SecurityServiceProvider.getUserManagementService().importUser(userNames,
                OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));
    }

    /**
     * Get All Imported Users.
     * @return the list of all users which can be imported.
     * @throws Exception if an error occurs.
     */
    public List<OpenWISUser> getAllImportedUsers() throws Exception {
        return SecurityServiceProvider.getUserManagementService().getImportUserList(
                OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));
    }

    /**
     * update login timestamp.
     * After each successful login the timestamp must be updated. Otherwise 2FA will be not possible anymore
     * @param username
     * @param timestamp
     * @throws Exception
     */
   public void updateLoginTimestamp(String username, long timestamp) throws Exception {
        SecurityServiceProvider.getUserManagementService().updateLoginTimestamp(username, timestamp);
   }

    /**
     * Change user password.
     * @param username The user name.
     * @param password The password to update.
     * @throws Exception if an error occurs.
     */
    public void changePassword(String username, String password, Boolean forceReset) throws Exception {
        SecurityServiceProvider.getUserManagementService().changePassword(username, password, forceReset);
    }

    /**
     * Get All users who belong to this centre group and who the user name is like the user name filter.
     * @param userFilter The user name filter
     * @return all users who belong to this centre group and who the username is like the usernamefilter.
     * @throws UserManagementException_Exception if an error occurs.
     */
    public List<User> getAllUserLike(String userFilter) throws UserManagementException_Exception {
        List<OpenWISUser> openWISUsers = SecurityServiceProvider.getUserManagementService()
                .getAllUserByUserNameFilter(userFilter,
                        OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));
        List<User> users = new ArrayList<User>();
        for (OpenWISUser openWISUser : openWISUsers) {
            User user = buildUserFromOpenWisUser(openWISUser);
            users.add(user);
        }
        return users;
    }

    /**
     * Get All users who belong to these groups and who the user name is like the user name filter.
     * @param userFilter The user name filter
     * @param openwisGroups The groups list, if null, will consider the local centre.
     * @return all users who belong to this centre group and who the username is like the usernamefilter.
     * @throws UserManagementException_Exception if an error occurs.
     */
    public List<User> getAllUserLike(String userFilter, ArrayList<OpenWISGroup> openwisGroups)
            throws UserManagementException_Exception {
        List<OpenWISUser> openWISUsers = SecurityServiceProvider.getUserManagementService()
                .getAllUserByUserNameAndGroupsFilter(userFilter, openwisGroups);
        List<User> users = new ArrayList<User>();
        for (OpenWISUser openWISUser : openWISUsers) {
            users.add(buildUserFromOpenWisUser(openWISUser));
        }
        return users;
    }

    /**
     * Get All Imported Users matches to user filter.
     * @param userFilter The user filter.
     * @return the list of all users which can be imported.
     * @throws Exception if an error occurs.
     */
    public List<OpenWISUser> getAllImportedUsersLike(String userFilter)
            throws UserManagementException_Exception {
        return SecurityServiceProvider.getUserManagementService().getImportUserListFilter(userFilter,
                OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));
    }

    /**
     * Get all user names (no size limit) of the current deployment.
     * @return the username list
     * @throws UserManagementException_Exception
     */
    public List<String> getAllUserNames() throws UserManagementException_Exception {
        return SecurityServiceProvider.getGroupManagementService().getAllUserNameByCentre(
                OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));
    }
}
