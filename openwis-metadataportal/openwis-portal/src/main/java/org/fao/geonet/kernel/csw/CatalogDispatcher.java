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

package org.fao.geonet.kernel.csw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.server.sources.ServiceRequest.InputMethod;
import jeeves.server.sources.ServiceRequest.OutputMethod;
import jeeves.utils.Log;
import jeeves.utils.SOAPUtil;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.exceptions.CatalogException;
import org.fao.geonet.csw.common.exceptions.MissingParameterValueEx;
import org.fao.geonet.csw.common.exceptions.NoApplicableCodeEx;
import org.fao.geonet.csw.common.exceptions.OperationNotSupportedEx;
import org.fao.geonet.kernel.csw.services.DescribeRecord;
import org.fao.geonet.kernel.csw.services.GetCapabilities;
import org.fao.geonet.kernel.csw.services.GetDomain;
import org.fao.geonet.kernel.csw.services.GetRecordById;
import org.fao.geonet.kernel.csw.services.GetRecords;
import org.fao.geonet.kernel.csw.services.Harvest;
import org.fao.geonet.kernel.csw.services.Transaction;
import org.jdom.Element;

//=============================================================================

public class CatalogDispatcher {
   public static Map<String, CatalogService> hmServices = new HashMap<String, CatalogService>();

   //---------------------------------------------------------------------------
   //---
   //--- Constructor
   //---
   //---------------------------------------------------------------------------

   public CatalogDispatcher(ServiceConfig config, String appPath) {
      super();
      // Registering
      register(new DescribeRecord());
      register(new GetCapabilities());
      register(new GetDomain());
      register(new GetRecordById());
      register(new GetRecords(config, appPath));
      register(new Harvest());
      register(new Transaction());
   }

   //---------------------------------------------------------------------------

   private void register(CatalogService s) {
      hmServices.put(s.getName(), s);
   }

   //---------------------------------------------------------------------------
   //---
   //--- API methods
   //---
   //---------------------------------------------------------------------------

   public Element dispatch(Element request, ServiceContext context) {
      context.info("Received:\n" + Xml.getString(request));

      InputMethod im = context.getInputMethod();
      OutputMethod om = context.getOutputMethod();

      boolean inSOAP = (im == InputMethod.SOAP);
      boolean outSOAP = (inSOAP || om == OutputMethod.SOAP);

      CatalogException exc;

      try {
         if (inSOAP)
            request = SOAPUtil.unembed(request);

         Element response = dispatchI(request, context);

         if (outSOAP)
            response = SOAPUtil.embed(response);

         return response;
      }

      catch (CatalogException e) {
         exc = e;
      }

      catch (Exception e) {
         context.info("Exception stack trace : \n" + Util.getStackTrace(e));
         // TODO what's this ?
         exc = new NoApplicableCodeEx(e.toString());
      }

      Element response = CatalogException.marshal(exc);
      boolean sender = (exc instanceof NoApplicableCodeEx);

      if (outSOAP)
         return SOAPUtil.embedExc(response, sender, exc.getCode(), exc.toString());

      //TODO: need to set the status code

      return response;
   }

   //---------------------------------------------------------------------------
   //---
   //--- Private method
   //---
   //---------------------------------------------------------------------------

   private Element dispatchI(Element request, ServiceContext context) throws CatalogException {
      InputMethod im = context.getInputMethod();

      if (im == InputMethod.XML || im == InputMethod.SOAP) {
         String operation = request.getName();

         CatalogService cs = hmServices.get(operation);

         if (cs == null)
            throw new OperationNotSupportedEx(operation);

         Log.info(Geonet.CSW, "Dispatching operation : " + operation);

         return cs.execute(request, context);
      }

      else //--- GET or POST/www-encoded request
      {
         Map<String, String> params = extractParams(request);

         String operation = params.get("request");

         if (operation == null)
            throw new MissingParameterValueEx("request");

         CatalogService cs = hmServices.get(operation);

         if (cs == null)
            throw new OperationNotSupportedEx(operation);

         request = cs.adaptGetRequest(params);

         context.debug("Adapted GET request is:\n" + Xml.getString(request));
         context.info("Dispatching operation : " + operation);

         return cs.execute(request, context);
      }
   }

   //---------------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   private Map<String, String> extractParams(Element request) {
      Map<String, String> hm = new HashMap<String, String>();

      List<Element> params = request.getChildren();

      String name;
      String value;
      for (Element param : params) {
         name = param.getName().toLowerCase();
         value = param.getTextTrim();

         hm.put(name, value);
      }

      return hm;
   }

}
