package org.openwis.metadataportal.search.solr.spatial.parser;

import io.openwis.solr.SolrTestBase;

import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

/**
 * OpenwisLuceneQParser Tests
 *  
 * @author gollogly_m
 *
 */
public class OpenwisLuceneQParserTests extends SolrTestBase {

	OpenwisLuceneQParser owlqp;
	
	public OpenwisLuceneQParserTests() throws Exception {
		super();
	}
	
	@Test
	public void testCreateOpenwisLuceneQParser() throws ParseException {
		// Create Parser
		owlqp = createOpenwisLuceneQParser();

		// Assert object is not null
		assertNotNull(owlqp);
	}

	@Test
	public void testParse() throws ParseException {
		// Set up prerequisites for Test
		owlqp = createOpenwisLuceneQParser();

		// Call parse Method
		Query query = owlqp.parse();
		
		// Assert query is parsed as expected
		assertEquals(query.toString(), "*:*");
	}
	
	@Test
	public void testBadQueryShouldThrowParseException() throws ParseException {
		// Set up prerequisites for Test
		owlqp = createOpenwisLuceneQParser();
		owlqp.setString("!BAD QUERY!");

		// Call parse Method
		try{
			owlqp.parse();
		} catch (ParseException pe) {
			// pass, ParseException exception expected
			assertTrue(pe.getMessage().contains("Cannot parse"));
		}
	}
	
	@Test
	public void testGetQuery() throws ParseException {
		// Set up prerequisites for Test
		owlqp = createOpenwisLuceneQParser();

		// Call getQuery Method, should call parse
		Query query = owlqp.getQuery();
		
		// Assert query is parsed as expected
		assertEquals(query.toString(), "*:*");
	}
	

	/**
	 * Create OpenwisLuceneQParser
	 * 
	 * @return OpenwisLuceneQParser
	 */
	private OpenwisLuceneQParser createOpenwisLuceneQParser() {
		
		ModifiableSolrParams params = new ModifiableSolrParams();
		ModifiableSolrParams localParams = new ModifiableSolrParams();

		SolrQueryRequest req = new LocalSolrQueryRequest(h.getCore(), params);
		
		OpenwisLuceneQParser owlqp = new OpenwisLuceneQParser("*:*", localParams,
				params, req);
		return owlqp;
	}
	
	@AfterTest
	public void destroy() throws IOException {
		owlqp = null;
	}

}
