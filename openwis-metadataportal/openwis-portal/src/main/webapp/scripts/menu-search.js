function showItem(id) {

   const searchKeys = {
        noaa20: 'noaa-20 image',
        suomiNpp: 'suomi npp image',
        aqua: 'Satellite Imagery AQUA',
        terra: 'Satellite Imagery TERRA',
        haze: 'haze_map',
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
        var buttons = document.getElementsByClassName('iconBtnSearch');
        buttons[0].click();
   } else {
        // not on the home page. go back to homepage & search..
        window.location.href = getBaseUrl() + "?productKey=" + id;
   }
}

function getSearchInput() {
        var elements = document.querySelectorAll('input[name$="what"]');
        if (elements.length > 0) {
                return elements[0];
        }

        return null;
}

function getBaseUrl() {
    var pathArray = window.location.pathname.split('/');
    var baseUrl = "";

    for(var i=0; i<pathArray.length; i++) {
        var e = pathArray[i];
        baseUrl += e + "/";
        if (e === "openwis-user-portal") {
            break;
        }
    }
    return baseUrl;
}


