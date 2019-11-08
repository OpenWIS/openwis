package org.openwis.dataservice.util;

import javax.ejb.Remote;

@Remote
public interface DatabaseInitializer {
	
	public Long initForExtraction();
	
	public void initForCollection();
	
	public void emptyDatabaseTables();
}