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

package jeeves.server.sources.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jeeves.server.sources.ServiceRequest;

//=============================================================================

/** Represents a request from tomcat (ie http)
  */

public class HttpServiceRequest extends ServiceRequest
{
   private HttpServletRequest httpReq;
	private HttpServletResponse httpRes;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public HttpServiceRequest(HttpServletRequest req, HttpServletResponse res)
	{
	   httpReq = req;
		httpRes = res;
	}

	@Override
	public void setOutputStream(OutputStream os) {
	   //super.setOutputStream(os);
	}
	@Override
	public OutputStream getOutputStream() throws IOException {
	   return httpRes.getOutputStream();
	}
	
	public void setAttribute(String key, Object value) {
	   httpReq.setAttribute(key, value);
	}
	
	public void forward(String uri) throws IOException, ServletException {
	   httpReq.getRequestDispatcher(uri).forward(httpReq, httpRes);
	}
	
	public void sendRedirect(String location) throws IOException, ServletException {
      httpRes.sendRedirect(location);
   }
	
	//---------------------------------------------------------------------------

	public void beginStream(String contentType, boolean cache)
	{
		beginStream(contentType, -1, null, cache);
	}

	//---------------------------------------------------------------------------

	public void beginStream(String contentType, int contentLength,
									String contentDisposition, boolean cache)
	{
		httpRes.setStatus(statusCode);

		if (contentType != null)
			httpRes.setContentType(contentType);

		if (contentLength != -1)
			httpRes.setContentLength(contentLength);

		if (contentDisposition != null)
			httpRes.addHeader("Content-disposition", contentDisposition);
		else
		{
			//--- this else is needed by IExplorer6
			//--- maybe we can use the <meta> tag instead of these lines

			if (!cache)
			{
				httpRes.addHeader("Pragma",        "no-cache");
				httpRes.addHeader("Cache-Control", "no-cache");
				httpRes.addHeader("Expires",       "-1");
			}
		}
	}

	//---------------------------------------------------------------------------

	public void endStream() throws IOException
	{
		httpRes.flushBuffer();
	}
}

//=============================================================================

