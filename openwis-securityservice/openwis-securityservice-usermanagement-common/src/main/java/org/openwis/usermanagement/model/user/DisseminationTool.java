/**
 * 
 */
package org.openwis.usermanagement.model.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Dissemination Tool Value. <P>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "disseminationTool")
public enum DisseminationTool {

   /**
    * @member: RMDCN
    */
   RMDCN, 
   /**
    * @member: PUBLIC
    */
   PUBLIC;

}
