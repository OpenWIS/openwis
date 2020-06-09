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

import org.fao.oaipmh.OaiPmh;
import org.fao.oaipmh.util.Lib;
import org.jdom.Element;
import org.jdom.Namespace;

//=============================================================================

public class MetadataFormat {
    //---------------------------------------------------------------------------
    //---
    //--- Constructor
    //---
    //---------------------------------------------------------------------------

    public MetadataFormat() {
    }

    //---------------------------------------------------------------------------

    public MetadataFormat(Element mdForm) {
        setPrefix(mdForm.getChildText("metadataPrefix", OaiPmh.Namespaces.OAI_PMH));
        setSchema(mdForm.getChildText("schema", OaiPmh.Namespaces.OAI_PMH));

        String ns = mdForm.getChildText("metadataNamespace", OaiPmh.Namespaces.OAI_PMH);
        setNamespace(Namespace.getNamespace(ns));
    }

    //---------------------------------------------------------------------------

    public MetadataFormat(String prefix, String schema, String ns) {
        this.setPrefix(prefix);
        this.setSchema(schema);

        setNamespace(Namespace.getNamespace(ns));
    }

    //---------------------------------------------------------------------------
    //---
    //--- API methods
    //---
    //---------------------------------------------------------------------------

    public Element toXml() {
        Element root = new Element("metadataFormat", OaiPmh.Namespaces.OAI_PMH);

        Lib.add(root, "metadataPrefix", getPrefix());
        Lib.add(root, "schema", getSchema());
        Lib.add(root, "metadataNamespace", getNamespace().getURI());

        return root;
    }

    //---------------------------------------------------------------------------
    //---
    //--- Variables
    //---
    //---------------------------------------------------------------------------

    private String prefix;
    private String schema;
    private Namespace namespace;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }
}

//=============================================================================

