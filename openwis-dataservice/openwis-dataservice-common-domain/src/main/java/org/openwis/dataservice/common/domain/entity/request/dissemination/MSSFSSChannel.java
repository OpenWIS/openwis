package org.openwis.dataservice.common.domain.entity.request.dissemination;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The MSS/FSS channel entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mssfssChannel")

@Entity
@Table(name = "OPENWIS_MSS_FSS_CHANNEL")
@SequenceGenerator(name = "MSSFSS_CHANNEL_GEN", sequenceName = "MSSFSS_CHANNEL_SEQ", initialValue = 1, allocationSize = 1)
public class MSSFSSChannel implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MSSFSS_CHANNEL_GEN")
   @Column(name = "MSS_FSS_CHANNEL_ID")
   private Long id;

   /** */
   @Column(name = "CHANNEL")
   private String channel;

   /**
    * Default constructor.
    */
   public MSSFSSChannel() {
      super();
   }

   /**
    * @return the id
    */
   public Long getId() {
      return id;
   }

   /**
    * @return the channel
    */
   public String getChannel() {
      return channel;
   }

   /**
    * @param channel
    *            the channel to set
    */
   public void setChannel(String channel) {
      this.channel = channel;
   }

}
