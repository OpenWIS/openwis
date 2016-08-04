package org.openwis.dataservice.common.domain.entity.subscription;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The event based frequency entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventBasedFrequency")

@Entity
@DiscriminatorValue(value = "EVENT_BASED")
public class EventBasedFrequency extends Frequency {

   /**
    * Default constructor.
    */
   public EventBasedFrequency() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "OnProductArrival";
   }

}
