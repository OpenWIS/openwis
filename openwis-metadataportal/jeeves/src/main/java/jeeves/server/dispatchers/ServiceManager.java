//=============================================================================
//===	Copyright (C) 2001-2005 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This library is free software; you can redistribute it and/or
//===	modify it under the terms of the GNU Lesser General Public
//===	License as published by the Free Software Foundation; either
//===	version 2.1 of the License, or (at your option) any later version.
//===
//===	This library is distributed in the hope that it will be useful,
//===	but WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//===	Lesser General Public License for more details.
//===
//===	You should have received a copy of the GNU Lesser General Public
//===	License along with this library; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: GeoNetwork@fao.org
//==============================================================================

package jeeves.server.dispatchers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jeeves.constants.ConfigFile;
import jeeves.constants.Jeeves;
import jeeves.exceptions.JeevesException;
import jeeves.exceptions.ServiceNotAllowedEx;
import jeeves.exceptions.ServiceNotFoundEx;
import jeeves.exceptions.ServiceNotMatchedEx;
import jeeves.interfaces.Service;
import jeeves.server.ProfileManager;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.server.dispatchers.guiservices.Call;
import jeeves.server.dispatchers.guiservices.GuiService;
import jeeves.server.dispatchers.guiservices.XmlFile;
import jeeves.server.resources.ProviderManager;
import jeeves.server.sources.ServiceRequest;
import jeeves.server.sources.ServiceRequest.InputMethod;
import jeeves.server.sources.ServiceRequest.OutputMethod;
import jeeves.server.sources.http.HttpServiceRequest;
import jeeves.server.sources.http.JeevesServlet;
import jeeves.utils.BLOB;
import jeeves.utils.BinaryFile;
import jeeves.utils.Log;
import jeeves.utils.SOAPUtil;
import jeeves.utils.SerialFactory;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.jdom.Element;

//=============================================================================

public class ServiceManager
{
	private final Hashtable<String, ArrayList<ServiceInfo>> htServices =
		new Hashtable<String, ArrayList<ServiceInfo>>(100);
	private final Hashtable<String, Object> htContexts =
		new Hashtable<String, Object>();
	private final Vector<ErrorPage> vErrorPipe = new Vector<ErrorPage>();
	private final Vector<GuiService> vDefaultGui = new Vector<GuiService>();

	private ProviderManager providMan;
	private ProfileManager  profilMan;
	private SerialFactory   serialFact;

	private String  appPath;
	private String  baseUrl;
	private String  uploadDir;
    private int     maxUploadSize;
	private String  defaultLang;
	private String  defaultContType;
	private boolean defaultLocal;
	private JeevesServlet servlet;
   private String portalType;

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setAppPath        (String  path)  { appPath        = path;  }
	public void setDefaultLang    (String  lang)  { defaultLang    = lang;  }
	public void setDefaultContType(String  type)  { defaultContType= type;  }
	public void setUploadDir      (String  dir)   { uploadDir      = dir;   }
    public void setMaxUploadSize  (int  size)     { maxUploadSize  = size;  }
	public void setDefaultLocal   (boolean yesno) { defaultLocal   = yesno; }

	public void setProviderMan  (ProviderManager p) { providMan  = p; }
	public void setSerialFactory(SerialFactory   s) { serialFact = s; }
	public void setServlet(JeevesServlet serv) { servlet = serv; }
   public void setPortalType(String p) { portalType = p;}

	//---------------------------------------------------------------------------

	public void setBaseUrl(String name)
	{
		baseUrl = name;

		if (!baseUrl.startsWith("/") && baseUrl.length() != 0)
			baseUrl = "/"+ baseUrl;
	}

	//---------------------------------------------------------------------------

	public void loadProfiles(String file) throws Exception
	{
		profilMan = new ProfileManager(appPath + Jeeves.Path.WEBINF + file);
	}

	//---------------------------------------------------------------------------

	public void registerContext(String name, Object context)
	{
		htContexts.put(name, context);
	}
	//---------------------------------------------------------------------------

