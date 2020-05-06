<div class="dss-logo-container">
    <div>
        <div class="dss-logo">
            <div class="dss-logo-left dss-logo-common">
                <div>
                    <img src="<%= context.getBaseUrl() %>/images/logos/logo-asmc.png" alt="World picture" align="top">
                </div>
            </div>
            <div class="dss-logo-middle dss-logo-common">
                <div class="dss-logo-middle-container dss-logo-common">
                    <div>
                        <a class="dss-logo-middle-link" href="<%= locService %>/about.home">About ASMC</a>&nbsp;
                    </div>
                    <div>
                        <a class="dss-logo-middle-link" href="">Contact Us</a>
                    </div>
                </div>
            </div>
            <div class="dss-logo-right dss-logo-common">
                <div class="dss-logo-right-container">
                    <img src="<%= context.getBaseUrl() %>/images/logos/logo-asean.png" width="150"
                         alt="GeoNetwork opensource logo" align="top">
                </div>
            </div>
        </div>
        <div class="dss-main-nav">
            <div class="dss-nav-left">
                <div class="dss-nav-item"><a href="<%= locService %>/main.home">DATA CATALOG</a></div>
                <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) {%>
                    <% if ("myaccount".equals(context.getService())) { %>
                            <div class="dss-nav-item"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount'))</script></div>
                    <% } else { %>
                            <a class="dss-nav-item" href="<%= locService %>/myaccount"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount').toUpperCase())</script></a>
                    <% } %>
                <% } %>
                <div class="dss-nav-item"><a href="<%= locService %>/help.home">HELP</a></div>
            </div>
            <!-- Login -->
            <div class="dss-nav-login">
                <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) {%>
                    <div class="dss-logout-container">
                        <div class="dss-logout-username">
                            <%= context.getUserSession().getName() %> <%= context.getUserSession().getSurname() %>
                        </div>
                        <form name="logout" action="<%= context.getBaseUrl() %>/openWisLogout" method="post" id="loginFormEl">
                		    <div class="dss-logoutDiv">
                			    <button type="submit" class="dss-logout-button"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Logout').toUpperCase())</script></button>
                			    <i class="iconIOS7-bt_link_on"></i>
                			    <input type="hidden" name="lang" value="<%= context.getLanguage() %>"/>
                		    </div>
                        </form>
                    </div>
                <% } else { %>
                    <% if ("user".equals(portalType) && !context.isDebug() ) { %>
                        <div class="dss-loginDiv">
                            <a class="dss-login-ref" href="<%= locService %>/user.loginCaptcha.get"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login').toUpperCase())</script></a>
                    	</div>
                    	<%} else {%>
                        <div class="dss-loginDiv">
                            <a class="dss-login-ref" href="<%= context.getBaseUrl() %>/openWisInit"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login').toUpperCase())</script></a>
                        </div>
                    <% } %>
                <% } %>
            </div>
        </div>
    </div>
</div>
<div class="dss-menu-container">
<table class="dss-menu">
    <tr>
        <td class="dss-menu-nav-item">ASMC</td>
        <td class="dss-menu-nav-item">/</td>
        <td>
            <div class="dss-datacatalog">
                <div class="dropdown">
                    <a class="dss-menu-nav-item" href="#">Data Catalog</a>
                    <ul class="dropdown-content">
                        <li>
                        <a class="prod" id="noaa-20" onclick="showItem('noaa-20')" href="#">Satellite Images (JPSS 1/NOAA-20)</a></li>
                        <li><a class="prod" id="Suomi-NPP" onclick="showItem('Suomi-NPP')" href="#">Satellite Images (Suomi-NPP)</a></li>
                        <li><a class="prod" id="aqua" onclick="showItem('aqua')" href="#">Satellite Images (AQUA)</a></li>
                        <li><a class="prod" id="terra" onclick="showItem('terra')" href="#">Satellite Images (TERRA)</a></li>
                        <li><a class="prod" id="haze" onclick="showItem('haze')" href="#">Regional Haze Situation</a></li>
                        <li><a class="prod" id="hostpot_noaa20" onclick="showItem('hostpot noaa20')" href="#">Hotspot Reports (JPSS 1/NOAA-20)</a></li>
                        <li><a class="prod" id="hostpot_npp" onclick="showItem('hostpot npp')" href="#">Hotspot Reports (Suomi-NPP)</a></li>
                        <li><a class="prod" id="hostpot_aqua" onclick="showItem('hostpot aqua')" href="#">Hotspot Reports (AQUA)</a></li>
                        <li><a class="prod" id="hostpot_terra" onclick="showItem('hostpot terra')" href="#">Hotspot Reports (TERRA)</a></li>
                        <li><a class="prod" id="haze_dispersion" onclick="showItem('haze dispersion')" href="#">Smoke Haze Dispersion
                            Model</a></li>
                    </ul>
                </div>
            </div>
        </td>

    </tr>
</table>
   <div class="dss-menu-nav-item" id="produit">
       <script type="text/javascript">
           function showItem(id) {
               let product = document.getElementById("produit");
               product.innerHTML = "/ " + document.getElementById(id).innerHTML;
               document.getElementById("ext-comp-1019").value = document.getElementById(id).innerHTML;
               document.getElementById("ext-gen105").click();
           }
       </script>
   </div>
</div>

