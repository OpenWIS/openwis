<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat" %>
<%! final static String DATE_FORMAT_NOW = "dd/MM/yy"; %>

<div class="footer">
    <div class="main-content">
        <div class="main-content-left">
            <div class="title title-border">
                <p>DATA CATALOG</p>
            </div>
            <div class="catalog-links-container">
                <ul class="links">
                    <li><a class="footer-link" id="noaa20" onclick="showItem('noaa20')" href="#">Satellite Images (JPSS 1/NOAA-20)</a></li>
                    <li><a class="footer-link" id="suomiNpp" onclick="showItem('suomiNpp')" href="#">Satellite Images (Suomi-NPP)</a></li>
                    <li><a class="footer-link" id="aqua" onclick="showItem('aqua')" href="#">Satellite Images (AQUA)</a></li>
                    <li><a class="footer-link" id="terra" onclick="showItem('terra')" href="#">Satellite Images (TERRA)</a></li>
                    <li><a class="footer-link" id="haze" onclick="showItem('haze_map')" href="#">Regional Haze Situation</a></li>
                    <li><a class="footer-link" id="hotspotNoaa20" onclick="showItem('hotspotNoaa20')" href="#">Hotspot Reports (JPSS 1/NOAA-20)</a></li>
                    <li><a class="footer-link" id="hotspotNpp" onclick="showItem('hotspotNpp')" href="#">Hotspot Reports (Suomi-NPP)</a></li>
                    <li><a class="footer-link" id="hotspotAqua" onclick="showItem('hotspotAqua')" href="#">Hotspot Reports (AQUA)</a></li>
                    <li><a class="footer-link" id="hotspotTerra" onclick="showItem('hotspotTerra')" href="#">Hotspot Reports (TERRA)</a></li>
                    <li><a class="footer-link" id="hazeDispersion" onclick="showItem('hazeDispersion')" href="#">Smoke Haze Dispersion
                    Model</a></li>
                </ul>
            </div>
        </div>
        <div class="main-content-right">
            <ul class="links">
                <li><a onclick="redirectWarning()" href="http://asmc.asean.org/asmc-about/"><p class="title">ABOUT</p></a></li>
                <li><a onclick="redirectWarning()" href="http://asmc.asean.org/asmc-contact-us/"><p class="title">CONTACT US</p></a></li>
                <li class="subtitle"><p>FOLLOW US ON</p>
			<a  href="http://asmc.asean.org/feed/">
                    <img src="<%= context.getBaseUrl() %>/images/icon-rss.png" width="150"
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
		<ul class="bottom-links">
                <li><a class="footer-link" onclick="redirectWarning()" href="https://tech.gov.sg/report_vulnerability" target="_blank">Report Vulnerability</a></li>
		<li>|</li>
                <li><a class="footer-link" onclick="redirectWarning()" href="http://asmc.asean.org/terms-of-use/" target="_blank">Terms of use</a></li>
		<li>|</li>
                <li><a class="footer-link" onclick="redirectWarning()" href="http://asmc.asean.org/privacy-statement/" target="_blank">Privacy Statement</a></li>
        </ul>
        </div>
        <script type="text/javascript">
            function redirectWarning() {
                alert("Warning! You will be redirected to an external site.");
            }
        </script>

        <div class="copyright text-to-right">
            <div id="copyrights">&copy;2020 National Environment Agency</div>
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
