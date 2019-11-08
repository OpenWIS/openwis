//=============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet.kernel.csw.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.csw.common.exceptions.CatalogException;
import org.fao.geonet.csw.common.exceptions.NoApplicableCodeEx;
import org.fao.geonet.csw.common.exceptions.OperationNotSupportedEx;
import org.fao.geonet.kernel.csw.CatalogConfiguration;
import org.fao.geonet.kernel.csw.CatalogDispatcher;
import org.fao.geonet.kernel.csw.CatalogService;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.Range;
import org.fao.geonet.kernel.search.TermFrequency;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.search.query.SearchException;

/**
 * The Class GetDomain. <P>
 * Explanation goes here. <P>
 */
public class GetDomain extends AbstractOperation implements CatalogService {

   /**
    * Instantiates a new gets the domain.
    */
   public GetDomain() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.csw.CatalogService#getName()
    */
   @Override
   public String getName() {
      return "GetDomain";
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.csw.CatalogService#execute(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element execute(Element request, ServiceContext context) throws CatalogException {
      checkService(request);
      checkVersion(request);

      Element response = new Element(getName() + "Response", Csw.NAMESPACE_CSW);

      String[] propertyNames = getParameters(request, "PropertyName");
      String[] parameterNames = getParameters(request, "ParameterName");

      // PropertyName handled first.
      if (propertyNames != null) {
         List<Element> domainValues;
         try {
            domainValues = handlePropertyName(propertyNames, context, false,
                  CatalogConfiguration.getMaxNumberOfRecordsForPropertyNames());
         } catch (Exception e) {
            Log.error(Geonet.CSW, "Error getting domain value for specified PropertyName : " + e);
            throw new NoApplicableCodeEx(
                  "Raised exception while getting domain value for specified PropertyName  : " + e);
         }
         response.addContent(domainValues);
         return response;
      }

      if (parameterNames != null) {
         List<Element> domainValues = handleParameterName(parameterNames);
         response.addContent(domainValues);
      }

      return response;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.csw.CatalogService#adaptGetRequest(java.util.Map)
    */
   @Override
   public Element adaptGetRequest(Map<String, String> params) {
      String service = params.get("service");
      String version = params.get("version");
      String parameterName = params.get("parametername");
      String propertyName = params.get("propertyname");

      Element request = new Element(getName(), Csw.NAMESPACE_CSW);

      setAttrib(request, "service", service);
      setAttrib(request, "version", version);

      //--- these 2 are in mutual exclusion.
      Element propName = new Element("PropertyName", Csw.NAMESPACE_CSW).setText(propertyName);
      Element paramName = new Element("ParameterName", Csw.NAMESPACE_CSW).setText(parameterName);

      // Property is handled first.
      if (propertyName != null && !propertyName.equals(""))
         request.addContent(propName);
      else if (parameterName != null && !parameterName.equals(""))
         request.addContent(paramName);

      return request;
   }

   //---------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.csw.CatalogService#retrieveValues(java.lang.String)
    */
   @Override
   public Element retrieveValues(String parameterName) throws CatalogException {
      return null;
   }

   /**
    * Handle property name.
    *
    * @param propertyNames the property names
    * @param context the context
    * @param freq the frequency
    * @param maxRecords the max records
    * @return the list
    * @throws Exception the exception
    */
   public List<Element> handlePropertyName(String[] propertyNames, ServiceContext context,
         boolean freq, int maxRecords) throws Exception {
      List<Element> result = new ArrayList<Element>();

      // Get SearchManager
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ISearchManager sm = gc.getSearchmanager();

      // Iterate over properties
      Element elt;
      Element child;
      IndexField field;
      for (String prop : propertyNames) {
         // Initialize element
         elt = new Element("DomainValues", Csw.NAMESPACE_CSW);
         result.add(elt);
         elt.setAttribute("type", "csw:Record"); // FIXME what should be the type ???
         child = new Element("PropertyName", Csw.NAMESPACE_CSW);
         elt.addContent(child);
         child.setText(prop);

         // Get index field
         field = CatalogConfiguration.getFieldMapping().get(prop);
         if (field != null) {
            if (CatalogConfiguration.getGetRecordsRangeFields().contains(field)) {
               // Ranged field
               child = getRangedElement(field, sm);
            } else {
               Comparator<TermFrequency> comp = getTermFrequencyComparator(freq);
               // Load values
               child = getValuesElement(field, maxRecords, comp, sm);
            }
            // add
            if (child != null) {
               elt.addContent(child);
            }
         }
      }
      return result;
   }

   /**
    * Gets the term frequency comparator.
    *
    * @param freq the freq
    * @return the term frequency comparator
    */
   private Comparator<TermFrequency> getTermFrequencyComparator(boolean freq) {
      Comparator<TermFrequency> result = null;
      // The base comparator
      final Comparator<TermFrequency> baseComparator = new Comparator<TermFrequency>() {
         @Override
         public int compare(TermFrequency o1, TermFrequency o2) {
            return o1.getTerm().compareTo(o2.getTerm());
         }
      };
      if (freq) {
         result = new Comparator<TermFrequency>() {
            @Override
            public int compare(TermFrequency o1, TermFrequency o2) {
               int res = o2.getFrequency() - o1.getFrequency();
               if (res == 0) {
                  res = baseComparator.compare(o1, o2);
               }
               return res;
            }
         };
      } else {
         result = baseComparator;
      }
      return result;
   }

   /**
    * Gets the ranged element.
    *
    * @param field the field
    * @param sm the search management
    * @return the ranged element
    * @throws SearchException the search exception
    */
   private Element getRangedElement(IndexField field, ISearchManager sm) throws SearchException {
      Element result = null;
      Range<String> range = sm.getRange(field);
      if (range != null) {
         result = new Element("RangeOfValues", Csw.NAMESPACE_CSW);

         Element bound = new Element("MinValue", Csw.NAMESPACE_CSW);
         bound.setText(range.getLowerBound());
         result.addContent(bound);

         bound = new Element("MaxValue", Csw.NAMESPACE_CSW);
         bound.setText(range.getUpperBound());
         result.addContent(bound);
      }
      return result;
   }

   /**
    * Gets the values element.
    *
    * @param field the field
    * @param maxRecords the max records
    * @param comparator the comparator
    * @param sm the search management
    * @return the values element
    * @throws SearchException the search exception
    */
   private Element getValuesElement(IndexField field, int maxRecords,
         Comparator<TermFrequency> comparator, ISearchManager sm) throws SearchException {
      Element result = null;
      List<TermFrequency> values = sm.getTermFrequency(field, maxRecords);
      if (!values.isEmpty()) {
         // Append to result
         result = new Element("ListOfValues", Csw.NAMESPACE_CSW);

         // Sort
         Collections.sort(values, comparator);
         for (TermFrequency tf : values) {
            result.addContent(createValueElement(tf.getTerm(), tf.getFrequency()));
         }
      }
      return result;
   }

   /**
    * Creates the value element.
    *
    * @param value the value
    * @param count the count
    * @return the element
    */
   private Element createValueElement(String value, int count) {
      Element result = new Element("Value", Csw.NAMESPACE_CSW);
      result.setAttribute("count", String.valueOf(count));
      result.setText(value);
      return result;
   }

   /**
    * Handle parameter name.
    *
    * @param parameterNames the parameter names
    * @return the list
    * @throws CatalogException the catalog exception
    */
   private List<Element> handleParameterName(String[] parameterNames) throws CatalogException {
      Element values;
      List<Element> domainValuesList = null;

      if (parameterNames != null && parameterNames.length > 0) {
         domainValuesList = new ArrayList<Element>();
         Element domainValues;
         Element pn;
         CatalogService cs;
         for (String paramName : parameterNames) {
            // Generate DomainValues element
            domainValues = new Element("DomainValues", Csw.NAMESPACE_CSW);

            // FIXME what should be the type ???
            domainValues.setAttribute("type", "csw:Record");

            // Set parameterName in any case.
            pn = new Element("ParameterName", Csw.NAMESPACE_CSW);
            domainValues.addContent(pn.setText(paramName));

            String operationName = paramName.substring(0, paramName.indexOf('.'));
            String parameterName = paramName.substring(paramName.indexOf('.') + 1);

            cs = checkOperation(operationName);
            values = cs.retrieveValues(parameterName);

            // values null mean that the catalog was unable to determine
            // anything about the specified parameter
            if (values != null) {
               domainValues.addContent(values);
            }
            // Add current DomainValues to the list
            domainValuesList.add(domainValues);
         }
      }
      return domainValuesList;
   }

   /**
    * Check operation.
    *
    * @param operationName the operation name
    * @return the catalog service
    * @throws CatalogException the catalog exception
    */
   private CatalogService checkOperation(String operationName) throws CatalogException {
      CatalogService cs = CatalogDispatcher.hmServices.get(operationName);
      if (cs == null) {
         throw new OperationNotSupportedEx(operationName);
      }
      return cs;
   }

   /**
    * Gets the parameters.
    *
    * @param request the request
    * @param parameter the parameter
    * @return the parameters
    */
   private String[] getParameters(Element request, String parameter) {
      if (request == null) {
         return null;
      }

      Element paramElt = request.getChild(parameter, Csw.NAMESPACE_CSW);
      if (paramElt == null) {
         return null;
      }

      String parameterName = paramElt.getText();
      return parameterName.split(",");
   }
}
