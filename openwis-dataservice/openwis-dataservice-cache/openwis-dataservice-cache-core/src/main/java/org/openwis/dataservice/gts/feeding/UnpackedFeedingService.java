package org.openwis.dataservice.gts.feeding;

import javax.jms.Message;

public interface UnpackedFeedingService {
	
	public void onMessage(Message message);
}