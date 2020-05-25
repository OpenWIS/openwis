function showItem(id) {

   const searchKeys = {
        noaa20: 'noaa-20',
        suomiNpp: 'suomi npp',
        aqua: 'Satellite Imagery AQUA',
        terra: 'Satellite Imagery TERRA',
        haze: 'haze',
        hotspotNoaa20: 'noaa20',
        hotspotNpp: 'npp',
        hotspotAqua: 'AQUA_hotspot_location',
        hotspotTerra: 'TERRA_hotspot_location',
        hazeDispersion: 'haze_dispersion',
    };
   product = document.getElementById("produit");
   product.innerHTML = '/ ' + document.getElementById(id).innerHTML;
   document.title = 'ASEAN | ' + document.getElementById(id).innerHTML;
   document.getE('ext-comp-1019').value = searchKeys[id];
   var buttons = document.getElementByClass('iconBtnSearch');
   buttons[0].click();
}
