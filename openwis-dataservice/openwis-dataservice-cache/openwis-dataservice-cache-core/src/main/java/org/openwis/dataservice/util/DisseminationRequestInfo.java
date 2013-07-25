package org.openwis.dataservice.util;

import java.io.Serializable;

public class DisseminationRequestInfo implements Serializable{
	
	private long processedRequestId;	
	private long timeStamp;
		
	public long getProcessedRequestId() {
		return processedRequestId;
	}
	public void setProcessedRequestId(long processedRequestId) {
		this.processedRequestId = processedRequestId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}	
}