//=============================================================================
//=== This program is free software; you can redistribute it and/or modify
//=== it under the terms of the GNU General Public License as published by
//=== the Free Software Foundation; either version 2 of the License, or (at
//=== your option) any later version.
//===
//=== This program is distributed in the hope that it will be useful, but
//=== WITHOUT ANY WARRANTY; without even the implied warranty of
//=== MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//=== General Public License for more details.
//===
//=== You should have received a copy of the GNU General Public License
//=== along with this program; if not, write to the Free Software
//=== Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//=== Contact: Jeroen Ticheler email: geonetwork@osgeo.org
//==============================================================================
package org.fao.geonet.services.util.z3950.transformers;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.jdom.input.DOMBuilder;
import org.jzkit.search.util.RecordConversion.FragmentTransformationException;
import org.jzkit.search.util.RecordConversion.FragmentTransformer;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class GNTransformer.
 */
@SuppressWarnings("rawtypes")
public class GNTransformer extends FragmentTransformer {

   /** The stylesheet. */
   private String stylesheet;

   /** The htmldb. */
   private DocumentBuilder htmldb = null;

   /**
    * Instantiates a new gN transformer.
    *
    * @param from the from
    * @param to the to
    * @param properties the properties
    * @param context the context
    * @param ctx the ctx
    */
   public GNTransformer(String from, String to, Map properties, Map context, ApplicationContext ctx) {
      super(from, to, properties, context, ctx);

      stylesheet = (String) properties.get("Sheet");
      if (stylesheet == null) {
         Log.error(
               Geonet.SEARCH_ENGINE,
               "Failed to get name of stylesheet from properties - looking for property with name 'Sheet' - found instead: "
                     + properties);
         return;
      }

      try {
         stylesheet = ctx.getResource(stylesheet).getFile().getAbsolutePath();
         Log.debug(Geonet.SEARCH_ENGINE, "Stylesheet for " + from + " to " + to + " is "
               + stylesheet);
      } catch (Exception e) {
         Log.error(Geonet.SEARCH_ENGINE, "Problem with stylesheet: " + stylesheet);
         e.printStackTrace();
      }

      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setValidating(false);
         dbf.setNamespaceAware(true);
         dbf.setIgnoringComments(false);
         dbf.setIgnoringElementContentWhitespace(false);
         dbf.setExpandEntityReferences(false);
         htmldb = dbf.newDocumentBuilder();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.util.RecordConversion.FragmentTransformer#transform(org.w3c.dom.Document, java.util.Map)
    */
   @Override
   public Document transform(Document input, Map additionalProperties)
         throws FragmentTransformationException {

      DOMBuilder builder = new DOMBuilder();
      org.jdom.Document jdomDoc = builder.build(input);

      // now transform using stylesheet passed in

      org.jdom.Element elem = null;
      try {
         elem = Xml.transform(jdomDoc.detachRootElement(), stylesheet);
      } catch (Exception e) {
         throw new FragmentTransformationException(e.getMessage());
      }

      // give back a DOM document with html as text (suits jzkit which expects
      // html output to be text because parsing may not work)
      Document output = null;
      try {
         output = htmldb.newDocument();
         Element root = output.createElement("HTML");
         root.appendChild(output.createTextNode(Xml.getString(elem)));
         output.appendChild(root);
      } catch (Exception e) {
         throw new FragmentTransformationException(e.getMessage());
      }

      return output;
   }

}
