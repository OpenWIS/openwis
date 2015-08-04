Ext.ns('Openwis.HomePage.Search');
Openwis.HomePage.Search.StartRemoteSearchPanel = Ext.extend(Openwis.HomePage.Search.SruSearchPanel, {
	initComponent: function() {
		Ext.apply(this);
		Openwis.HomePage.Search.SruSearchPanel.superclass.initComponent.apply(this, arguments);
		this.initialize();
	},
    initialize: function(){
    	this.add(this.getWhatOthersCriteriaFieldSet());
    	this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.From")));
    	this.getWhenFieldSet().add(this.getWhenFromDateField());
    	this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.To")));
    	this.getWhenFieldSet().add(this.getWhenToDateField());
    	this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.Userd")));
    	this.getWhenFieldSet().add(this.getDateFieldComboBox());
    	this.add(this.getWhenFieldSet());
    	this.add(this.getSizeResultLabel());
    	this.add(this.getSizeResultField());
    	this.add(this.getButtonsSruPanel());
    },
    buildSearchParams: function(){
    	    	
    	var server = this.getServerComboBox().getValue();
    	var termField1 = this.getWhatOthersCriteriaFieldSet().get(0).getValue();
    	var termText1 = this.getWhatOthersCriteriaFieldSet().get(1).getValue();
    	var termBool1 = "";
    	var term1 = "";
    	var termField2 = this.getWhatOthersCriteriaFieldSet().get(4).getValue();
    	var termText2 = this.getWhatOthersCriteriaFieldSet().get(5).getValue();
    	var termBool2 = "";
    	var term2 = "";
    	var termField3 = this.getWhatOthersCriteriaFieldSet().get(8).getValue();
    	var termText3 = this.getWhatOthersCriteriaFieldSet().get(9).getValue();
    	var term3 = "";
    	if( termText1 != "" ) {
    		if( termText2 != "" || termText3 != "" ) {
    			termBool1 = this.getWhatOthersCriteriaFieldSet().get(2).getValue();
    		}
    		term1 = termField1 + ' ' + '"' + termText1 + '"' + ' ' + termBool1 + ' ';
    	}
    	if( termText2 != "" ) {
    		if( termText3 != "" ) {
    			termBool2 = this.getWhatOthersCriteriaFieldSet().get(6).getValue();
    		}
    		term2 = termField2 + ' ' + '"' + termText2 + '"' + ' ' + termBool2 + ' ';
    	}
    	if( termText3 != "" ) {
    		term3 = termField3 + ' ' + '"' + termText3 +'"';
    	}
    	var fromDate = this.getWhenFromDateField().getValue();
    	var toDate = this.getWhenToDateField().getValue();
    	var dateField = this.getDateFieldComboBox().getValue();
    	var date = "";
    	if( fromDate != "" && toDate != "" ) {
    		fromDate = Openwis.Utils.Date.formatDateForServer(this.getWhenFromDateField().getValue());
    		toDate = Openwis.Utils.Date.formatDateForServer(this.getWhenToDateField().getValue());
    		date = ' and ' + dateField + '>' + fromDate.split('-').join('') + ' and ' + dateField + '<' + toDate.split('-').join('');
    	}
    	var maxRecords = this.getSizeResultField().getValue();
    	if( maxRecords == "" ) {
    		maxRecords = 20;
    	}
    	var url = ""
    	if( server != "http://gisc.dwd.de/SRU2JDBC/sru?" ) {
    		url = server + 'operation=searchRetrieve&version=1.1&query=' + term1 + term2 + term3 + date + '&startRecord=1&maximumRecords=' + maxRecords + '&&';
    	} else {
    		url = server + 'operation=searchRetrieve&version=1.1&query=' + term1 + term2 + term3 + date + '&startRecord=1&maximumRecords=' + maxRecords + '&&stylesheet=xsl/dwd-sru.xsl&x-dwd-stylesheetDetailLevel=1';
    	}
    	window.open(url);    	    	
    	var parentWindow = this.findParentByType('window');
        if (parentWindow) parentWindow[parentWindow.closeAction]();
        
        // Reset Remote(SRU) SearchParams
        this.getServerComboBox().reset();
    	this.getWhatOthersCriteriaFieldSet().get(0).reset();
    	this.getWhatOthersCriteriaFieldSet().get(2).reset();
    	this.getWhatOthersCriteriaFieldSet().get(4).reset();
    	this.getWhatOthersCriteriaFieldSet().get(6).reset();
    	this.getWhatOthersCriteriaFieldSet().get(8).reset();
    	this.getWhatOthersCriteriaFieldSet().get(9).reset();
    	this.getWhatOthersCriteriaFieldSet().get(1).reset();
    	this.getWhatOthersCriteriaFieldSet().get(5).reset();
    	this.getWhenFromDateField().reset();
    	this.getWhenToDateField().reset();
    	this.getDateFieldComboBox().reset();
    	this.getSizeResultField().reset();
    	
    }
});