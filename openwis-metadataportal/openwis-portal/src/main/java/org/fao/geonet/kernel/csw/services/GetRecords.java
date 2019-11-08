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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.ConstraintLanguage;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.csw.common.ElementSetName;
import org.fao.geonet.csw.common.OutputSchema;
import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.csw.common.exceptions.CatalogException;
import org.fao.geonet.csw.common.exceptions.InvalidParameterValueEx;
import org.fao.geonet.csw.common.exceptions.MissingParameterValueEx;
import org.fao.geonet.csw.common.exceptions.NoApplicableCodeEx;
import org.fao.geonet.kernel.csw.CatalogConfiguration;
import org.fao.geonet.kernel.csw.CatalogService;
import org.fao.geonet.kernel.csw.services.getrecords.FieldMapper;
import org.fao.geonet.kernel.csw.services.getrecords.SearchController;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.Pair;
import org.fao.geonet.kernel.search.SortingInfo;
import org.fao.geonet.kernel.search.SortingInfoImpl;
import org.fao.geonet.util.ISODate;
import org.jdom.Attribute;
import org.jdom.Element;
import org.openwis.metadataportal.common.search.SortDir;

//=============================================================================

public class GetRecords extends AbstractOperation implements CatalogService {
   //---------------------------------------------------------------------------
   //---
   //--- Constructor
   //---
   //---------------------------------------------------------------------------

   private final SearchController _searchController;

   public GetRecords(ServiceConfig config, String appPath) {
      _searchController = new SearchController(config, appPath);
   }

   //---------------------------------------------------------------------------
   //---
   //--- API methods
   //---
   //---------------------------------------------------------------------------

   @Override
   public String getName() {
      return "GetRecords";
   }

   //---------------------------------------------------------------------------

   @Override
   public Element execute(Element request, ServiceContext context) throws CatalogException {
      checkService(request);
      checkVersion(request);
      checkOutputFormat(request);
      checkTypenames(request);

      String timeStamp = new ISODate().toString();

      int startPos = getStartPosition(request);
      int maxRecords = getMaxRecords(request);

      Element query = request.getChild("Query", Csw.NAMESPACE_CSW);

      ResultType resultType = ResultType.parse(request.getAttributeValue("resultType"));
      OutputSchema outSchema = OutputSchema.parse(request.getAttributeValue("outputSchema"));
      Set<String> elemNames = getElementNames(query);
      ElementSetName setName = ElementSetName.FULL;

      // If any element names are specified, it's an ad hoc query and overrides the
      // element set name default.  In that case, we set setName to FULL instead of
      // SUMMARY so that we can retrieve a CSW:Record and trim out the elements that
      // aren't in the elemNames set.
      if ((elemNames == null) || (elemNames.size() == 0))
         setName = getElementSetName(query, ElementSetName.SUMMARY);

      Element constr = query.getChild("Constraint", Csw.NAMESPACE_CSW);
      Element filterExpr = getFilterExpression(constr);
      String filterVersion = getFilterVersion(constr);

      // Get max hits to be used for summary - CSW GeoNetwork extension
      int maxHitsInSummary = 1000;
      String sMaxRecordsInKeywordSummary = query.getAttributeValue("maxHitsInSummary");
      if (sMaxRecordsInKeywordSummary != null) {
         // TODO : it could be better to use service config parameter instead
         // sMaxRecordsInKeywordSummary = config.getValue("maxHitsInSummary", "1000");
         maxHitsInSummary = Integer.parseInt(sMaxRecordsInKeywordSummary);
      }

      SortingInfo sort = getSortFields(request);

      Element response;

      if (resultType == ResultType.VALIDATE) {
         //String schema = context.getAppPath() + Geonet.Path.VALIDATION + "csw/2.0.2/csw-2.0.2.xsd";
         String schema = context.getAppPath() + Geonet.Path.VALIDATION
               + "csw202_apiso100/csw/2.0.2/CSW-discovery.xsd";

         Log.debug(Geonet.CSW, "Validating request against " + schema);

         try {
            Xml.validate(schema, request);
         }

         catch (Exception e) {
            throw new NoApplicableCodeEx("Request failed validation:" + e.toString());
         }

         response = new Element("Acknowledgement", Csw.NAMESPACE_CSW);
         response.setAttribute("timeStamp", timeStamp);

         Element echoedRequest = new Element("EchoedRequest", Csw.NAMESPACE_CSW);
         echoedRequest.addContent((Element) request.clone());

         response.addContent(echoedRequest);
      } else {

         response = new Element(getName() + "Response", Csw.NAMESPACE_CSW);

         Attribute schemaLocation = new Attribute(
               "schemaLocation",
               "http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd",
               Csw.NAMESPACE_XSI);
         response.setAttribute(schemaLocation);

         Element status = new Element("SearchStatus", Csw.NAMESPACE_CSW);
         status.setAttribute("timestamp", timeStamp);

         response.addContent(status);
         Pair<Element, Element> search = _searchController.search(context, startPos, maxRecords,
               resultType, outSchema, setName, filterExpr, filterVersion, sort, elemNames,
               maxHitsInSummary);

         // Only add GeoNetwork summary on results_with_summary option
         if (resultType == ResultType.RESULTS_WITH_SUMMARY)
            response.addContent(search.one());

         response.addContent(search.two());
      }

      return response;
   }

