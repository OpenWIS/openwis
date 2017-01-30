/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.dataservice.Diffusion;
import org.openwis.dataservice.Dissemination;
import org.openwis.dataservice.EventBasedFrequency;
import org.openwis.dataservice.Frequency;
import org.openwis.dataservice.FtpDiffusion;
import org.openwis.dataservice.MailDiffusion;
import org.openwis.dataservice.MssfssDissemination;
import org.openwis.dataservice.Parameter;
import org.openwis.dataservice.PublicDissemination;
import org.openwis.dataservice.RecurrentFrequency;
import org.openwis.dataservice.RmdcnDissemination;
import org.openwis.dataservice.ShoppingCartDissemination;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionBackup;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.dataservice.Value;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.util.DateTimeUtils;

import com.google.common.base.Joiner;

/**
 * This service enables to export Subscriptions. <P>
 * 
 */
public class ExportSubscription implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   public Element exec(Element params, ServiceContext context) throws Exception {
      int subscriptionId = Util.getParamAsInt(params, "subscriptionId");

      SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
      Subscription subscription = subscriptionService.getFullSubscription(Long
            .valueOf(subscriptionId));

      return createSubscriptionExportElt(subscription);
   }

   private Element createSubscriptionExportElt(Subscription subscription) {
      Element root = new Element("subscription");

      createSimpleChild(root, "urn", subscription.getProductMetadata().getUrn());
      createSimpleChild(root, "user", subscription.getUser());
      createSimpleChild(root, "email", subscription.getEmail());
      createSimpleChild(root, "extractMode", subscription.getExtractMode().toString());
      createSimpleChild(root, "classOfService", String.valueOf(subscription.getClassOfService()));
      root.addContent(createDisseminationElement(subscription.getPrimaryDissemination(), true));
      root.addContent(createDisseminationElement(subscription.getSecondaryDissemination(), false));
      root.addContent(createParametersElement(subscription.getParameters()));
      createSimpleChild(root, "backup",Boolean.toString(subscription.isBackup()));
      root.addContent(createSubscriptionBackupElement(subscription.getSubscriptionBackup()));
      root.addContent(createFrequencyElement(subscription.getFrequency()));
      createSimpleChild(root, "startingDate",DateTimeUtils.format(subscription.getStartingDate().toGregorianCalendar().getTime()));
      createSimpleChild(root, "state", subscription.getState().toString());
      createSimpleChild(root, "valid", Boolean.toString(subscription.isValid()));
      return root;
   }

   private Element createFrequencyElement(Frequency frequency) {
      Element element = new Element("frequency");
      if (frequency instanceof EventBasedFrequency) {
         element.setAttribute("type", "onArrival");
      } else if (frequency instanceof RecurrentFrequency) {
         element.setAttribute("type", "recurrent");
         RecurrentFrequency recurrentFrequency = (RecurrentFrequency) frequency;
         createSimpleChild(element, "period", recurrentFrequency.getReccurencePeriod().toString());
         createSimpleChild(element, "scale", recurrentFrequency.getReccurentScale().toString());
      }
      return element;
   }

   private Element createSubscriptionBackupElement(SubscriptionBackup subscriptionBackup) {
      Element element = new Element("subscriptionBackup");
      if (subscriptionBackup != null) {
         createSimpleChild(element, "subscriptionId",
               String.valueOf(subscriptionBackup.getSubscriptionId()));
         createSimpleChild(element, "deployment", subscriptionBackup.getDeployment());
      }

      return element;
   }

   private Element createDisseminationElement(Dissemination dissemination, boolean primary) {
      String disseminationEltName= primary ? "primaryDissemination" : "secondaryDissemination";
      Element element = new Element(disseminationEltName);
      if (dissemination != null) {
         createSimpleChild(element, "zipMode", dissemination.getZipMode().toString());
         if (dissemination instanceof ShoppingCartDissemination) {
            element.setAttribute("type", "stagingPost");
         } else if (dissemination instanceof PublicDissemination) {
            element.setAttribute("type", "public");
            element.addContent(createDiffusionElement(((PublicDissemination) dissemination)
                  .getDiffusion()));
         } else if (dissemination instanceof RmdcnDissemination) {
            element.setAttribute("type", "RMCDN");
            element.addContent(createDiffusionElement(((RmdcnDissemination) dissemination)
                  .getDiffusion()));
         } else if (dissemination instanceof MssfssDissemination) {
            element.setAttribute("type", "MssFss");
            element.setAttribute("channel", ((MssfssDissemination) dissemination).getChannel()
                  .getChannel());
         }
      }
      return element;
   }

   private Element createDiffusionElement(Diffusion diffusion) {
      Element element = new Element("diffusion");
      if (diffusion instanceof FtpDiffusion) {
         element.setAttribute("type", "ftp");
         FtpDiffusion ftpDiffusion = (FtpDiffusion) diffusion;
         createSimpleChild(element, "checkFileSize",
               Boolean.toString(ftpDiffusion.isCheckFileSize()));
         createSimpleChild(element, "encrypted", Boolean.toString(ftpDiffusion.isEncrypted()));
         createSimpleChild(element, "fileName", ftpDiffusion.getFileName());
         createSimpleChild(element, "host", ftpDiffusion.getHost());
         createSimpleChild(element, "passive", Boolean.toString(ftpDiffusion.isPassive()));
         createSimpleChild(element, "password", ftpDiffusion.getPassword());
         createSimpleChild(element, "path", ftpDiffusion.getPath());
         createSimpleChild(element, "port", ftpDiffusion.getPort());
         createSimpleChild(element, "user", ftpDiffusion.getUser());

      } else if (diffusion instanceof MailDiffusion) {
         element.setAttribute("type", "mail");
         MailDiffusion mailDiffusion = (MailDiffusion) diffusion;
         createSimpleChild(element, "address", mailDiffusion.getAddress());
         createSimpleChild(element, "fileName", mailDiffusion.getFileName());
         createSimpleChild(element, "headerLine", mailDiffusion.getHeaderLine());
         createSimpleChild(element, "mailAttachmentMode", mailDiffusion.getMailAttachmentMode()
               .toString());
         createSimpleChild(element, "mailDispatchMode", mailDiffusion.getMailDispatchMode()
               .toString());
         createSimpleChild(element, "subject", mailDiffusion.getSubject());
      }

      return element;
   }

   private Element createParametersElement(List<Parameter> parameters) {
      Element element = new Element("parameters");
      for (Parameter parameter : parameters) {
         Element elementParameter = new Element("parameter");
         createSimpleChild(elementParameter, "code", parameter.getCode());
         ArrayList<String> valuesStr = new ArrayList<String>(parameter.getValues().size());
         for (Value value : parameter.getValues()) {
            valuesStr.add(value.getValue());
         }
         createSimpleChild(elementParameter, "values", valuesStr.toString());
         element.addContent(elementParameter);
      }

      return element;
   }

   private void createSimpleChild(Element root, String name, String value) {
      Element child = new Element(name);
      child.setText(value);
      root.addContent(child);
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
