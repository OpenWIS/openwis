/**
 * 
 */
package org.openwis.usermanagement;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openwis.securityservice.ClassOfService;
import org.openwis.securityservice.DisseminationTool;
import org.openwis.securityservice.OpenWISAddress;
import org.openwis.securityservice.OpenWISEmail;
import org.openwis.securityservice.OpenWISFTP;
import org.openwis.securityservice.OpenWISGroup;
import org.openwis.securityservice.OpenWISUser;
import org.openwis.usermanagement.ldap.model.UserAndGroup;

import com.thoughtworks.xstream.XStream;

/**
 * Create User And Group File.
 * 
 */
public class CreateUserAndGroupFile {

   /**
    * @member: ftp
    */
   private static OpenWISFTP ftp;

   /**
    * @member: dcpcDemo : local1 & local2
    */
   private static OpenWISGroup dcpcDemoGroup;

   /**
    * @member: Global : institutional, OACI, Public
    */
   private static OpenWISGroup globalGroup;

   /**
    * @member: address
    */
   private static OpenWISAddress address;

   /**
    * Create a File.
    */
   public void createFile() {

      ftp = new OpenWISFTP();
      ftp.setCheckFileSize(false);
      ftp.setFileName("file name");
      ftp.setHost("ftp://meteo-france.fr");
      ftp.setPassive(true);
      ftp.setPassword("openwis");
      ftp.setPath("./path/");
      ftp.setPort("2145");
      ftp.setUser("ftpUser");
      ftp.setDisseminationTool(DisseminationTool.RMDCN);

      address = new OpenWISAddress();
      address.setAddress("6 rue Leon");
      address.setCity("Toulouse");
      address.setCountry("France");
      address.setState("Fr");
      address.setZip("31000");

      OpenWISUser icaobs = createICaobs();
      OpenWISUser jpaubagnac = createJPAubagnac();
      OpenWISUser sbenchimol = createSBenchimol();
      OpenWISUser ckent = createCKent();
      OpenWISUser administrator = createAdministrator();
      OpenWISUser jdoe = createJDoe();

      XStream xStream = new XStream();
      List<OpenWISUser> users = new ArrayList<OpenWISUser>();
      users.add(icaobs);
      users.add(jpaubagnac);
      users.add(sbenchimol);
      users.add(ckent);
      users.add(administrator);
      users.add(jdoe);
      
      List<OpenWISGroup> groups = new ArrayList<OpenWISGroup>();
      createGlobalGroup();
      createDCPCDemoGroup();
      groups.add(globalGroup);
      groups.add(dcpcDemoGroup);

      UserAndGroup userAndGroup = new UserAndGroup();
      userAndGroup.setGroups(groups);
      userAndGroup.setUsers(users);

      try {

         xStream.toXML(userAndGroup, new FileOutputStream("userAndGroup.xml"));
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * Create John Doe.
    * @return OpenWIS user
    */
   public OpenWISUser createJDoe() {
      OpenWISUser jdoe = new OpenWISUser();
      jdoe.setName("Doe");
      jdoe.setUserName("jdoe");
      jdoe.setSurName("John");
      jdoe.setPassword("openwis");
      jdoe.setEmailContact("j.doe@metservice.fr");

      jdoe.setAddress(address);

      OpenWISEmail email = new OpenWISEmail();
      email.setAddress("j.doe@metservice.fr");
      email.setMailAttachmentMode("TO");
      email.setMailDispatchMode("As Attachment");
      email.setFileName("the file name");
      email.setHeaderLine("Header");
      email.setSubject("Subject");
      email.setDisseminationTool(DisseminationTool.PUBLIC);
      jdoe.getEmails().add(email);
      
      jdoe.getFtps().add(ftp);
      
      jdoe.setProfile("RegisteredUser");

      jdoe.setNeedUserAccount(false);

      jdoe.getBackUps().add("DcpcDemo");
      jdoe.getBackUps().add("DCPC1");
      
      jdoe.setClassOfService(ClassOfService.BRONZE);

      OpenWISGroup instGlob = new OpenWISGroup();
      instGlob.setIsGlobal(true);
      instGlob.getGroupIds().add("institutional");
      
      jdoe.getGroups().add(instGlob);
      
      return jdoe;
   }

   /**
    * Create Clark Kent.
    * @return OpenWIS user
    */
   public OpenWISUser createCKent() {
      OpenWISUser ckent = new OpenWISUser();
      ckent.setName("Kent");
      ckent.setUserName("ckent");
      ckent.setSurName("Clark");
      ckent.setPassword("openwis");
      ckent.setEmailContact("c.kent@lemonde.fr");

      ckent.setAddress(address);

      OpenWISEmail email = new OpenWISEmail();
      email.setAddress("c.kent@lemonde.fr");
      email.setMailAttachmentMode("TO");
      email.setMailDispatchMode("As Attachment");
      email.setFileName("the file name");
      email.setHeaderLine("Header");
      email.setSubject("Subject");
      email.setDisseminationTool(DisseminationTool.PUBLIC);
      ckent.getEmails().add(email);

      ckent.getFtps().add(ftp);
      
      ckent.setNeedUserAccount(true);

      ckent.getBackUps().add("DCPC2");
      
      ckent.setClassOfService(ClassOfService.BRONZE);
      
      ckent.setProfile("Editor");

      OpenWISGroup publicGlob = new OpenWISGroup();
      publicGlob.setIsGlobal(true);
      publicGlob.getGroupIds().add("Public");
      
      ckent.getGroups().add(publicGlob);
      return ckent;
   }

   /**
    * Create Ida Caobs.
    * @return OpenWIS user
    */
   public OpenWISUser createICaobs() {
      OpenWISUser icaobs = new OpenWISUser();
      icaobs.setName("Caobs");
      icaobs.setUserName("icaobs");
      icaobs.setSurName("Ida");
      icaobs.setPassword("openwis");
      icaobs.setEmailContact("i.caobs@oaci.fr");

      icaobs.setAddress(address);

      OpenWISEmail email = new OpenWISEmail();
      email.setAddress("i.caobs@oaci.fr");
      email.setMailAttachmentMode("TO");
      email.setMailDispatchMode("As Attachment");
      email.setFileName("the file name");
      email.setHeaderLine("Header");
      email.setSubject("Subject");
      email.setDisseminationTool(DisseminationTool.PUBLIC);
      icaobs.getEmails().add(email);

      icaobs.getFtps().add(ftp);

      icaobs.setNeedUserAccount(true);

      icaobs.getBackUps().add("DCPC1");
      icaobs.getBackUps().add("GISC1");
      
      icaobs.setClassOfService(ClassOfService.GOLD);
      
      icaobs.setProfile("Editor");
      
      OpenWISGroup oaciGlob = new OpenWISGroup();
      oaciGlob.setIsGlobal(true);
      oaciGlob.getGroupIds().add("OACI");
      
      icaobs.getGroups().add(oaciGlob);
      
      return icaobs;
   }

   /**
    * Create JP Aubagnac.
    * @return OpenWIS user
    */
   public OpenWISUser createJPAubagnac() {
      OpenWISUser jpaubagnac = new OpenWISUser();
      jpaubagnac.setName("Aubagnac");
      jpaubagnac.setUserName("jpaubagnac");
      jpaubagnac.setSurName("Jean-Pierre");
      jpaubagnac.setPassword("openwis");
      jpaubagnac.setEmailContact("jean-pierre.aubagnac@meteo.fr");

      jpaubagnac.setAddress(address);

      OpenWISEmail email = new OpenWISEmail();
      email.setAddress("jean-pierre.aubagnac@meteo.fr");
      email.setMailAttachmentMode("TO");
      email.setMailDispatchMode("As Attachment");
      email.setFileName("the file name");
      email.setHeaderLine("Header");
      email.setSubject("Subject");
      email.setDisseminationTool(DisseminationTool.PUBLIC);
      jpaubagnac.getEmails().add(email);

      jpaubagnac.getFtps().add(ftp);

      jpaubagnac.setNeedUserAccount(false);

      jpaubagnac.getBackUps().add("GISC1");
      
      jpaubagnac.setClassOfService(ClassOfService.SILVER);
      
      jpaubagnac.setProfile("Editor");

      OpenWISGroup dcpcDemoLoc1 = new OpenWISGroup();
      dcpcDemoLoc1.setIsGlobal(false);
      dcpcDemoLoc1.setCentreName("DcpcDemo");
      dcpcDemoLoc1.getGroupIds().add("local1");
      
      jpaubagnac.getGroups().add(dcpcDemoLoc1);
      
      OpenWISGroup instGlob = new OpenWISGroup();
      instGlob.setIsGlobal(true);
      instGlob.getGroupIds().add("institutional");
      
      jpaubagnac.getGroups().add(instGlob);
      
      return jpaubagnac;
   }

   /**
    * Create Stephane Benchimol.
    * @return OpenWIS user
    */
   public OpenWISUser createSBenchimol() {
      OpenWISUser sbenchimol = new OpenWISUser();
      sbenchimol.setName("Benchimol");
      sbenchimol.setUserName("sbenchimol");
      sbenchimol.setSurName("Stephane");
      sbenchimol.setPassword("openwis");
      sbenchimol.setEmailContact("stephane.benchimol@mfi.fr");

      sbenchimol.setAddress(address);

      OpenWISEmail email = new OpenWISEmail();
      email.setAddress("stephane.benchimol@mfi.fr");
      email.setMailAttachmentMode("TO");
      email.setMailDispatchMode("As Attachment");
      email.setFileName("the file name");
      email.setHeaderLine("Header");
      email.setSubject("Subject");
      email.setDisseminationTool(DisseminationTool.PUBLIC);
      sbenchimol.getEmails().add(email);

      sbenchimol.getFtps().add(ftp);

      sbenchimol.setNeedUserAccount(false);

      sbenchimol.getBackUps().add("DcpcDemo");
      
      sbenchimol.setClassOfService(ClassOfService.GOLD);
      
      sbenchimol.setProfile("Editor");


      OpenWISGroup instGlob = new OpenWISGroup();
      instGlob.setIsGlobal(true);
      instGlob.getGroupIds().add("institutional");
      
      sbenchimol.getGroups().add(instGlob);
      
      OpenWISGroup dcpcDemoLoc2 = new OpenWISGroup();
      dcpcDemoLoc2.setIsGlobal(false);
      dcpcDemoLoc2.setCentreName("DcpcDemo");
      dcpcDemoLoc2.getGroupIds().add("local2");
      
      sbenchimol.getGroups().add(dcpcDemoLoc2);
      return sbenchimol;
   }

   /**
    * Create Administrator.
    * @return OpenWIS user
    */
   public OpenWISUser createAdministrator() {
      OpenWISUser administrator = new OpenWISUser();
      administrator.setName("Administrator");
      administrator.setUserName("administrator");
      administrator.setSurName("Administrator");
      administrator.setPassword("openwis");
      administrator.setEmailContact("administrator@openwis.fr");

      administrator.setAddress(address);

      OpenWISEmail email = new OpenWISEmail();
      email.setAddress("jean-pierre.aubagnac@meteo.fr");
      email.setMailAttachmentMode("TO");
      email.setMailDispatchMode("As Attachment");
      email.setFileName("the file name");
      email.setHeaderLine("Header");
      email.setSubject("Subject");
      email.setDisseminationTool(DisseminationTool.PUBLIC);
      administrator.getEmails().add(email);

      administrator.getFtps().add(ftp);

      administrator.setNeedUserAccount(true);

      administrator.getBackUps().add("DCPC1");
      administrator.getBackUps().add("DCPC2");
      
      administrator.setClassOfService(ClassOfService.BRONZE);
      
      administrator.setProfile("Administrator");

      OpenWISGroup instGlob = new OpenWISGroup();
      instGlob.setIsGlobal(true);
      instGlob.getGroupIds().add("institutional");
      
      administrator.getGroups().add(instGlob);
      return administrator;
   }

   /**
    * dcpcDemo : local1 & local2
    */
   public void createDCPCDemoGroup() {
      dcpcDemoGroup = new OpenWISGroup();
      dcpcDemoGroup.setIsGlobal(false);
      dcpcDemoGroup.setCentreName("DcpcDemo");
      dcpcDemoGroup.getGroupIds().add("local1");
      dcpcDemoGroup.getGroupIds().add("local2");
   }

   /**
    * Global : institutional, OACI, Public
    */
   public void createGlobalGroup() {
      globalGroup = new OpenWISGroup();
      globalGroup.setIsGlobal(true);
      globalGroup.getGroupIds().add("institutional");
      globalGroup.getGroupIds().add("OACI");
      globalGroup.getGroupIds().add("Public");
   }

   /**
    * Create file usersAndGroups.xml
    * @param args Args
    */
   public static void main(String[] args) {
      CreateUserAndGroupFile createUserAndGroupFile = new CreateUserAndGroupFile();
      createUserAndGroupFile.createFile();
   }
}
