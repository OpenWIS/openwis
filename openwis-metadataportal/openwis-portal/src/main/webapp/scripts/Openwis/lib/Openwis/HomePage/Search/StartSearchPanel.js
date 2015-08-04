Ext.ns('Openwis.HomePage.Search');
Openwis.HomePage.Search.StartSearchPanel = Ext.extend(Openwis.HomePage.Search.AbstractSearchPanel, {
	initComponent: function() {
		Ext.apply(this, {
		    border: false,
		    autoHeight: true,
		    cls: 'homePageStartSearch'
		});
		Openwis.HomePage.Search.StartSearchPanel.superclass.initComponent.apply(this, arguments);
		this.initialize();
	},
	initialize: function() {
		this.selected_button = 'normal';
		this.add(this.getLayoutPanel());
		this.add(this.getStartMapPanel());
    },
    
    //----------------------------------------------------------------- Layer.
    
    getLayoutPanel: function() {
	    if(!this.layoutPanel) {
	        this.layoutPanel = new Ext.Panel({
				layout:'border',
				border:false,
				width: 992,
				height: 54,
				id: 'start_search_layer',
				cls: 'start-search-layer',
        		items: [
        		    this.getSelectButtonsPanel(),
        		    this.getSearchKeyPanel(),
        		    this.getAdvancedPanel()
        		]
        	});
	    }
	    return this.layoutPanel;
	},
	getStartMapPanel: function() {
		var start_map_html = '<div class="main_map"><div class="main_map_bg"><div class="main_map_inner">';
		start_map_html += '<div class="main_map_img"><img src="/openwis-user-portal/images/openwis/main/map.png" alt="Map"/>';
		// seoul
		var seoul = '<div class="sign_point sign_rb sign_openwis sign_seoul"><a href="#" class="sign_spot">Seoul</a><div class="sign_content"><div>';
		seoul += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscSeoul") + '</span></p>';
		seoul += '<ul><li><a href="http://gisc.kma.go.kr" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://gisc.kma.go.kr/openwis-user-portal/srv/en/main.home/portal.sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscSeoul") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += seoul;
		// tokyo
		var tokyo = '<div class="sign_point sign_rt sign_tokyo"><a href="#" class="sign_spot">Tokyo</a><div class="sign_content"><div>';
		tokyo += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscTokyo") + '</span></p>';
		tokyo += '<ul><li><a href="http://www.wis-jma.go.jp" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://www.wis-jma.go.jp/meta/sru.jsp?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscTokyo") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += tokyo;
		// beijing
		var beijing = '<div class="sign_point sign_rb sign_beijing"><a href="#" class="sign_spot">Beijing</a><div class="sign_content"><div>';
		beijing += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscBeijing") + '</span></p>';
		beijing += '<ul><li><a href="http://wisportal.cma.gov.cn" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://wisportal.cma.gov.cn/srw/search?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscBeijing") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += beijing;
		// toulouse
		var toulouse = '<div class="sign_point sign_rb sign_openwis sign_toulouse"><a href="#" class="sign_spot">Toulouse</a><div class="sign_content"><div>';
		toulouse += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscToulouse") + '</span></p>';
		toulouse += '<ul><li><a href="http://wispi.meteo.fr" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://wispi.meteo.fr/openwis-user-portal/srv/en/main.home/portal.sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscToulouse") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += toulouse;
		// exeter
		var exeter = '<div class="sign_point sign_lt sign_openwis sign_exeter"><a href="#" class="sign_spot">Exeter</a><div class="sign_content"><div>';
		exeter += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscExeter") + '</span></p>';
		exeter += '<ul><li><a href="http://wis.metoffice.gov.uk" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://wis.metoffice.gov.uk/openwis-user-portal/srv/en/main.home/portal.sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscExeter") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += exeter;
		// melbourne
		var melbourne = '<div class="sign_point sign_rt sign_openwis sign_melbourne"><a href="#" class="sign_spot">Melbourne</a><div class="sign_content"><div>';
		melbourne += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscMelbourne") + '</span></p>';
		melbourne += '<ul><li><a href="http://wis.bom.gov.au" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://wis.bom.gov.au/openwis-user-portal/srv/en/main.home/portal.sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscMelbourne") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += melbourne;
		// offenbach
		var offenbach = '<div class="sign_point sign_rb sign_offenbach"><a href="#" class="sign_spot">Offenbach</a><div class="sign_content"><div>';
		offenbach += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscOffenbach") + '</span></p>';
		offenbach += '<ul><li><a href="http://gisc.dwd.de" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://gisc.dwd.de/SRU2JDBC/sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscOffenbach") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += offenbach;
		// moscow
		var moscow = '<div class="sign_point sign_rt sign_moscow"><a href="#" class="sign_spot">Moscow</a><div class="sign_content"><div>';
		moscow += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscMoscow") + '</span></p>';
		moscow += '<ul><li><a href="http://portal.gisc-msk.wis.mecom.ru" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://meta.gisc-msk.wis.mecom.ru/openwis-portal/srv/en/portal.sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscMoscow") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += moscow;
		// jeddah
		var jeddah = '<div class="sign_point sign_rb sign_jeddah"><a href="#" class="sign_spot">Jeddah</a><div class="sign_content"><div>';
		jeddah += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscJeddah") + '</span></p>';
		jeddah += '<ul><li><a href="http://wis.pme.gov.sa" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://wis.pme.gov.sa/MessirWIS/srv/en/main.home/portal.sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscJeddah") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += jeddah;
		// newdelhi
		var newdelhi = '<div class="sign_point sign_rt sign_newdelhi"><a href="#" class="sign_spot">NewDelhi</a><div class="sign_content"><div>';
		newdelhi += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscNewDelhi") + '</span></p>';
		newdelhi += '<ul><li><a href="http://wis.imd.gov.in" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://wis.imd.gov.in/MessirWIS/srv/en/main.home/portal.sr?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscNewDelhi") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += newdelhi;
		// pretoria
		var pretoria = '<div class="sign_point sign_rt sign_pretoria"><a href="#" class="sign_spot">Pretoria</a><div class="sign_content"><div>';
		pretoria += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscPretoria") + '</span></p>';
		pretoria += '<ul><li><a href="http://gisc.weathersa.co.za" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://gisc.weathersa.co.za/?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscPretoria") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += pretoria;
		// brasilia
		var brasilia = '<div class="sign_point sign_lt sign_brasilia"><a href="#" class="sign_spot">Brasilia</a><div class="sign_content"><div>';
		brasilia += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscBrasilia") + '</span></p>';
		brasilia += '<ul><li><a href="http://gisc.inmet.gov.br" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://gisc.inmet.gov.br/sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscBrasilia") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += brasilia;
		// tehran
		var tehran = '<div class="sign_point sign_rt sign_tehran"><a href="#" class="sign_spot">Tehran</a><div class="sign_content"><div>';
		tehran += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscTehran") + '</span></p>';
		tehran += '<ul><li><a href="http://gisc.irimo.ir" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://gisc.irimo.ir/index.php/srusearchmnu?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscTehran") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += tehran;
		// washington
		var washington = '<div class="sign_point sign_rt sign_openwis sign_washington"><a href="#" class="sign_spot">Washington</a><div class="sign_content"><div>';
		washington += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscWashington") + '</span></p>';
		washington += '<ul><li><a href="http://giscportal.washington.weather.gov" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://giscportal.washington.weather.gov/openwis-user-portal/srv/en/main.home/portal.sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscWashington") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += washington;
		// casablanca
		var casablanca = '<div class="sign_point sign_rb sign_openwis sign_casablanca"><a href="#" class="sign_spot">Casablanca</a><div class="sign_content"><div>';
		casablanca += '<div class="sign_line"></div><p><span>' + Openwis.i18n("HomePage.Search.Criteria.What.GiscCasablanca") + '</span></p>';
		casablanca += '<ul><li><a href="http://gisc.marocmeteo.ma" target="_blank">'+Openwis.i18n("HomePage.Search.Criteria.What.HomePage")+'</a></li><li><a href="#" onclick="homePageViewport.getStartRemoteSearchWindow(\'http://gisc.marocmeteo.ma/openwis-user-portal/srv/en/main.home/portal.sru?\', \'' + Openwis.i18n("HomePage.Search.Criteria.What.GiscCasablanca") + '\');return false;">'+Openwis.i18n("HomePage.Search.Criteria.What.Sru")+'</a></li></ul></div></div></div>';
		start_map_html += casablanca;		
		start_map_html += '</div></div></div></div>';
		// welcome html
		var welcome_html = Openwis.i18n('HomePage.Main.Welcome.Content');
		start_map_html += welcome_html;
	    if(!this.startMapPanel) {
	        this.startMapPanel = new Ext.Panel({
				layout:'fit',
				border:false,
				width: '100%',
				height: 430,
				html: start_map_html
        	});
	    }
	    return this.startMapPanel;
	},
	
	//--------------------------------------------------- normal.
    
	getSelectButtonsPanel: function() {
        if(!this.selectButtonsPanel) {
            this.selectButtonsPanel = new Ext.Panel({
            	region: 'west',
        		layout: 'table',
            	width: 260,
            	height: 44,
            	border: false,
        		layoutConfig: {
        		    columns: 2
        		},
        		cls: 'start-basic-search',
        		items: [
        		    new Ext.Button(this.getNormalAction()),
        		    new Ext.Button(this.getAdvancedAction())
        		]
            });
        }
        return this.selectButtonsPanel;        
    },
    getSearchKeyPanel: function() {
        if(!this.searchKeyPanel) {
            this.searchKeyPanel = new Ext.Panel({
            	region: 'center',
        		layout: 'table',
        		width: 592,
        		height: 44,
            	border: false,
        		layoutConfig: {
        		    columns: 2
        		},
        		cls: 'start-basic-search2',
        		items: [
        		    this.getSearchTextField(),
        		    new Ext.Button(this.getSearchAction())
        		]
            });
        }
        return this.searchKeyPanel;        
    },

    //--------------------------------------------------- advanced.
    
    getAdvancedPanel: function() {
        if(!this.advancedPanel) {
            this.advancedPanel = new Ext.Panel({
            	region: 'south',
            	layout: 'table',
        		width: 992,
        		height: 330,
            	border: false,
            	id: 'start_search_advanced',
            	cls: 'start-search-advanced',
        		layoutConfig: {
        		    columns: 4
        		},
        		items: [
		        	this.getWherePanel(),
        		    this.getWhatPanel(),
        		    this.getWhenPanel(),
        		    this.getRestrictPanel()
        		]
            }).hide();
        }
        return this.advancedPanel;        
    },
	
    //--------------------------------------------------- where.
    
    getWherePanel: function() {
        if(!this.wherePanel) {
            this.wherePanel = new Ext.Panel({
        		layout: 'table',
        		width: 226,
        		height: 270,
            	border: false,
            	cls: 'start-advanced-column',
        		layoutConfig: {
        		    columns: 1
        		},
        		items: [
		        	this.getWhereLabel(),
        		    this.getWhereMapPanel()
        		]
            });
        }
        return this.wherePanel;        
    },
    getWhereLabel: function() {
        if(!this.wherelabel) {
            this.wherelabel = new Ext.Container({
                border: false,
                width: 226,
                html: Openwis.i18n('HomePage.Search.Criteria.Where'),
                cls: 'column-label'
            });       
        }
        return this.wherelabel;
    },
    getWhereMapPanel: function() {
        if(!this.whereMapPanel) {
            this.whereMapPanel = new Openwis.Common.Components.GeographicalExtentSelection({
                geoExtentType: 'RECTANGLE',
                wmsUrl: "http://vmap0.tiles.osgeo.org/wms/vmap0?",
                layerName: 'basic',
                maxExtent: new OpenLayers.Bounds(-180,-90,180,90),
                width: 224,
			    height: 130,
                listeners: {
                    valueChanged: function(bounds) {
                    	var latMin = bounds.bottom;
                    	var latMax = bounds.top;
                    	var longMin = bounds.left;
                    	var longMax = bounds.right;
                    	// Fix coords
                    	bounds.bottom = Math.max(Math.min(latMin, 90), -90);
                    	bounds.top = Math.max(Math.min(latMax, 90), -90);
                    	bounds.left = this.getLongitude(longMin);
                    	bounds.right = this.getLongitude(longMax);
                    	this.updateMapFields(bounds, true);
                    },
                    scope: this
                }
            });
        }
        return this.whereMapPanel;
    },
	
    //--------------------------------------------------- what.
    
    getWhatPanel: function() {
        if(!this.whatPanel) {
            this.whatPanel = new Ext.Panel({
        		layout: 'table',
        		width: 200,
        		height: 270,
            	border: false,
            	cls: 'start-advanced-column ml-55 what',
        		layoutConfig: {
        		    columns: 1
        		},
        		items: [
		        	this.getWhatLabel(),
		        	this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.Title')),
		    	    this.getTitleTextField(),
		    	    this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.Abstract')),
		    	    this.getAbstractTextField(),
		    	    this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.Keywords')),
		    	    this.getKeywordsTextField()
        		]
            });
        }
        return this.whatPanel;        
    },
    getWhatLabel: function() {
        if(!this.whatlabel) {
            this.whatlabel = new Ext.Container({
                border: false,
                width: 200,
                html: Openwis.i18n('HomePage.Search.Criteria.What'),
                cls: 'column-label'
            });     
        }
        return this.whatlabel;
    },
    getTitleTextField: function() {
        if(!this.titleTextField) {
            this.titleTextField = new Ext.form.TextField({
                name: 'title',
				allowBlank: true,
				width: 200
            });
        }
        return this.titleTextField;
    },
    getAbstractTextField: function() {
        if(!this.abstractTextField) {
            this.abstractTextField = new Ext.form.TextField({
                name: 'abstract',
				allowBlank: true,
				width: 200
            });
        }
        return this.abstractTextField;
    },
    getKeywordsTextField: function() {
        if(!this.keywordsTextField) {
            this.keywordsTextField = new Ext.form.TextField({
                name: 'themekey',
				allowBlank: true,
				width: 200,
				listeners: {
	            	focus :{fn:function(){
	                	new Openwis.Common.Search.KeywordsSearch({
							keywordsFromTf: this.getValue(),
							isXML: false,
							listeners: {
								keywordsSelection: function(records) {
									this.setValue(records);
								},
								scope: this
							}
						});
	                }}
		        }
            });
        }
        return this.keywordsTextField;
    },
	
    //--------------------------------------------------- when.
    
    getWhenPanel: function() {
        if(!this.whenPanel) {
            this.whenPanel = new Ext.Panel({
        		layout: 'table',
        		width: 200,
        		height: 270,
            	border: false,
            	cls: 'start-advanced-column ml-55 when',
        		layoutConfig: {
        		    columns: 1
        		},
        		items: [
		        	this.getWhenLabel(),
		        	this.getRadio2ColmnPanel(this.getWhenAnytimeRadio(), this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.When.Any')), ''),
					this.getRadio2ColmnPanel(this.getWhenMetadataChangeDateRadio(), this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.When.MetadataChangeDate')), 'when-radio-mtb'),
					this.getWhenMetadataChangeDateFromDateField(),
					this.getWhenMetadataChangeDateToDateField(),
					this.getRadio2ColmnPanel(this.getWhenTemporalExtentRadio(), this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.When.TemporalExtent')), 'when-radio-mtb'),
					this.getWhenTemporalExtentFromDateField(),
					this.getWhenTemporalExtentToDateField()
        		]
            });
        }
        return this.whenPanel;        
    },
    getWhenLabel: function() {
        if(!this.whenlabel) {
            this.whenlabel = new Ext.Container({
                border: false,
                width: 200,
                html: Openwis.i18n('HomePage.Search.Criteria.When'),
                cls: 'column-label'
            });       
        }
        return this.whenlabel;
    },
    getWhenAnytimeRadio: function() {
        if(!this.whenAnytimeRadio) {
            this.whenAnytimeRadio = new Ext.form.Radio({
            	name: 'whenMode',
    			inputValue: 'Anytime',
    			checked: true
    		});
    	}
    	return this.whenAnytimeRadio;
    },
    getWhenMetadataChangeDateRadio: function() {
        if(!this.whenMetadataChangeDateRadio) {
            this.whenMetadataChangeDateRadio = new Ext.form.Radio({
    			name: 'whenMode',
    			inputValue: 'MetadataChangeDate',
    			checked: false,
    			listeners : {
    				check: function(checkbox, checked) {
    					if(checked) {
    						this.getWhenMetadataChangeDateFromDateField().enable();
    						this.getWhenMetadataChangeDateToDateField().enable();
    					} else {
    						this.getWhenMetadataChangeDateFromDateField().disable();
    						this.getWhenMetadataChangeDateToDateField().disable();
    					}
    				},
    				scope: this
    			}
    		});
    	}
    	return this.whenMetadataChangeDateRadio;
    },
    getWhenMetadataChangeDateFromDateField: function() {
        if(!this.whenMetadataChangeDateFromDateField) {
            this.whenMetadataChangeDateFromDateField = new Ext.form.DateField({
            	allowBlank: false,
            	emptyText: Openwis.i18n('Common.Extent.Temporal.From'),
    			name: 'MetadataChangeDateFrom',
        		editable: false,
        		format: 'Y-m-d',
        		disabled: true,
        		width: 215
    		});
    	}
    	return this.whenMetadataChangeDateFromDateField;
    },
    getWhenMetadataChangeDateToDateField: function() {
        if(!this.whenMetadataChangeDateToDateField) {
            this.whenMetadataChangeDateToDateField = new Ext.form.DateField({
            	allowBlank: false,
            	emptyText: Openwis.i18n('Common.Extent.Temporal.To'),
    			name: 'MetadataChangeDateTo',
        		editable: false,
        		format: 'Y-m-d',
        		disabled: true,
        		width: 215
    		});
    	}
    	return this.whenMetadataChangeDateToDateField;
    },
    getWhenTemporalExtentRadio: function() {
        if(!this.whenTemporalExtentRadio) {
            this.whenTemporalExtentRadio = new Ext.form.Radio({
    			name: 'whenMode',
    			inputValue: 'TemporalExtent',
    			checked: false,
    			listeners : {
    				check: function(checkbox, checked) {
    					if(checked) {
    						this.getWhenTemporalExtentFromDateField().enable();
    						this.getWhenTemporalExtentToDateField().enable();
    					} else {
    						this.getWhenTemporalExtentFromDateField().disable();
    						this.getWhenTemporalExtentToDateField().disable();
    					}
    				},
    				scope: this
    			}
    		});
    	}
    	return this.whenTemporalExtentRadio;
    },
    getWhenTemporalExtentFromDateField: function() {
        if(!this.whenTemporalExtentFromDateField) {
            this.whenTemporalExtentFromDateField = new Ext.form.DateField({
            	allowBlank: false,
            	emptyText: Openwis.i18n('Common.Extent.Temporal.From'),
    			name: 'TemporalExtentFrom',
        		editable: false,
        		format: 'Y-m-d',
        		disabled: true,
        		width: 215
    		});
    	}
    	return this.whenTemporalExtentFromDateField;
    },
    getWhenTemporalExtentToDateField: function() {
        if(!this.whenTemporalExtentToDateField) {
            this.whenTemporalExtentToDateField = new Ext.form.DateField({
            	allowBlank: false,
            	emptyText: Openwis.i18n('Common.Extent.Temporal.To'),
            	name: 'TemporalExtentTo',
        		editable: false,
        		format: 'Y-m-d',
        		disabled: true,
        		width: 215
    		});
    	}
    	return this.whenTemporalExtentToDateField;
    },
    
	//--------------------------------------------------- restrict.
    
    getRestrictPanel: function() {
        if(!this.restrictPanel) {
            this.restrictPanel = new Ext.Panel({
        		layout: 'table',
        		width: 200,
        		height: 270,
            	border: false,
            	cls: 'start-advanced-column ml-55 restrict',
        		layoutConfig: {
        		    columns: 1
        		},
        		items: [
		        	this.getRestrictLabel(),
		        	this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Category')),
		    	    this.getRestrictToCategoryComboBox(),
		    	    this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Kind')),
		    	    this.getRestrictToKindComboBox()
        		]
            });
        }
        return this.restrictPanel;        
    },
    getRestrictLabel: function() {
        if(!this.restrictlabel) {
            this.restrictlabel = new Ext.Container({
                border: false,
                width: 200,
                html: Openwis.i18n('HomePage.Search.Criteria.RestrictTo'),
                cls: 'column-label'
            });       
        }
        return this.restrictlabel;
    },
    getRestrictToCategoryComboBox: function() {
        if(!this.restrictToCategoryComboBox) {
            var categoryStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService+ '/xml.get.home.page.category.all',
    			idProperty: 'id',
    			fields: [
    				{
    					name:'id'
    				},{
    					name:'name'
    				}
    			],
    			listeners: {
    			    load: function(store, records, options) {
    			        var anyRecord = new Ext.data.Record({
        		            id: '',
        		            name: Openwis.i18n('Common.List.Any')
        		        });
        		        store.insert(0, [anyRecord]);
    			    }
    			}
    		});
            this.restrictToCategoryComboBox = new Ext.form.ComboBox({
                store: categoryStore,
				valueField: 'id',
				displayField:'name',
                name: 'category',
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 215,
				emptyText: Openwis.i18n('Common.List.Any')
            });
        }
        return this.restrictToCategoryComboBox;
    },
    getRestrictToKindComboBox: function() {
        if(!this.restrictToKindComboBox) {
            this.restrictToKindComboBox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['',        Openwis.i18n('Common.List.Any')], 
					    ['metadata',Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Kind.Metadata')],
					    ['template',Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Kind.Template')]
					]
				}),
				valueField: 'id',
				displayField:'value',
                name: 'restrictToKind',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 215
            });
            this.restrictToKindComboBox.setValue('');
        }
        return this.restrictToKindComboBox;
    },
    
    //----------------------------------------------------------------- Layout helpers.
    
    getRadio2ColmnPanel: function(radio, item, cls) {
    	return new Ext.Panel({
            layout: 'table',
            border: false,
            width: 200,
            cls: cls,
            layoutConfig: {
                 columns: 2  
            },
			items: [
		        radio,
		        item
			]
        });
    },
    
    //----------------------------------------------------------------- Form fields & Actions.
    
    getNormalAction: function() {
        if(!this.normalAction) {
            this.normalAction = new Ext.Action({
                text: 'Normal',
                toggleGroup: 'startSearchType',
                pressed: true,
                allowDepress: false,
                width: 132,
                cls: 'start-search-select-btn',
				scope: this,
				handler: function() {
					this.selected_button = 'normal';
					this.getAdvancedPanel().hide();
					this.getLayoutPanel().setHeight(54);
				}
            });
        }
        return this.normalAction;
    },
    
    getAdvancedAction: function() {
        if(!this.advancedAction) {
            this.advancedAction = new Ext.Action({
                text: 'Advanced',
                toggleGroup: 'startSearchType',
                allowDepress: false,
                width: 132,
                cls: 'start-search-select-btn',
				scope: this,
				handler: function() {
					this.selected_button = 'advanced';
					this.getLayoutPanel().setHeight(384);
					this.getAdvancedPanel().show();
				}
            });
        }
        return this.advancedAction;
    },
    getSearchTextField: function() {
        if(!this.searchTextField) {
            this.searchTextField = new Ext.form.TextField({
                name: 'search-key',
                emptyText: 'Please enter a search word.',
				allowBlank: true,
				width: 474,
				cls: 'start-search-text',
				listeners: {
					specialkey: function(f,e){
						if (e.getKey() == e.ENTER) {
							this.getSearchAction().execute();
						}
					},
					scope: this
                }
            });
        }
        return this.searchTextField;
    },
    getSearchAction: function() {
    	var me = this;
        if(!this.searchAction) {
            this.searchAction = new Ext.Action({
                text: 'SEARCH',
                width: 117,
                cls: 'start-search-btn',
				scope: this,
				handler: function() {
					// Send data to the search panel
					var searchPanel = homePageViewport.getSearchPanel();
					if (this.selected_button == 'advanced') { // advanced
						searchPanel.setActiveTab(1);
						var advancedSearchPanel = searchPanel.getAdvancedSearchPanel();
						advancedSearchPanel.getWhatTextField().setValue(this.getSearchTextField().getValue());
						advancedSearchPanel.getTitleTextField().setValue(this.getTitleTextField().getValue());
						advancedSearchPanel.getAbstractTextField().setValue(this.getAbstractTextField().getValue());
						advancedSearchPanel.getKeywordsTextField().setValue(this.getKeywordsTextField().getValue());
						advancedSearchPanel.getWhenAnytimeRadio().setValue(this.getWhenAnytimeRadio().getValue());
						advancedSearchPanel.getWhenMetadataChangeDateRadio().setValue(this.getWhenMetadataChangeDateRadio().checked);
						advancedSearchPanel.getWhenMetadataChangeDateFromDateField().setValue(this.getWhenMetadataChangeDateFromDateField().getValue());
						advancedSearchPanel.getWhenMetadataChangeDateToDateField().setValue(this.getWhenMetadataChangeDateToDateField().getValue());
						advancedSearchPanel.getWhenTemporalExtentRadio().setValue(this.getWhenTemporalExtentRadio().checked);
						advancedSearchPanel.getWhenTemporalExtentFromDateField().setValue(this.getWhenTemporalExtentFromDateField().getValue());
						advancedSearchPanel.getWhenTemporalExtentToDateField().setValue(this.getWhenTemporalExtentToDateField().getValue());
						if (this.getRestrictToCategoryComboBox().getValue()) {
							var store = advancedSearchPanel.getRestrictToCategoryComboBox().getStore();
							var selected_value = this.getRestrictToCategoryComboBox().getValue();
							advancedSearchPanel.getRestrictToCategoryComboBox().setValue(selected_value);
							store.load({
							   callback: function() {
								   advancedSearchPanel.getRestrictToCategoryComboBox().setValue(selected_value);
							   }
							});
						}
						advancedSearchPanel.getRestrictToKindComboBox().setValue(this.getRestrictToKindComboBox().getValue());
						if (advancedSearchPanel.validate()) {
							var params = advancedSearchPanel.buildSearchParams();
						    var url = advancedSearchPanel.searchUrl();
						    advancedSearchPanel.targetResult.loadSearchResults(url, params);
						}
					} else { // normal or geoss
						var normalSearchPanel = searchPanel.getNormalSearchPanel();
						if (this.selected_button == 'geoss') {
							normalSearchPanel.getOnlyGeossMetadataCheckbox().setValue(true);
						}
						normalSearchPanel.getWhatTextField().setValue(this.getSearchTextField().getValue());
						if (normalSearchPanel.validate()) {
							var params = normalSearchPanel.buildSearchParams();
						    var url = normalSearchPanel.searchUrl();
						    normalSearchPanel.targetResult.loadSearchResults(url, params);
						}
					}
				}
            });
        }
        return this.searchAction;
    },
    updateMapFields: function(bounds, setRegionToUserDefined) {
    	var advancedSearchPanel = homePageViewport.getSearchPanel().getAdvancedSearchPanel();
    	advancedSearchPanel.getWhereBoundsLatMinTextField().setValue(bounds.bottom);
    	advancedSearchPanel.getWhereBoundsLatMaxTextField().setValue(bounds.top);
    	advancedSearchPanel.getWhereBoundsLongMinTextField().setValue(bounds.left);
    	advancedSearchPanel.getWhereBoundsLongMaxTextField().setValue(bounds.right);
        if(bounds.bottom.constrain(-90, 90) != bounds.bottom) {
        	advancedSearchPanel.getWhereBoundsLatMinTextField().markInvalid(Openwis.i18n('Common.Validation.NumberOutOfRange', {from:-90, to: 90}));
        }
        if(bounds.top.constrain(-90, 90) != bounds.top) {
        	advancedSearchPanel.getWhereBoundsLatMaxTextField().markInvalid(Openwis.i18n('Common.Validation.NumberOutOfRange', {from:-90, to: 90}));
        }
        if(bounds.left.constrain(-180, 180) != bounds.left) {
        	advancedSearchPanel.getWhereBoundsLongMinTextField().markInvalid(Openwis.i18n('Common.Validation.NumberOutOfRange', {from:-180, to: 180}));
        }
        if(bounds.right.constrain(-180, 180) != bounds.right) {
        	advancedSearchPanel.getWhereBoundsLongMaxTextField().markInvalid(Openwis.i18n('Common.Validation.NumberOutOfRange', {from:-180, to: 180}));
        }
        if(setRegionToUserDefined) {
        	advancedSearchPanel.setRegionToUserDefined();
        }
        if (bounds.left>bounds.right) {
        	this.getMapPanel().drawExtent(bounds);
        	this.getMapPanel().zoomToExtent(bounds);
        }
    }
});