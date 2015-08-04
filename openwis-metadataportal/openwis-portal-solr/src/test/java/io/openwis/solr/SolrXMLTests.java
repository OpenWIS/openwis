package io.openwis.solr;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.XML;
import org.testng.annotations.Test;

/**
 * Solr Tests to Test the opwenwis solr schema Uses Solr Embedded Server for
 * Testing
 * 
 * @author gollogly_m
 *
 */

public class SolrXMLTests extends SolrTestBase {

	SolrInputDocument[] doc = null;
	public SolrXMLTests() throws Exception {
		super();
	}
	
	/**
	 * Test Multiple insert documents
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@Test
	public void testThatDirectXMLInsertRequests() throws SolrServerException,
			IOException {

		doc = new SolrInputDocument[3];
		
		for (int i = 0; i < 3; i++) {
			doc[i] = new SolrInputDocument();
			doc[i].setField("_uuid", i + " & 222", 1.0f);
		}
		// Add three documents
		for (SolrInputDocument d : doc) {
			server.add(d);
		}

		server.commit();

		assertNumFound("*:*", 3); // make sure it got in
	}
	
	/**
	 * Test Multiple delete commands as Direct XML Request
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@Test  (dependsOnMethods = { "testThatDirectXMLInsertRequests" })
	public void testThatDirectXMLDeleteRequests() throws SolrServerException,
			IOException {

		StringWriter xml = new StringWriter();
		xml.append("<delete>");
		for (SolrInputDocument d : doc) {
			xml.append("<id>");
			XML.escapeCharData((String) d.getField("_uuid").getFirstValue(),
					xml);
			xml.append("</id>");
		}
		xml.append("</delete>");
		
		DirectXmlRequest up = new DirectXmlRequest("/update", xml.toString());
		server.request(up);
		server.commit();
		assertNumFound("*:*", 0); // make sure it got out
	}

}
