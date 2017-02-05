//=============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.oaipmh.requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jeeves.utils.Log;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.Validate;
import org.fao.oaipmh.OaiPmh;
import org.fao.oaipmh.util.Xml;
import org.jdom.Element;
import org.jdom.JDOMException;

//=============================================================================

public class Transport
{
	public enum Method { GET, POST }
	public enum Scheme { HTTP, HTTPS }

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public Transport() { this(null); }

	//---------------------------------------------------------------------------

	public Transport(String host) { this(host, 80); }

	//---------------------------------------------------------------------------

	public Transport(String host, int port)
	{
	   this.scheme = Scheme.HTTP;
		this.host = host;
		this.port = port;

		setMethod(Method.POST);
		state.addCookie(cookie);
		client.setState(state);
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		client.setHostConfiguration(config);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public Scheme getScheme()       { return scheme;       }
	public String getHost()         { return host;         }
	public int    getPort()         { return port;         }
	public String getAddress()      { return address;      }
	public Method getMethod()       { return method;       }
	public String getSentData()     { return sentData;     }
	public String getReceivedData() { return receivedData; }

	//---------------------------------------------------------------------------

	public void setHost(String host)
	{
		this.host = host;
	}

	//---------------------------------------------------------------------------

	public void setPort(int port)
	{
		this.port = port;
	}

	//---------------------------------------------------------------------------

	public void setAddress(String address)
	{
		this.address = address;
	}

	//---------------------------------------------------------------------------

	public void setMethod(Method m)
	{
		method = m;
	}
	
	//---------------------------------------------------------------------------
	
	public void setScheme(Scheme scheme)
	{
	   Validate.notNull(scheme, "Scheme cannot be set to null");
	   this.scheme = scheme;
	}

	//---------------------------------------------------------------------------

	public void setUrl(URL url) throws MalformedURLException
	{
		host    = url.getHost();
		port    = url.getPort();
		address = url.getPath();
		
		try {
		   scheme  = Scheme.valueOf(url.getProtocol().toUpperCase());
		} catch (IllegalArgumentException e) {
		   throw new MalformedURLException("Unsupported protocol scheme: " + url.getProtocol());
		}

		if (port == -1)
			port = url.getDefaultPort();
	}

	//---------------------------------------------------------------------------

	public void setUseProxy(boolean yesno)
	{
		useProxy = yesno;
	}

	//---------------------------------------------------------------------------

	public void setProxyHost(String host)
	{
		proxyHost = host;
	}

	//---------------------------------------------------------------------------

	public void setProxyPort(int port)
	{
		proxyPort = port;
	}

	//---------------------------------------------------------------------------

	public void setProxyCredentials(String username, String password)
	{
		if (username == null || username.trim().length() == 0)
			return;

		this.proxyAuthent = true;

		Credentials cred = new UsernamePasswordCredentials(username, password);
		AuthScope   scope= new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);

		client.getState().setProxyCredentials(scope, cred);
	}

	//---------------------------------------------------------------------------

	public void setCredentials(String username, String password)
	{
		this.serverAuthent = (username != null);

		if (username != null)
		{
			Credentials cred = new UsernamePasswordCredentials(username, password);
			AuthScope   scope= new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);

			client.getState().setCredentials(scope, cred);
		}
	}

	//---------------------------------------------------------------------------

	public Element execute() throws IOException, JDOMException
	{
		HttpMethodBase httpMethod = setupHttpMethod();

		return doExecute(httpMethod);
	}

	//---------------------------------------------------------------------------

	/* package */ void clearParameters()
	{
		alParams.clear();
	}

	//---------------------------------------------------------------------------

	/* package */ void addParameter(String name, String value)
	{
		alParams.add(new NameValuePair(name, value));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private HttpMethodBase setupHttpMethod()
	{
		HttpMethodBase httpMethod;

		if (method == Method.GET)
		{
			httpMethod = new GetMethod();
			httpMethod.setQueryString(alParams.toArray(new NameValuePair[1]));
		}
		else
		{
			PostMethod pm = new PostMethod();
			pm.setRequestBody(alParams.toArray(new NameValuePair[1]));

			httpMethod = pm;
		}

		httpMethod.setPath(address);
		httpMethod.setDoAuthentication(useAuthent());

		return httpMethod;
	}

	//---------------------------------------------------------------------------

	private Element doExecute(HttpMethodBase httpMethod) throws IOException, JDOMException
	{
		config.setHost(host, port, scheme.toString().toLowerCase());

		if (useProxy)
			config.setProxy(proxyHost, proxyPort);

		byte[] data = null;

		try
		{
         String reqMessage = "Sending OAIPMH request to " + host + ":" + port + address
               + "; params=" + alParams;
		   Log.info(OaiPmh.OAI_HARVESTER_LOGGER, reqMessage);
		   
			client.executeMethod(httpMethod);
			data = httpMethod.getResponseBody();
			
			if (Log.isDebug(OaiPmh.OAI_HARVESTER_LOGGER)) {
			   Log.debug(OaiPmh.OAI_HARVESTER_LOGGER, "OAIPMH response:\n" + new String(data, "UTF-8"));
			}

			Element response = Xml.loadStream(new ByteArrayInputStream(data));

			setupSentData(httpMethod);
			setupReceivedData(httpMethod, data);

			return response;
		}
		finally
		{
			httpMethod.releaseConnection();
		}
	}

	//---------------------------------------------------------------------------

	private void setupSentData(HttpMethodBase httpMethod)
	{
		sentData = httpMethod.getName() +" "+ httpMethod.getPath();

		if (httpMethod.getQueryString() != null)
			sentData += "?"+ httpMethod.getQueryString();

		sentData += "\r\n";

		for (Header h : httpMethod.getRequestHeaders())
			sentData += h;

		sentData += "\r\n";
	}

	//---------------------------------------------------------------------------

	private void setupReceivedData(HttpMethodBase httpMethod, byte[] response)
	{
		receivedData = httpMethod.getStatusText() +"\r\r";

		for (Header h : httpMethod.getResponseHeaders())
			receivedData += h;

		receivedData += "\r\n";

		try
		{
			if (response != null)
				receivedData += new String(response, "UTF8");
		}
		catch (UnsupportedEncodingException e) {}
	}
	
	//---------------------------------------------------------------------------
	
	private boolean useAuthent() {
		return proxyAuthent||serverAuthent;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private Scheme  scheme;
	private String  host;
	private int     port;
	private String  address;
	private Method  method;
	private boolean serverAuthent;
	private boolean useProxy;
	private String  proxyHost;
	private int     proxyPort;
	private boolean proxyAuthent;

	private HttpClient client = new HttpClient();
	private HttpState  state  = new HttpState();
	private Cookie     cookie = new Cookie();

	private HostConfiguration config = new HostConfiguration();

	private ArrayList<NameValuePair> alParams = new ArrayList<NameValuePair>();

	//--- transient vars

	private String sentData;
	private String receivedData;
}

//=============================================================================

