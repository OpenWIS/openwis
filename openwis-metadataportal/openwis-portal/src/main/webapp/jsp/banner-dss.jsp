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
                        <a class="prod" id="noaa20" onclick="showItem('noaa20')" href="#">Satellite Images (JPSS 1/NOAA-20)</a></li>
                        <li><a class="prod" id="suomiNpp" onclick="showItem('suomiNpp')" href="#">Satellite Images (Suomi-NPP)</a></li>
                        <li><a class="prod" id="aqua" onclick="showItem('aqua')" href="#">Satellite Images (AQUA)</a></li>
                        <li><a class="prod" id="terra" onclick="showItem('terra')" href="#">Satellite Images (TERRA)</a></li>
                        <li><a class="prod" id="haze" onclick="showItem('haze')" href="#">Regional Haze Situation</a></li>
                        <li><a class="prod" id="hotspotNoaa20" onclick="showItem('hotspotNoaa20')" href="#">Hotspot Reports (JPSS 1/NOAA-20)</a></li>
                        <li><a class="prod" id="hotspotNpp" onclick="showItem('hotspotNpp')" href="#">Hotspot Reports (Suomi-NPP)</a></li>
                        <li><a class="prod" id="hotspotAqua" onclick="showItem('hotspotAqua')" href="#">Hotspot Reports (AQUA)</a></li>
                        <li><a class="prod" id="hotspotTerra" onclick="showItem('hotspotTerra')" href="#">Hotspot Reports (TERRA)</a></li>
                        <li><a class="prod" id="hazeDispersion" onclick="showItem('hazeDispersion')" href="#">Smoke Haze Dispersion
                            Model</a></li>
                    </ul>
                </div>
            </div>
        </td>

    </tr>
</table>
   <div class="dss-menu-nav-item" id="produit">
   </div>
</div>
<% if (request.getAttribute("maintenanceDate") != null) {%>
    <div class="dss-maintenance-container">
        <div class="dss-maintenance-title">
            <p>Maintenance Notice</p>
        </div>
        <div class="dss-maintenance-notice">
            <p>The WIS Portal will be undergoing scheduled maintenance and will be unavailable on </p>
            <p class="dss-maintenance-date"><%=request.getAttribute("maintenanceDate")%></p>
        </div>
    </div>
<% } %>