   //---------------------------------------------------------------------------

   @Override
   public Element adaptGetRequest(Map<String, String> params) throws CatalogException {
      String service = params.get("service");
      String version = params.get("version");
      String resultType = params.get("resulttype");
      String outputFormat = params.get("outputformat");
      String outputSchema = params.get("outputschema");
      String startPosition = params.get("startposition");
      String maxRecords = params.get("maxrecords");
      String hopCount = params.get("hopcount");
      String distribSearch = params.get("distributedsearch");
      String typeNames = params.get("typenames");
      String elemSetName = params.get("elementsetname");
      String elemName = params.get("elementname");
      String constraint = params.get("constraint");
      String constrLang = params.get("constraintlanguage");
      String constrLangVer = params.get("constraint_language_version");
      String sortby = params.get("sortby");

      //--- build POST request

      Element request = new Element(getName(), Csw.NAMESPACE_CSW);

      setAttrib(request, "service", service);
      setAttrib(request, "version", version);
      setAttrib(request, "resultType", resultType);
      setAttrib(request, "outputFormat", outputFormat);
      setAttrib(request, "outputSchema", outputSchema);
      setAttrib(request, "startPosition", startPosition);
      setAttrib(request, "maxRecords", maxRecords);

      if (distribSearch != null && distribSearch.equals("true")) {
         Element ds = new Element("DistributedSearch", Csw.NAMESPACE_CSW);
         ds.setText("TRUE");

         if (hopCount != null)
            ds.setAttribute("hopCount", hopCount);

         request.addContent(ds);
      }

      //------------------------------------------------------------------------
      //--- build query element

      Element query = new Element("Query", Csw.NAMESPACE_CSW);
      request.addContent(query);

      if (typeNames != null)
         setAttrib(query, "typeNames", typeNames.replace(',', ' '));

      //--- these 2 are in mutual exclusion

      addElement(query, "ElementSetName", elemSetName);
      fill(query, "ElementName", elemName);

      //------------------------------------------------------------------------
      //--- handle constraint

      ConstraintLanguage language = ConstraintLanguage.parse(constrLang);

      if (constraint != null) {
         Element constr = new Element("Constraint", Csw.NAMESPACE_CSW);
         query.addContent(constr);

         if (language == ConstraintLanguage.CQL)
            addElement(constr, "CqlText", constraint);
         else
            try {
               constr.addContent(Xml.loadString(constraint, false));
            } catch (Exception e) {
               e.printStackTrace();
               throw new NoApplicableCodeEx("Constraint is not a valid xml");
            }

         setAttrib(constr, "version", constrLangVer);
      }

      //------------------------------------------------------------------------
      //--- handle sortby

      if (sortby != null) {
         Element sortBy = new Element("SortBy", Csw.NAMESPACE_OGC);
         query.addContent(sortBy);

         StringTokenizer st = new StringTokenizer(sortby, ",");

         while (st.hasMoreTokens()) {
            String sortInfo = st.nextToken();
            String field = sortInfo.substring(0, sortInfo.length() - 2);
            boolean ascen = sortInfo.endsWith(":A");

            Element sortProp = new Element("SortProperty", Csw.NAMESPACE_OGC);
            sortBy.addContent(sortProp);

            Element propName = new Element("PropertyName", Csw.NAMESPACE_OGC).setText(field);
            Element sortOrder = new Element("SortOrder", Csw.NAMESPACE_OGC).setText(ascen ? "ASC"
                  : "DESC");

            sortProp.addContent(propName);
            sortProp.addContent(sortOrder);
         }
      }

      return request;
   }

   //---------------------------------------------------------------------------

