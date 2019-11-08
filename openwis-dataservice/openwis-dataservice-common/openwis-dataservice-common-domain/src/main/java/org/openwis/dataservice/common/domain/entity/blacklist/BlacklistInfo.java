package org.openwis.dataservice.common.domain.entity.blacklist;

import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.domain.entity.enumeration.BlacklistStatus;

/**
 * The Class BlackListInfo.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "blacklistInfo")
@Entity
@Table(name = "OPENWIS_BLACKLIST_INFO")
@SequenceGenerator(name = "BLACKLIST_INFO_GEN", sequenceName = "BLACKLIST_INFO_SEQ", initialValue = 1, allocationSize = 1)
@NamedQueries({
      @NamedQuery(name = "BlacklistInfo.FindByUser", query = "FROM BlacklistInfo bi WHERE bi.user = :user)"),
      @NamedQuery(name = "BlacklistInfo.countByUser", query = "SELECT COUNT(bi) FROM BlacklistInfo bi WHERE bi.user LIKE :user)"),
      @NamedQuery(name = "BlacklistInfo.count", query = "SELECT COUNT(bi) FROM BlacklistInfo bi")})
public class BlacklistInfo {

   /** The id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BLACKLIST_INFO_GEN")
   @Column(name = "BLACKLIST_INFO_ID")
   private Long id;

   /** The user. */
   @Column(name = "USER_ID", unique = true, nullable = false)
   private String user;

   /** The number dissemination warn threshold. */
   @Column(name = "NB_DISS_WARN", nullable = false)
   private long nbDisseminationWarnThreshold;

   /** The number dissemination blacklist threshold. */
   @Column(name = "NB_DISS_BLACKLIST", nullable = false)
   private long nbDisseminationBlacklistThreshold;

   /** The volume dissemination warn threshold. */
   @Column(name = "VOL_DISS_WARN", nullable = false)
   private long volDisseminationWarnThreshold;

   /** The volume dissemination blacklist threshold. */
   @Column(name = "VOL_DISS_BLACKLIST", nullable = false)
   private long volDisseminationBlacklistThreshold;

   /** The status. */
   @Enumerated(EnumType.STRING)
   @Column(name = "STATUS")
   private BlacklistStatus status;

   /**
    * Instantiates a new black list info.
    */
   public BlacklistInfo() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[Blacklist] {0}\tNumber {1}/{2}\tVolume {3}/{4}", user,
            nbDisseminationWarnThreshold, nbDisseminationBlacklistThreshold,
            volDisseminationWarnThreshold, volDisseminationBlacklistThreshold);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((user == null) ? 0 : user.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof BlacklistInfo)) {
         return false;
      }
      BlacklistInfo other = (BlacklistInfo) obj;
      if (user == null) {
         if (other.user != null) {
            return false;
         }
      } else if (!user.equals(other.user)) {
         return false;
      }
      return true;
   }

   /**
    * Gets the id.
    *
    * @return the id
    */
   public Long getId() {
      return id;
   }

   /**
    * Sets the id.
    *
    * @param id the new id
    */
   public void setId(Long id) {
      this.id = id;
   }

   /**
    * Gets the user.
    *
    * @return the user
    */
   public String getUser() {
      return user;
   }

   /**
    * Sets the user.
    *
    * @param user the new user
    */
   public void setUser(String user) {
      this.user = user;
   }

   /**
    * Gets the number dissemination warn threshold.
    *
    * @return the number dissemination warn threshold
    */
   public long getNbDisseminationWarnThreshold() {
      return nbDisseminationWarnThreshold;
   }

   /**
    * Sets the number dissemination warn threshold.
    *
    * @param nbDisseminationWarnThreshold the new number dissemination warn threshold
    */
   public void setNbDisseminationWarnThreshold(long nbDisseminationWarnThreshold) {
      this.nbDisseminationWarnThreshold = nbDisseminationWarnThreshold;
   }

   /**
    * Gets the number dissemination blacklist threshold.
    *
    * @return the number dissemination blacklist threshold
    */
   public long getNbDisseminationBlacklistThreshold() {
      return nbDisseminationBlacklistThreshold;
   }

   /**
    * Sets the number dissemination blacklist threshold.
    *
    * @param nbDisseminationBlacklistThreshold the new number dissemination blacklist threshold
    */
   public void setNbDisseminationBlacklistThreshold(long nbDisseminationBlacklistThreshold) {
      this.nbDisseminationBlacklistThreshold = nbDisseminationBlacklistThreshold;
   }

   /**
    * Gets the volume dissemination warn threshold.
    *
    * @return the volume dissemination warn threshold
    */
   public long getVolDisseminationWarnThreshold() {
      return volDisseminationWarnThreshold;
   }

   /**
    * Sets the volume dissemination warn threshold.
    *
    * @param volumeDisseminationWarnThreshold the new volume dissemination warn threshold
    */
   public void setVolDisseminationWarnThreshold(long volDisseminationWarnThreshold) {
      this.volDisseminationWarnThreshold = volDisseminationWarnThreshold;
   }

   /**
    * Gets the volume dissemination blacklist threshold.
    *
    * @return the volume dissemination blacklist threshold
    */
   public long getVolDisseminationBlacklistThreshold() {
      return volDisseminationBlacklistThreshold;
   }

   /**
    * Sets the volume dissemination blacklist threshold.
    *
    * @param volDisseminationBlacklistThreshold the new volume dissemination blacklist threshold
    */
   public void setVolDisseminationBlacklistThreshold(long volDisseminationBlacklistThreshold) {
      this.volDisseminationBlacklistThreshold = volDisseminationBlacklistThreshold;
   }

   /**
    * Gets the status.
    *
    * @return the status
    */
   public BlacklistStatus getStatus() {
      return status;
   }

   /**
    * Sets the status.
    *
    * @param status the new status
    */
   public void setStatus(BlacklistStatus status) {
      this.status = status;
   }

}
