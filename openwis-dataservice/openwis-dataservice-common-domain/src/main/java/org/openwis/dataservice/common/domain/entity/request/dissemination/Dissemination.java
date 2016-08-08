package org.openwis.dataservice.common.domain.entity.request.dissemination;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * The dissemination entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dissemination")
@XmlSeeAlso({ShoppingCartDissemination.class, RMDCNDissemination.class, PublicDissemination.class,
        MSSFSSDissemination.class})
@Entity
@Table(name = "OPENWIS_DISSEMINATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "REQUEST_OBJECT_TYPE", length = 30)
@SequenceGenerator(name = "DISSEMINATION_GEN", sequenceName = "DISSEMINATION_SEQ", initialValue = 1, allocationSize = 1)
public abstract class Dissemination implements Serializable {

    /** The generated id. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DISSEMINATION_GEN")
    @Column(name = "DISSEMINATION_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISSEMINATION_ZIP_MODE")
    private DisseminationZipMode zipMode;

    /**
     * Default constructor.
     */
    public Dissemination() {
        super();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the zipMode.
     * @return the zipMode.
     */
    public DisseminationZipMode getZipMode() {
        return zipMode;
    }

    /**
     * Sets the zipMode.
     * @param zipMode the zipMode to set.
     */
    public void setZipMode(DisseminationZipMode zipMode) {
        this.zipMode = zipMode;
    }
}
