//=============================================================================
//===	Copyright (C) 2001-2005 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This library is free software; you can redistribute it and/or
//===	modify it under the terms of the GNU Lesser General Public
//===	License as published by the Free Software Foundation; either
//===	version 2.1 of the License, or (at your option) any later version.
//===
//===	This library is distributed in the hope that it will be useful,
//===	but WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//===	Lesser General Public License for more details.
//===
//===	You should have received a copy of the GNU Lesser General Public
//===	License along with this library; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: GeoNetwork@fao.org
//==============================================================================

package jeeves.server.dispatchers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jeeves.server.context.ServiceContext;

import org.jdom.Element;


//=============================================================================

/**
 * This class represents a single output page of a service
 */
public class ErrorPage extends AbstractPage
{
	private int statusCode;
	
	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public boolean matches(String id)
	{
		String test = getTestCondition();

		//--- if the test is missing -> this is the last error page

		if (test == null)
			return true;

		return test.equals(id);
	}
	
   public Map<String, Object> invokeGuiServicesWithJsp(ServiceContext context, Element response) {
      HashMap<String, Object> attrMap = new HashMap<String, Object>();
      attrMap.put("context", context);
      
      for (Iterator iterator = response.getContent().iterator(); iterator.hasNext();) {
         Element elt = (Element) iterator.next();
         if ("message".equals(elt.getName())) {
            attrMap.put("errorMsg", elt.getValue());
         } else if ("needLogout".equals(elt.getName())) {
            attrMap.put("needLogout", elt.getValue());
         } 
      }
      //TODO add stacktrace + msg
      attrMap.put("statusCode", statusCode);
      return attrMap;
   }
}

//=============================================================================


