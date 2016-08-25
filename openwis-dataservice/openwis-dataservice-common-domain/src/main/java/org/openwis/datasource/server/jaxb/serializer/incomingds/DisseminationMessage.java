package org.openwis.datasource.server.jaxb.serializer.incomingds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class DisseminationMessage.
 * <P>
 * Explanation goes here.
 * <P>
 */
@XmlRootElement(name = "disseminationmessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class DisseminationMessage {

	/** The id. */
	@XmlAttribute
	private Long id;

	/**
	 * Instantiates a new dissemination message.
	 */
	public DisseminationMessage() {
		// Default Constructor
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
	 * @param id
	 *            the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
