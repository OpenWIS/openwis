<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat" %>
<%! final static String DATE_FORMAT_NOW = "dd/MM/yy"; %>

<div class="footer">
    <div class="main-content">
        <!--<div class="main-content-left">
            <div class="title title-border">
                <p>DATA CATALOG</p>
            </div>
            <div class="catalog-links-container">
                <ul class="links">
                    <li><a class="footer-link" id="noaa20" onclick="showItem('category1')" href="#">Category 1</a></li>
                    <li><a class="footer-link" id="suomiNpp" onclick="showItem('category2')" href="#">Category 2</a></li>
                </ul>
            </div>
        </div>-->
        <div class="main-content-right">
            <ul class="links">
                <li><a href="http://www.openwis.io/"><p class="title">ABOUT</p></a></li>
                <li><a href="mailto: contact@openwis.io"><p class="title">CONTACT US</p></a></li>
                <li class="subtitle"><p>FOLLOW US ON</p>
			<a  href="https://twitter.com/MFI_met">
                    <img src="<%= context.getBaseUrl() %>/images/openwis/twitter.png" width="150"
                         alt="icon rss" align="top">
			</a>
                </li>
            </ul>
        </div>
    </div>
    <div class="bottom-content">
        <div class="browser title-border text-to-right">
            <p>Best view using IE 11, Firefox 52, Chrome 52, Safari 8, Opera 43 and above</p>
        </div>
        <div class="bottom-link-container">
	    <%-- <ul class="bottom-links">
        </ul> --%>
        </div>
        <script type="text/javascript">
            function redirectWarning() {
                alert("Warning! You will be redirected to an external site.");
            }
        </script>

        <div class="copyright text-to-right">
            <div id="copyrights">&copy;2020 OpenWIS Association</div>
            <div class="footer-dates">
             <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) {%>
                <div class="loginDate">
                    Last login:
                        <%if (request.getAttribute("userLastLogin") !=  null) {%>
                            <div class="login-date-inner"><%=request.getAttribute("userLastLogin")%></div>
                         <%}%>
                </div>
                <%} else {%>
                    <div></div>
                <%}%>
                <div class="date">Last updated
                  <script>
                        var today  = new Date();
                        const months = {
                            0: 'January',1: 'February', 2: 'March', 3: 'April', 4: 'May', 5: 'June', 6: 'July', 7: 'August',
                            8: 'September', 9: 'October', 10: 'November', 11: 'December' }
                        const m = months[today.getMonth()]
                        document.write(today.getDate() + " " + m + " " +  today.getFullYear()); // Sat
                </script>
                </div>
             </div>
        </div>
    </div>
</div>
