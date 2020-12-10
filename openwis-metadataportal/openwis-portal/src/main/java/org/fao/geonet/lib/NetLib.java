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

package org.fao.geonet.lib;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.XmlRequest;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.oaipmh.requests.Transport;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//=============================================================================

public class NetLib
{
	public static final String ENABLED  = "system/proxy/use";
	public static final String HOST     = "system/proxy/host";
	public static final String PORT     = "system/proxy/port";
	public static final String USERNAME = "system/proxy/username";
	public static final String PASSWORD = "system/proxy/password";

	/**
	 * Initialisation of proxy config to be used in non-jeeves servlet
	 */
	private ProxyConfig proxyConfig = null;
	private class ProxyConfig  {
		private boolean enabled ;
		private String  host;
		private String  port;
		private String  username;
		private String  password;
		public ProxyConfig(SettingManager sm) {
			enabled = sm.getValueAsBool(ENABLED, false);
			host    = sm.getValue(HOST);
			port    = sm.getValue(PORT);
			username= sm.getValue(USERNAME);
			password= sm.getValue(PASSWORD);
		}

		@Override
		public String toString() {
			return username + "@" + proxyConfig.host + ":" + proxyConfig.port;
		}
	}

	/**
	 * Initialisation of proxy config to be used in non-jeeves servlet
	 * @return true if proxy is enabled
	 */
	public boolean initProxyConfig(SettingManager sm) {
		proxyConfig = new ProxyConfig(sm);
		if (proxyConfig.enabled) {
			Log.info(Geonet.GEONETWORK,"  - Proxy Enabled : " + proxyConfig);
		} else {
			Log.info(Geonet.GEONETWORK,"  - Proxy Disabled");
		}
		return proxyConfig.enabled;
	}

	/**
	 * Setup proxy for http client to be used in non-jeeves servlet
	 */
	public void setupProxy(HttpClient client)
	{
		if (proxyConfig != null) {
			new NetLib().setupProxy(proxyConfig, client);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setupProxy(ServiceContext context, XmlRequest req)
	{
		GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
		SettingManager sm = gc.getSettingManager();

		setupProxy(sm, req);
	}

	//---------------------------------------------------------------------------
	/** Setup proxy for XmlRequest
	  */

	public void setupProxy(SettingManager sm, XmlRequest req)
	{
		boolean enabled = sm.getValueAsBool(ENABLED, false);
		String  host    = sm.getValue(HOST);
		String  port    = sm.getValue(PORT);
		String  username= sm.getValue(USERNAME);
		String  password= sm.getValue(PASSWORD);

		if (!enabled) {
			req.setUseProxy(false);
		} else {
			if (!Lib.type.isInteger(port))
				Log.error(Geonet.GEONETWORK, "Proxy port is not an integer : "+ port);
			else
			{
				req.setUseProxy(true);
				req.setProxyHost(host);
				req.setProxyPort(Integer.parseInt(port));
				if (username.trim().length()!=0) {
					req.setProxyCredentials(username, password);
				} 
			}
		}
	}

	//---------------------------------------------------------------------------

	public void setupProxy(ServiceContext context, HttpClient client)
	{
		GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
		SettingManager sm = gc.getSettingManager();

		setupProxy(sm, client);
	}

	//---------------------------------------------------------------------------

	/** Setup proxy for http client
	  */
	public void setupProxy(SettingManager sm, HttpClient client)
	{
		setupProxy(new ProxyConfig(sm), client);
	}

	private void setupProxy(ProxyConfig proxyConfig, HttpClient client)
	{
		if (proxyConfig.enabled) {
			if (!Lib.type.isInteger(proxyConfig.port)) {
				Log.error(Geonet.GEONETWORK, "Proxy port is not an integer : "+ proxyConfig.port);
			} else {
				HostConfiguration config = client.getHostConfiguration();
				if (config == null) config = new HostConfiguration();
				config.setProxy(proxyConfig.host,Integer.parseInt(proxyConfig.port));
				client.setHostConfiguration(config);

				if (proxyConfig.username.trim().length()!=0) {
					Credentials cred = new UsernamePasswordCredentials(proxyConfig.username, proxyConfig.password);
					AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);

					client.getState().setProxyCredentials(scope, cred);
				}
				List authPrefs = new ArrayList(2);
				authPrefs.add(AuthPolicy.DIGEST);
				authPrefs.add(AuthPolicy.BASIC);
				// This will exclude the NTLM authentication scheme
				client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
			}
		}
	}


	//---------------------------------------------------------------------------

	public void setupProxy(ServiceContext context, Transport t)
	{
		GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
		SettingManager sm = gc.getSettingManager();

		setupProxy(sm, t);
	}

	//---------------------------------------------------------------------------

	private void setupProxy(SettingManager sm, Transport t) {
		boolean enabled = sm.getValueAsBool(ENABLED, false);
		String  host    = sm.getValue(HOST);
		String  port    = sm.getValue(PORT);
		String  username= sm.getValue(USERNAME);
		String  password= sm.getValue(PASSWORD);
		if (enabled) {
			if (!Lib.type.isInteger(port)) {
				Log.error(Geonet.GEONETWORK, "Proxy port is not an integer : "+ port);
			} else {
				t.setUseProxy(enabled);
				t.setProxyHost(host);
				t.setProxyPort(Integer.parseInt(port));
				if (username.trim().length() != 0) {
					t.setProxyCredentials(username, password);	
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	public void setupProxy(ServiceContext context)
	{
		GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
		SettingManager sm = gc.getSettingManager();

		setupProxy(sm);
	}

	//---------------------------------------------------------------------------

	/** Setup proxy for http client
	  */
	public void setupProxy(SettingManager sm)
	{
		boolean enabled = sm.getValueAsBool(ENABLED, false);
		String  host    = sm.getValue(HOST);
		String  port    = sm.getValue(PORT);
		String  username= sm.getValue(USERNAME);
		String  password= sm.getValue(PASSWORD);

		Properties props = System.getProperties();
		props.put("http.proxyHost", host);
		props.put("http.proxyPort", port);
		if (username.trim().length() > 0) {
			Log.error(Geonet.GEONETWORK, "Proxy credentials cannot be used");
		}

	}

	//---------------------------------------------------------------------------

	public boolean isUrlValid(String url)
	{
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}

//=============================================================================

