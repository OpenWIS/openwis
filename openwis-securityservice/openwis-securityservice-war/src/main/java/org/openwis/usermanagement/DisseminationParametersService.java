package org.openwis.usermanagement;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.model.user.OpenWISEmail;
import org.openwis.usermanagement.model.user.OpenWISFTP;

/**
 *  The Dissemination Parameters Component is used for managed user's dissemination parameters. <P>
 * -  create / add / update / get / remove email or ftp for dissemination parameters. <P>
 */
@WebService(targetNamespace = "http://securityservice.openwis.org/", name = "DisseminationParametersService", portName = "DisseminationParametersServicePort", serviceName = "DisseminationParametersService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DisseminationParametersService {

   /**
    * Add or Update Email for Dissemination Parameters.
    * @param username The user name to add Email for Dissemination Parameters.
    * @param email Email to add.
    * @throws UserManagementException if an error occurs 
    */
   public void addOrUpdateEmailForDisseminationParameters(
         @WebParam(name = "username") String username, @WebParam(name = "email") OpenWISEmail email)
         throws UserManagementException;

   /**
    * Remove Email for Dissemination Parameters.
    * @param username The user name to remove Email for Dissemination Parameters.
    * @param emailAddress Email to remove.
    * @throws UserManagementException if an error occurs 
    */
   public void removeEmailForDisseminationParameters(@WebParam(name = "username") String username,
         @WebParam(name = "email") OpenWISEmail emailAddress)
         throws UserManagementException;

   /**
    * Get Emails for Dissemination Parameters.
    * @param username The user name to get Email for Dissemination Parameters.
    * @return Emails for Dissemination Parameters.
    * @throws UserManagementException if an error occurs 
    */
   public List<OpenWISEmail> getEmailForDisseminationParameters(
         @WebParam(name = "username") String username)
         throws UserManagementException;

   /**
    * Add or Update FTP for Dissemination Parameters.
    * @param username The user name to add FTP for Dissemination Parameters.
    * @param ftp FTP to add.
    * @throws UserManagementException if an error occurs 
    */
   public void addOrUpdateFTPForDisseminationParameters(
         @WebParam(name = "username") String username, @WebParam(name = "ftp") OpenWISFTP ftp)
         throws UserManagementException;

   /**
    * Remove FTP for Dissemination Parameters.
    * @param username The user name to remove FTP for Dissemination Parameters.
    * @param ftp FTP to remove.
    * @throws UserManagementException if an error occurs 
    */
   public void removeFTPForDisseminationParameters(@WebParam(name = "username") String username,
         @WebParam(name = "ftp") OpenWISFTP ftp)
         throws UserManagementException;

   /**
    * Get FTPs for Dissemination Parameters.
    * @param username The user name to get FTP for Dissemination Parameters.
    * @return FTPs for Dissemination Parameters.
    * @throws UserManagementException if an error occurs 
    */
   public List<OpenWISFTP> getFTPForDisseminationParameters(
         @WebParam(name = "username") String username)
         throws UserManagementException;
}
