package org.openwis.dataservice.common.domain.entity.request;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.domain.entity.enumeration.RecurrentScale;

/**
 * The recurrent update frequency entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recurrentUpdateFrequency")
@Entity
@DiscriminatorValue(value = "RECURRENT")
public class RecurrentUpdateFrequency extends UpdateFrequency {

    /** */
    @Enumerated(EnumType.STRING)
    @Column(name = "RECURRENT_SCALE")
    private RecurrentScale recurrentScale;

    /** The recurrent period. */
    @Column(name = "RECURRENT_PERIOD")
    private int recurrentPeriod;

    /**
     * Default constructor.
     */
    public RecurrentUpdateFrequency() {
        super();
    }

    /**
     * Gets the recurrentScale.
     * @return the recurrentScale.
     */
    public RecurrentScale getRecurrentScale() {
        return recurrentScale;
    }

    /**
     * Sets the recurrentScale.
     * @param recurrentScale the recurrentScale to set.
     */
    public void setRecurrentScale(RecurrentScale recurrentScale) {
        this.recurrentScale = recurrentScale;
    }

    /**
     * Gets the recurrentPeriod.
     * @return the recurrentPeriod.
     */
    public int getRecurrentPeriod() {
        return recurrentPeriod;
    }

    /**
     * Sets the recurrentPeriod.
     * @param recurrentPeriod the recurrentPeriod to set.
     */
    public void setRecurrentPeriod(int recurrentPeriod) {
        this.recurrentPeriod = recurrentPeriod;
    }
}
