package org.openwis.harness.dissemination;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ClientTest {
	
	private Dissemination harness;

	@Before
	@Test
	public void createHarnessTest(){
		DisseminationImplService harnessService = new DisseminationImplService();
		harness = harnessService.getDisseminationImplPort();
		assertEquals(true, harness != null);
		assertEquals(true, harness instanceof Dissemination);
	}	
	
	@Test
	public void disseminateTest(){
		
	}
	
	@Test
	public void monitorDisseminationTest(){
		
	}
}