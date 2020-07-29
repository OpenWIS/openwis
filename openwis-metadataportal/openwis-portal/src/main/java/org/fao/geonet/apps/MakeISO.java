//==============================================================================
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

package org.fao.geonet.apps;

import jeeves.utils.Xml;
import org.dlib.tools.FullTokenizer;
import org.jdom.Document;
import org.jdom.Element;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;

//==============================================================================

public class MakeISO
{
	public static void main(String args[]) throws Exception {
		// check args
		if (args.length != 1) {
			System.err.println("usage: makeISO file");
			System.exit(1);
		}

		try (
				FileInputStream is = new FileInputStream(new File(args[0]));
				FileOutputStream os = new FileOutputStream(new File(args[0] + ".sql"))) {

			BufferedReader ir = new BufferedReader(new InputStreamReader(is));
			String line;

			Element root = new Element("mapping");

			HashSet<String> set = new HashSet<String>();

			while ((line = ir.readLine()) != null) {
				FullTokenizer ft = new FullTokenizer(line, "|");
				String longCode = ft.nextToken();
				ft.nextToken();
				String shortCode = ft.nextToken();

				if (shortCode.length() == 2) {
					if (set.contains(shortCode))
						System.out.println("Skipped short code : " + shortCode);
					else {
						set.add(shortCode);

						Element elem = new Element("map");
						elem.setAttribute("longCode", longCode);
						elem.setAttribute("shortCode", shortCode);
						root.addContent(elem);
					}
				}
			}

			String xml = Xml.getString(new Document(root));
			try (BufferedWriter ow = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))) {
				ow.write(xml);
			}
			ir.close();
		}
	}

}

//==============================================================================

