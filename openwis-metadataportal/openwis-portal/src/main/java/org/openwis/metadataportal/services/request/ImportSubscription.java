/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;

import jeeves.exceptions.BadFormatEx;
import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.util.ZipUtil;
import org.jdom.Element;
import org.jzkit.z3950.gen.v3.RecordSyntax_explain.internetAddress_inline116_codec;
import org.openwis.dataservice.ClassOfService;
import org.openwis.dataservice.Diffusion;
import org.openwis.dataservice.Dissemination;
import org.openwis.dataservice.DisseminationZipMode;
import org.openwis.dataservice.EventBasedFrequency;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.Frequency;
import org.openwis.dataservice.FtpDiffusion;
import org.openwis.dataservice.MailAttachmentMode;
import org.openwis.dataservice.MailDiffusion;
import org.openwis.dataservice.MailDispatchMode;
import org.openwis.dataservice.MssfssChannel;
import org.openwis.dataservice.MssfssDissemination;
import org.openwis.dataservice.Parameter;
import org.openwis.dataservice.PublicDissemination;
import org.openwis.dataservice.RecurrentFrequency;
import org.openwis.dataservice.RecurrentScale;
import org.openwis.dataservice.RmdcnDissemination;
import org.openwis.dataservice.ShoppingCartDissemination;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionBackup;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.dataservice.SubscriptionState;
import org.openwis.dataservice.Value;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.request.dto.submit.ImportSubscriptionResultDTO;
import org.openwis.metadataportal.services.util.DateTimeUtils;

/**
 * This service enables to export Subscriptions. <P>
 * 
 */
public class ImportSubscription implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   public Element exec(Element params, ServiceContext context) throws Exception {
      final String uploadDir = context.getUploadDir();
      String paramFiles = Util.getParam(params, "openwisFiles");
      File f = new File(uploadDir, paramFiles);

      System.out.println(f.getAbsolutePath());
      System.out.println(f.exists());
            
      List<File> subscriptionFiles;
      if (f.getName().endsWith(".zip")){
         File unzipDir = new File(f.getParentFile(), "unzipping");

         if (unzipDir.exists())
            ZipUtil.deleteAllFiles(unzipDir);

         ZipUtil.extract(new ZipFile(f), unzipDir);
         subscriptionFiles=Arrays.asList(unzipDir.listFiles()) ;
      } else {
         //xml file
         subscriptionFiles=Collections.singletonList(f);
      }

      
      ImportSubscriptionResultDTO dto=new ImportSubscriptionResultDTO();
      dto.setResult(true);
      int subscriptionCreated=0;
      int subscriptionFailure=0;

