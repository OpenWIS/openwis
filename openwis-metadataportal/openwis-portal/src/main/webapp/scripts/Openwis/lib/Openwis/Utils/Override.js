
if(Ext.form.DateField) {
    Ext.override(Ext.form.DateField, {
        initComponent : function(){
            Ext.form.DateField.superclass.initComponent.call(this);
            this.addEvents(
                'select'
            );
            if(Ext.isString(this.minValue)){
                this.minValue = this.parseDate(this.minValue);
            }
            if(Ext.isString(this.maxValue)){
                this.maxValue = this.parseDate(this.maxValue);
            }
            /*this.disabledDatesRE = null;*/
            this.initDisabledDays();
        },
        initDisabledDays : function(){
            if(this.disabledDates){
                var dd = this.disabledDates,
                    len = dd.length - 1,
                    re = "(?:";
                Ext.each(dd, function(d, i){
                    re += Ext.isDate(d) ? '^' + Ext.escapeRe(d.dateFormat(this.format)) + '$' : dd[i];
                    if(i != len){
                        re += '|';
                    }
                }, this);
                this.disabledDatesRE = new RegExp(re + ')');
            } else {
                this.disabledDatesRE = null;
            }
        }
    });
}

if(Ext.layout.BorderLayout) {
    Ext.override(Ext.layout.BorderLayout, {
           onLayout : function(ct, target){
            var collapsed, i, c, pos, items = ct.items.items, len = items.length;
            if(!this.rendered){
                collapsed = [];
                for(i = 0; i < len; i++) {
                    c = items[i];
                    pos = c.region;
                    if(c.collapsed){
                        collapsed.push(c);
                    }
                    c.collapsed = false;
                    if(!c.rendered){
                        c.render(target, i);
                        c.getPositionEl().addClass('x-border-panel');
                    }
                    this[pos] = pos != 'center' && c.split ?
                        new Ext.layout.BorderLayout.SplitRegion(this, c.initialConfig, pos) :
                        new Ext.layout.BorderLayout.Region(this, c.initialConfig, pos);
                    this[pos].render(target, c);
                }
                this.rendered = true;
            }
    
            var size = this.getLayoutTargetSize();
            // -- to use minWidth! As suggested by Animal!
            if (size.width < this.minWidth) {
                target.setStyle('width', this.minWidth + 'px');
                size.width = this.minWidth;
                target.up('').setStyle('overflow', 'auto');
            } else {
                target.setStyle('width', '');
            }
            //------
    
            if(size.width < 20 || size.height < 20){ // display none?
                if(collapsed){
                    this.restoreCollapsed = collapsed;
                }
                return;
            }else if(this.restoreCollapsed){
                collapsed = this.restoreCollapsed;
                delete this.restoreCollapsed;
            }
    
            var w = size.width, h = size.height,
                centerW = w, centerH = h, centerY = 0, centerX = 0,
                n = this.north, s = this.south, west = this.west, e = this.east, c = this.center,
                b, m, totalWidth, totalHeight;
            if(!c && Ext.layout.BorderLayout.WARN !== false){
                throw 'No center region defined in BorderLayout ' + ct.id;
            }
    
            if(n && n.isVisible()){
                b = n.getSize();
                m = n.getMargins();
                b.width = w - (m.left+m.right);
                b.x = m.left;
                b.y = m.top;
                centerY = b.height + b.y + m.bottom;
                centerH -= centerY;
                n.applyLayout(b);
            }
            if(s && s.isVisible()){
                b = s.getSize();
                m = s.getMargins();
                b.width = w - (m.left+m.right);
                b.x = m.left;
                totalHeight = (b.height + m.top + m.bottom);
                b.y = h - totalHeight + m.top;
                centerH -= totalHeight;
                s.applyLayout(b);
            }
            if(west && west.isVisible()){
                b = west.getSize();
                m = west.getMargins();
                b.height = centerH - (m.top+m.bottom);
                b.x = m.left;
                b.y = centerY + m.top;
                totalWidth = (b.width + m.left + m.right);
                centerX += totalWidth;
                centerW -= totalWidth;
                west.applyLayout(b);
            }
            if(e && e.isVisible()){
                b = e.getSize();
                m = e.getMargins();
                b.height = centerH - (m.top+m.bottom);
                totalWidth = (b.width + m.left + m.right);
                b.x = w - totalWidth + m.left;
                b.y = centerY + m.top;
                centerW -= totalWidth;
                e.applyLayout(b);
            }
            if(c){
                m = c.getMargins();
                var centerBox = {
                    x: centerX + m.left,
                    y: centerY + m.top,
                    width: centerW - (m.left+m.right),
                    height: centerH - (m.top+m.bottom)
                };
                c.applyLayout(centerBox);
            }
            if(collapsed){
                for(i = 0, len = collapsed.length; i < len; i++){
                    collapsed[i].collapse(false);
                }
            }
            if(Ext.isIE && Ext.isStrict){ // workaround IE strict repainting issue
                target.repaint();
            }
            // Putting a border layout into an overflowed container is NOT correct and will make a second layout pass necessary.
            if (i = target.getStyle('overflow') && i != 'hidden' && !this.adjustmentPass) {
                var ts = this.getLayoutTargetSize();
                if (ts.width != size.width || ts.height != size.height){
                    this.adjustmentPass = true;
                    this.onLayout(ct, target);
                }
            }
            delete this.adjustmentPass;
        }
    });
}

