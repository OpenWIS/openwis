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

package org.fao.geonet.kernel;

import jeeves.utils.Xml;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

//=============================================================================

public class SchemaSuggestions
{
	private Hashtable<String, Element> htFields = new Hashtable<String, Element>();

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public SchemaSuggestions(String xmlSuggestFile) throws Exception
	{
		Element sugg = Xml.loadFile(xmlSuggestFile);

		List list = sugg.getChildren();

        for (Object aList : list) {
            Element el = (Element) aList;

            if (el.getName().equals("field")) {
                htFields.put(el.getAttributeValue("name"), el);
            }
        }
	}

	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	public boolean isSuggested(String parent, String child)
	{
		Element el = htFields.get(parent);

		if (el == null)
			return false;

		List list = el.getChildren();

        for (Object aList : list) {
            el = (Element) aList;

            if (el.getName().equals("suggest")) {
                String name = el.getAttributeValue("name");

                if (child.equals(name)) {
                    return true;
                }
            }
        }

		return false;
	}
	
	/**
	 * Return true if parent element is defined in suggestion
	 * file and check that suggested elements are valid children
	 * of current element.
	 * <br/>
	 * For example, gmd:extent could have suggestions as a child
	 * of gmd:identificationInfo or as a child of gmd:EX_TemporalExtent.
	 *  
	 * 
	 * @param parent
	 * @param childElements
	 * @return	true if having suggestion for at least one of its child elements.
	 */
	public boolean hasSuggestion(String parent, List<String> childElements) {
		Element el = htFields.get(parent);

		if (el == null)
			return false;	// No suggestion available for element
		else {
			for (String child : childElements) {
				if (isSuggested(parent, child))	
					return true; // At least one child element is suggested for this element type
			}
			return false;
		}
	}
}

//=============================================================================

