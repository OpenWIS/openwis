/**
 * 
 */
package org.openwis.dataservice.common.domain.entity.request.dissemination;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
@XmlType(name = "disseminationZipMode")
@XmlEnum
public enum DisseminationZipMode {

    NONE,
    
    ZIPPED,
    
    WMO_FTP
}
