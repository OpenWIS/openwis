package org.openwis.dataservice.common.domain.bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The Enum OperationAllowed.
 */
@XmlType(name = "Operation")
@XmlEnum
public enum Operation {

   /** The View. */
   View,

   /** The Editing. */
   Editing,

   /** The Download. */
   Download,

   /** The Public email. */
   PublicEmail,

   /** The Public FTP. */
   PublicFTP,

   /** The RMDCN email. */
   RMDCNEmail,

   /** The RMDCNFTP. */
   RMDCNFTP,

   /** The FTP secured. */
   FTPSecured
}
