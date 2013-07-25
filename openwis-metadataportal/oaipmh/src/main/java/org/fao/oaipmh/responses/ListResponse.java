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

import java.io.IOException;
import java.util.Iterator;

import org.fao.oaipmh.OaiPmh;
import org.fao.oaipmh.exceptions.OaiPmhException;
import org.fao.oaipmh.requests.ListRequest;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

//=============================================================================

public abstract class ListResponse extends AbstractResponse
{
	private ListRequest listReq;
	private ResumptionToken token;
	private Iterator<Element> iterator;

	/**
	 * Default constructor.
	 * Builds a ListResponse.
	 */
	public ListResponse() {}

	/**
	 * Default constructor.
	 * Builds a ListResponse.
	 * @param lr
	 * @param response
	 */
	public ListResponse(ListRequest lr, Element response)
	{
		super(response);

		listReq = lr;
		build(response);
	}
	
	/**
	 * Check response has a next element
	 * @return a boolean 
	 */
   public boolean hasNextItem() {
      if (iterator.hasNext()) {
         return true;
      }
      return false;
   }
	
   /**
    * Check response has a next page
    * @return
    */
   public boolean hasNextPage() {
      if (token != null && !token.isTokenEmpty()) {
         return true;
      }
      return false;
   }

	/**
	 * Description goes here.
	 * @return
	 * @throws IOException
	 * @throws OaiPmhException
	 * @throws JDOMException
	 * @throws SAXException
	 * @throws Exception
	 */
   public Object nextItem() throws IOException, OaiPmhException, JDOMException, SAXException,
         Exception {
      if (!iterator.hasNext()) {
         throw new RuntimeException("Iterator exausted");
      }
      return createObject(iterator.next());
   }
	
   /**
    * Description goes here.
    * @return
    * @throws IOException
    * @throws OaiPmhException
    * @throws JDOMException
    * @throws SAXException
    * @throws Exception
    */
   public Object nextPage() throws IOException, OaiPmhException, JDOMException, SAXException,
         Exception {
      if (token == null || token.isTokenEmpty()) {
         throw new RuntimeException("Iterator exausted");
      }

      build(listReq.resume(token));

      if (!iterator.hasNext()) {
         throw new RuntimeException("Iterator exausted");
      }

      return createObject(iterator.next());
   }
	
	public abstract int getSize();

	//---------------------------------------------------------------------------

	public ResumptionToken getResumptionToken() { return token; }

	//---------------------------------------------------------------------------

	public void setResumptionToken(ResumptionToken token)
	{
		this.token = token;
	}
	

	//---------------------------------------------------------------------------
	//---
	//--- Protected methods
	//---
	//---------------------------------------------------------------------------

	protected abstract Object createObject(Element object);
	protected abstract String getListElementName();

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private void build(Element response)
	{
		Element operElem = response.getChild(listReq.getVerb(), OaiPmh.Namespaces.OAI_PMH);
		Element resToken = operElem.getChild("resumptionToken", OaiPmh.Namespaces.OAI_PMH);

		token    = (resToken == null) ? null : new ResumptionToken(resToken);
		iterator = operElem.getChildren(getListElementName(), OaiPmh.Namespaces.OAI_PMH).iterator();
	}

}

//=============================================================================

