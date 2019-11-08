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

package jeeves.resources.dbms;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jeeves.constants.Jeeves;
import jeeves.server.resources.ResourceListener;
import jeeves.server.resources.ResourceProvider;
import jeeves.utils.Log;

import org.jdom.Element;

//=============================================================================

/** A pool of database connections
  */

public class DbmsPool implements ResourceProvider
{
	private Hashtable<Dbms, Boolean> htDbms = new Hashtable<Dbms,Boolean>(100, 0.75f);

	private String name;
	private String user;
	private String passwd;
	private String url;
	private int    maxTries;
	private int    maxWait;
	private long   reconnectTime;

	private Set<ResourceListener> hsListeners = Collections.synchronizedSet(new HashSet<ResourceListener>());

	//--------------------------------------------------------------------------
	//---
	//--- API
	//---
	//--------------------------------------------------------------------------

	/** Builds the pool using init parameters from config
	  */

	public void init(String name, Element config) throws Exception
	{
		this.name = name;

		user          = config.getChildText(Jeeves.Res.Pool.USER);
		passwd        = config.getChildText(Jeeves.Res.Pool.PASSWORD);
		url 			  = config.getChildText(Jeeves.Res.Pool.URL);
		String driver = config.getChildText(Jeeves.Res.Pool.DRIVER);
		String size   = config.getChildText(Jeeves.Res.Pool.POOL_SIZE);
		String maxt   = config.getChildText(Jeeves.Res.Pool.MAX_TRIES);
		String maxw   = config.getChildText(Jeeves.Res.Pool.MAX_WAIT);
		String rect   = config.getChildText(Jeeves.Res.Pool.RECONNECT_TIME);
		
		// Other properties
		Map<String, String> otherProperties = new HashMap<String, String>();
		Element propertiesElement = config.getChild(Jeeves.Res.Pool.PROPERTIES);
		if (propertiesElement != null)
		{
		   for (Object propertyElementObject : propertiesElement.getChildren(Jeeves.Res.Pool.PROPERTY))
		   {
		      Element propertyElement = (Element)propertyElementObject;
		      String propertyKey = propertyElement.getAttributeValue("key");
		      String propertyValue = propertyElement.getTextTrim();
		      
		      if (propertyKey == null)
		      {
		         throw new Exception("Property for JDBC connection with URL '" + url + "' has no key");
		      }
		      
		      otherProperties.put(propertyKey, propertyValue);
		   }
		}
		

		int poolSize  = (size == null) ? Jeeves.Res.Pool.DEF_POOL_SIZE : Integer.parseInt(size);
		maxTries      = (maxt == null) ? Jeeves.Res.Pool.DEF_MAX_TRIES : Integer.parseInt(maxt);
		maxWait       = (maxw == null) ? Jeeves.Res.Pool.DEF_MAX_WAIT  : Integer.parseInt(maxw);
		reconnectTime = (rect == null) ? 0 /* never */                 : Long.parseLong(rect) * 1000;

		for(int i=0; i<poolSize; i++)
		{
			Dbms dbms = new Dbms(driver, url);
			dbms.connect(user,passwd,otherProperties);
			htDbms.put(dbms, new Boolean(false));
		}
	}

	//--------------------------------------------------------------------------

	public Map<String,String> getProps() {
		Map<String,String> result = new HashMap<String,String>();
		result.put("name",		 name);
		result.put("user",		 user);
		result.put("password", passwd);
		result.put("url",	 		 url);
		return result;
	}

	//--------------------------------------------------------------------------
	public void end()
	{
		for(Enumeration<Dbms> e=htDbms.keys(); e.hasMoreElements();)
		{
			Dbms dbms = e.nextElement();
			dbms.disconnect();
		}
	}

	//--------------------------------------------------------------------------

	public String getName() { return name; }

