package org.openwis.harness.samples.db.localdatasource;

/**
 * The Interface SqlRequest. <P>
 * Contains parameterized SQL requests. <P>
 */
public interface SqlRequest {

   /** The Availability request. */
   String AVAILABILITY_FROM = "SELECT DISTINCT md.urn FROM product p, product_metadata md WHERE p.md_id=md.id AND p.product_timestamp>=?";

   /** The Availability request. */
   String AVAILABILITY = "SELECT DISTINCT md.urn FROM product_metadata md";

   /** The extract with timestamp interval. */
   String EXTRACT_BY_TIMESTAMP = "SELECT p.id, p.urn, p.product_timestamp "
         + "FROM product p, product_metadata md "
         + "WHERE p.md_id=md.id AND md.urn=? AND p.product_timestamp>=? AND p.product_timestamp<=?";

   /** The extract all. */
   String EXTRACT_ALL = "SELECT p.id, p.urn, p.product_timestamp "
         + "FROM product p, product_metadata md " + "WHERE p.md_id=md.id AND md.urn=? ";

}
