package org.openwis.dataservice.common.domain.entity.request.adhoc;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.domain.entity.request.Request;

/**
 * The adHoc entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adHoc")
@Entity
@DiscriminatorValue(value = "ADHOC")
public class AdHoc extends Request {

   /**
    * Default constructor.
    */
   public AdHoc() {
      super();
   }

}
