package org.openwis.dataservice.common.domain.entity.request.dissemination;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The MSS/FSS dissemination. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mssfssDissemination")

@Entity
@DiscriminatorValue(value = "MSS_FSS")
public class MSSFSSDissemination extends Dissemination {

   /** */
   @OneToOne(fetch = FetchType.EAGER, optional = true, cascade = CascadeType.ALL)
   @JoinColumn(name = "MSS_FSS_CHANNEL_ID", referencedColumnName = "MSS_FSS_CHANNEL_ID")
   private MSSFSSChannel channel;

   /**
    * Default constructor.
    */
   public MSSFSSDissemination() {
      super();
   }

   /**
    * @return the channel
    */
   public MSSFSSChannel getChannel() {
      return channel;
   }

   /**
    * @param channel
    *            the channel to set
    */
   public void setChannel(MSSFSSChannel channel) {
      this.channel = channel;
   }

}
