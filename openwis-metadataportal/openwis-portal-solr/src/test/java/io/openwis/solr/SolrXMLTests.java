package io.openwis.solr;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.XML;
import org.testng.annotations.Test;

/**
 * Solr Tests to Test the XML Requests for the opwenwis solr schema 
 * 
 * Uses Solr Embedded Server for
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
	public void testThatMultipleDocumentsCanInsert() throws SolrServerException,
			IOException {

		// Prepare documents
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

		// Assert all 3 documents were added
		assertNumFound("*:*", 3); 
	}
	
	/**
	 * Test Multiple delete commands as a Direct XML Request
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@Test  (dependsOnMethods = { "testThatMultipleDocumentsCanInsert" })
	public void testThatDirectXMLRequestsCanDelete() throws SolrServerException,
			IOException {

		// Prepare Documents
		StringWriter xml = new StringWriter();
		xml.append("<delete>");
		for (SolrInputDocument d : doc) {
			xml.append("<id>");
			XML.escapeCharData((String) d.getField("_uuid").getFirstValue(),
					xml);
			xml.append("</id>");
		}
		xml.append("</delete>");
		
		// Call Service
		DirectXmlRequest up = new DirectXmlRequest("/update", xml.toString());
		server.request(up);
		server.commit();
		
		// Assert, make sure no documents are left
		assertNumFound("*:*", 0); 
	}

}
