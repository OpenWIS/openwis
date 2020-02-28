<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.time.LocalDate"%>
<STYLE type="text/css">

#mss-footer {
width: 990px;
margin-left: auto;
margin-right: auto;
padding-top: 10px;
color: white;
background-color:#006FA1;
font-size: 1.07em;
}

.test {
  color :#FF8C00;
}

#mss-footer a {
text-decoration: none;
color: #3C4751;
}

#copyrights {
    float: right;
    font-weight: bold;
}
#tos {
    float: left;
}
 </STYLE>
<!-- <div id="mss-footer">
   <div id="tos">
        <a href="https://tech.gov.sg/report_vulnerability" target="_blank">Report Vulnerability</a> | 
        <a href="http://asmc.asean.org/terms-of-use/" target="_blank">Privacy Statement</a> | 
        <a href="http://asmc.asean.org/privacy-statement/" target="_blank">Terms of Use</a> | 
        <a href="https://form.sg/#!/forms/nea/5a5ff71aba8b0c6b0022dead" target="_blank">Rate this Website</a>
   </div>
   <div id="copyrights">&copy;2019 National Environment Agency</div>
</div> -->
<div id="mss-footer">
    <div style="margin:10px;">
      <div>
        <div align="left" width="33%">
          <h5 style="font-weight: bold;">DATA CATALOG</h5><hr>
          <p>Satellite Images (JPSS 1/NOAA-20)<br>Satellite Images (Suomi-NPP)<br>Satellite Images (AQUA)<br>Satellite Images (TERRA)<br>Regional Haze Situation<br>Hotspot Reports (JPSS 1/NOAA-20)<br>Hotspot Reports (Suomi-NPP)<br>Hotspot Reports (AQUA)<br>Hotspot Reports (TERRA)<br>Smoke Haze Dispersion Model</p>
        </div>
        <div align="left" width="33%" style="margin-left:900px;">
          <h5 style="font-weight: bold;">ABOUT</h5>
		  <h5 style="font-weight: bold;">CONTACT US</h5>
		  <h5 style="font-weight: bold;">FOLLOW US ON</h5>
        </div>
        <div width="66%" align="left" style=" margin:auto;">
		  <p>Best viewed using IE 11, Firefox 52, Chrome 52, Safari 8, Opera 43 and above</p>
	    </div>
	    <div width="66%" align="left" style="float: left;">
		 <p>Report Vulnerability | Privacy Statement | Terms of Use | Rate this Website</p>
		</div>
		<br>
		<div>
		  <p><hr class ="test"><p>
		</div>
		<div>
		  <p width="33%" align="right">&copy;2020 ASEAN Specialised Meteorological Centre</p>
		</div>
		<div>
		  <p width="33%" align="right">Last Updated <%= LocalDate.now() %></p>
		</div>
      </div>
  </div>
</div>