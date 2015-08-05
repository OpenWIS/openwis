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
	public void testThatDocumentCannotBeInsertedWithoutPrimaryKeyUUID()
			throws Exception {
		SolrInputDocument document = new SolrInputDocument();

		try {
			server.add(document);
			server.commit();
			fail("UUID PK is needed for a document to be added");
		} catch (SolrServerException sse) {
			// pass, SolrServer exception expected
			assertTrue(sse.getMessage().contains("_uuid"));
		}

	}
	
	@Test
	public void testThatDocumentCannotBeInsertedWithInvalidField()
			throws Exception {
		SolrInputDocument document = new SolrInputDocument();
		document.addField("SOME_INVALID_FIELD", "SOME VALUE");

		try {
			server.add(document);
			server.commit();
			fail("UUID PK is needed for a document to be added");
		} catch (SolrServerException sse) {
			// pass, SolrServer exception expected
			assertTrue(sse.getMessage().contains("SOME_INVALID_FIELD"));
		}
	}
	
	@Test
	public void testThatSimpleDocumentIsInserted() throws SolrServerException,
			IOException {
		SolrInputDocument document = new SolrInputDocument();
		document.addField("_uuid", "abc");
		document.addField("title", "my title");
		document.addField("anytext", "any text");

		server.add(document);
		server.commit();

		SolrParams params = new SolrQuery("_uuid:a*");
		QueryResponse response = server.query(params);

		assertEquals(1L, response.getResults().getNumFound());
		assertEquals("abc", response.getResults().get(0).get("_uuid"));
	}
	
	@Test
	public void testSearchByByPrimaryKeyUUID()
			throws Exception {
		// Add document
		addDocumentWithSupportFields();

		// Find Document by Default Search Field (anytext)
		SolrParams titleParams = new SolrQuery("_uuid:a*");
		QueryResponse response = server.query(titleParams);

		// Assert Document is ok
		assertEquals(1L, response.getResults().getNumFound());
		assertEquals("abc", response.getResults().get(0).get("_uuid"));
		assertEquals("My title", response.getResults().get(0).get("title"));
		assertEquals("Level 1", response.getResults().get(0).get("levelName"));
	}
	
	@Test
	public void testSearchByDefaultSearchFieldAnyText()
			throws Exception {
		// Add document
		addDocumentWithSupportFields();

		// Find Document by Default Search Field (anytext)
		SolrParams titleParams = new SolrQuery("anytext:ABC Org");
		QueryResponse response2 = server.query(titleParams);

		// Assert Document is ok
		assertEquals(1L, response2.getResults().getNumFound());
		assertEquals("abc", response2.getResults().get(0).get("_uuid"));
		assertEquals("My title", response2.getResults().get(0).get("title"));
		assertEquals("Level 1", response2.getResults().get(0).get("levelName"));
	}
	

	
	@Test
	public void testThatDocumentWithAllSupportedFieldsCanBeInserted()
			throws Exception {
		// Add document
		addDocumentWithSupportFields();

		// Find Document by Unique ID
		SolrParams params = new SolrQuery("_uuid:a*");
		QueryResponse response1 = server.query(params);

		assertEquals(1L, response1.getResults().getNumFound());
		assertEquals("abc", response1.getResults().get(0).get("_uuid"));
		
		
	}

	private void addDocumentWithSupportFields() throws SolrServerException,
			IOException {
		SolrInputDocument document = new SolrInputDocument();
		document.addField("_uuid", "abc");
		document.addField("anytext", "Any text");
		document.addField("abstract", "This is something abstract");
		document.addField("altTitle", "Alt Title");
		document.addField("keywordType", "Some Keyword Type");
		document.addField("levelName", "Level 1");
		document.addField("orgName", "ABC Org");
		document.addField("specificationTitle", "Specification Title");
		document.addField("title", "My title");

		server.add(document);
		server.commit();
	}


	@Test
	public void testThatDocumentIsDeleted() throws SolrServerException,
			IOException {

		SolrInputDocument document = new SolrInputDocument();
		document.addField("_uuid", "def");
		document.addField("title", "my title");
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
