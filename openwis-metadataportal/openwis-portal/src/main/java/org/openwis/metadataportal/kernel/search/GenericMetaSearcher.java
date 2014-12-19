package org.openwis.metadataportal.kernel.search;

import java.util.ArrayList;
import java.util.List;

import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Geonet.SearchResult.SortBy;
import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.MetaSearcher;
import org.fao.geonet.kernel.search.SortingInfo;
import org.fao.geonet.kernel.search.SortingInfoImpl;
import org.fao.geonet.services.util.SearchDefaults;
import org.jdom.Element;
import org.openwis.metadataportal.common.configuration.OpenwisSearchConfig;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.search.query.IQueryManager;
import org.openwis.metadataportal.kernel.search.query.SearchException;
import org.openwis.metadataportal.kernel.search.query.SearchQuery;
import org.openwis.metadataportal.kernel.search.query.SearchQueryFactory;
import org.openwis.metadataportal.kernel.search.query.SearchQueryManagerFactory;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResultDocument;

/**
 * The Class GenericMetaSearcher. <P>
 * Explanation goes here. <P>
 */
public class GenericMetaSearcher<T extends SearchQuery> extends MetaSearcher {

   /** The query manager factory. */
   private final SearchQueryManagerFactory<T> queryManagerFactory;

   /** The result. */
   private SearchResult result;

   /**
    * Instantiates a new generic meta searcher.
    *
    * @param queryManagerFactory the query manager factory
    */
   public GenericMetaSearcher(SearchQueryManagerFactory<T> queryManagerFactory) {
      super();
      this.queryManagerFactory = queryManagerFactory;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.MetaSearcher#search(jeeves.server.context.ServiceContext, org.jdom.Element, jeeves.server.ServiceConfig)
    */
   @Override
   public void search(ServiceContext srvContext, Element request, ServiceConfig config)
         throws Exception {
      if (Log.isDebug(Geonet.SEARCH_ENGINE)) {
         Log.debug(Geonet.SEARCH_ENGINE, "Search with request:\n" + Xml.getString(request));
      }
      // from and to values
      int hitsPerPage = this.getHitsPerPage(srvContext, request);
      int from = this.getFrom(request);
      int to = this.getTo(request, hitsPerPage);

      // Manager and query factory
      IQueryManager<T> queryManager = getQueryManager();
      SearchQueryFactory<T> queryFactory = queryManager.getQueryFactory();

      // Create query
      T query = buildQuery(queryFactory, request);
      if (Log.isInfo(Geonet.SEARCH_ENGINE)) {
         Log.info(Geonet.SEARCH_ENGINE, "Search query: " + query);
      }

      // spatial
      query = queryFactory.addSpatialQuery(query, request, null);

      // Sorting
      SortingInfo sort = new SortingInfoImpl();
      String sortBy = request.getChildText("sortBy");
      if (SortBy.DATE.equals(sortBy)) {
         sort.add(IndexField._CHANGE_DATE, SortDir.ASC);
      } else if (SortBy.POPULARITY.equals(sortBy)) {
         sort.add(IndexField.POPULARITY, SortDir.ASC);
      } else if (SortBy.RATING.equals(sortBy)) {
         sort.add(IndexField.RATING, SortDir.ASC);
      } else if (SortBy.TITLE.equals(sortBy)) {
         sort.add(IndexField._TITLE, SortDir.ASC);
      }
      sort.add(IndexField.SCORE, SortDir.DESC);
      query.setSortFields(sort);

      // Define scope
      query.setRange(from, to);
      query.setHitsPerPage(hitsPerPage);

      result = queryManager.search(query);
   }

   /**
    * Gets the result.
    *
    * @return the result
    */
   public SearchResult getResult() {
      return result;
   }

   /**
    * Gets the hits per page.
    *
    * @param srvContext the context
    * @param request the request
    * @return the hits per page
    */
   private int getHitsPerPage(ServiceContext srvContext, Element request) {
      int hitsPerPage;
      try {
         String hit = request.getChildText("hitsPerPage");
         hitsPerPage = Integer.valueOf(hit);
      } catch (Exception e) {
         UserSession session = null;
         if (srvContext != null) {
            session = srvContext.getUserSession();
         }
         Element defaultSearch = SearchDefaults.getDefaultSearch(session, null);
         String defaultHits = defaultSearch.getChildText(Geonet.SearchResult.HITS_PER_PAGE);
         if (StringUtils.isNumeric(defaultHits) && !defaultHits.isEmpty()) {
            hitsPerPage = Integer.valueOf(defaultHits);
         } else {
            hitsPerPage = 10;
         }
      }
      return hitsPerPage;
   }

   /**
    * Gets the from.
    *
    * @param request the request
    * @return the from
    */
   private int getFrom(Element request) {
      int from;
      try {
         String sFrom = request.getChildText("from");
         from = Integer.valueOf(sFrom);
      } catch (Exception e) {
         from = 0;
      }
      return from;
   }

   /**
    * Gets the to.
    *
    * @param request the request
    * @param hitsPerPage the hits per page
    * @return the to
    */
   private int getTo(Element request, int hitsPerPage) {
      int to;
      try {
         String sTo = request.getChildText("to");
         to = Integer.valueOf(sTo);
      } catch (Exception e) {
         to = hitsPerPage - 1;
      }
      return to;
   }

   /**
    * Gets the query manager.
    *
    * @return the query manager
    * @throws SearchException the search exception
    */
   protected IQueryManager<T> getQueryManager() throws SearchException {
      return this.queryManagerFactory.buildIQueryManager();
   }

   /**
    * Builds the query.
    *
    * @param queryFactory the query factory
    * @param request the request
    * @return the search query
    */
   protected T buildQuery(SearchQueryFactory<T> queryFactory, Element request) {
      T query = null;

      // Similarity
      float similarity = 1.0F;
      try {
         String sim = request.getChildText("similarity");
         if (StringUtils.isNotBlank(sim)) {
            similarity = Float.valueOf(sim);
         }
      } catch (NumberFormatException e) {
         Log.warning(Geonet.SEARCH_ENGINE, "Invalid similiarity !");
      }

      // any
      query = buildAnyQueries(queryFactory, request, query, similarity);

      // isTemplate
      String isTemplate;
      if ("template".equals(request.getChildText("kind"))) {
         isTemplate = "y";
      } else {
         isTemplate = "n";
      }
      query = queryFactory.and(query, queryFactory.buildQuery(IndexField.IS_TEMPLATE, isTemplate));

      // permanent!!!
      String permanentLink = request.getChildText("permanentLink");
      String value = request.getChildText("any");
      if ("true".equals(permanentLink)) {
         value = queryFactory.escapeQueryChars(value);
         query = queryFactory.and(query, queryFactory.buildQuery(IndexField.UUID, value));
      }
      // !!!
      
      // Category
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.CATEGORY_ID, "category", request, 1.0F));