   @Override
   public Element retrieveValues(String parameterName) throws CatalogException {

      Element listOfValues = null;
      if (parameterName.equalsIgnoreCase("resultType")
            || parameterName.equalsIgnoreCase("outputFormat")
            || parameterName.equalsIgnoreCase("elementSetName")
            || parameterName.equalsIgnoreCase("outputSchema")
            || parameterName.equalsIgnoreCase("typenames"))
         listOfValues = new Element("ListOfValues", Csw.NAMESPACE_CSW);

      // Handle resultType parameter
      if (parameterName.equalsIgnoreCase("resultType")) {
         List<Element> values = new ArrayList<Element>();
         ResultType[] resultType = ResultType.values();
         for (ResultType aResultType : resultType) {
            String value = aResultType.toString();
            values.add(new Element("Value", Csw.NAMESPACE_CSW).setText(value));
         }
         if (listOfValues != null) {
            listOfValues.addContent(values);
         }
      }

      // Handle elementSetName parameter
      if (parameterName.equalsIgnoreCase("elementSetName")) {
         List<Element> values = new ArrayList<Element>();
         ElementSetName[] esn = ElementSetName.values();
         for (ElementSetName anEsn : esn) {
            String value = anEsn.toString();
            values.add(new Element("Value", Csw.NAMESPACE_CSW).setText(value));
         }
         if (listOfValues != null) {
            listOfValues.addContent(values);
         }
      }

      // Handle outputFormat parameter
      if (parameterName.equalsIgnoreCase("outputformat")) {
         Set<String> formats = CatalogConfiguration.getGetRecordsOutputFormat();
         List<Element> values = createValuesElement(formats);
         if (listOfValues != null) {
            listOfValues.addContent(values);
         }
      }

      // Handle outputSchema parameter
      if (parameterName.equalsIgnoreCase("outputSchema")) {
         Set<String> namespacesUri = CatalogConfiguration.getGetRecordsOutputSchema();
         List<Element> values = createValuesElement(namespacesUri);
         if (listOfValues != null) {
            listOfValues.addContent(values);
         }
      }

      // Handle typenames parameter
      if (parameterName.equalsIgnoreCase("typenames")) {
         Set<String> typenames = CatalogConfiguration.getGetRecordsTypenames();
         List<Element> values = createValuesElement(typenames);
         if (listOfValues != null) {
            listOfValues.addContent(values);
         }
      }

      return listOfValues;
   }

   //---------------------------------------------------------------------------
   //---
   //--- Private methods
   //---
   //---------------------------------------------------------------------------

   private void checkOutputFormat(Element request) throws InvalidParameterValueEx {
      String format = request.getAttributeValue("outputFormat");

      if (format == null)
         return;

      if (!format.equals("application/xml"))
         throw new InvalidParameterValueEx("outputFormat", format);
   }

   //---------------------------------------------------------------------------

   private void checkTypenames(Element request) throws MissingParameterValueEx {
      Element query = request.getChild("Query", Csw.NAMESPACE_CSW);

      if (query == null)
         return;

      if (query.getAttributeValue("typeNames") == null)
         throw new MissingParameterValueEx("typeNames");
   }

   //---------------------------------------------------------------------------

   private int getStartPosition(Element request) throws InvalidParameterValueEx {
      String start = request.getAttributeValue("startPosition");

      if (start == null)
         return 0;

      try {
         int value = Integer.parseInt(start);

         if (value >= 0)
            return value;
      } catch (NumberFormatException ignored) {
         // TODO what's with this?
      }

      throw new InvalidParameterValueEx("startPosition", start);
   }

   //---------------------------------------------------------------------------

   private int getMaxRecords(Element request) throws InvalidParameterValueEx {
      String max = request.getAttributeValue("maxRecords");

      if (max == null)
         return 10;

      try {
         int value = Integer.parseInt(max);

         if (value >= 1)
            return value;
      } catch (NumberFormatException ignored) {
         // TODO what's with this ?
      }

      throw new InvalidParameterValueEx("maxRecords", max);
   }


   //---------------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   private SortingInfo getSortFields(Element request) {
      SortingInfo result = new SortingInfoImpl();

      Element query = request.getChild("Query", Csw.NAMESPACE_CSW);
      Element sortBy = query.getChild("SortBy", Csw.NAMESPACE_OGC);
      if (query != null && sortBy != null) {

         String field;
         String order;
         SortDir sd;
         IndexField indexField;
         for (Element el : (List<Element>) sortBy.getChildren()) {
            field = el.getChildText("PropertyName", Csw.NAMESPACE_OGC);
            order = el.getChildText("SortOrder", Csw.NAMESPACE_OGC);

            // Map CSW search field to for sorting. And if not mapped assumes the field is a field.
            indexField = FieldMapper.map(field);

            if (indexField != null) {
               sd = SortDir.valueOf(order);
               if (sd == null) {
                  sd = SortDir.ASC;
               }
               result.add(indexField, sd);
            }
         }
      }
      // we always want to keep the relevancy as part of the sorting mechanism
      result.add(IndexField.SCORE, SortDir.DESC);

      return result;
   }

   //---------------------------------------------------------------------------

   private Set<String> getElementNames(Element query) {
      if (query == null)
         return null;

      Iterator<Element> i = query.getChildren("ElementName", query.getNamespace()).iterator();

      if (!i.hasNext())
         return null;

      HashSet<String> hs = new HashSet<String>();

      while (i.hasNext()) {
         Element elem = i.next();

         hs.add(elem.getText());
      }

      return hs;
   }

}

//=============================================================================

