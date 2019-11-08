/**
 * Copyright (c) 2008-2009 The Open Source Geospatial Foundation
 * 
 * Published under the BSD license.
 * See http://svn.geoext.org/core/trunk/geoext/license.txt for the full text
 * of the license.
 */

/** api: (define)
 *  module = GeoExt
 *  class = SliderTip
 *  base_link = `Ext.Tip <http://extjs.com/deploy/dev/docs/?class=Ext.Tip>`_
 */
Ext.namespace("GeoExt");

/** api: example
 *  Sample code to create a slider tip to display slider value on hover:
 * 
 *  .. code-block:: javascript
 *     
 *      var slider = new Ext.Slider({
 *          renderTo: document.body,
 *          width: 200,
 *          plugins: new GeoExt.SliderTip()
 *      });
 */

/** api: constructor
 *  .. class:: SliderTip(config)
 *   
 *      Create a slider tip displaying ``Ext.Slider`` values over slider thumbs.
 */
GeoExt.SliderTip = Ext.extend(Ext.Tip, {

    /** api: config[hover]
     *  ``Boolean``
     *  Display the tip when hovering over the thumb.  If ``false``, tip will
     *  only be displayed while dragging.  Default is ``true``.
     */
    hover: true,
    
    /** api: config[minWidth]
     *  ``Number``
     *  Minimum width of the tip.  Default is 10.
     */
    minWidth: 10,

    /** api: config[minWidth]
     *  ``Number``
     *  Minimum width of the tip.  Default is 10.
     */
    minWidth: 10,
    
    /** api: config[offsets]
     *  ``Array(Number)``
     *  A two item list that provides x, y offsets for the tip.  Default is
     *  [0, -10].
     */
    offsets : [0, -10],
    
    /** private: property[dragging]
     *  ``Boolean``
     *  The thumb is currently being dragged.
     */
    dragging: false,

    /** private: method[init]
     *  :param slider: ``Ext.Slider``
     *  
     *  Called when the plugin is initialized.
     */
    init: function(slider) {
        slider.on({
            dragstart: this.onSlide,
            drag: this.onSlide,
            dragend: this.hide,
            destroy: this.destroy,
            scope: this
        });
        if(this.hover) {
            slider.on("render", this.registerThumbListeners, this);
        }
        this.slider = slider;
    },

    /** private: method[registerThumbListeners]
     *  Set as a listener for 'render' if hover is true.
     */
    registerThumbListeners: function() {
        this.slider.thumb.on({
            "mouseover": function() {
                this.onSlide(this.slider);
                this.dragging = false;
            },
            "mouseout": function() {
                if(!this.dragging) {
                    this.hide.apply(this, arguments);
                }
            },
            scope: this
        });
    },

    /** private: method[onSlide]
     *  :param slider: ``Ext.Slider``
     *
     *  Listener for dragstart and drag.
     */
    onSlide: function(slider) {
        this.dragging = true;
        this.show();
        this.body.update(this.getText(slider));
        this.doAutoWidth();
        this.el.alignTo(slider.thumb, 'b-t?', this.offsets);
    },

    /** api: config[getText]
     *  :param slider: ``Ext.Slider``
     *
     *  ``Function``
     *  Function that generates the string value to be displayed in the tip.  By
     *  default, the return from slider.getValue() is displayed.
     */
    getText : function(slider) {
        return slider.getValue();
    }
});