      for (File file : subscriptionFiles) {
         try {
            Element subscriptionElt = Xml.loadFile(file);
            Long id=createSubscription(subscriptionElt);
            Log.info(Geonet.DATA_MANAGER, "susbscription created : "+ id );
            subscriptionCreated++;
         } catch (Exception e) {
            dto.setResult(false);
            Log.error(Geonet.DATA_MANAGER, "Error while importing subscription",e);
            subscriptionFailure++;
         }
         file.delete();
         dto.setMessage("Subscription created: "+subscriptionCreated+ "<BR>Failure: "+subscriptionFailure);
      }
      f.delete();
      return JeevesJsonWrapper.sendBasicFormResult(dto);
   }

   private Long createSubscription(Element subscriptionElt) throws Exception {
      Subscription subscription = new Subscription();
      String urn = subscriptionElt.getChildText("urn");
      subscription.setUser(subscriptionElt.getChildText("user"));
      subscription.setEmail(subscriptionElt.getChildText("email"));
      subscription.setExtractMode(ExtractMode.valueOf(subscriptionElt.getChildText("extractMode")));

      String classOfService = subscriptionElt.getChildText("classOfService");
      if (classOfService == null || "null".equals(classOfService)) {
         subscription.setClassOfService(ClassOfService.BRONZE);
      } else {
         subscription.setClassOfService(ClassOfService.valueOf(classOfService));
      }

      subscription.setPrimaryDissemination(createDissemination(subscriptionElt
            .getChild("primaryDissemination")));
      subscription.setSecondaryDissemination(createDissemination(subscriptionElt
            .getChild("secondaryDissemination")));
      subscription.getParameters().addAll(createParameters(subscriptionElt.getChild("parameters")));
      subscription.setBackup(Boolean.valueOf(subscriptionElt.getChildText("backup")));
      if (subscription.isBackup()) {
         subscription.setSubscriptionBackup(createSubscriptionBackup(subscriptionElt
               .getChild("subscriptionBackup")));
      }

      subscription.setFrequency(createFrequency(subscriptionElt.getChild("frequency")));
      GregorianCalendar cDate = (GregorianCalendar) DateTimeUtils.getUTCCalendar();
      cDate.setTime(DateTimeUtils.parse(subscriptionElt.getChildText("startingDate")));
      subscription.setStartingDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(cDate));

      subscription.setState(SubscriptionState.valueOf(subscriptionElt.getChildText("state")));
      subscription.setValid(Boolean.valueOf(subscriptionElt.getChildText("valid")));

      SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
      Long id = subscriptionService.createSubscription(subscription, urn);

      return id;
   }

   private Frequency createFrequency(Element frequencyElt) {
      String type = frequencyElt.getAttributeValue("type");
      Frequency frequency = null;

      if ("onArrival".equals(type)) {
         frequency = new EventBasedFrequency();
      } else if ("recurrent".equals(type)) {
         frequency = new RecurrentFrequency();
         ((RecurrentFrequency) frequency).setReccurencePeriod(Integer.parseInt(frequencyElt
               .getChildText("period")));
         ((RecurrentFrequency) frequency).setReccurentScale(RecurrentScale.fromValue(frequencyElt
               .getChildText("scale")));
      }
      return frequency;
   }

   private SubscriptionBackup createSubscriptionBackup(Element backupElement) {
      SubscriptionBackup subscriptionBackup = new SubscriptionBackup();

      subscriptionBackup.setSubscriptionId(Long.parseLong(backupElement
            .getChildText("subscriptionId")));
      subscriptionBackup.setDeployment(backupElement.getChildText("deployment"));
      return subscriptionBackup;
   }

   private Dissemination createDissemination(Element dissElt) {
      String type = dissElt.getAttributeValue("type");
      Dissemination dissemination = null;
      
      if ("stagingPost".equals(type)) {
         dissemination = new ShoppingCartDissemination();
      } else if ("public".equals(type)) {
         dissemination = new PublicDissemination();
         ((PublicDissemination) dissemination).setDiffusion(createDiffusion(dissElt
               .getChild("diffusion")));
      } else if ("RMCDN".equals(type)) {
         dissemination = new RmdcnDissemination();
         ((RmdcnDissemination) dissemination).setDiffusion(createDiffusion(dissElt
               .getChild("diffusion")));
      } else if ("MssFss".equals(type)) {
         dissemination = new MssfssDissemination();
         MssfssChannel mssfssChanel = new MssfssChannel();
         mssfssChanel.setChannel(dissElt.getAttributeValue("channel"));
         ((MssfssDissemination) dissemination).setChannel(mssfssChanel);
      }
      if (dissemination != null){
         dissemination.setZipMode(DisseminationZipMode.fromValue(dissElt.getChildText("zipMode")));
      }
         
      return dissemination;

   }

   private Diffusion createDiffusion(Element diffusionElt) {
      Diffusion diffusion = null;
      String type = diffusionElt.getAttributeValue("type");

      if ("ftp".equals(type)) {
         FtpDiffusion ftpDiffusion = new FtpDiffusion();
         ftpDiffusion.setCheckFileSize(Boolean.valueOf(diffusionElt.getChildText("checkFileSize")));
         ftpDiffusion.setEncrypted(Boolean.valueOf(diffusionElt.getChildText("encrypted")));
         ftpDiffusion.setFileName(diffusionElt.getChildText("fileName"));
         ftpDiffusion.setHost(diffusionElt.getChildText("host"));
         ftpDiffusion.setPassive(Boolean.valueOf(diffusionElt.getChildText("passive")));
         ftpDiffusion.setPassword(diffusionElt.getChildText("password"));
         ftpDiffusion.setPath(diffusionElt.getChildText("path"));
         ftpDiffusion.setPort(diffusionElt.getChildText("port"));
         ftpDiffusion.setUser(diffusionElt.getChildText("user"));

         diffusion = ftpDiffusion;

      } else if ("mail".equals(type)) {
         MailDiffusion mailDiffusion = new MailDiffusion();
         mailDiffusion.setAddress(diffusionElt.getChildText("address"));
         mailDiffusion.setFileName(diffusionElt.getChildText("fileName"));
         mailDiffusion.setHeaderLine(diffusionElt.getChildText("headerLine"));
         mailDiffusion.setMailAttachmentMode(MailAttachmentMode.valueOf(diffusionElt
               .getChildText("mailAttachmentMode")));
         mailDiffusion.setMailDispatchMode(MailDispatchMode.valueOf(diffusionElt
               .getChildText("mailDispatchMode")));
         mailDiffusion.setSubject(diffusionElt.getChildText("subject"));

         diffusion = mailDiffusion;

      }
      return diffusion;
   }

   private List<Parameter> createParameters(Element parametersElt) {

      ArrayList<Parameter> parameters = new ArrayList<Parameter>();
      for (Object parameterEltO : parametersElt.getChildren("parameter")) {
         Element parameterElt = (Element) parameterEltO;
         Parameter parameter = new Parameter();
         parameter.setCode(parameterElt.getChildText("code"));
         String valuesStr = parameterElt.getChildText("values");
         valuesStr = valuesStr.replace("[", "");
         valuesStr = valuesStr.replace("]", "");
         for (String s : valuesStr.split(",")) {
            Value value = new Value();
            value.setValue(s.trim());
            parameter.getValues().add(value);
         }
         parameters.add(parameter);
      }
      return parameters;
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   public void init(String arg0, ServiceConfig arg1) throws Exception {
   }

   /**
    * Serialize an object.
    *
    * @param o the o
    * @return the string
    * @throws JAXBException the jAXB exception
    */
   public static String serialize(Object o) throws JAXBException {
      // write it out as XML
      final JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
      StringWriter writer = new StringWriter();

      // for cool output
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(o, writer);
      return writer.toString();
   }

}
