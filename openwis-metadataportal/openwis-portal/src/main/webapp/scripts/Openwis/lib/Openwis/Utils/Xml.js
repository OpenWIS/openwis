Ext.ns('Openwis.Utils.Xml');

/**
 * Gets a named attribute from a set of XML attributes as string.
 * @param attributes set of attributes
 * @param name key to identify the attribute
 * @return attribute (if any) or null
 */
Openwis.Utils.Xml.getAttribute = function(attributes, name) {
	var attribute = null;
	if (attributes != null) {
		attribute = attributes.getNamedItem(name);
	}
	return attribute;
};

/**
 * Gets a named attribute value from a set of XML attributes as string.
 * @param attributes set of attributes
 * @param name key to identify the attribute
 * @return attribute value (if any) or empty string
 */
Openwis.Utils.Xml.getAttributeValue = function(attributes, name) {
	var value = "";
	var attribute = Openwis.Utils.Xml.getAttribute(attributes, name);
	if (attribute != null) {
		value = attribute.nodeValue;
	}
	return value;
};

/**
 * Gets a named element from an XML text
 * @param xmlText text to parse
 * @param element name name of  the wanted element
 * @return element if found else null
 */
Openwis.Utils.Xml.getElement = function(xmlText, elementName) {
	var element = null
	var parser = new OpenLayers.Format.XML();
	var document = parser.read(xmlText);
	if (document != null) {
		element = parser.getChildEl(document, elementName);		
	}
    return element;
};
