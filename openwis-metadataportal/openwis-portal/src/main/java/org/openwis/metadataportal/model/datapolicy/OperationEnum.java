/**
 *
 */
package org.openwis.metadataportal.model.datapolicy;

/**
 * A list of operations. <P>
 * Explanation goes here. <P>
 *
 */
public enum OperationEnum {
    VIEW(0),

    DOWNLOAD(1),

    EDITING(2),

    /*FEATURED(6),*/

    PUBLIC_EMAIL(7),

    PUBLIC_FTP(8),

    RMDCN_EMAIL(9),

    RMDCN_FTP(10),

    FTP_SECURED(11);

    /**
     * The level.
     */
    private final int id;

    /**
     * Default constructor.
     * Builds a OperationEnum.
     * @param id.
     */
    private OperationEnum(int id) {
        this.id = id;
    }

    /**
     * Returns the id.
     * @return the id.
     */
    public int getId() {
        return id;
    }

   /**
    * Gets the from id.
    *
    * @param id the id
    * @return the from id
    */
   public static OperationEnum getFromId(int id) {
      for (OperationEnum ope : OperationEnum.values()) {
         if (ope.id == id) {
            return ope;
         }
      }
      return null;
   }

}