	//--------------------------------------------------------------------------
	/** Gets an element from the pool
	 * 
	 * @deprecated Method does not support case insensitive columns.  Do not use.
	  */
	public synchronized Object open() throws Exception
	{
		String lastMessage = null;

		// try to connect MAX_TRIES times
		for (int nTries = 0; nTries < maxTries; nTries++)
		{
			// try to get a free dbms
			int i = 0;
			for(Enumeration<Dbms> e=htDbms.keys(); e.hasMoreElements();)
			{

				Dbms    dbms   = e.nextElement();
				Boolean locked = htDbms.get(dbms);
				debug("DBMS Resource "+i+" is "+locked);

				if (!locked.booleanValue())
				{
					try
					{
						if (dbms.isClosed())
							reconnectTime = 1;

						// reconnect if needed
						if (reconnectTime > 0)
						{
							long currTime     = System.currentTimeMillis();
							long lastConnTime = dbms.getLastConnTime();

							if (currTime - lastConnTime >= reconnectTime)
							{
								error("reconnecting: " + (currTime - lastConnTime) + ">=" + reconnectTime + " ms since last connection"); // FIXME

								// FIXME: what happens if it disconnects but is unable to connect again?
								dbms.disconnect();
								dbms.connect(user, passwd, Collections.<String, String>emptyMap());
								reconnectTime = 0;
							}
						}

						debug("SUCCESS: DBMS Resource "+i+" is not locked");
						htDbms.put(dbms, new Boolean(true));
						return dbms;
					}
					catch (Exception ex)
					{
						error("Unable to connect to dbms: " + ex.getMessage(), ex);
						lastMessage = ex.getMessage();
					}
				}
				i++;
			}
			// wait MAX_WAIT msecs (but not after last try)
			if (nTries < maxTries - 1)
			{
				try { Thread.sleep(maxWait); }
				catch (InterruptedException ex) {}
			}
		}
		throw new Exception("unable to open resource " + name + " after " + maxTries + "attempts: " + lastMessage);
	}

	//--------------------------------------------------------------------------
	/** Releases one element from the pool
	  */

	public void close(Object resource) throws Exception
	{
		checkResource(resource);
		debug("Committing and closing "+resource);

		try
		{
		    ((Dbms) resource).commit();
        }
		finally
		{
		    htDbms.put((Dbms) resource, new Boolean(false));
        }

		synchronized(hsListeners) {
			for(ResourceListener l : hsListeners)
				l.close(resource);
		}
	}

	//--------------------------------------------------------------------------
	/** Releases one element from the pool doing an abort
	  */

	public void abort(Object resource) throws Exception
	{
		checkResource(resource);
		debug("Aborting "+resource);

		try
		{
			((Dbms) resource).abort();
		}
		finally
		{
			htDbms.put((Dbms) resource, new Boolean(false));
		}

		synchronized(hsListeners) {
			for(ResourceListener l : hsListeners)
				l.abort(resource);
		}
	}

	//--------------------------------------------------------------------------

	public void addListener(ResourceListener l)
	{
			hsListeners.add(l);
	}

	//--------------------------------------------------------------------------

	public void removeListener(ResourceListener l)
	{
			hsListeners.remove(l);
	}

	//--------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//--------------------------------------------------------------------------

	private void checkResource(Object resource)
	{
		Boolean locked = (Boolean) htDbms.get(resource);

		if (locked == null)
			throw new IllegalArgumentException("Resource not found :"+resource);

		if (!locked.booleanValue())
			throw new IllegalArgumentException("Resource not locked :"+resource);
	}

	private void debug  (String message) { Log.debug  (Log.DBMSPOOL, message); }
	static  void info   (String message) { Log.info   (Log.DBMSPOOL, message); }
	static  void error  (String message) { Log.error  (Log.DBMSPOOL, message); }
   static  void error  (String message, Throwable t) { Log.error  (Log.DBMSPOOL, message, t); }
}

//=============================================================================

