package jeeves.utils;

import java.io.IOException;
import java.io.Writer;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Hack JDOM outputter not to remove namespace declaration of sub-elements when already defined in parent, 
 * for the OAI PMH response elements.
 * 
 * @see OpenWISXMLOutputter#EXCLUDED_ELEMENTS_FROM_NS_FILTER
 */
public class OpenWISXMLOutputter extends XMLOutputter {

   /**
    * Comment for <code>OAI_PMH_ROOT</code>.
    * The element to remove from namespace filter.
    */
   public final static String OAI_PMH_ROOT = "OAI-PMH";

   /**
    * Comment for <code>OAI_PMH</code>.
    * the OAI-PMH default namespace.
    */
   public static final Namespace OAI_PMH = Namespace
         .getNamespace("http://www.openarchives.org/OAI/2.0/");

   private boolean inOai;

   /**
    * This will create an <code>XMLOutputter</code> with the specified
    * format characteristics.  Note the format object is cloned internally
    * before use.
    */
   public OpenWISXMLOutputter(Format format) {
      super(format);
   }

   /**
    * Overridden to handle exclusion of ns filters.
    * {@inheritDoc}
    * @see org.jdom.output.XMLOutputter#printElement(java.io.Writer, org.jdom.Element, int, org.jdom.output.XMLOutputter.NamespaceStack)
    */
   @Override
   protected void printElement(Writer out, Element element, int level,
         org.jdom.output.XMLOutputter.NamespaceStack namespaces) throws IOException {
      if (OAI_PMH_ROOT.equals(element.getName())) {
         inOai = true;
      } else if (inOai) {
         // empty namespaces to avoid filtering
         while (namespaces.size() > 0) {
            namespaces.pop();
         }
         // Add default OAI PMH namespace.
         namespaces.push(Namespace.getNamespace("http://www.openarchives.org/OAI/2.0/"));
         inOai = false;
      }
      super.printElement(out, element, level, namespaces);
   }

}
