<%@page import="org.openwis.metadataportal.services.search.dto.RelatedMetadataDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.io.File"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.lang.*"%>
<%@ page import="jeeves.server.context.*"%>
<%@ page import="org.fao.geonet.kernel.search.*"%>
<%@ page import="org.openwis.metadataportal.kernel.search.query.*"%>
<%@ page import="org.openwis.metadataportal.model.datapolicy.*"%>

<%@ taglib prefix="openwis" tagdir="/WEB-INF/tags/openwis"%>
<%
	ServiceContext context = (ServiceContext) request.getAttribute("context");
	
	request.setAttribute("message",
         ResourceBundle.getBundle("msgSearchResult", new Locale(context.getLanguage())));
	
	// Search result attribtues
	String locService = context.getBaseUrl() + "/srv/" + context.getLanguage();
	SearchResult sr = (SearchResult) request.getAttribute("searchResult");
	Map<String, Set<OperationEnum>> operationsAllowed = (Map<String, Set<OperationEnum>>) request.getAttribute("operationsAllowed");
	Map<String, List<RelatedMetadataDTO>> relatedMetadata = (Map<String, List<RelatedMetadataDTO>>) request.getAttribute("relatedMetadata");
	String username = (String) request.getAttribute("username");
	boolean editorProfile = "Editor".equalsIgnoreCase(context.getUserSession().getProfile());
	boolean isCacheEnable = Boolean.TRUE.equals((Boolean) request.getAttribute("isCacheEnable"));  