      // uuid
      query = queryFactory.or(query,
            this.buildQuery(queryFactory, IndexField.UUID, "uuid", request, similarity));

      // title
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.TITLE, "title", request, similarity));

      // abstract
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.ABSTRACT, "abstract", request, similarity));

      // digital and paper maps
      String digital = request.getChildText("digital");
      String paper = request.getChildText("paper");

      // if both are off or both are on then no clauses are added
      if (StringUtils.isNotBlank(digital) && "on".equals(digital)
            && (!StringUtils.isNotBlank(paper) || "off".equals(paper))) {
         query = queryFactory.and(query, queryFactory.buildQuery(IndexField.DIGITAL, "true"));
      }

      if (StringUtils.isNotBlank(paper) && "on".equals(paper)
            && (!StringUtils.isNotBlank(digital) || "off".equals(digital))) {
         query = queryFactory.and(query, queryFactory.buildQuery(IndexField.PAPER, "true"));
      }

      // siteId
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.SOURCE, "siteId", request, 1F));

      // type
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.TYPE, "type", request, similarity));

      // serviceType
      query = queryFactory.and(query, this.buildQuery(queryFactory, IndexField.SERVICE_TYPE,
            "serviceType", request, similarity));

      // operatesOn
      query = queryFactory
            .and(query, this.buildQuery(queryFactory, IndexField.OPERATESON, "operatesOn", request,
                  similarity));

      // ParentUUID
      query = queryFactory
            .and(query, this.buildQuery(queryFactory, IndexField.PARENTUUID, "parentUuid", request,
                  similarity));

      // Schema
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.SCHEMA, "_schema", request, similarity));

      // Metadata Standard Name
      query = queryFactory.and(query, this.buildQuery(queryFactory,
            IndexField.METADATA_STANDARD_NAME, "metadataStandardName", request, similarity));

      // Categories
      @SuppressWarnings("unchecked")
      List<Element> categories = request.getChildren("category");
      if (categories != null && (categories.isEmpty())) {
         T catQuery = null;
         for (Element category : categories) {
            catQuery = queryFactory.or(catQuery,
                  queryFactory.buildQuery(IndexField.CATEGORY_ID, category.getText()));
         }
         query = queryFactory.and(query, catQuery);
      }

      // Protocol
      query = buildProtocolQueries(queryFactory, request, query, similarity);

      // topic-category
      @SuppressWarnings("unchecked")
      List<Element> isoTopicCategories = request.getChildren("topic-category");
      if (isoTopicCategories != null && !isoTopicCategories.isEmpty()) {
         T topicCatQuery = null;

         T currentQuery;
         for (Element topicCatElt : isoTopicCategories) {
            String isoTopicCategory = topicCatElt.getText();
            isoTopicCategory = isoTopicCategory.trim();
            if (isoTopicCategory.length() > 0) {
               if (isoTopicCategory.endsWith("*")) {
                  isoTopicCategory = isoTopicCategory.substring(0, isoTopicCategory.length() - 1);
               }
               currentQuery = queryFactory.buildQuery(IndexField.TOPIC_CATEGORY,
                     queryFactory.escapeQueryChars(isoTopicCategory) + '*');
               topicCatQuery = queryFactory.or(topicCatQuery, currentQuery);
            }
         }
         query = queryFactory.and(query, topicCatQuery);
      }

      // Inspire
      query = buildInspireQuery(queryFactory, request, query, similarity);

      // Date
      query = queryFactory.and(
            query,
            addDateRangeQuery(queryFactory, request.getChildText("dateFrom"),
                  request.getChildText("dateTo"), IndexField.CHANGE_DATE));

      // revisionDate
      query = queryFactory.and(
            query,
            addDateRangeQuery(queryFactory, request.getChildText("revisionDateFrom"),
                  request.getChildText("revisionDateTo"), IndexField.REVISION_DATE));

      // publicationDate
      query = queryFactory.and(
            query,
            addDateRangeQuery(queryFactory, request.getChildText("publicationDateFrom"),
                  request.getChildText("publicationDateTo"), IndexField.PUBLICATION_DATE));

      // creationDate
      query = queryFactory.and(
            query,
            addDateRangeQuery(queryFactory, request.getChildText("creationDateFrom"),
                  request.getChildText("creationDateTo"), IndexField.CREATE_DATE));

      // Extend date
      query = queryFactory.and(
            query,
            addTemporalExtendDateRangeQuery(queryFactory, request.getChildText("extFrom"),
                  request.getChildText("extTo")));

      return query;
   }

   /**
    * Adds the temporal extend date range query.
    *
    * @param queryFactory the query factory
    * @param start the start
    * @param stop the stop
    * @return the t
    */
   private T addTemporalExtendDateRangeQuery(SearchQueryFactory<T> queryFactory, String start,
         String stop) {
      T tempQuery = null;
      IndexField from = IndexField.TEMPORALEXTENT_BEGIN;
      IndexField to = IndexField.TEMPORALEXTENT_END;

      if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(stop)) {
         // (From < stop) & (To > stop))
         T qFrom = queryFactory.or(queryFactory.buildFieldNotPresentQuery(from),
               queryFactory.buildBeforeQuery(from, stop));
         T qTo = queryFactory.or(queryFactory.buildFieldNotPresentQuery(to),
               queryFactory.buildAfterQuery(to, start));
         tempQuery = queryFactory.and(qFrom, qTo);
      } else if (StringUtils.isNotBlank(start)) {
         tempQuery = queryFactory.buildAfterQuery(from, stop);
      } else if (StringUtils.isNotBlank(stop)) {
         tempQuery = queryFactory.buildBeforeQuery(to, start);
      }
      return tempQuery;
   }

   /**
    * Return the date range query.
    *
    * @param queryFactory the query factory
    * @param from the from
    * @param to the to
    * @param field the field
    * @return the date query
    */
   private T addDateRangeQuery(SearchQueryFactory<T> queryFactory, String from, String to,
         IndexField field) {
      T dateQuery = null;

      if (StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
         dateQuery = queryFactory.buildBetweenQuery(field, from, to);
      } else if (StringUtils.isNotBlank(from)) {
         dateQuery = queryFactory.buildAfterQuery(field, from);
      } else if (StringUtils.isNotBlank(to)) {
         dateQuery = queryFactory.buildBeforeQuery(field, to);
      }

      return dateQuery;
   }

   /**
    * Builds the protocol queries.
    *
    * @param queryFactory the query factory
    * @param request the request
    * @param query the query
    * @param similarity the similarity
    * @return the t
    */
   private T buildProtocolQueries(SearchQueryFactory<T> queryFactory, Element request, T q,
         float similarity) {
      T query = q;
      // Protocol
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.PROTOCOL, "protocol", request, 1F));

      // dynamic
      String dynamic = request.getChildText("dynamic");
      if ("on".equals(dynamic)) {
         T dynamicQuery = null;
         dynamicQuery = queryFactory.or(
               dynamicQuery,
               queryFactory.buildQuery(
                     IndexField.PROTOCOL,
                     queryFactory.escapeQueryChars("OGC:WMS-") + '*'
                           + queryFactory.escapeQueryChars("-get-map")));
         dynamicQuery = queryFactory.or(
               dynamicQuery,
               queryFactory.buildQuery(
                     IndexField.PROTOCOL,
                     queryFactory.escapeQueryChars("OGC:WMS-") + '*'
                           + queryFactory.escapeQueryChars("-get-capabilities")));
         dynamicQuery = queryFactory.or(
               dynamicQuery,
               queryFactory.buildQuery(
                     IndexField.PROTOCOL,
                     queryFactory.escapeQueryChars("ESRI:AIMS-") + '*'
                           + queryFactory.escapeQueryChars("-get-image")));

         query = queryFactory.and(query, dynamicQuery);
      }

      // download
      String download = request.getChildText("download");
      if ("on".equals(download)) {
         query = queryFactory.and(
               query,
               queryFactory.buildQuery(
                     IndexField.PROTOCOL,
                     queryFactory.escapeQueryChars("WWW:DOWNLOAD-") + '*'
                           + queryFactory.escapeQueryChars("--download")));
      }
      return query;
   }

   /**
    * Builds the any queries.
    *
    * @param queryFactory the query factory
    * @param request the request
    * @param query the query
    * @param similarity the similarity
    * @return the t
    */
   private T buildAnyQueries(SearchQueryFactory<T> queryFactory, Element request, T q,
         float similarity) {
      T query = q;
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.ANYTEXT, "any", request, similarity));
      // all
      query = queryFactory.and(query,
            this.buildQuery(queryFactory, IndexField.ANYTEXT, "all", request, similarity));

      // pondération du title, de l'abstract et des mots-clefs en utilisant la methode or
      query = queryFactory.or(query, this.buildQuery(queryFactory, IndexField.TITLE, "any",
            request, similarity, true, OpenwisSearchConfig.getTitleWeight()));
      query = queryFactory.or(query, this.buildQuery(queryFactory, IndexField.ABSTRACT, "any",
            request, similarity, true, OpenwisSearchConfig.getAbstractWeight()));
      query = queryFactory.or(query, this.buildQuery(queryFactory, IndexField.KEYWORD, "any",
            request, similarity, true, OpenwisSearchConfig.getKeywordsWeight()));
      
      // or
      query = queryFactory.or(query,
            this.buildOrQuery(queryFactory, IndexField.ANYTEXT, "or", request, similarity));
      // phrase
      query = queryFactory
            .and(query, this.buildQuery(queryFactory, IndexField.ANYTEXT, "phrase", request,
                  similarity, false));
      // without
      query = queryFactory.and(query, queryFactory.not(this.buildQuery(queryFactory,
            IndexField.ANYTEXT, "without", request, similarity)));
      return query;
   }

   /**
    * Builds the query.
    *
    * @param queryFactory the query factory
    * @param field the field
    * @param xmlElement the string
    * @param request the request
    * @param similarity the similarity
    * @return the t
    */
   private T buildQuery(SearchQueryFactory<T> queryFactory, IndexField field, String xmlElement,
         Element request, float similarity) {
      return buildQuery(queryFactory, field, xmlElement, request, similarity, true);
   }

   /**
    * Builds the inspire query.
    *
    * @param queryFactory the query factory
    * @param request the request
    * @param query the query
    * @param similarity the similarity
    * @return the t
    */
   private T buildInspireQuery(SearchQueryFactory<T> queryFactory, Element request, T query,
         float similarity) {
      T result = query;

      // inspire only
      String inspireOnly = request.getChildText("inspireOnly");
      if ("on".equals(inspireOnly)) {
         result = queryFactory.and(result, queryFactory.buildQuery(IndexField.INSPIRE_CAT, "true"));
      }

      // inspireAnnex
      result = queryFactory.and(result, this.buildQuery(queryFactory, IndexField.INSPIRE_ANNEX,
 "inspireAnnex", request, 1F));

      // inspireTheme
      @SuppressWarnings("unchecked")
      List<Element> inspireThemes = request.getChildren("inspiretheme");
      if (inspireThemes != null && !inspireThemes.isEmpty()) {
         T inspireThemeQuery = null;
         String theme;
         for (Element elt : inspireThemes) {
            theme = elt.getText();
            inspireThemeQuery = queryFactory.or(inspireThemeQuery,
                  queryFactory.buildQuery(IndexField.INSPIRE_THEME, theme));
         }
         result = queryFactory.and(result, inspireThemeQuery);
      }

      // themekey
      @SuppressWarnings("unchecked")
      List<Element> themeKeys = request.getChildren("themekey");
      if (themeKeys != null && !themeKeys.isEmpty()) {
         T themeKeyQuery = null;
         String themeKey;
         String[] tokens;
         for (Element elt : themeKeys) {
            themeKey = elt.getText();
            tokens = StringUtils.split(themeKey, "|");
            for (String token : tokens) {
               token = token.trim();
               if (token.startsWith("\"")) {
                  token = token.substring(1);
               }
               if (token.endsWith("\"")) {
                  token = token.substring(0, token.length() - 1);
               }
               themeKeyQuery = queryFactory.or(
                     themeKeyQuery,
                     queryFactory.buildQuery(IndexField.KEYWORD,
                           queryFactory.escapeQueryChars(token)));
            }
         }
         result = queryFactory.and(result, themeKeyQuery);
      }
      // Handle empty query
      if (result == null) {
         result = queryFactory.buildAll();
      }
      return result;
   }

   /**
    * Filtrer les mots de type stopwords configurés dans openwis-search.properties.
    */
   private String[] filterStopWords(String[] split) {
      ArrayList<String> filteredWords = new ArrayList<String>();
      for (String s : split) {
         if (!OpenwisSearchConfig.isInStopWords(s)) {
            filteredWords.add(s);
         }
      }
      return (String[]) filteredWords.toArray(new String[filteredWords.size()]);
   }

   //on rajoute une methode buildQuery avec le parametre de boost
   private T buildQuery(SearchQueryFactory<T> queryFactory, IndexField field, String xmlElement,
         Element request, float similarity, boolean splitText, int boostFactor) {
      T query = null;
      T fuzzy;
      String value = request.getChildText(xmlElement);
      if (StringUtils.isNotBlank(value)) {
         // filtre stopWords
         String[] split = filterStopWords(StringUtils.split(value));
         if (!splitText || (split.length == 0)) {
            // Assume that text analyze is provided by Solr
            query = queryFactory.buildQuery(field, queryFactory.escapeQueryChars(value));
            if (1.0F != similarity) {
               // Fuzzy
               fuzzy = queryFactory.fuzzy(query, similarity);
               query = queryFactory.or(query, fuzzy);
            }
         } else {
            for (String arg : split) {
               T query2 = queryFactory.buildQuery(field, queryFactory.escapeQueryChars(arg));
               if (1.0F != similarity) {
                  // Fuzzy
                  fuzzy = queryFactory.fuzzy(query2, similarity);
                  query2 = queryFactory.or(query2, fuzzy);
               }
               query = queryFactory.and(query, query2);
            }
         }
         
         // gestion du parametre de boost et fabrication de la query
         if (boostFactor > 0) {
            query = queryFactory.boost(query, boostFactor);
         }
      }
      return query;
   }
   /**
    * Builds the query.
    *
    * @param queryFactory the query factory
    * @param field the field
    * @param xmlElement the xml element
    * @param request the request
    * @param similarity the similarity
    * @return the built query
    */
   private T buildQuery(SearchQueryFactory<T> queryFactory, IndexField field, String xmlElement,
         Element request, float similarity, boolean splitText) {
      return this.buildQuery(queryFactory, field, xmlElement, request, similarity, splitText, -1);
   }

   /**
    * Builds the or query.
    *
    * @param queryFactory the query factory
    * @param field the field
    * @param xmlElement the xml element
    * @param request the request
    * @param similarity the similarity
    * @return the t
    */
   private T buildOrQuery(SearchQueryFactory<T> queryFactory, IndexField field, String xmlElement,
         Element request, float similarity) {
      return this.buildOrQuery(queryFactory, field, xmlElement, request, similarity, -1);
   }
   // la methode qui suit prend desormais le boost en parametre mais n est pas appelee par buildAnyQueries lors qu'on rajoute les boost
   private T buildOrQuery(SearchQueryFactory<T> queryFactory, IndexField field, String xmlElement,
         Element request, float similarity, int boostFactor) {
      T query = null;
      T fuzzy;
      String value = request.getChildText(xmlElement);
      if (StringUtils.isNotBlank(value)) {
         String[] split = StringUtils.split(value);
         if (split.length == 0) {
            // Assume that text analyze is provided by Solr
            query = queryFactory.buildQuery(field, queryFactory.escapeQueryChars(value));
            if (1.0F != similarity) {
               // Fuzzy
               fuzzy = queryFactory.fuzzy(query, similarity);
               query = queryFactory.or(query, fuzzy);
            }
         } else {
            for (String arg : split) {
               T query2 = queryFactory.buildQuery(field, queryFactory.escapeQueryChars(arg));
               if (1.0F != similarity) {
                  // Fuzzy
                  fuzzy = queryFactory.fuzzy(query2, similarity);
                  query2 = queryFactory.or(query2, fuzzy);
               }
               query = queryFactory.or(query, query2);
            }
         }
         
         // gestion du parametre de boost et fabrication de la query
         if (boostFactor > 0) {
            query = queryFactory.boost(query, boostFactor);
         }
      }
      return query;
   }
   
   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.MetaSearcher#present(jeeves.server.context.ServiceContext, org.jdom.Element, jeeves.server.ServiceConfig)
    */
   @Override
   public Element present(ServiceContext srvContext, Element request, ServiceConfig config)
         throws Exception {
      Element presentElt = null;
      if (result != null) {
         presentElt = result.toPresent();
      } else {
         Log.warning(Geonet.SEARCH_ENGINE, "No search result !");
      }

      if (Log.isDebug(Geonet.SEARCH_ENGINE)) {
         Log.debug(Geonet.SEARCH_ENGINE, "Search result (present)\n" + Xml.getString(presentElt));
      }
      return presentElt;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.MetaSearcher#getSize()
    */
   @Override
   public int getSize() {
      int size = 0;
      if (result != null) {
         size = result.getCount();
      } else {
         Log.warning(Geonet.SEARCH_ENGINE, "No search result !");
      }
      return size;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.MetaSearcher#getSummary()
    */
   @Override
   public Element getSummary() throws Exception {
      Element summaryElt = null;
      if (result != null) {
         summaryElt = result.toSummary(ResultType.HITS);
      } else {
         Log.warning(Geonet.SEARCH_ENGINE, "No search result !");
      }

      if (Log.isDebug(Geonet.SEARCH_ENGINE)) {
         Log.debug(Geonet.SEARCH_ENGINE, "Search result (summary)\n" + Xml.getString(summaryElt));
      }
      return summaryElt;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.MetaSearcher#close()
    */
   @Override
   public void close() {
      // Clear context
      result = null;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.MetaSearcher#getAllUuids(int)
    */
   @Override
   public List<String> getAllUuids(int maxhits) throws Exception {
      List<String> result = new ArrayList<String>();

      IQueryManager<T> queryManager = getQueryManager();
      SearchQueryFactory<T> queryFactory = queryManager.getQueryFactory();
      T query = queryFactory.buildTermQuery(IndexField.UUID, null, maxhits, 0);

      SearchResult searchResult = queryManager.search(query);

      for (SearchResultDocument doc : searchResult) {
         result.add((String) doc.getField(IndexField.TERM));
      }

      return result;
   }

}
