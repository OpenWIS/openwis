//==============================================================================
//===
//===   ISODate
//===
//==============================================================================
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

package org.fao.oaipmh.util;

import java.util.Calendar;
import java.util.TimeZone;

//==============================================================================

public class ISODate implements Cloneable
{
	private int year;	//--- 4 digits
	private int month;	//--- 1..12
	private int day;	//--- 1..31
	private int hour;	//--- 0..23
	private int min;	//--- 0..59
	private int sec;	//--- 0..59

	private boolean isShort; //--- 'true' if the format is yyyy-mm-dd

	//---------------------------------------------------------------------------

	private static Calendar calendar = Calendar.getInstance();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ISODate()
	{
		this(System.currentTimeMillis());
	}

	//---------------------------------------------------------------------------

	public ISODate(long time)
	{
		synchronized(calendar)
		{
			calendar.setTimeInMillis(time);
			calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

			setYear(calendar.get(Calendar.YEAR));
			setMonth(calendar.get(Calendar.MONTH) +1);
			setDay(calendar.get(Calendar.DAY_OF_MONTH));

			setHour(calendar.get(Calendar.HOUR_OF_DAY));
			min   = calendar.get(Calendar.MINUTE);
			setSec(calendar.get(Calendar.SECOND));
		}
	}

	//---------------------------------------------------------------------------

	public ISODate(String isoDate)
	{
		setDate(isoDate);
	}

	//---------------------------------------------------------------------------

	private ISODate(int y, int m, int d, int h, int mi, int s)
	{
		setYear(y);
		setMonth(m);
		setDay(d);
		setHour(h);
		min   = mi;
		setSec(s);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public ISODate clone()
	{
		return new ISODate(getYear(), getMonth(), getDay(), getHour(), min, getSec());
	}

	//---------------------------------------------------------------------------

	public void setDate(String isoDate)
	{
		if (isoDate == null)
			throw new IllegalArgumentException("ISO date is null");

		if (isoDate.length() < 10)
			throw new IllegalArgumentException("Invalid ISO date : "+ isoDate);

		try
		{
			setYear(Integer.parseInt(isoDate.substring(0,  4)));
			setMonth(Integer.parseInt(isoDate.substring(5,  7)));
			setDay(Integer.parseInt(isoDate.substring(8, 10)));

			setShort(true);

			setHour(0);
			min  = 0;
			setSec(0);

			//--- is the date in 'yyyy-mm-dd' or 'yyyy-mm-ddZ' format?

			if (isoDate.length() < 12)
				return;

			isoDate = isoDate.substring(11);

			setHour(Integer.parseInt(isoDate.substring(0,2)));
			min   = Integer.parseInt(isoDate.substring(3,5));
			setSec(Integer.parseInt(isoDate.substring(6,8)));

			setShort(false);
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Invalid ISO date : "+ isoDate);
		}
	}

	//---------------------------------------------------------------------------
	/** Subtract a date from this date and return the seconds between them */

	public long sub(ISODate date)
	{
		return getSeconds() - date.getSeconds();
	}

	//--------------------------------------------------------------------------

	public String getDate()
	{
		return getYear() +"-"+ pad(getMonth()) +"-"+ pad(getDay());
	}

	//--------------------------------------------------------------------------

	public String getTime()
	{
		return pad(getHour()) +":"+ pad(min) +":"+ pad(getSec());
	}

	//--------------------------------------------------------------------------

	public String toString()
	{
		return getDate() +"T"+ getTime();
	}

	//---------------------------------------------------------------------------

	public long getSeconds()
	{
		synchronized(calendar)
		{
			calendar.clear();
   	   calendar.set(getYear(), getMonth() -1, getDay(), getHour(), min, getSec());

			return calendar.getTimeInMillis() / 1000;
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private String pad(int value)
	{
		if (value > 9)
			return Integer.toString(value);

		return "0"+ value;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public boolean isShort() {
		return isShort;
	}

	public void setShort(boolean aShort) {
		isShort = aShort;
	}
}

//==============================================================================




