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
                    <li><a class="footer-link" id="noaa-20" onclick="showItem('noaa-20')" href="#">Satellite Images (JPSS 1/NOAA-20)</a></li>
                    <li><a class="footer-link" id="Suomi-NPP" onclick="showItem('Suomi-NPP')" href="#">Satellite Images (Suomi-NPP)</a></li>
                    <li><a class="footer-link" id="aqua" onclick="showItem('aqua')" href="#">Satellite Images (AQUA)</a></li>
                    <li><a class="footer-link" id="terra" onclick="showItem('terra')" href="#">Satellite Images (TERRA)</a></li>
                    <li><a class="footer-link" id="haze" onclick="showItem('haze')" href="#">Regional Haze Situation</a></li>
                    <li><a class="footer-link" id="hostpot_noaa20" onclick="showItem('hostpot noaa20')" href="#">Hotspot Reports (JPSS 1/NOAA-20)</a></li>
                    <li><a class="footer-link" id="hostpot_npp" onclick="showItem('hostpot npp')" href="#">Hotspot Reports (Suomi-NPP)</a></li>
                    <li><a class="footer-link" id="hostpot_aqua" onclick="showItem('hostpot aqua')" href="#">Hotspot Reports (AQUA)</a></li>
                    <li><a class="footer-link" id="hostpot_terra" onclick="showItem('hostpot terra')" href="#">Hotspot Reports (TERRA)</a></li>
                    <li><a class="footer-link" id="haze_dispersion" onclick="showItem('haze dispersion')" href="#">Smoke Haze Dispersion
                    Model</a></li>
                </ul>
            </div>
        </div>
        <div class="main-content-right">
            <ul class="links">
                <li><p class="title">ABOUT</p></li>
                <li><p class="title">CONTACT US</p></li>
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
                <li><a class="footer-link" href="https://tech.gov.sg/report_vulnerability" target="_blank">Report Vulnerability</a></li>
		<li>|</li>
                <li><a class="footer-link" href="http://asmc.asean.org/terms-of-use/" target="_blank">Privacy Statement</a></li>
		<li>|</li>
                <li><a class="footer-link" href="http://asmc.asean.org/privacy-statement/" target="_blank">Terms of Use</a></li>
		<li>|</li>
                <li><a class="footer-link" href="https://form.sg/#!/forms/nea/5a5ff71aba8b0c6b0022dead" target="_blank">Rate this Website</a></li>
            </ul>
        </div>
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