	public void addDefaultGui(Element gui) throws Exception
	{
		vDefaultGui.add(getGuiService("", gui));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Registering methods (service)
	//---
	//---------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public void addService(String pack, Element srv) throws Exception
	{
		String name  = srv.getAttributeValue(ConfigFile.Service.Attr.NAME);
		String match = srv.getAttributeValue(ConfigFile.Service.Attr.MATCH);
		String sheet = srv.getAttributeValue(ConfigFile.Service.Attr.SHEET);
		String cache = srv.getAttributeValue(ConfigFile.Service.Attr.CACHE);

		ServiceInfo si = new ServiceInfo(appPath);

		si.setMatch(match);
		si.setSheet(sheet);
		si.setCache(cache);

		ArrayList<ServiceInfo> al = htServices.get(name);

		if (al == null) {
			al = new ArrayList<ServiceInfo>();
			htServices.put(name, al);
		}

		al.add(si);

		//--- parse classes elements

		List<Element> classes = srv.getChildren(ConfigFile.Service.Child.CLASS);

		for (Element classe : classes) {
			si.addService(buildService(pack, classe));
		}

		//--- parse output pages

		List<Element> outputs = srv.getChildren(ConfigFile.Service.Child.OUTPUT);

		for(Element output : outputs) {
			si.addOutputPage(buildOutputPage(pack, output));
		}

		//--- parse error pages

		List<Element> errors = srv.getChildren(ConfigFile.Service.Child.ERROR);

		for(Element error : errors) {
			si.addErrorPage(buildErrorPage(error));
		}
	}

	//---------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
   private Service buildService(String pack, Element clas) throws Exception
	{
		//--- get class name

		String name = clas.getAttributeValue(ConfigFile.Class.Attr.NAME);

		if (name == null)
			throw new IllegalArgumentException("Missing 'name' attrib in 'class' element");

		if (name.startsWith("."))
			name = pack + name;

		//--- create instance

		Service service = (Service) Class.forName(name).newInstance();

		service.init(appPath, new ServiceConfig(clas.getChildren(ConfigFile.Class.Child.PARAM)));

		return service;
	}

	//---------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private OutputPage buildOutputPage(String pack, Element output) throws Exception
	{
		OutputPage outPage = new OutputPage();

		outPage.setStyleSheet   (output.getAttributeValue(ConfigFile.Output.Attr.SHEET));
		outPage.setForward      (output.getAttributeValue(ConfigFile.Output.Attr.FORWARD));
		outPage.setRedirectLocation(output.getAttributeValue(ConfigFile.Output.Attr.REDIRECT));
		outPage.setTestCondition(output.getAttributeValue(ConfigFile.Output.Attr.TEST));
		outPage.setFile         (output.getAttributeValue(ConfigFile.Output.Attr.FILE) != null);
		outPage.setBLOB         (output.getAttributeValue(ConfigFile.Output.Attr.BLOB) != null);

		//--- set content type

		String contType = output.getAttributeValue(ConfigFile.Output.Attr.CONTENT_TYPE);

		if (contType == null)
			contType = defaultContType;

		outPage.setContentType(contType);

		//--- handle children

		List<Element> guiList = output.getChildren();

		for(Element gui : guiList) {
			outPage.addGuiService(getGuiService(pack, gui));
		}

		return outPage;
	}

	//---------------------------------------------------------------------------

	private GuiService getGuiService(String pack, Element elem) throws Exception
	{
		if (ConfigFile.Output.Child.XML.equals(elem.getName()))
			return new XmlFile(elem, defaultLang, defaultLocal);

		if (ConfigFile.Output.Child.CALL.equals(elem.getName()))
			return new Call(elem, pack, appPath);

		throw new IllegalArgumentException("Unknown GUI element : "+ Xml.getString(elem));
	}

	//---------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private ErrorPage buildErrorPage(Element err) throws Exception
	{
		ErrorPage errPage = new ErrorPage();

		errPage.setStyleSheet   (err.getAttributeValue(ConfigFile.Error.Attr.SHEET));
		errPage.setTestCondition(err.getAttributeValue(ConfigFile.Error.Attr.ID));

		//--- set content type

		String contType = err.getAttributeValue(ConfigFile.Error.Attr.CONTENT_TYPE);

		if (contType == null)
			contType = defaultContType;

		errPage.setContentType(contType);

		// -- set status code
		int statusCode;
		String strStatusCode = err.getAttributeValue(ConfigFile.Error.Attr.STATUS_CODE);

		try {
			statusCode = Integer.parseInt(strStatusCode);
		} catch (Exception e) {
			// Default value for an error page where status code is not defined.
			statusCode = 500;
		}

		errPage.setStatusCode(statusCode);

		//--- handle children

		List<Element> guiList = err.getChildren();

		for(Element gui : guiList) {
			errPage.addGuiService(getGuiService("?", gui));
		}

		return errPage;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Registering methods (error)
	//---
	//---------------------------------------------------------------------------

	public void addErrorPage(Element err) throws Exception
	{
		vErrorPipe.add(buildErrorPage(err));
	}

	//---------------------------------------------------------------------------

	public ServiceContext createServiceContext(String name)
	{
		ServiceContext context = new ServiceContext(name, providMan, serialFact, profilMan, htContexts);

		context.setBaseUrl(baseUrl);
		context.setLanguage("?");
		context.setUserSession(null);
		context.setIpAddress("?");
		context.setAppPath(appPath);
		context.setUploadDir(uploadDir);
        context.setMaxUploadSize(maxUploadSize);
		context.setServlet(servlet);

      context.setPortalType(portalType);

		return context;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Dispatching methods
	//---
	//---------------------------------------------------------------------------

	public void dispatch(ServiceRequest req, UserSession session)
	{
		//--- create the corresponding service request
      long startDispatch = System.currentTimeMillis();

		ServiceContext context = new ServiceContext(req.getService(), providMan, serialFact, profilMan, htContexts);

		context.setBaseUrl(baseUrl);
		context.setLanguage(req.getLanguage());
		context.setUserSession(session);
		context.setIpAddress(req.getAddress());
		context.setAppPath(appPath);
		context.setUploadDir(uploadDir);
      context.setMaxUploadSize(maxUploadSize);
		context.setInputMethod(req.getInputMethod());
		context.setOutputMethod(req.getOutputMethod());
		context.setHeaders(req.getHeaders());
		context.setServlet(servlet);
      context.setPortalType(portalType);
      context.setDebug(req.hasDebug());

      long contextCreated = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime(req.getService(), "ServiceManager.dispatch", "Context creation", contextCreated - startDispatch);
      }

		//--- invoke service and build result

		Element     response = null;
		ServiceInfo srvInfo  = null;

		try
		{
			while(true)
			{
				String srvName = req.getService();
				if (Log.isInfo(Log.SERVICE)) {
				   Log.info   (Log.SERVICE,"Dispatching : " +srvName);
				}
				logParameters(req.getParams());

				ArrayList<ServiceInfo> al = htServices.get(srvName);

				if (al == null)
				{
					error("Service not found : " +srvName);
					throw new ServiceNotFoundEx(srvName);
				}

				for (ServiceInfo si : al) {
					if (si.matches(req.getParams())) {
						srvInfo = si;
						break;
					}
				}

				if (srvInfo == null)
				{
					error("Service not matched in list : " +srvName);
					throw new ServiceNotMatchedEx(srvName);
				}

				//---------------------------------------------------------------------
				//--- check access

				String profile = ProfileManager.GUEST;

				if (session.isAuthenticated())
					profile = session.getProfile();

				if (!profilMan.hasAccessTo(profile, srvName))
				{
					error("Service not allowed : "+ srvName);
					throw new ServiceNotAllowedEx(srvName);
				}

		      long accessChecked = System.currentTimeMillis();
		      if (Log.isStatEnabled()) {
		         Log.statTime(req.getService(), "ServiceManager.dispatch", "Access check", accessChecked - contextCreated);
		      }


				response = srvInfo.execServices(req.getParams(), context);

				long serviceExecuted = System.currentTimeMillis();
            if (Log.isStatEnabled()) {
               Log.statTime(req.getService(), "ServiceManager.dispatch", "Exec service", serviceExecuted - accessChecked);
            }

				//---------------------------------------------------------------------
				//--- handle forward

				OutputPage outPage = srvInfo.findOutputPage(response);
				String     forward = dispatchOutput(req, context, response, outPage, srvInfo.isCacheSet());

				long outputDispatched = System.currentTimeMillis();
            if (Log.isStatEnabled()) {
               Log.statTime(req.getService(), "ServiceManager.dispatch", "Dispath output", outputDispatched - serviceExecuted);
            }

				if (forward == null)
				{
				   if (Log.isInfo(Log.SERVICE)) {
	               Log.info   (Log.SERVICE," -> dispatch ended for : " +srvName);
				   }
					return;
				}
				else
				{
				   if (Log.isInfo(Log.SERVICE)) {
	               Log.info   (Log.SERVICE," -> forwarding to : " +forward);
				   }

					Element elForward = new Element(Jeeves.Elem.FORWARD);
					elForward.setAttribute(Jeeves.Attr.SERVICE, srvName);

					// --- send response to forwarded service request

					response.setName(Jeeves.Elem.REQUEST);
					response.addContent(elForward);

					context.setService(forward);
					req.setService(forward);
					req.setParams(response);
				}
			}
		}
		catch(Throwable e)
		{
			handleError(req, response, context, srvInfo, e);
		}
	}

	//---------------------------------------------------------------------------
	//--- Handle error
	//---------------------------------------------------------------------------

	private void handleError(ServiceRequest req, Element response, ServiceContext context,
										ServiceInfo srvInfo, Throwable e)
	{
		Element error = getError(req, e, response);
		String  id    = error.getAttributeValue("id");
		int     code  = getErrorCode(e);
		boolean cache = (srvInfo == null) ? false : srvInfo.isCacheSet();

		error("Raised exception while executing service", e);

		try
		{
			InputMethod  input  = req.getInputMethod();
			OutputMethod output = req.getOutputMethod();

			if (input == InputMethod.SOAP || output == OutputMethod.SOAP)
			{
				req.setStatusCode(code);
				req.beginStream("application/soap+xml; charset=UTF-8", cache);

				error.setAttribute("encodingStyle", "http://www.geonetwork.org/encoding/error",
										SOAPUtil.NAMESPACE_ENV);

				boolean sender  = (code < 500);
				String  message = error.getChildText("class") +" : "+ error.getChildText("message");

				req.write(SOAPUtil.embedExc(error, sender, id, message));
			}
         else if ((input == InputMethod.GET && output == OutputMethod.JSON) ||
                  (context.getService().startsWith("xml.")))
         {
            req.setStatusCode(code);
            req.beginStream("application/xml; charset=UTF-8", cache);
            req.write(error);
         }
			else
			{
				//--- try to dispatch to the error output

				ErrorPage errPage = (srvInfo != null) ? srvInfo.findErrorPage(id) : null;

				if (errPage == null)
					errPage = findErrorPage(id);

				try
				{
					dispatchError(req, context, error, errPage, cache);
				}
				catch(Exception ex)
				{
					//--- ok, if we are here there is no error page
					//--- so we display plain xml data

					req.setStatusCode(code);
					req.beginStream("application/xml; charset=UTF-8", cache);
					req.write(error);
				}
			}
		}
		catch(Exception ex)
		{
			error("Raised exception while writing response to exception");
			error("  Exception : " +ex);
			error("  Message   : " +ex.getMessage());
			error("  Stack     : " +Util.getStackTrace(ex));
		}
	}

	//---------------------------------------------------------------------------
	//--- Dispatch output
	//---------------------------------------------------------------------------

	/** Takes a service's response and builds the output
	  */

	private String dispatchOutput(ServiceRequest req, ServiceContext context,
									Element response, OutputPage outPage, boolean cache) throws Exception
	{
	   if (Log.isInfo(Log.SERVICE)) {
         Log.info   (Log.SERVICE,"   -> dispatching to output for : "+ req.getService());
	   }

		//------------------------------------------------------------------------
		//--- check if the output page is a foward

		if (outPage != null)
		{
			String sForward = outPage.getForward();

			if (sForward != null)
				return sForward;
		}

		//------------------------------------------------------------------------
		//--- write result to output page

		if (outPage == null)
		{
			//--- if there is no output page we output the xml result (if any)

			if	(response == null)
				warning("Response is null and there is no output page for : " +req.getService());
			else
			{
			   if (Log.isInfo(Log.SERVICE)) {
               Log.info   (Log.SERVICE,"     -> writing xml for : " +req.getService());
			   }

				//--- this logging is usefull for xml services that are called by javascript code
				if (Log.isDebug(Log.SERVICE)) {
		         Log.debug(Log.SERVICE, "Service xml is :\n"+Xml.getString(response));
				}

				InputMethod  in  = req.getInputMethod();
				OutputMethod out = req.getOutputMethod();

				if (in == InputMethod.SOAP || out == OutputMethod.SOAP)
				{
					req.beginStream("application/soap+xml; charset=UTF-8", cache);

					if (!SOAPUtil.isEnvelope(response))
						response = SOAPUtil.embed(response);
				}
				else
					req.beginStream("text/xml; charset=UTF-8", cache);

				req.write(response);
			}
		}

		//--- FILE output

		else if (outPage.isFile())
		{
			// PDF Output
			if (outPage.getContentType().equals("application/pdf") && !outPage.getStyleSheet().equals("")) {

				//--- build the xml data for the XSL/FO translation
				String styleSheet = outPage.getStyleSheet();
				Element guiElem = outPage.invokeGuiServices(context, response, vDefaultGui);

				addPrefixes(guiElem, context.getLanguage(), req.getService());

				Element rootElem = new Element(Jeeves.Elem.ROOT)
												.addContent(guiElem)
												.addContent(response);

				Element reqElem = (Element) req.getParams().clone();
				reqElem.setName(Jeeves.Elem.REQUEST);

				rootElem.addContent(reqElem);

				//--- do an XSL transformation

				styleSheet = appPath + Jeeves.Path.XSL + styleSheet;

				if (!new File(styleSheet).exists())
					error(" -> stylesheet not found on disk, aborting : " +styleSheet);
				else
				{
				   if (Log.isInfo(Log.SERVICE)) {
	               Log.info   (Log.SERVICE," -> transforming with stylesheet : " +styleSheet);
				   }

					try
					{
						//--- first we do the transformation
						String file = Xml.transformFOP(uploadDir, rootElem, styleSheet);
						response = BinaryFile.encode(200, file, "document.pdf", true);
					}
					catch(Exception e)
					{
						error(" -> exception during XSL/FO transformation for : " +req.getService());
						error(" -> (C) stylesheet : "+ styleSheet);
						error(" -> (C) message : "+ e.getMessage());
						error(" -> (C) exception : "+ e.getClass().getSimpleName());

						throw e;
					}

					if (Log.isInfo(Log.SERVICE)) {
	               Log.info   (Log.SERVICE," -> end transformation for : " +req.getService());
					}
				}


			}
			String contentType = BinaryFile.getContentType(response);

			if (contentType == null)
				contentType = "application/octet-stream";

			String contentDisposition = BinaryFile.getContentDisposition(response);
			String contentLength      = BinaryFile.getContentLength(response);

			int cl = (contentLength == null) ? -1 : Integer.parseInt(contentLength);

			req.beginStream(contentType, cl, contentDisposition, cache);
			BinaryFile.write(response, req.getOutputStream());
			req.endStream();
			BinaryFile.removeIfTheCase(response);
		}

		//--- BLOB output

		else if (outPage.isBLOB())
		{
			String contentType = BLOB.getContentType(response);

			if (contentType == null)
				contentType = "application/octet-stream";

			String contentDisposition = BLOB.getContentDisposition(response);
			String contentLength      = BLOB.getContentLength(response);

			int cl = (contentLength == null) ? -1 : Integer.parseInt(contentLength);

			req.beginStream(contentType, cl, contentDisposition, cache);
			BLOB.write(response, req.getOutputStream());
			req.endStream();
		}

		//--- JSP output

		else if (outPage.isJsp()) {
		   dispatchJsp(req, context, response, outPage);
		}

		//--- Redirect
      else if (outPage.isRedirect()) {
         sendRedirect(req, context, outPage);
      }

		//--- HTML/XML output

		else
		{
			//--- build the xml data for the XSL translation
         long startHtmlOutput = System.currentTimeMillis();

			String  styleSheet = outPage.getStyleSheet();
			Element guiElem    = outPage.invokeGuiServices(context, response, vDefaultGui);

			addPrefixes(guiElem, context.getLanguage(), req.getService());

			Element rootElem = new Element(Jeeves.Elem.ROOT)
											.addContent(guiElem)
											.addContent(response);

			Element reqElem = (Element) req.getParams().clone();
			reqElem.setName(Jeeves.Elem.REQUEST);

			rootElem.addContent(reqElem);

         long domElementCreated = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime(req.getService(), "ServiceManager.dispatchOutput[HTML/XML]", "DOM Element creation", domElementCreated - startHtmlOutput);
            Log.statSize(req.getService(), "ServiceManager.dispatchOutput[HTML/XML]", "DOM Element", Xml.getString(rootElem).getBytes().length);
         }

			//--- do an XSL translation or send xml data to a debug routine

			if (req.hasDebug())
			{
				req.beginStream("application/xml; charset=UTF-8", cache);
				req.write(rootElem);
			}
			else
			{
				//--- do an XSL transformation

				styleSheet = appPath + Jeeves.Path.XSL + styleSheet;

				if (!new File(styleSheet).exists())
					error("     -> stylesheet not found on disk, aborting : " +styleSheet);
				else
				{
				   if (Log.isInfo(Log.SERVICE)) {
	               Log.info   (Log.SERVICE,"     -> transforming with stylesheet : " +styleSheet);
				   }

					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					try
					{
						//--- first we do the transformation

						Xml.transform(rootElem, styleSheet, baos);

						//--- then we set the content-type and output the result

						req.beginStream(outPage.getContentType(), cache);
						req.getOutputStream().write(baos.toByteArray());
						req.endStream();
					}
					catch(Exception e)
					{
						error("   -> exception during transformation for : " +req.getService());
						error("   ->  (C) stylesheet : "+ styleSheet);
						error("   ->  (C) message    : "+ e.getMessage());
						error("   ->  (C) exception  : "+ e.getClass().getSimpleName());

						throw e;
					}

					if (Log.isInfo(Log.SERVICE)) {
	               Log.info   (Log.SERVICE,"     -> end transformation for : " +req.getService());
					}

					long endOfTransform = System.currentTimeMillis();
		         if (Log.isStatEnabled()) {
		            Log.statTime(req.getService(), "ServiceManager.dispatchOutput[HTML/XML]", "XSL transformation", endOfTransform - domElementCreated);
		         }

				}
			}
		}

		//------------------------------------------------------------------------
		//--- end data stream

		if (Log.isInfo(Log.SERVICE)) {
         Log.info   (Log.SERVICE,"   -> output ended for : "+ req.getService());
		}

		return null;
	}

	/**
	 * Dispatch to JSP page. Pass services attribute to the request.
	 */
	private void dispatchJsp(ServiceRequest req, ServiceContext context,
         Element response, AbstractPage outPage) throws Exception {
	   HttpServiceRequest httpReq = (HttpServiceRequest) req;
      httpReq.setAttribute("context", context);
      httpReq.setAttribute("portalType", portalType);
      Map<String, Object> attrMap    = outPage.invokeGuiServicesWithJsp(context, response);
      for (String key : attrMap.keySet()) {
         httpReq.setAttribute(key, attrMap.get(key));
      }
      Object isUPEnabled = attrMap.get("isUserPortalEnabled");
      if ("user".equals(portalType)  && isUPEnabled != null && (((Boolean) isUPEnabled) == false)) {
         httpReq.forward("/jsp/maintenance.jsp");
      } else {
         httpReq.forward(outPage.getStyleSheet());
      }
	}

   /**
    * Redirect to the redirect location.
    */
   private void sendRedirect(ServiceRequest req, ServiceContext context, OutputPage outPage) throws Exception {
      HttpServiceRequest httpReq = (HttpServiceRequest) req;
      String location = baseUrl +"/"+ Jeeves.Prefix.SERVICE +"/"+ context.getLanguage() + "/" + outPage.getRedirectLocation();
      httpReq.sendRedirect(location);
   }

	//---------------------------------------------------------------------------
	//--- Dispatch error
	//---------------------------------------------------------------------------

	/** Takes a service's response and builds the output
	  */

	private void dispatchError(ServiceRequest req, ServiceContext context,
									   Element response, ErrorPage outPage, boolean cache) throws Exception
	{
	   if (Log.isInfo(Log.SERVICE)) {
         Log.info   (Log.SERVICE,"   -> dispatching to error for : "+ req.getService());
	   }

		//--- build the xml data for the XSL translation

		String  styleSheet = outPage.getStyleSheet();
		Element guiElem    = outPage.invokeGuiServices(context, response, vDefaultGui);

		// Dispatch HTTP status code
		req.setStatusCode(outPage.getStatusCode());

		addPrefixes(guiElem, context.getLanguage(), req.getService());

		Element rootElem = new Element(Jeeves.Elem.ROOT)
										.addContent(guiElem)
										.addContent(response);

		if (req.hasDebug())
		{
			req.beginStream("application/xml; charset=UTF-8", cache);
			req.write(rootElem);
		}
		else
		{

		  if (outPage.isJsp()) {
		     dispatchJsp(req, context, response, outPage);
		  } else {
	        //--- do an XSL transformation

	         styleSheet = appPath + Jeeves.Path.XSL + styleSheet;

	         if (Log.isInfo(Log.SERVICE)) {
	            Log.info   (Log.SERVICE,"     -> transforming with stylesheet : " +styleSheet);
	         }

	         try
	         {
	            req.beginStream(outPage.getContentType(), cache);
	            Xml.transform(rootElem, styleSheet, req.getOutputStream());
	            req.endStream();
	         }
	         catch(Exception e)
	         {
	            e.printStackTrace();
	         }
		  }
		}

		if (Log.isInfo(Log.SERVICE)) {
         Log.info(Log.SERVICE,"     -> end error transformation for : " +req.getService());
         Log.info(Log.SERVICE, "   -> error ended for : "+ req.getService());
		}
	}

	//---------------------------------------------------------------------------

	private ErrorPage findErrorPage(String id)
	{
		for(int i=0; i<vErrorPipe.size(); i++)
		{
			ErrorPage p = vErrorPipe.get(i);

			if (p.matches(id))
				return p;
		}

		error("No default error found for id : " +id);
		throw new NullPointerException("No default error found for id : " +id);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private Element getError(ServiceRequest req, Throwable t, Element response)
	{
		Element params = new Element(Jeeves.Elem.REQUEST)
								.addContent(new Element("language").setText(req.getLanguage()))
								.addContent(new Element("service") .setText(req.getService()));

      Element error = JeevesException.toElement(t).addContent(params);
    
   	for (Iterator iterator = req.getParams().getContent().iterator(); iterator.hasNext();) {
           Element elt = (Element) iterator.next();
           if ("needLogout".equals(elt.getName())) {
              error.getContent().add(new Element("needLogout").setText(elt.getValue()));
           } 
      }

		//--- add response (if any)

		if (response != null)
			error.addContent((Element) response.clone());

		return error;
	}

	//---------------------------------------------------------------------------

	private int getErrorCode(Throwable t)
	{
		int code = 500;

		if (t instanceof JeevesException)
			code = ((JeevesException) t).getCode();

		return code;
	}

	//---------------------------------------------------------------------------

	private void addPrefixes(Element root, String lang, String service)
	{
		root.addContent(new Element(Jeeves.Elem.LANGUAGE)    .setText(lang));
		root.addContent(new Element(Jeeves.Elem.REQ_SERVICE) .setText(service));
		root.addContent(new Element(Jeeves.Elem.BASE_URL)    .setText(baseUrl));
		root.addContent(new Element(Jeeves.Elem.LOC_URL)     .setText(baseUrl +"/loc/"+ lang));
		root.addContent(new Element(Jeeves.Elem.BASE_SERVICE).setText(baseUrl +"/"+ Jeeves.Prefix.SERVICE));
		root.addContent(new Element(Jeeves.Elem.LOC_SERVICE) .setText(baseUrl +"/"+ Jeeves.Prefix.SERVICE +"/"+ lang));
      root.addContent(new Element(Jeeves.Elem.PORTAL_TYPE) .setText(portalType));
	}

	//---------------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   private void logParameters(Element params) {
      List<Element> paramsList = params.getChildren();
      if (Log.isDebug(Log.SERVICE)) {
         if (paramsList.isEmpty()) {
            Log.debug(Log.SERVICE, " -> no input parameters");
         } else {
            Log.debug(Log.SERVICE, " -> parameters are : \n" + Xml.getString(params));
         }
      }
   }

	//---------------------------------------------------------------------------

	private void warning(String message) { Log.warning(Log.SERVICE, message); }
	static  void error  (String message) { Log.error  (Log.SERVICE, message); }
   static  void error  (String message, Throwable t) { Log.error  (Log.SERVICE, message, t); }

}

//=============================================================================

