/**
 * Copyright (c) 2008-2009 The Open Source Geospatial Foundation
 * 
 * Published under the BSD license.
 * See http://svn.geoext.org/core/trunk/geoext/license.txt for the full text
 * of the license.
 */

/** api: (define)
 *  module = GeoExt.data
 *  class = AttributeReader
 *  base_link = `Ext.data.DataReader <http://extjs.com/deploy/dev/docs/?class=Ext.data.DataReader>`_
 */
Ext.namespace("GeoExt.data");

/** api: constructor
 *  .. class:: AttributeReader(meta, recordType)
 *  
 *      :arg meta: ``Object`` Reader configuration.
 *      :arg recordType: ``Array or Ext.data.Record`` An array of field
 *          configuration objects or a record object.
 *
 *      Create a new attributes reader object.
 *      
 *      Valid meta properties:
 *      
 *      * format - ``OpenLayers.Format`` A parser for transforming the XHR response
 *        into an array of objects representing attributes.  Defaults to
 *        an ``OpenLayers.Format.WFSDescribeFeatureType`` parser.
 *      * ignore - ``Object`` Properties of the ignore object should be field names.
 *        Values are either arrays or regular expressions.
 */
GeoExt.data.AttributeReader = function(meta, recordType) {
    meta = meta || {};
    if(!meta.format) {
        meta.format = new OpenLayers.Format.WFSDescribeFeatureType();
    }
    GeoExt.data.AttributeReader.superclass.constructor.call(
        this, meta, recordType || meta.fields
    );
};

Ext.extend(GeoExt.data.AttributeReader, Ext.data.DataReader, {

    /** private: method[read]
     *  :arg request: ``Object`` The XHR object that contains the parsed doc.
     *  :return: ``Object``  A data block which is used by an ``Ext.data.Store``
     *      as a cache of ``Ext.data.Records``.
     *  
     *  This method is only used by a DataProxy which has retrieved data from a
     *  remote server.
     */
    read: function(request) {
        var data = request.responseXML;
        if(!data || !data.documentElement) {
            data = request.responseText;
        }
        return this.readRecords(data);
    },

    /** private: method[readRecords]
     *  :arg data: ``DOMElement or String or Array`` A document element or XHR
     *      response string.  As an alternative to fetching attributes data from
     *      a remote source, an array of attribute objects can be provided given
     *      that the properties of each attribute object map to a provided field
     *      name.
     *  :return: ``Object`` A data block which is used by an ``Ext.data.Store``
     *      as a cache of ``Ext.data.Records``.
     *  
     *  Create a data block containing Ext.data.Records from an XML document.
     */
    readRecords: function(data) {
        var attributes;
        if(data instanceof Array) {
            attributes = data;
        } else {
            // only works with one featureType in the doc
            attributes = this.meta.format.read(data).featureTypes[0].properties;
        }
        var recordType = this.recordType;
        var fields = recordType.prototype.fields;
        var numFields = fields.length;
        var attr, values, name, record, ignore, matches, value, records = [];
        for(var i=0, len=attributes.length; i<len; ++i) {
            ignore = false;
            attr = attributes[i];
            values = {};
            for(var j=0; j<numFields; ++j) {
                name = fields.items[j].name;
                value = attr[name];
                if(this.meta.ignore && this.meta.ignore[name]) {
                    matches = this.meta.ignore[name];
                    if(typeof matches == "string") {
                        ignore = (matches === value);
                    } else if(matches instanceof Array) {
                        ignore = (matches.indexOf(value) > -1);
                    } else if(matches instanceof RegExp) {
                        ignore = (matches.test(value));
                    }
                    if(ignore) {
                        break;
                    }
                }
                values[name] = attr[name];
            }
            if(!ignore) {
                records[records.length] = new recordType(values);
            }
        }

        return {
            success: true,
            records: records,
            totalRecords: records.length
        };

    }

});
