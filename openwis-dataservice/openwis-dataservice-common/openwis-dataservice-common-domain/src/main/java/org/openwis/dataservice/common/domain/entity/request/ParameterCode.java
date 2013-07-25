/**
 *
 */
package org.openwis.dataservice.common.domain.entity.request;

/**
 * Defines the possible values for the {@code Parameter} {@link Parameter#getCode() code} property.
 */
public interface ParameterCode {

   /**
    * Represents a date-time interval specification. <br>
    * The value of the corresponding parameter is expected to have the format:
    * <tt>HH:mm:ss'Z'/HH:mm:ss'Z'</tt>.
    */
   String TIME_INTERVAL = "parameter.time.interval";

   /**
    * Represents a date-time interval specification. <br>
    * The value of the corresponding parameter is expected to have the format:
    * <tt>yyyy-MM-dd'T'HH:mm:ss'Z'/yyyy-MM-dd'T'HH:mm:ss'Z'</tt>.
    */
   String DATE_TIME_INTERVAL = "parameter.date.interval";

   /**
    * Represents an identifier of a (cached) product.
    * The value of the corresponding parameter is expected to be an integer mapping to a cached file entry.
    */
   String PRODUCT_ID = "parameter.product.id";
}
