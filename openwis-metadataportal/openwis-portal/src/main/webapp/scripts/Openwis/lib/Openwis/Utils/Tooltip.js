Ext.ns('Openwis.Utils.Tooltip');

Openwis.Utils.Tooltip.Display = function addTooltip(value, metadata, record, rowIndex, colIndex, store){
    metadata.attr = 'ext:qtip="' + value + '"';
    if (value.length > 100)
	{
		return value.substring(0, 100);
	}
	return value;
};