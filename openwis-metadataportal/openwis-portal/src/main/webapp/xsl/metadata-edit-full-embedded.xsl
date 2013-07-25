<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:geonet="http://www.fao.org/geonetwork" exclude-result-prefixes="geonet">
    <!-- XSL is associated with metadata.edit.embedded and allows a full editor to be in a dialog
        
                NOTE: In order for the editor to function correctly embedded the following functions must be defined:
        
                    metadata.edit.embedded.doTabAction(action,tab)
                    metadata.edit.embedded.doAction(action,tab)
        
            -->

    <xsl:import href="main.xsl"/>
    <xsl:include href="metadata.xsl"/>

    <xsl:template match="/">
        <table width="100%" height="100%">

            <!-- content -->
            <tr height="100%">
                <td>
                    <xsl:call-template name="content"/>
                </td>
            </tr>
        </table>
    </xsl:template>

    <!--
        	page content
        	-->
    <xsl:template name="content">
        <img id="editorBusy" src="{/root/gui/url}/images/spinner.gif" alt="busy"
            style="display:none"/>
        <table id="editFormTable" width="100%">
            <xsl:for-each select="/root/*[name(.)!='gui' and name(.)!='request']">
                <!-- just one -->
                <tr>
                    <td class="content" valign="top">
                        <form id="editForm" name="mainForm" accept-charset="UTF-8" method="POST"
                            action="{/root/gui/locService}/metadata.update">
                            <input class="md" type="hidden" name="id" value="{geonet:info/id}"/>
                            <input class="md" type="hidden" name="version"
                                value="{geonet:info/version}"/>
                            <input class="md" type="hidden" name="ref"/>
                            <input class="md" type="hidden" name="name"/>
                            <input class="md" type="hidden" name="licenseurl"/>
                            <input class="md" type="hidden" name="type"/>
                            <input class="md" type="hidden" name="editing" value="{geonet:info/id}"/>
                            <input class="md" type="hidden" name="child"/>
                            <input class="md" type="hidden" name="fname"/>
                            <input class="md" type="hidden" name="access"/>
                            <input class="md" type="hidden" name="position" value="-1"/>
                            <!-- showvalidationerrors is only set to true when 'Check' is
                                							     pressed - default is false -->
                            <input class="md" type="hidden" name="showvalidationerrors"
                                value="{/root/request/showvalidationerrors}"/>
                            <input class="md" type="hidden" name="currTab"
                                value="{/root/gui/currTab}"/>

                            <!-- Hidden div to contains extra elements like when posting multiple keywords. -->
                            <div id="hiddenFormElements" style="display:none;"/>

                            <table width="100%">
                                <tr>
                                    <td class="padded-content">
                                        <table class="md" width="100%">
                                            <xsl:choose>
                                                <xsl:when test="$currTab='xml'">
                                                  <xsl:apply-templates mode="xmlDocument" select=".">
                                                  <xsl:with-param name="edit" select="true()"/>
                                                  </xsl:apply-templates>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <xsl:apply-templates mode="elementEP" select=".">
                                                  <xsl:with-param name="edit" select="true()"/>
                                                  </xsl:apply-templates>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="padded-content" height="100%" align="center"
                                        valign="top">
                                        <xsl:call-template name="templateChoice"/>
                                    </td>
                                </tr>
                            </table>
                        </form>

                        <div id="validationReport" class="content" style="display:none;"/>

                    </td>
                </tr>
            </xsl:for-each>
        </table>

        <xsl:if test="/root/request/download_scripts/text() = 'true'">
            <xsl:call-template name="edit_js"/>
        </xsl:if>
    </xsl:template>

    <xsl:template name="templateChoice" match="*">

        <b>
            <xsl:value-of select="/root/gui/strings/type"/>
        </b>
        <xsl:text>&#160;</xsl:text>
        <select class="content" name="template" size="1">
            <!--  OpenWIS: do not allow going from template to metadata -->
            <xsl:if test="string(geonet:info/isTemplate)='n'">
               <option value="n">
                <xsl:if test="string(geonet:info/isTemplate)='n'">
                    <xsl:attribute name="selected">true</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="/root/gui/strings/metadata"/>
               </option>
            </xsl:if>
            <option value="y">
                <xsl:if test="string(geonet:info/isTemplate)='y'">
                    <xsl:attribute name="selected">true</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="/root/gui/strings/template"/>
            </option>
        </select>
    </xsl:template>


    <xsl:template name="edit_js">

        <!--<xsl:choose>
            <xsl:when test="/root/request/debug">-->
                <script type="text/javascript" src="{/root/gui/url}/scripts/editor/metadata-editor.js"/>
                <script type="text/javascript" src="{/root/gui/url}/scripts/editor/simpletooltip.js"/>
            <!--</xsl:when>
            <xsl:otherwise>
                <script type="text/javascript" src="{/root/gui/url}/scripts/lib/gn.editor.js"/>
            </xsl:otherwise>
        </xsl:choose>-->

        <xsl:call-template name="edit-header"/>

        <style type="text/css">
            @import url(<xsl:value-of select="/root/gui/url"/>
            /scripts/calendar/calendar-blue2.css);</style>
        <script type="text/javascript" src="{/root/gui/url}/scripts/webtoolkit.aim.js"/>

        <!-- =================================
                            Google translation API demo (Load the API in version 1).
                    ================================= -->
        <xsl:if test="/root/gui/config/editor-google-translate = 1">
            <script type="text/javascript" src="http://www.google.com/jsapi"/>
            <script type="text/javascript">
                google.load("language", "1");
            </script>
        </xsl:if>

    </xsl:template>
</xsl:stylesheet>
