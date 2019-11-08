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

package org.fao.oaipmh.responses;

import java.util.HashMap;
import java.util.Map;

import org.fao.oaipmh.OaiPmh;
import org.fao.oaipmh.util.ISODate;
import org.jdom.Attribute;
import org.jdom.Element;

//=============================================================================

public abstract class AbstractResponse
{
   private Element response;
   private ISODate responseDate;
   
   private Map<String, String> request = new HashMap<String, String>();

	/**
	 * Default constructor.
	 * Builds a AbstractResponse.
	 */
	public AbstractResponse()
	{
		responseDate = new ISODate();
	}

	/**
	 * Default constructor.
	 * Builds a AbstractResponse.
	 * @param response
	 */
	public AbstractResponse(Element response)
	{
		this.response = response;
		build(response);
	}

   /**
    * Sets the response.
    * @param response the response to set.
    */
   public void setResponse(Element response) {
      this.response = response;
   }

   /**
    * Gets the response.
    * @return the response.
    */
   public Element getResponse() {
      return response;
   }

   /**
    * Gets the responseDate.
    * @return the responseDate.
    */
   public ISODate getResponseDate() {
      return responseDate;
   }

   /**
    * Sets the responseDate.
    * @param responseDate the responseDate to set.
    */
   public void setResponseDate(ISODate responseDate) {
      this.responseDate = responseDate;
   }

   public abstract Element toXml();

	/**
	 * Add method
	 * @param parent
	 * @param name
	 * @param value
	 */
	protected void add(Element parent, String name, String value)
	{
		parent.addContent(new Element(name, OaiPmh.Namespaces.OAI_PMH).setText(value));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void build(Element response)
	{
		//--- save response date

		responseDate = new ISODate(response.getChildText("responseDate", OaiPmh.Namespaces.OAI_PMH));

		//--- save request parameters

		Element req = response.getChild("request", OaiPmh.Namespaces.OAI_PMH);

      for (Object o : req.getAttributes()) {
         Attribute attr = (Attribute) o;
         request.put(attr.getName(), attr.getValue());
      }
	}

}

//=============================================================================

