<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:output method="html" encoding="iso-8859-1" indent="no" />

   <xsl:template match="listing">
      <html>
         <head>
            <title>
               OpenWIS Staging Post
            </title>
            <style>
               html {
               height:100%;
               }

               body {
               margin: 0;
               padding: 0;
               font-family: Arial,
               Helvetica, FreeSans, sans-serif;
               font-size: 11px;
               line-height: 1.4;
               color: black;
               height:100%;
               }

               b{color :
               white;background-color : #0086b2;}
               a{color : black;}

               .body-west-panel {
               background:
               url("/stagingPost/images/body-border-left.gif") repeat-y right top
               #F2F2F2;}
               .body-east-panel {
               background:
               url("/stagingPost/images/body-border-right.gif") repeat-y left top
               #F2F2F2;}
               .body-center-panel {
               background:
               url("/stagingPost/images/fond.png") repeat-x scroll 0 0px white
               !important;
               height:100%;}

               .myAccountTitle1 {
               border-bottom: 1px solid
               #2A628F;
               color: black;
               font: bold 12px
               Verdana,Arial,Geneva,Helvetica,sans-serif;
               height: 30px;
               margin-top: 30px;
               margin-left: 10px;
               margin-bottom: 10px;
               margin-right: 10px;
               text-align: left;
               text-transform:
               uppercase;
               }

               .fileTable {
               margin-top: 20px;
               }
             
      </style>
         </head>
         <body>
            <table cellspacing="0" width="100%" height="100%" align="center">
               <tr>
                  <td width="100px" class="body-west-panel">
                  </td>
                  <td class="body-center-panel" valign="top">
                     <table cellspacing="0" width="100%" align="center">
                        <tr>
                           <td>
                              <div class="myAccountTitle1">
                                 OpenWIS Staging Post</div>
                           </td>
                        </tr>
                        <tr>
                           <td>
                              <table class="fileTable"
                                 cellspacing="0" width="95%"
                                 cellpadding="5" align="center">
                                 <tr>
                                    <th align="left">Filename</th>
                                    <th align="center">Size</th>
                                    <th align="right">Last Modified</th>
                                 </tr>
                                 <xsl:apply-templates
                                    select="entries" />
                              </table>
                           </td>
                        </tr>
                     </table>
                  </td>
                  <td width="100px" class="body-east-panel">
                  </td>
               </tr>
            </table>
            <xsl:apply-templates select="readme" />
         </body>
      </html>
   </xsl:template>


   <xsl:template match="entries">
      <xsl:apply-templates select="entry" />
   </xsl:template>

   <xsl:template match="readme">
      <hr size="1" />
      <pre>
         <xsl:apply-templates />
      </pre>
   </xsl:template>

   <xsl:template match="entry">
      <xsl:if test="@type='file'">
         <tr>
            <td align="left">
               <xsl:variable name="urlPath" select="@urlPath" />
               <a href="{$urlPath}">
                  <tt>
                     <xsl:apply-templates />
                  </tt>
               </a>
            </td>
            <td align="center">
               <tt>
                  <xsl:value-of select="@size" />
               </tt>
            </td>
            <td align="right">
               <tt>
                  <xsl:value-of select="@date" />
               </tt>
            </td>
         </tr>
      </xsl:if>
   </xsl:template>

</xsl:stylesheet>
