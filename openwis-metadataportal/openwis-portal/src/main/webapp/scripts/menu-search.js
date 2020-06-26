function showItem(id) {

   const searchKeys = {
        noaa20: 'noaa-20 image',
        suomiNpp: 'suomi npp image',
        aqua: 'Satellite Imagery AQUA',
        terra: 'Satellite Imagery TERRA',
        haze: 'haze',
        hotspotNoaa20: 'noaa20 record',
        hotspotNpp: 'npp record',
        hotspotAqua: 'AQUA_hotspot_location',
        hotspotTerra: 'TERRA_hotspot_location',
        hazeDispersion: 'haze_dispersion',
    };
   product = document.getElementById("produit");
   product.innerHTML = "";

   var separator = document.createElement("div");
   separator.classList.add("dss-menu-nav-separator");
   separator.innerHTML = "/";
   var productName = document.createElement("div");
   productName.innerHTML = document.getElementById(id).innerHTML;

   product.appendChild(separator);
   product.appendChild(productName);
   document.title = 'ASEAN | ' + document.getElementById(id).innerHTML;

   var whatInput = getSearchInput();
   if (whatInput !== null) {
           whatInput.value = searchKeys[id];
   } else {
        console.log("cannot find 'what' input element");
         return
   }
   var buttons = document.getElementsByClassName('iconBtnSearch');
   buttons[0].click();
}

function getSearchInput() {
        var elements = document.querySelectorAll('input[name$="what"]');
        if (elements.length > 0) {
                return elements[0];
        }

        return null;
}