if(Ext.ux.GroupTabPanel) {
    Ext.override(Ext.ux.GroupTabPanel, {
    	onRender: function(ct, position){
            Ext.TabPanel.superclass.onRender.call(this, ct, position);
            if(this.plain){
                var pos = this.tabPosition == 'top' ? 'header' : 'footer';
                this[pos].addClass('x-tab-panel-'+pos+'-plain');
            }
    
            var st = this[this.stripTarget];
    
            this.stripWrap = st.createChild({cls:'x-tab-strip-wrap ', cn:{
                tag:'ul', cls:'x-grouptabs-strip x-grouptabs-tab-strip-'+this.tabPosition}});
    
            var beforeEl = (this.tabPosition=='bottom' ? this.stripWrap : null);
            this.strip = new Ext.Element(this.stripWrap.dom.firstChild);
    
            this.header.addClass('x-grouptabs-panel-header');
            this.bwrap.addClass('x-grouptabs-bwrap');
            this.body.addClass('x-tab-panel-body-'+this.tabPosition + ' x-grouptabs-panel-body');
    
            if (!this.groupTpl) {
                var tt = new Ext.Template(
                    '<li class="{cls}" id="{id}">',
    				//-- Override: Remove link
                    '<a class="x-grouptabs-text {iconCls}" href="#" onclick="return false;">',
                    '<span>{text}</span></a>',
                    '</li>'
                );
                tt.disableFormats = true;
                tt.compile();
                Ext.ux.GroupTabPanel.prototype.groupTpl = tt;
            }
            this.items.each(this.initGroup, this);
        },
    	
    	onGroupBeforeTabChange: function(group, newTab, oldTab){
            if(group !== this.activeGroup || newTab !== oldTab) {
                this.strip.select('.x-grouptabs-sub > li.x-grouptabs-strip-active', true).removeClass('x-grouptabs-strip-active');
            }
            this.expandGroup(this.getGroupEl(group));
    		
    
    		//-- Begin Override: Collapse all others items.
    		var groupEl = this.getGroupEl(group);
    		
    		if(this.items) {
    			for(var i = 0; i < this.items.getCount(); i++) {
    				var tmpVal = this.items.get(i);
    				if(groupEl != tmpVal.groupEl) {
    					this.collapseGroup(tmpVal.groupEl);
    				}
    			}
    		}
    		//-- End Override.
    		
            if(group !== this.activeGroup) {
                return this.setActiveGroup(group);
            }
        }
    });
}