%>
<div id="search-results-content">
<% if (sr==null) {
%>
	<span class="error"><openwis:i18n key="HomePage.Search.Result.Error"/></span>
<% } else {
	// param 
	int from = sr.getFrom();
	int to = sr.getTo();
	int count = sr.getCount();
	int hits = sr.getQuery().getHitsPerPage();
	
	// page
	int pageRoll = 5; 
	int currentPage = (from/hits) + 1;
	int maxPage = ((count-1)/hits) + 1;
   %>
	<%-- Title --%>
	<div class="results_header">
<%-- 		<a name="page_<%=currentPage%>"/> --%>
		<div class="results_title" style="float: left;">
			<openwis:i18n key="HomePage.Search.Result.ResultsMatching"/>
			&nbsp;<%=from+1 %>-<%=to+1%>/<%=count%>&nbsp;(<openwis:i18n key="HomePage.Search.Result.Page"/>
			<%=currentPage%>/<%=maxPage%>)&nbsp;
		</div>
	</div>

	<%-- Hits --%>
	<% 
	   String id;
	   String uuid;
	   Set<OperationEnum> ops;
	   List<RelatedMetadataDTO> relatedMds;
	   String title;
	   String titleForHtml;
	   String titleForJs;
	   String abst;
	   String source;
	   List<String> keywords;
	   boolean isGlobal;
	   for (SearchResultDocument doc : sr) {
	     pageContext.setAttribute("doc",doc);
	     id = doc.getId();
	     title = doc.getFieldAsString(IndexField._TITLE);
	     titleForHtml = StringEscapeUtils.escapeHtml(title);
	     titleForJs = StringEscapeUtils.escapeJavaScript(title);
	     keywords = doc.getFieldAsListOfString(IndexField.KEYWORD);
	     abst = doc.getFieldAsString(IndexField.ABSTRACT);
	     source =  doc.getFieldAsString(IndexField.SOURCE);
	     isGlobal = "true".equals(doc.getField(IndexField.IS_GLOBAL));
	     uuid = doc.getFieldAsString(IndexField.UUID);
	     ops = operationsAllowed.get(uuid.toLowerCase());
	     
	     // Get the list of related metadata for the current hit
	     relatedMds = relatedMetadata.get(uuid);

	     pageContext.setAttribute("ops",ops);
	     boolean editable = "n".equals(doc.getField(IndexField.IS_HARVESTED)) 
	       && (ops!=null && ops.contains(OperationEnum.EDITING))
	       && editorProfile;
	%>
	<div class="hit">
<%-- 		<a name="<%=uuid%>"/> --%>
		<div class="hittext">
			<div class="thumbnail_results">
				<div class="icones">
					<%-- View --%>
					<% if (ops!=null && ops.contains(OperationEnum.VIEW)) {%>
					<div class="metadatabuttons">
						<button id="gn_showmd_<%=id%>" class="content"
							title='<openwis:i18n key="HomePage.Search.Result.Show"/>'
							onclick="doShowMetadataById(<%=id%>, '<%=titleForJs%>', <%= editable %>)" >
							<img
								src="<%= context.getBaseUrl() %>/images/openwis/icone_voir_detail.png"
								alt='<openwis:i18n key="HomePage.Search.Result.ShowMetadata"/>' />
						</button>
						<button id="gn_loadmd_<%=id%>" class="content"
							title='<openwis:i18n key="HomePage.Search.Result.Show"/>'
							style="display: none;">
							<img src="<%= context.getBaseUrl() %>/images/openwis/loading.gif"
								title='<openwis:i18n key="HomePage.Search.Result.Loading"/>' />
						</button>
					</div>
					<% } %>
					
					<%-- Edit --%>
					<% if (editable) { %>
					<span class="metadatabuttons">								
						<button id="gn_editmd_<%=id%>"  
							class="content" onclick="doEditMetadataById(<%=id%>, '<%=titleForJs%>')"
							title='<openwis:i18n key="HomePage.Search.Result.Btn.Edit"/>'>
							<img src="<%= context.getBaseUrl() %>/images/openwis/icone_editer.png"
							 alt="<openwis:i18n key="HomePage.Search.Result.Btn.EditMetadata"/>"/>
						</button>
					</span>
					<% } %>
					
					<%-- Delete 
					<% if (username !=null && username.equals(doc.getField(IndexField.OWNER))) { %>
					<span class="metadatabuttons">
						<button id="gn_deletemd_<%=id%>"  
						class="content"
						onclick="return doConfirmDelete('<%=locService%>/metadata.delete?id=<%=id%>', '<openwis:i18n key="HomePage.Search.Result.Btn.Delete.Confirm"/>','title','<%=id%>', '<openwis:i18n key="HomePage.Search.Result.Btn.Delete.ConfirmTitle"/>')"
						title='<openwis:i18n key="HomePage.Search.Result.Btn.Delete"/>'>
							<img src="<%= context.getBaseUrl() %>/images/openwis/icone_suppr.png" 
							alt='<openwis:i18n key="HomePage.Search.Result.Btn.DeleteMetadata"/>'/>
						</button>
					</span>
					<% } %>
					--%>
					<%-- hit export --%>
					<div class="hitexport">
						<% if ("dublin-core".equals(doc.getField(IndexField.SCHEMA))) { %>
						<a href="<%=locService%>/dc.xml?id=<%=id%>" target="_blank" 
							title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.DublinCore"/>'>
							<img src="<%= context.getBaseUrl() %>/images/openwis/icone_export_XML.png" 
							alt='<openwis:i18n key="HomePage.Search.Result.Btn.Download.DublinCore.Alt"/>' 
							title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.DublinCore.Title"/>' border="0"/>
						</a>
						<% } else if ("fgdc-std".equals(doc.getField(IndexField.SCHEMA))) { %>
						<a href="<%=locService%>/fgdc.xml?id=<%=id%>" target="_blank" 
							title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.FgdcStd"/>'>
							<img src="<%= context.getBaseUrl() %>/images/openwis/icone_export_XML.png" 
								alt='<openwis:i18n key="HomePage.Search.Result.Btn.Download.FgdcStd.Alt"/>' 
								title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.FgdcStd.Title"/>' border="0"/>
						</a>
						<% } else if ("iso19115".equals(doc.getField(IndexField.SCHEMA))) { %>
						<a href="<%=locService%>/iso19115to19139.xml?id=<%=id%>" target="_blank" 
							title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19115"/>'>
							<img src="<%= context.getBaseUrl() %>/images/openwis/icone_export_XML.png" 
								alt='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19115.Alt"/>' 
								title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19115.Title"/>' border="0"/>
						</a>
						<% } else if ("iso19139".equals(doc.getField(IndexField.SCHEMA))) { %>
						<a href="<%=locService%>/iso19139.xml?id=<%=id%>" target="_blank" 
							title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19139"/>'>
							<img src="<%= context.getBaseUrl() %>/images/openwis/icone_export_XML.png" 
								alt='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19139.Alt"/>' 
									title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19139.Title"/>' border="0"/>
						</a>
						<% } else if ("iso19110".equals(doc.getField(IndexField.SCHEMA))) { %>
                        <a href="<%=locService%>/iso19110.xml?id=<%=id%>" target="_blank" 
                            title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19110"/>'>
                            <img src="<%= context.getBaseUrl() %>/images/openwis/icone_export_XML.png" 
                                alt='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19110.Alt"/>' 
                                    title='<openwis:i18n key="HomePage.Search.Result.Btn.Download.Iso19110.Title"/>' border="0"/>
                        </a>
                        <% } else { %>
						&nbsp;
						<% } %>
					</div>
				</div>
			</div>
			<%-- Body --%>
			<div class="hittext_top">
				<!-- Title -->
				<% if (doc.getField(IndexField._TITLE)!=null) { %>
				<div class="hittitle">
					<% if ("true".equals(doc.getField(IndexField.STOP_GAP))) { %>
					<span><openwis:i18n key="HomePage.Search.Result.Draft"/></span>
					<% } %>
					<%=titleForHtml%>
				</div>
				<% } else { %>
				<div class="hittitle" style="font-style: italic;">
					<% if ("true".equals(doc.getField(IndexField.STOP_GAP))) { %>
					<span><openwis:i18n key="HomePage.Search.Result.Draft"/></span>
					<% } %>
					<openwis:i18n key="HomePage.Search.Result.Untitled"/>
				</div>
				<% } %>
				
				<!-- Attribution logo -->
				<div class="attributionlogo">
					<% if (doc.getField(IndexField.LOGO_URL)!=null) { %>
					<img src="<%= doc.getField(IndexField.LOGO_URL) %>" width="40"/>
					<% } else {
					    String logoPath = "images/logos/" + source + ".gif";
					    File logoFile = new File(context.getAppPath(), logoPath);
					    if (!logoFile.exists()) {
					       logoPath = "images/logos/dummy.gif";
					    } %>
				    <img src="<%= context.getBaseUrl() + "/" + logoPath %>" width="40"/> 
					<% } %>
				</div>
				
				
				<!-- Action buttons -->
				<div class="action_buttons">
				<%
					String reqUrl = (String) doc.getField(IndexField.REQUEST_URL);
					String subUrl = (String) doc.getField(IndexField.SUBSCRIBE_URL);
					String localDataSource = (String) doc.getField(IndexField.LOCAL_DATA_SOURCE);
					List<String> otherActions = doc.getFieldAsListOfString(IndexField.OTHER_ACTIONS_URL);
					String linkOpenwisRequestUrl = "";
					if (reqUrl != null) {
					   try {
					      linkOpenwisRequestUrl = java.net.URLDecoder.decode(reqUrl, "UTF-8");
					   } catch (Exception e) {
					      linkOpenwisRequestUrl = reqUrl;
					   }
					}
					String linkOpenwisSubscribeUrl = "";
					if (subUrl != null) {
	                  try {
						   linkOpenwisSubscribeUrl = java.net.URLDecoder.decode(subUrl, "UTF-8");
	                  } catch (Exception e) {
	                     linkOpenwisSubscribeUrl = subUrl;
	                  }
					}
					pageContext.setAttribute("localDataSource", localDataSource);
					pageContext.setAttribute("linkOpenwisRequestUrl", linkOpenwisRequestUrl);
					pageContext.setAttribute("linkOpenwisSubscribeUrl", linkOpenwisSubscribeUrl);
				%>
				<%  if((isCacheEnable && isGlobal) || (isCacheEnable && !isGlobal && !linkOpenwisRequestUrl.equals("") && !linkOpenwisSubscribeUrl.equals("")) 
				      || (localDataSource != null && !localDataSource.equals(""))) { %>
					<div style="cursor:pointer;" class="btnXXLFixedSize btnSearchResultsActions"
						onClick='<openwis:requestAction doc="${doc}" ops="${ops}" localDataSource="${localDataSource}" url="${linkOpenwisRequestUrl}"/>'>
						<a style="padding-right:10px; padding-left:10px;">
							<openwis:i18n key="HomePage.Search.Result.Btn.Request"/>
						</a>
					</div>
					<div style="cursor:pointer;" class="btnXXLFixedSize btnSearchResultsActions"
						onClick='<openwis:subscribeAction doc="${doc}" ops="${ops}" localDataSource="${localDataSource}" url="${linkOpenwisSubscribeUrl}"/>'>
						<a style="padding-right:10px; padding-left:10px;">
							<openwis:i18n key="HomePage.Search.Result.Btn.Subscribe"/>
						</a>
					</div>
				<%
					}
					
					if ((isCacheEnable && isGlobal && !linkOpenwisRequestUrl.equals("") && !linkOpenwisSubscribeUrl.equals("")) || (otherActions != null && !otherActions.isEmpty())) { %>
					<div id="oAc<%=id%>" name="oAc<%=id%>" class="btnXXLFixedSize btnSearchResultsActions" onclick="oActions('oAc',<%=id%>);" style="cursor:pointer;">
						<img id="oAcImg<%=id%>" name="oAcImg<%=id%>" src="<%= context.getBaseUrl() %>/images/plus.gif" style="padding-right:3px;"/>
						<a style="padding-right:10px; padding-left:5px;"><openwis:i18n key="HomePage.Search.Result.Btn.OtherAction"/></a>
					</div>
					
					<div id="oAcEle<%=id%>" class="oAcEle" style="display: none;" onClick="oActions('oAc',<%=id%>);">
						<% if (isCacheEnable && isGlobal && !linkOpenwisRequestUrl.equals("") && !linkOpenwisSubscribeUrl.equals("")) { %>
						<div style="cursor:pointer;" class="otherActionTarget"
							onClick='<openwis:requestAction doc="${doc}" ops="${ops}" localDataSource="${localDataSource}" url="${linkOpenwisRequestUrl}" forceUrl="true"/>'>
							<a style="font-size: 10px;">
								<openwis:i18n key="HomePage.Search.Result.Btn.RequestProducer"/>
							</a>
						</div>
						<div style="cursor:pointer;" class="otherActionTarget"
							onClick='<openwis:subscribeAction doc="${doc}" ops="${ops}" localDataSource="${localDataSource}" url="${linkOpenwisSubscribeUrl}" forceUrl="true"/>'>
							<a style="font-size: 10px;">
								<openwis:i18n key="HomePage.Search.Result.Btn.SubscribeProducer"/>
							</a>
						</div>
						<% } 
						
							if(otherActions != null && !otherActions.isEmpty()) {
								for(String otherAction : otherActions) {
								   //0: Name of the button, 1: URN, 2: tooltip
									String[] otherActionInfo = otherAction.split("@@@");
								    if (otherActionInfo.length >= 2) {
										String otherActionName;
										try {
										   otherActionName = java.net.URLDecoder.decode(otherActionInfo[0], "UTF-8");
										} catch (Exception e) {
										   otherActionName = otherActionInfo[0];
										}
										String otherActionTooltip = "";
										
										if(otherActionName.length() > 50) {
										   otherActionTooltip = otherActionName + " ";
										   otherActionName = otherActionName.substring(0,47) + "...";
										}
										String otherActionUrl;
										try {
										   otherActionUrl = java.net.URLDecoder.decode(otherActionInfo[1], "UTF-8");
										} catch (Exception e) {
										   otherActionUrl = otherActionInfo[1];
										}
										otherActionTooltip += otherActionUrl;
										if(otherActionInfo.length > 2) {
										   otherActionTooltip += " " + otherActionInfo[2];
										}
										
										String otherActionNameForHtml = StringEscapeUtils.escapeHtml(otherActionName);
										String otherActionTooltipForHtml = StringEscapeUtils.escapeHtml(otherActionTooltip);
										String otherActionUrlForHtml = StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJavaScript(otherActionUrl));
								   %>
								 		<div style="cursor:pointer;" class="otherActionTarget" onClick='window.open("<%=otherActionUrlForHtml%>")'>
											<a style="font-size: 10px;" title="<%= otherActionTooltipForHtml %>">
												<%= otherActionNameForHtml %>
											</a>
										</div>
							   
						              <%		
						              }
								    }
							} %>
						
						
					</div>
					<% } %> 
					
					<!-- Buttons for related services -->
					<% if (relatedMds != null && relatedMds.size() > 0) { %>
					<div id="services<%=id%>" name="services" class="btnXXLFixedSize btnSearchResultsActions" onclick="doShowRelatedServices('<%=id%>', '<%= uuid %>');" style="cursor:pointer;">
						<img src="<%= context.getBaseUrl() %>/images/plus.gif" style="padding-right:3px;"/>
						<a style="padding-right:10px; padding-left:5px;">Services</a>
					</div>
					<div id="serviceElt<%=id%>" class="oAcEle" style="display: none;" onClick="">
					
					<% 
						for (RelatedMetadataDTO md : relatedMds) {
						   String relatedMdTitle = (md.getTitle().length()) > 50 ? md.getTitle().substring(0, 50) : md.getTitle();
						   String relatedMdTitleForJS = StringEscapeUtils.escapeJavaScript(md.getTitle());
					%>
					
						<div class="otherActionTarget" onclick="doShowMetadataById(<%=md.getId()%>, '<%=relatedMdTitleForJS%>', <%= editable %>);">
							<a href="#"><%= relatedMdTitle %></a>
						</div>
					
					<% } %>
					
					</div>
					
					<% } %>
				</div> <!-- End of Action buttons -->
			</div>

			<!-- abstract -->
			<% if (abst != null) { %>
			<div class="hittext_middle">
				<div class="caption">
					<img src="<%= context.getBaseUrl() %>/images/openwis/puce.gif" />
					<openwis:i18n key="HomePage.Search.Result.Abstract" />
				</div>

				<div class="abstract1">
					<%=StringEscapeUtils.escapeHtml(StringUtils.abbreviate(abst,200)) %>
				</div>
			</div>
			<% } %>

				<!-- keywords -->
			<% if (keywords != null && ! keywords.isEmpty()) { %>
			<div class="hittext_keywords">
				<div class="caption">
					<img src="<%= context.getBaseUrl() %>/images/openwis/puce.gif" />
					<openwis:i18n key="HomePage.Search.Result.Keywords" />
				</div>
				<div class="keywords">
					<% for (String kw : keywords) { %>
					<%=StringEscapeUtils.escapeHtml(kw)%>
					<% } %>
				</div>
			</div>
			<% } %>
		</div>
		<div class="ownership">
			<% if (doc.getField(IndexField.OWNER)!=null ) { %>
			<span class="owner"><%=doc.getFieldAsString(IndexField.OWNER) %></span>
			<% } else { %>
			<span class="owner" style="font-style: italic;"><openwis:i18n key="HomePage.Search.Result.NoOwner"/></span>
			<% } %>
			&nbsp;
			<% if (username!=null && username.equals(doc.getField(IndexField.OWNER))) { %>
			<img src="<%= context.getBaseUrl() %>/images/owner.png"
				title="<openwis:i18n key="HomePage.Search.Result.OwnerRights"/>" />
			<% } else { %>
			<img src="<%= context.getBaseUrl() %>/images/notowner.png"
				title="<openwis:i18n key="HomePage.Search.Result.NotOwnerRights"/>" />
			<% } %>
		</div>
	</div>
	<% } %>

	<%--Page List --%>
	<% if (maxPage > 1) { %>
	<div class="pageList" id="pagination-flickr">
		
		<% if (currentPage > 1) { %>
		<span class="previous">
			<a id="searchPage_Previous" href="javascript:gn_present(<%=(currentPage-2)*hits%>, <%=hits*(currentPage-1) - 1%>);"><openwis:i18n key="HomePage.Search.Result.Btn.Previous" /></a>
		</span>
		<% } else { %>
		<span class="previous-off"><openwis:i18n key="HomePage.Search.Result.Btn.Previous" /></span>
		<% } %>

		<% if (currentPage>pageRoll) { %>
		<span>...</span>
		<% } %>
		<% for(int i= Math.max(0,currentPage-pageRoll); i< Math.min(maxPage,currentPage+pageRoll); i++) { %>
			<% if (currentPage == (i+1) ) { %>
		<span class="active"><%=i+1%></span>
			<% } else { %>
		<span><a id="searchPage_<%=i+1%>" href="javascript:gn_present(<%=i*hits%>, <%=Math.min(count-1, hits*(i+1) - 1)%>);"><%=i+1%></a></span>
			<% } %>
		<% } %>
		<% if (maxPage >currentPage+pageRoll) { %>
		<span>...</span>
		<% } %>

		<% if (currentPage < maxPage) { %>
		<span class="next">
			<a id="searchPage_Next" href="javascript:gn_present(<%=(currentPage)*hits%>, <%=hits*(currentPage+1) - 1%>);"><openwis:i18n key="HomePage.Search.Result.Btn.Next" /></a>
		</span>
		<% } else { %>
		<span class="next-off"><openwis:i18n key="HomePage.Search.Result.Btn.Next" /></span>
		<% } %>
	</div>
	<% }
	} %>
</div>