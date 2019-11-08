package org.fao.geonet.kernel.oaipmh;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.oaipmh.responses.GeonetworkResumptionToken;
import org.fao.oaipmh.util.ISODate;

public class ResumptionTokenCache extends Thread {

	public final static int CACHE_EXPUNGE_DELAY = 10*1000; // 10 seconds

	private Map<String,GeonetworkResumptionToken> map ; 
	private boolean running = true;
	private SettingManager settingMan;

	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return settingMan.getValueAsInt("system/oai/tokentimeout");
	}

	/**
	 * @return the cachemaxsize
	 */
	public int getCachemaxsize() {
	    return settingMan.getValueAsInt("system/oai/cachesize");
	}

	/**
	 * Constructor
	 * @param sm
	 */
	public ResumptionTokenCache(SettingManager sm) {
		
		this.settingMan=sm;
		Log.debug(Geonet.OAI_HARVESTER,"OAI cache ::init timout:"+getTimeout());
		
		map = Collections.synchronizedMap( new HashMap<String,GeonetworkResumptionToken>()  );

		this.setDaemon(true);
		this.setName("Cached Search Session Expiry Thread");
		this.start();

	}

	public void run() {

		while(running) {
			try {
				Thread.sleep(CACHE_EXPUNGE_DELAY);
				expunge();
			}
			catch ( java.lang.InterruptedException ie ) {
				ie.printStackTrace();
			}
		}
	}

	private synchronized void expunge() {

		Date now = getUTCTime();

		for (String key : map.keySet() ) {
			if ( map.get(key) != null && map.get(key).getExpirDate().getSeconds() < (now.getTime()/1000)  ) {
				map.remove(key);
				Log.debug(Geonet.OAI_HARVESTER,"OAI cache ::expunge removing:"+key);
			}
		}
	}
	
	// remove oldest token from cache
	private void removeLast() {
		Log.debug(Geonet.OAI_HARVESTER,"OAI cache ::removeLast" );

		
		long oldest=Long.MAX_VALUE;
		String oldkey="";
		
		for (String key : map.keySet() ) {
         if ( map.get(key).getExpirDate().getSeconds() < oldest   ) {
				oldkey = key;
				oldest = map.get(key).getExpirDate().getSeconds();
			}
		}
		
		map.remove(oldkey);
		Log.debug(Geonet.OAI_HARVESTER,"OAI cache ::removeLast removing:"+oldkey);

		
	}

	public synchronized GeonetworkResumptionToken getResumptionToken(String str) {
		return map.get(str);
	}
	
	public synchronized void storeResumptionToken(GeonetworkResumptionToken resumptionToken) {
		Log.debug(Geonet.OAI_HARVESTER,"OAI cache ::store "+resumptionToken.getKey() + " size: "+map.size() );
		
		if ( map.size() == getCachemaxsize() ) {
			removeLast();
		}
		
		resumptionToken.setExpirDate(new ISODate( getUTCTime().getTime() + getTimeout()  ));
		map.put(resumptionToken.getKey(), resumptionToken);
	}

	private static Date getUTCTime()
	{
		Date date = new Date();
		TimeZone tz = TimeZone.getDefault();
		Date ret = new Date( date.getTime() - tz.getRawOffset() );

		if ( tz.inDaylightTime( ret ))
		{
			Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );

			// check to make sure we have not crossed back into standard time
			// this happens when we are on the cusp of DST (7pm the day before the change for PDT)
			if ( tz.inDaylightTime( dstDate ))
			{
				ret = dstDate;
			}
		}
		return ret;
	}

}
