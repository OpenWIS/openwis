package org.openwis.metadataportal.kernel.search.query.solr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.fao.geonet.constants.Edit;
import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.search.query.SearchResultDocument;

/**
 * The Class SolrSearchResultDocument. <P>
 * Explanation goes here. <P>
 */
public class SolrSearchResultDocument implements SearchResultDocument {

   /** The solr doc. */
   private final SolrDocument solrDoc;

   /** The element. */
   private Element elt;

   /**
    * Instantiates a new solr search result document.
    *
    * @param doc the doc
    */
   public SolrSearchResultDocument(SolrDocument doc) {
      super();
      solrDoc = doc;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return solrDoc.toString();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResultDocument#getScore()
    */
   @Override
   public float getScore() {
      return (Float) getField(IndexField.SCORE);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResultDocument#getId()
    */
   @Override
   public String getId() {
      return String.valueOf(solrDoc.getFirstValue(IndexField.ID.getField()));
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResultDocument#getField(java.lang.String)
    */
   @Override
   public Object getField(IndexField field) {
      return solrDoc.get(field.getField());
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResultDocument#getFieldAsString(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public String getFieldAsString(IndexField field) {
      return ObjectUtils.toString(getField(field), null);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResultDocument#getFieldAsListOfString(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public List<String> getFieldAsListOfString(IndexField field) {
      List<String> result = new ArrayList<String>();
      Collection<Object> values = solrDoc.getFieldValues(field.getField());
      if (values != null) {
         for (Object obj : values) {
            if (obj != null) {
            result.add(ObjectUtils.toString(obj));
            }
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResultDocument#getElement()
    */
   @Override
   public Element getElement() {
      // lazy
      if (elt == null) {
         String root = String.valueOf(getField(IndexField.ROOT));

         Element md = new Element(root);
         // Title
         String title = String.valueOf(getField(IndexField.TITLE));
         md.addContent(new Element(Edit.RootChild.TITLE).setText(title));

         // Abstract
         String abs = String.valueOf(getField(IndexField.ABSTRACT));
         md.addContent(new Element(Edit.RootChild.ABSTRACT).setText(abs));

         // Keywords
         Collection<Object> keywords = solrDoc.getFieldValues(IndexField.KEYWORD.getField());
         if (keywords != null) {
            for (Object kw : keywords) {
               md.addContent(new Element(Edit.RootChild.KEYWORD).setText(String.valueOf(kw)));
            }
         }

         // Build geonetwork info
         Element info = new Element(Edit.RootChild.INFO, Edit.NAMESPACE);
         md.addContent(info);
         addElement(info, Edit.Info.Elem.ID, IndexField.ID);
         addElement(info, Edit.Info.Elem.UUID, IndexField.UUID);
         addElement(info, Edit.Info.Elem.SCHEMA, IndexField.SCHEMA);
         addElement(info, Edit.Info.Elem.CREATE_DATE, IndexField.CREATE_DATE);
         addElement(info, Edit.Info.Elem.CHANGE_DATE, IndexField.CHANGE_DATE);
         addElement(info, Edit.Info.Elem.SOURCE, IndexField.SOURCE);
         addElement(info, Edit.Info.Elem.IS_HARVESTED, IndexField.IS_HARVESTED);
         addElement(info, Edit.Info.Elem.DATAPOLICY, IndexField.DATAPOLICY);

         info.addContent(new Element(Edit.Info.Elem.SCORE).setText(String.valueOf(getScore())));

         // Owner
         String owner = (String) solrDoc.get(IndexField.OWNER);
         if (StringUtils.isNotBlank(owner)) {
            info.addContent(new Element(Edit.Info.Elem.OWNERNAME).setText(owner));
         }

         // isGlobal
         addElement(md, Edit.RootChild.IS_GLOBAL, IndexField.IS_GLOBAL);

         // Request link
         String requestUrl = (String) solrDoc.get(IndexField.REQUEST_URL);
         if (StringUtils.isNotBlank(requestUrl)) {
            Element lnkOpenwis = new Element(Edit.RootChild.LINK_OPENWIS);
            lnkOpenwis.setAttribute(Edit.ChildElem.Attr.ACTION, "Request");
            lnkOpenwis.setAttribute(Edit.ChildElem.Attr.HREF, requestUrl);
            md.addContent(lnkOpenwis);
         }
         // Subscription link
         String subscriptionUrl = (String) solrDoc.get(IndexField.REQUEST_URL);
         if (StringUtils.isNotBlank(requestUrl)) {
            Element lnkOpenwis = new Element(Edit.RootChild.LINK_OPENWIS);
            lnkOpenwis.setAttribute(Edit.ChildElem.Attr.ACTION, "Subscribe");
            lnkOpenwis.setAttribute(Edit.ChildElem.Attr.HREF, subscriptionUrl);
            md.addContent(lnkOpenwis);
         }
         elt = md;
      }
      return elt;
   }

   /**
    * Adds the element.
    *
    * @param element the element
    * @param xmlElement the xml element
    * @param field the field
    */
   private void addElement(Element element, String xmlElement, IndexField field) {
      Object fValue = getField(field);
      if (fValue != null) {
         String value = String.valueOf(fValue);
         Element childElt = new Element(xmlElement).setText(value);
         element.addContent(childElt);
      }
   }
}
