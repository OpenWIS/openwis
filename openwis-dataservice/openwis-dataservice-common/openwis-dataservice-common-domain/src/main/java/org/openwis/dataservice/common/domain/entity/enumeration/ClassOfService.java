
package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * Class of Service
 */
@XmlType(name = "classOfService")
@XmlEnum
public enum ClassOfService {

    SILVER,
    GOLD,
    BRONZE;

}
