<div class="dss-logo-container">
    <div>
        <div class="dss-logo">
            <div class="dss-logo-left dss-logo-common">
                <div>
                    <img src="<%= context.getBaseUrl() %>/images/openwis/header-left.png" alt="World picture" align="top">
                </div>
            </div>
            <div class="dss-logo-middle dss-logo-common">
                <div class="dss-logo-middle-container dss-logo-common">
                    <div>
                        <a class="dss-logo-middle-link" href="http://www.openwis.io/">About</a>&nbsp;
                    </div>
                    <div>
                        <a class="dss-logo-middle-link" href="mailto: contact@openwis.io">Contact Us</a>
                    </div>
                </div>
            </div>
            <div class="dss-logo-right dss-logo-common">
                <div class="dss-logo-right-container">
                    <img class="logo-mfi" src="<%= context.getBaseUrl() %>/images/openwis/header-right.gif" width="150"
                         alt="GeoNetwork opensource logo" align="top">
                </div>
            </div>
        </div>
        <div class="dss-main-nav">
            <div class="dss-nav-left">
                <div class="dss-nav-item"><a href="<%= locService %>/main.home">HOME</a></div>

               <% if (context.getUserSession() != null && context.getUserSession().getUserId() == null
                   && selfRegistrationEnabled && "user".equals(portalType)) { %>
                      <div class="dss-nav-item" >
                        <% if ("user.register.get".equals(context.getService())) { %>
                          <font class="dss-nav-item"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Register').toUpperCase())</script></font>
                            <% } else { %>
                         <a class="dss-nav-item" href="<%= locService %>/user.register.get"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Register').toUpperCase())</script></a>
                        <% } %>
                      </div>
                <% } %>


                <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) {%>
                    <% if ("myaccount".equals(context.getService())) { %>
                            <div class="dss-nav-item"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount').toUpperCase())</script></div>
                    <% } else { %>
                            <div class="dss-nav-item"><a href="<%= locService %>/myaccount"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount').toUpperCase())</script></a></div>
                    <% } %>
                <% } %>
                <div class="dss-nav-item"><a href="<%= locService %>/help.home">HELP</a></div>
            </div>
            <!-- Login -->
            <div class="dss-nav-login">
                <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) {%>
                    <%
                         String nameForHtml = StringEscapeUtils.escapeHtml(context.getUserSession().getName());
                         String surnameForHtml = StringEscapeUtils.escapeHtml(context.getUserSession().getSurname());
                    %>
                    <div class="dss-logout-container">
                        <div class="dss-logout-username">
                            <%= nameForHtml %> <%= surnameForHtml %>
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
                        <div class="dss-loginDiv">
                            <a class="dss-login-ref" href="<%= locService %>/user.loginCaptcha.get"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login').toUpperCase())</script></a>
                    	</div>
                <% } %>
            </div>
        </div>
    </div>
</div>
<div class="dss-menu-container">
<!--<table class="dss-menu">
    <tr>
        <td class="dss-menu-nav-item">WIS</td>
        <td class="dss-menu-nav-item dss-menu-nav-separator">/</td>
        <td>
            <div class="dss-datacatalog">
                <div class="dropdown">
                    <a class="dss-menu-nav-item" href="#">Data Catalog</a>
                    <ul class="dropdown-content">
                        <li><a class="prod" id="category1" onclick="showItem('category1')" href="#">Category 1</a></li>
                        <li><a class="prod" id="category2" onclick="showItem('category2')" href="#">Category 2</a></li>
                    </ul>
                </div>
            </div>
        </td>

    </tr>
</table>-->
   <div class="dss-menu-nav-item" id="produit">
   </div>
</div>
<% if (request.getAttribute("maintenanceBanner") != null) {%>
    <div class="dss-maintenance-container">
        <div class="dss-maintenance-title">
            <p>Maintenance Notice</p>
        </div>
        <div class="dss-maintenance-notice">
            <p class="dss-maintenance-date"><%=request.getAttribute("maintenanceBanner")%></p>
        </div>
    </div>
<% } %>

