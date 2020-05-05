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
            <div class="dss-nav-catalog"><a href="<%= locService %>/main.home">DATA CATALOG</a></div>
            <div><a href="<%= locService %>/help.home">HELP</a></div>
            <!-- Login -->
            <div class="dss-nav-login">
                <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) {
                     String entityID = (String) context.getUserSession().getProperty("idpEntityID");
                %>
                <form name="logout" action="<%= context.getBaseUrl() %>/openWisLogout" method="post" id="loginFormEl">
                    <%if (request.getAttribute("userLastLogin") !=  null) {%>
                	    <div><%=request.getAttribute("userLastLogin")%></div>
                	<%}%>
                		<div class="logoutDiv">
                	    	<%= context.getUserSession().getName() %> <%= context.getUserSession().getSurname() %><br/> (<%= entityID %>)
                		</div>
                		<div class="logoutDiv">
                			<button type="submit"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Logout'))</script></button>
                			<i class="iconIOS7-bt_link_on"></i>
                			<input type="hidden" name="lang" value="<%= context.getLanguage() %>"/>
                		</div>
                </form>
                <% } else { %>
                    <% if ("user".equals(portalType) && !context.isDebug() ) { %>
                        <div class="dss-loginDiv">
                            <a class="dss-login-ref" href="<%= locService %>/user.loginCaptcha.get"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login'))</script></a>
                    	</div>
                    	<%} else {%>
                        <div class="dss-loginDiv">
                            <a class="dss-login-ref" href="<%= context.getBaseUrl() %>/openWisInit"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login'))</script></a>
                        </div>
                    <% } %>
                <% } %>
            </div>
        </div>
    </div>
</div>

