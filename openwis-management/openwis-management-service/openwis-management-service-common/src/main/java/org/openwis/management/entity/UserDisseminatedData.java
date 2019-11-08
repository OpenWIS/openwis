/**
 *
 */
package org.openwis.management.entity;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Volume of data disseminated per day and per user. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userDisseminationData")
@Entity
@Table(name = "OPENWIS_DISSEMINATED_DATA", uniqueConstraints={@UniqueConstraint(columnNames = {"DATE", "USER_ID"})})
@SequenceGenerator(name = "DISSEMINATED_GEN", sequenceName = "DISSEMINATED_SEQ", initialValue = 1, allocationSize = 1)
@NamedQueries({
      @NamedQuery(name = "UserDisseminatedData.getByUserAndDate", query = "FROM UserDisseminatedData WHERE userId= :user AND date = :date"),
      @NamedQuery(name = "UserDisseminatedData.getByUserBetweenDate", query = "SELECT SUM(uds.nbFiles) AS nbFiles, SUM(uds.size) AS totalSize, SUM(uds.dissToolNbFiles) AS dissToolNbFiles, SUM(uds.dissToolSize) AS dissToolSize FROM UserDisseminatedData uds WHERE uds.userId= :user AND uds.date BETWEEN :from AND :to"),
      @NamedQuery(name = "UserDisseminatedData.getByDate", query = "FROM UserDisseminatedData WHERE date = :date"),
      @NamedQuery(name = "UserDisseminatedData.countByUser", query = "SELECT COUNT(udd) FROM UserDisseminatedData udd WHERE udd.userId LIKE :user "),
      @NamedQuery(name = "DisseminatedData.getByDate", query = "SELECT SUM(uds.nbFiles) AS nbFiles, SUM(uds.size) AS totalSize, SUM(uds.dissToolNbFiles) AS dissToolNbFiles, SUM(uds.dissToolSize) AS dissToolSize FROM UserDisseminatedData uds WHERE uds.date = :date"),
      @NamedQuery(name = "DisseminatedData.getBetweenDate", query = "SELECT SUM(uds.nbFiles) AS nbFiles, SUM(uds.size) AS totalSize, SUM(uds.dissToolNbFiles) AS dissToolNbFiles, SUM(uds.dissToolSize) AS dissToolSize FROM UserDisseminatedData uds WHERE uds.date BETWEEN :from AND :to")})
//@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class UserDisseminatedData implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DISSEMINATED_GEN")
   @Column(name = "USER_DISSEMINATED_DATA_ID")
   private Long id;

   /** The date. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "DATE")
   private Date date;

   /** The user id. */
   @Column(name = "USER_ID")
   private String userId;

   /** The size. */
   @Column(name = "SIZE", nullable = false)
   private Long size;

   /** The nb files. */
   @Column(name = "NB_FILES", nullable = false)
   private Integer nbFiles;

   /** The size. */
   @Column(name = "DISS_TOOL_SIZE", nullable = false)
   private Long dissToolSize;

   /** The nb files. */
   @Column(name = "DISS_TOOL_NB_FILES", nullable = false)
   private Integer dissToolNbFiles;

   /**
    * Default constructor.
    */
   public UserDisseminatedData() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[UserDisseminatedData] {0} : nb: {1}, size: {2}", userId,
            nbFiles, size);
   }

   /**
    * Gets the id.
    * @return the id.
    */
   public Long getId() {
      return id;
   }

   /**
    * Gets the date.
    * @return the date.
    */
   public Date getDate() {
      return date;
   }

   /**
    * Sets the date.
    * @param date the date to set.
    */
   public void setDate(Date date) {
      this.date = date;
   }

   /**
    * Gets the size.
    * @return the size.
    */
   public Long getSize() {
      return size;
   }

   /**
    * Sets the size.
    * @param size the size to set.
    */
   public void setSize(Long size) {
      this.size = size;
   }

   /**
    * Gets the userId.
    * @return the userId.
    */
   public String getUserId() {
      return userId;
   }

   /**
    * Sets the userId.
    * @param userId the userId to set.
    */
   public void setUserId(String userId) {
      this.userId = userId;
   }

   /**
    * Gets the nb files.
    *
    * @return the nb files
    */
   public Integer getNbFiles() {
      return nbFiles;
   }

   /**
    * Sets the nb files.
    *
    * @param nbFiles the new nb files
    */
   public void setNbFiles(Integer nbFiles) {
      this.nbFiles = nbFiles;
   }

   /**
    * Gets the disseminated by tool size.
    *
    * @return the disseminated by tool size
    */
   public Long getDissToolSize() {
      return dissToolSize;
   }

   /**
    * Sets the disseminated by tool size.
    *
    * @param dissToolSize the new disseminated by tool size
    */
   public void setDissToolSize(Long dissToolSize) {
      this.dissToolSize = dissToolSize;
   }

   /**
    * Gets the disseminated by tool number of files.
    *
    * @return the disseminated by tool number of files
    */
   public Integer getDissToolNbFiles() {
      return dissToolNbFiles;
   }

   /**
    * Sets the disseminated by tool nb files.
    *
    * @param dissToolNbFiles the new disseminated by tool nb files
    */
   public void setDissToolNbFiles(Integer dissToolNbFiles) {
      this.dissToolNbFiles = dissToolNbFiles;
   }

}
