package io.openwis.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.testng.annotations.Test;

/**
 * Solr Tests to Test the opwenwis solr schema Uses Solr Embedded Server for
 * Testing
 * 
 * @author gollogly_m
 *
 */

public class SolrTests extends SolrTestBase {

	public SolrTests() throws Exception {
		super();
	}

	@Test
	public void testThatNoResultsAreReturned() throws SolrServerException {
		SolrParams params = new SolrQuery("A few words that doesn't exist");
		QueryResponse response = server.query(params);

		assertEquals(0L, response.getResults().getNumFound());
	}

	@Test
	public void testThatDocumentCannotBeInsertedWithoutUUIDPK()
			throws Exception {
		SolrInputDocument document = new SolrInputDocument();

		try {
			server.add(document);
			server.commit();
			fail("UUID PK is needed for a document to be added");
		} catch (SolrServerException se) {
			// pass, SolrServer exception expected
			assertTrue(se.getMessage().contains("_uuid"));
		}

	}

	@Test
	public void testThatDocumentIsInserted() throws SolrServerException,
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

	@Test
	public void testThatDocumentIsDeleted() throws SolrServerException,
			IOException {

		SolrInputDocument document = new SolrInputDocument();
		document.addField("_uuid", "def");
		document.addField("_title", "my title");
		document.addField("anytext", "any text");

		server.add(document);
		server.commit();

		SolrParams params = new SolrQuery("_uuid:def");
		QueryResponse response = server.query(params);

		assertEquals(1L, response.getResults().getNumFound());
		assertEquals("def", response.getResults().get(0).get("_uuid"));

		server.deleteByQuery("_uuid:def");
		server.commit();

		QueryResponse responsePostDelete = server.query(params);

		assertEquals(0L, responsePostDelete.getStatus());
		assertEquals(0L, responsePostDelete.getResults().getNumFound());

	}

}
