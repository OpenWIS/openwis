function showRelatedServicesPanel(id, uuid) {
    
	
	
	var divElt = document.getElementById('serviceElt' + id);
	if (divElt.style.display == 'none') {
	
		divElt.style.display = 'block';
	
		var posBtn = document.getElementById('services' + id).positionedOffset();
		
		divElt.style.top = 30 + posBtn.top + 'px';
		divElt.style.left = posBtn.left + 'px';
		divElt.style.width = '280px';
	} else {
		divElt.style.display = 'none';
	}
	
	
	
	// ignored
	if (false) {
	
    new Openwis.Handler.GetWithoutError({
		url: configOptions.locService+ '/xml.relatedservices.search',
		params: {uuid: uuid},
		listeners: {
			success: function(res) {
				var metadataList = res.metadataList;
				
				var divElt = document.getElementById('serviceElt' + id);
				divElt.innerHTML = '';
				
				for (var i=0; i<metadataList.length; i++) {
					var md = metadataList[i];
					
					var aElt = document.createElement("a");
					aElt.id = 'id_lien';
					aElt.href = '#';
					aElt.innerHTML = rsp_getTitle(md);
					
					var divMd = document.createElement("div");
					divMd.className = 'otherActionTarget';
					divMd.appendChild(aElt);

					divElt.appendChild(divMd);
					
					//alert('le résultat: ' + md.title);
				}
				
				divElt.style.display = 'block';
				
				var posBtn = document.getElementById('services' + id).positionedOffset();
				
				divElt.style.top = 30 + posBtn.top + 'px';
				divElt.style.left = posBtn.left + 'px';
				divElt.style.width = '280px';
			},
			scope: this
		}
	}).proceed();
    
    
	}
}

function rsp_getTitle(md) {
	var maxLength = 50;
	if (md.title.length < maxLength ) {
		return md.title;
	}
	return md.title.substring(0, maxLength);
}




        		