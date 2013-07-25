package org.fao.geonet.kernel.search.spatial;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.FeatureKeys;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Xml. <P>
 * Explanation goes here. <P>
 */
public final class XmlUtils {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

   /**
    * Instantiates a new xml.
    */
   private XmlUtils() {
      super();
   }

   /**
    * Gets the string.
    *
    * @param element the element
    * @return the string
    */
   public static String getString(Element xml) {
      XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
      return outputter.outputString(xml);
   }

   /**
    * Load string.
    *
    * @param data the data
    * @param validate the validate
    * @return the element
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JDOMException the jDOM exception
    */
   public static Element loadString(String data, boolean validate) throws IOException,
         JDOMException {
      SAXBuilder builder = new SAXBuilder(validate);
      Document jdoc = builder.build(new StringReader(data));

      return (Element) jdoc.getRootElement().detach();
   }

   /**
    * Transform.
    *
    * @param xml the xml
    * @param stylesheet the stylesheet
    * @return the element
    * @throws TransformerException
    */
   public static Element transform(Element xml, String stylesheet) throws TransformerException {
      JDOMResult result = new JDOMResult();
      File styleSheet = new File(stylesheet);
      Source srcXml = new JDOMSource(new Document((Element) xml.detach()));
      Source srcSheet = new StreamSource(styleSheet);

      // Dear old saxon likes to yell loudly about each and every XSLT 1.0
      // stylesheet so switch it off but trap any exceptions because this
      // code is run on transformers other than saxon

      TransformerFactory transFact = TransformerFactory.newInstance(
            "net.sf.saxon.TransformerFactoryImpl", null);
      try {
         transFact.setAttribute(FeatureKeys.VERSION_WARNING, false);
         transFact.setAttribute(FeatureKeys.LINE_NUMBERING, true);
         transFact.setAttribute(FeatureKeys.PRE_EVALUATE_DOC_FUNCTION, true);
         transFact.setAttribute(FeatureKeys.RECOVERY_POLICY, Configuration.RECOVER_SILENTLY);
         // Add the following to get timing info on xslt transformations
         //transFact.setAttribute(FeatureKeys.TIMING,true);
      } catch (IllegalArgumentException e) {
         logger.warn("WARNING: transformerfactory doesnt like saxon attributes!", e);
      } finally {
         Transformer t = transFact.newTransformer(srcSheet);
         t.transform(srcXml, result);
      }
      return (Element) result.getDocument().getRootElement().detach();
   }

}
