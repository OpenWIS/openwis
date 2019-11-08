package org.openwis.dataservice.gts.feeding;

import javax.ejb.Local;

import org.openwis.dataservice.util.FileInfo;

@Local
public interface Feeder {
			
	public boolean add(FileInfo file);
}