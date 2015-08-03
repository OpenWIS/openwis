package io.openwis.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.util.AbstractSolrTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Solr Tests to Test the opwenwis solr schema Uses Solr Embedded Server for
 * Testing
 * 
 * @author gollogly_m
 *
 */

public class SolrTests extends AbstractSolrTestCase {

	private SolrServer server;

	@Override
	public String getSchemaFile() {
		return "conf/schema.xml";
	}

	@Override
	public String getSolrConfigFile() {
		return "src/test/resources/conf/solrconfig.xml";
	}

	@BeforeTest
	@Override
	public void setUp() throws Exception {
		System.setProperty("solr.solr.home", System.getProperty("user.dir")
				+ "\\src\\main\\resources\\");
		super.setUp();
		
		server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore()
				.getName());

	}

	@Test
	public void testThatNoResultsAreReturned() throws SolrServerException {
		SolrParams params = new SolrQuery("Some tesaxt that won't be found");
		QueryResponse response = server.query(params);
		assertEquals(0L, response.getResults().getNumFound());
	}

	@Test
	public void testThatDocumentIsFound() throws SolrServerException,
			IOException {
		SolrInputDocument document = new SolrInputDocument();
		document.addField("_uuid", "abc");
		document.addField("_title", "my title");
		document.addField("anytext", "any text");

		server.add(document);
		server.commit();

		SolrParams params = new SolrQuery("_uuid:a*");
		QueryResponse response = server.query(params);
		assertEquals(1L, response.getResults().getNumFound());
		assertEquals("abc", response.getResults().get(0).get("_uuid"));
	}

	@AfterTest
	public void destroy() {
		h.getCoreContainer().shutdown();
	}
}
