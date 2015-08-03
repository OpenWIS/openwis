package io.openwis.solr;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.util.AbstractSolrTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
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
	private final String indexLocation = System.getProperty("user.dir")
			+ "\\src\\main\\resources\\";

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
		System.setProperty("solr.solr.home", indexLocation);
		super.setUp();

		server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore()
				.getName());

	}

	@Test
	public void testThatNoResultsAreReturned() throws SolrServerException {
		SolrParams params = new SolrQuery("A few words that doesn't exist");
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

	private void removeIndexDirectory() throws IOException {
		File indexDir = new File(indexLocation, "data/index");
		FileUtils.deleteDirectory(indexDir);
	}

	@AfterClass
	public void testDestroy() throws IOException {
		h.getCoreContainer().shutdown();
		removeIndexDirectory();
	}
}
