package org.openwis.dataservice.common.domain.entity.request.dissemination;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The shopping cart dissemination. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shoppingCartDissemination")

@Entity
@DiscriminatorValue(value = "SHOPPING_CARD")
public class ShoppingCartDissemination extends Dissemination {

   /**
    * Default constructor.
    */
   public ShoppingCartDissemination() {
      super();
   }

}
