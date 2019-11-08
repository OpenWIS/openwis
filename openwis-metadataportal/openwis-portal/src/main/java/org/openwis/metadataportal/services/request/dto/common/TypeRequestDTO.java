/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.common;

/**
 * The request type used in request module. <P>
 * A request can be of two types : Adhoc request or Subscription. <P>
 * 
 */
public enum TypeRequestDTO {
    /**
     * The Adhoc request type.
     */
    ADHOC,

    /**
     * The Subscription type.
     */
    SUBSCRIPTION,
    
    /**
     * The processed request type.
     */
    PROCESSED_REQUEST,
    
    /**
     * A routing for MSS/FSS
     */
    ROUTING
}
