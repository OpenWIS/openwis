package org.openwis.dataservice.extraction;

import org.junit.BeforeClass;
import org.junit.Test;


public class RemoteExtractFromCacheTest {
		

   //	private static InitialContext initialContext;			
	
	@BeforeClass
	public static void init() throws Exception {
		/*String urlName = "172.17.143.31:1099";
	  	Properties p = new Properties();
	  	p.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");    	
	  	p.put("java.naming.provider.url", urlName);           
	  	initialContext = new InitialContext(p);*/	  		    
	}
	
	@Test
	public void testOne() throws Exception {
		/*Object x;
		x = initialContext.lookup("openwis-dataservice-cache/ExtractFromCache/remote");
		assertNotNull(x);		
		
		CacheExtraService  service;
		service = (CacheExtraService ) x;		
		assertNotNull(service);	
		
		String metadataURN = "urn:x-wmo:md:int.wmo.wis::TPGBE07CKWBC";
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter());		
		Long processedRequestId = 0L;		
		String stagingPostURI = "/";
		
		MessageStatus mStatus = service.extract(metadataURN, parameters, processedRequestId, stagingPostURI);
		System.out.println(mStatus.getMessage());
		System.out.println(mStatus.getStatus());*/
	}
	
}