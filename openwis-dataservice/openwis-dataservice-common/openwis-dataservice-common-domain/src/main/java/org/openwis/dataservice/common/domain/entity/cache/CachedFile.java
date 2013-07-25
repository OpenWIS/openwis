/**
 *
 */
package org.openwis.dataservice.common.domain.entity.cache;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

/**
 * Represents a file resource of the local cache.
 * <p>
 */
@Entity
@Table(name = "OPENWIS_CACHED_FILE", uniqueConstraints={@UniqueConstraint(columnNames = {"FILENAME", "CHECKSUM"})})
@SequenceGenerator(name = "CACHED_FILE_GEN", sequenceName = "CACHED_FILE_SEQ", initialValue = 1, allocationSize = 1)
public class CachedFile implements Serializable {

   /** The file name. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CACHED_FILE_GEN")
   @Column(name = "CACHED_FILE_ID")
   private Long id;

   @Column(name = "FILENAME")
   @Index(name="cached_file_filename_index")
   private String filename;

   @Column(name = "INTERNAL_FILENAME")
   private String internalFilename;

   @Column(name = "PATH")
   private String path;

   /** The computed checksum for the incoming file. */
   @Column(name = "CHECKSUM")
   @Index(name="cached_file_checksum_index")
   private String checksum;

   /** The insertion date. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "INSERTION_DATE", nullable = true)
   private Date insertionDate;

   @Column(name = "RECEIVED_FROM_GTS")
   private boolean receivedFromGTS;

   @Column(name = "PRIORITY")
   private int priority;

   @Column(name = "NUMBER_OF_CHECKSUM_BYTES")
   private int numberOfChecksumBytes;

   @Column(name = "FILESIZE")
   private Long filesize;

   /**
    * Bean constructor.
    */
   public CachedFile() {
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return getFilename();
   }

   public Long getId() {
      return id;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public int getPriority() {
      return priority;
   }

   public String getPath() {
      return path;
   }

   public void setPath(final String path) {
      this.path = path;
   }

   public void setPriority(final int priority) {
      this.priority = priority;
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(final String filename) {
      this.filename = filename;
   }

   public String getChecksum() {
      return checksum;
   }

   public void setChecksum(final String checksum) {
      this.checksum = checksum;
   }

   public boolean isReceivedFromGTS() {
      return receivedFromGTS;
   }

   public void setReceivedFromGTS(final boolean receivedFromGTS) {
      this.receivedFromGTS = receivedFromGTS;
   }

   public Date getInsertionDate() {
      return insertionDate;
   }

   public void setInsertionDate(final Date insertionDate) {
      this.insertionDate = insertionDate;
   }

   public int getNumberOfChecksumBytes() {
      return numberOfChecksumBytes;
   }

   public void setNumberOfChecksumBytes(final int numberOfChecksumBytes) {
      this.numberOfChecksumBytes = numberOfChecksumBytes;
   }

   public String getInternalFilename() {
      return internalFilename;
   }

   public void setInternalFilename(final String internalFilename) {
      this.internalFilename = internalFilename;
   }

   public Long getFilesize() {
      return filesize;
   }

   public void setFilesize(final Long filesize) {
      this.filesize = filesize;
   }
}
