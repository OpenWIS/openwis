package org.openwis.datasource.server.mocks;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Singleton;

import org.openwis.dataservice.gts.feeding.Feeder;
import org.openwis.dataservice.util.FileInfo;

@Stateless(name = "Feeder")
@Local(Feeder.class)
public class MockedFeederEjb implements Feeder {

   @Override
   public boolean add(FileInfo file) {
      return false;
   }
}
