package org.fao.oaipmh.requests;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import junit.framework.Assert;

public class TransportTest {

   @Test
   public void testSetUrl() throws MalformedURLException {
      Transport t1 = new Transport();
      Transport t2 = new Transport();
      Transport t3 = new Transport();
      Transport t4 = new Transport();
      
      t1.setUrl(new URL("http://www.example.com/oaipmh/endpoint"));
      t2.setUrl(new URL("http://localhost:8080/my-oaipmh-endpoint#with-a-fragment"));
      t3.setUrl(new URL("https://secure.example.com/encrypted/endpoint"));
      t4.setUrl(new URL("https://10.64.33.123:9999/encrypted/endpoint/on/another/port"));
      
      Assert.assertEquals("www.example.com", t1.getHost());
      Assert.assertEquals("localhost", t2.getHost());
      Assert.assertEquals("secure.example.com", t3.getHost());
      Assert.assertEquals("10.64.33.123", t4.getHost());

      Assert.assertEquals(80, t1.getPort());
      Assert.assertEquals(8080, t2.getPort());
      Assert.assertEquals(443, t3.getPort());
      Assert.assertEquals(9999, t4.getPort());

      Assert.assertEquals("/oaipmh/endpoint", t1.getAddress());
      Assert.assertEquals("/my-oaipmh-endpoint", t2.getAddress());
      Assert.assertEquals("/encrypted/endpoint", t3.getAddress());
      Assert.assertEquals("/encrypted/endpoint/on/another/port", t4.getAddress());
      
      Assert.assertEquals(Transport.Scheme.HTTP, t1.getScheme());
      Assert.assertEquals(Transport.Scheme.HTTP, t2.getScheme());
      Assert.assertEquals(Transport.Scheme.HTTPS, t3.getScheme());
      Assert.assertEquals(Transport.Scheme.HTTPS, t4.getScheme());
   }
   
   @Test(expected=MalformedURLException.class)
   public void testSetUrlWithBadScheme() throws MalformedURLException {
      Transport t1 = new Transport();
      
      t1.setUrl(new URL("file://bladibla/this/scheme/is/not/supported"));
   }
}
