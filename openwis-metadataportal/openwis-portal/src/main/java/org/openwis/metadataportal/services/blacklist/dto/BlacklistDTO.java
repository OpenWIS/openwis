/**
 * BlacklistDTO
 */
package org.openwis.metadataportal.services.blacklist.dto;

import java.util.Arrays;
import java.util.List;

import org.openwis.dataservice.BlacklistInfo;
import org.openwis.dataservice.BlacklistStatus;
import org.openwis.metadataportal.services.requestsStatistics.dto.DataDisseminatedDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class BlacklistDTO implements Comparable<BlacklistDTO> {

   private List<BlacklistStatus> notBlacklisted = Arrays.asList(
         BlacklistStatus.NOT_BLACKLISTED_BY_ADMIN, BlacklistStatus.NOT_BLACKLISTED);

   private Long id;

   private String user;

   private long nbDisseminationWarnThreshold;

   private long volDisseminationWarnThreshold;

   private long nbDisseminationBlacklistThreshold;

   private long volDisseminationBlacklistThreshold;

   private DataDisseminatedDTO userDisseminatedDataDTO;

   private String status;

   private boolean blacklisted;

   public BlacklistDTO() {
      super();
   }

   public BlacklistDTO(BlacklistInfo blacklistInfo) {
      this.setId(blacklistInfo.getId());
      this.setUser(blacklistInfo.getUser());
      this.setNbDisseminationWarnThreshold(blacklistInfo.getNbDisseminationWarnThreshold());
      this.setVolDisseminationWarnThreshold(blacklistInfo.getVolDisseminationWarnThreshold());
      this.setNbDisseminationBlacklistThreshold(blacklistInfo.getNbDisseminationBlacklistThreshold());
      this.setVolDisseminationBlacklistThreshold(blacklistInfo.getVolDisseminationBlacklistThreshold());
      this.setStatus(blacklistInfo.getStatus().value());
      this.setBlacklisted(!this.notBlacklisted.contains(blacklistInfo.getStatus()));
   }

   /**
    * Gets the id.
    * @return the id.
    */
   public Long getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(Long id) {
      this.id = id;
   }

   /**
    * Gets the user.
    * @return the user.
    */
   public String getUser() {
      return user;
   }

   /**
    * Sets the user.
    * @param user the user to set.
    */
   public void setUser(String user) {
      this.user = user;
   }

   /**
    * Gets the nbDisseminationWarnThreshold.
    * @return the nbDisseminationWarnThreshold.
    */
   public long getNbDisseminationWarnThreshold() {
      return nbDisseminationWarnThreshold;
   }

   /**
    * Sets the nbDisseminationWarnThreshold.
    * @param nbDisseminationWarnThreshold the nbDisseminationWarnThreshold to set.
    */
   public void setNbDisseminationWarnThreshold(long nbDisseminationWarnThreshold) {
      this.nbDisseminationWarnThreshold = nbDisseminationWarnThreshold;
   }

   /**
    * Gets the volDisseminationWarnThreshold.
    * @return the volDisseminationWarnThreshold.
    */
   public long getVolDisseminationWarnThreshold() {
      return volDisseminationWarnThreshold;
   }

   /**
    * Sets the volDisseminationWarnThreshold.
    * @param volDisseminationWarnThreshold the volDisseminationWarnThreshold to set.
    */
   public void setVolDisseminationWarnThreshold(long volDisseminationWarnThreshold) {
      this.volDisseminationWarnThreshold = volDisseminationWarnThreshold;
   }

   /**
    * Gets the nbDisseminationBlacklistThreshold.
    * @return the nbDisseminationBlacklistThreshold.
    */
   public long getNbDisseminationBlacklistThreshold() {
      return nbDisseminationBlacklistThreshold;
   }

   /**
    * Sets the nbDisseminationBlacklistThreshold.
    * @param nbDisseminationBlacklistThreshold the nbDisseminationBlacklistThreshold to set.
    */
   public void setNbDisseminationBlacklistThreshold(long nbDisseminationBlacklistThreshold) {
      this.nbDisseminationBlacklistThreshold = nbDisseminationBlacklistThreshold;
   }

   /**
    * Gets the volDisseminationBlacklistThreshold.
    * @return the volDisseminationBlacklistThreshold.
    */
   public long getVolDisseminationBlacklistThreshold() {
      return volDisseminationBlacklistThreshold;
   }

   /**
    * Sets the volDisseminationBlacklistThreshold.
    * @param volDisseminationBlacklistThreshold the volDisseminationBlacklistThreshold to set.
    */
   public void setVolDisseminationBlacklistThreshold(long volDisseminationBlacklistThreshold) {
      this.volDisseminationBlacklistThreshold = volDisseminationBlacklistThreshold;
   }

   /**
    * Gets the userDisseminatedDataDTO.
    * @return the userDisseminatedDataDTO.
    */
   public DataDisseminatedDTO getUserDisseminatedDataDTO() {
      return userDisseminatedDataDTO;
   }

   /**
    * Sets the userDisseminatedDataDTO.
    * @param userDisseminatedDataDTO the userDisseminatedDataDTO to set.
    */
   public void setUserDisseminatedDataDTO(DataDisseminatedDTO userDisseminatedDataDTO) {
      this.userDisseminatedDataDTO = userDisseminatedDataDTO;
   }

   /**
    * Gets the status.
    * @return the status.
    */
   public String getStatus() {
      return status;
   }

   /**
    * Sets the status.
    * @param status the status to set.
    */
   public void setStatus(String status) {
      this.status = status;
   }

   /**
    * Gets the blacklisted.
    * @return the blacklisted.
    */
   public boolean isBlacklisted() {
      return blacklisted;
   }

   /**
    * Sets the blacklisted.
    * @param blacklisted the blacklisted to set.
    */
   public void setBlacklisted(boolean blacklisted) {
      this.blacklisted = blacklisted;
   }

   @Override
   public int compareTo(BlacklistDTO o) {
      return (new Boolean(this.isBlacklisted())).compareTo(new Boolean(o.isBlacklisted()));
   }

}
