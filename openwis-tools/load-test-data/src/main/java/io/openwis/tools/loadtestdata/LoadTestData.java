package io.openwis.tools.loadtestdata;

import io.openwis.client.OpenWISClient;
import io.openwis.client.auth.BackDoorAuthentication;
import io.openwis.client.http.HttpOpenWISClient;
import io.openwis.tools.loadtestdata.loader.CacheDataLoader;
import io.openwis.tools.loadtestdata.loader.ClasspathProfileTestDataLocator;
import io.openwis.tools.loadtestdata.loader.DefaultTestDataLoader;
import io.openwis.tools.loadtestdata.loader.HttpMetadataLoader;
import io.openwis.tools.loadtestdata.loader.MetadataLoader;
import io.openwis.tools.loadtestdata.loader.TestDataLoader;
import io.openwis.tools.loadtestdata.loader.TestDataLocator;
import io.openwis.tools.loadtestdata.loader.VagrantCacheDataLoader;

/**
 * Entry point.
 */
public class LoadTestData {

	public static void main(String[] args) throws Exception {
		
		try (OpenWISClient client = new HttpOpenWISClient("http://localhost:8060/openwis-admin-portal/", new BackDoorAuthentication("admin", "admin"))) {
			TestDataLocator locator = new ClasspathProfileTestDataLocator("def");
			MetadataLoader mdLoader = new HttpMetadataLoader(client);
			CacheDataLoader cacheDataLoader = new VagrantCacheDataLoader("localhost", 2222, "openwis", "openwis1");
			
			TestDataLoader loader = new DefaultTestDataLoader(locator, mdLoader, cacheDataLoader);
			
			loader.loadAll();
		}
	}
}
