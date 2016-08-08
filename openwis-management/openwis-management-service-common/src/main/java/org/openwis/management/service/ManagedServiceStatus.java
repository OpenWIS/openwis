package org.openwis.management.service;

/**
 * Maps service status values to token strings.
 * <p>
 * Enumerated item value to use in the
 * {@link ControlService#setServiceStatus(ManagedServiceIdentifier, ManagedServiceStatus)}
 */
public enum ManagedServiceStatus {
   /**
    * The service status is unknown. <br>
    * This may indicate that the service is not reachable or that the service
    * status was never set.
    */
   UNKNOWN,
   /**
    * The service is up and operational.
    */
   ENABLED,
   /**
    * The service is up and not operational.
    */
   DISABLED,
   /**
    * The service is up and partly operational.
    */
   DEGRADED;
};
