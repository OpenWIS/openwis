package io.openwis.solr;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.util.AbstractSolrTestCase;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Test Base Class for running Solr Tests
 * 
 *  Uses Solr Embedded Server
 * 
 * @author gollogly_m
 *
 */
public abstract class SolrTestBase extends AbstractSolrTestCase {

	protected SolrServer server;
	private final String indexLocation = System.getProperty("user.dir")
			+ "/src/main/resources/";
	
	@Override
	public String getSchemaFile() {
		return "conf/schema.xml";
	}

	@Override
	public String getSolrConfigFile() {
		return "src/test/resources/conf/solrconfig.xml";
	}

	@BeforeSuite
	@Override
	public void setUp() throws Exception {
		System.setProperty("solr.solr.home", indexLocation);
		super.setUp();

		server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore()
				.getName());

	}
	
	public SolrTestBase() throws Exception {
		setUp();
	}
	
	private void removeIndexDirectory() throws IOException {
		File indexDir = new File(indexLocation, "data/index");
		FileUtils.deleteDirectory(indexDir);
	}
	
	protected void assertNumFound(String query, int num)
			throws SolrServerException, IOException {
		QueryResponse rsp = server.query(new SolrQuery(query));
		if (num != rsp.getResults().getNumFound()) {
			fail("expected: " + num + " but had: "
					+ rsp.getResults().getNumFound() + " :: "
					+ rsp.getResults());
		}
	}

	@AfterSuite
	public void destroy() throws IOException {
		h.getCoreContainer().shutdown();
		removeIndexDirectory();
	}

}