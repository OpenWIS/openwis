package org.openwis.metadataportal.search.solr.range;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.params.TermsParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.TermsComponent;
import org.apache.solr.schema.FieldType;

/**
 * The Class OpenwisRangeComponent. <P>
 * Explanation goes here. <P>
 */
public class OpenwisRangeComponent extends TermsComponent {

   /**
    * {@inheritDoc}
    * @see org.apache.solr.handler.component.TermsComponent#process(org.apache.solr.handler.component.ResponseBuilder)
    */
   @Override
   public void process(ResponseBuilder rb) throws IOException {
      SolrParams params = rb.req.getParams();
      String field = params.getParams(TermsParams.TERMS_FIELD)[0];

      // If no lower bound was specified, use the prefix
      Term tf = new Term(field);
      TermEnum termEnum = rb.req.getSearcher().getReader().terms(tf); //this will be positioned ready to go

      NamedList<NamedList<Integer>> terms = new NamedList<NamedList<Integer>>();
      rb.rsp.add(TermsParams.TERMS, terms);
      NamedList<Integer> fieldTerms = new NamedList<Integer>();
      terms.add(field, fieldTerms);

      Term min = null;
      Term max = null;
      Term term;
      while (termEnum.next()) {
         term = termEnum.term();
         if (field.equals(term.field())) {
            if (min == null || term.compareTo(min) < 0) {
               min = term;
            }
            if (max == null || term.compareTo(max) > 0) {
               max = term;
            }
         }
      }

      if (min != null && max != null) {
         FieldType ft = rb.req.getSchema().getFieldTypeNoEx(field);
         String txt = ft.indexedToReadable(min.text());
         fieldTerms.add(txt, 0);
         txt = ft.indexedToReadable(max.text());
         fieldTerms.add(txt, 1);
      }
   }
}
