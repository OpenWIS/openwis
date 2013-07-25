/**
 * 
 */
package org.openwis.dataservice.common.domain.dto;

import java.util.Date;

import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class LightProcessedRequestDTO {
    
    /** The generated id. */
    private Long id;

    /** The creation date. */
    private Date creationDate;

    /** The submitted dissemination date. */
    private Date submittedDisseminationDate;

    /** The completed date. */
    private Date completedDate;

    /** The size. */
    private long size;
    
    private String message;

    /** The request result status. */
    private RequestResultStatus requestResultStatus;

    /** The uri. */
    private String uri;

    /**
     * Default constructor.
     * Builds a LightProcessedRequestDTO.
     */
    public LightProcessedRequestDTO() {
        super();
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
     * Gets the creationDate.
     * @return the creationDate.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creationDate.
     * @param creationDate the creationDate to set.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the submittedDisseminationDate.
     * @return the submittedDisseminationDate.
     */
    public Date getSubmittedDisseminationDate() {
        return submittedDisseminationDate;
    }

    /**
     * Sets the submittedDisseminationDate.
     * @param submittedDisseminationDate the submittedDisseminationDate to set.
     */
    public void setSubmittedDisseminationDate(Date submittedDisseminationDate) {
        this.submittedDisseminationDate = submittedDisseminationDate;
    }

    /**
     * Gets the completedDate.
     * @return the completedDate.
     */
    public Date getCompletedDate() {
        return completedDate;
    }

    /**
     * Sets the completedDate.
     * @param completedDate the completedDate to set.
     */
    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * Gets the size.
     * @return the size.
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the size.
     * @param size the size to set.
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Gets the requestResultStatus.
     * @return the requestResultStatus.
     */
    public RequestResultStatus getRequestResultStatus() {
        return requestResultStatus;
    }

    /**
     * Sets the requestResultStatus.
     * @param requestResultStatus the requestResultStatus to set.
     */
    public void setRequestResultStatus(RequestResultStatus requestResultStatus) {
        this.requestResultStatus = requestResultStatus;
    }

    /**
     * Gets the uri.
     * @return the uri.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri.
     * @param uri the uri to set.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    /**
     * Converts a processed request to a light DTO.
     * @param pr the processed request to convert.
     * @return the processed request DTO.
     */
    public static LightProcessedRequestDTO processedRequestToDTO(ProcessedRequest pr) {
        LightProcessedRequestDTO dto = new LightProcessedRequestDTO();
        dto.setId(pr.getId());
        dto.setCompletedDate(pr.getCompletedDate());
        dto.setMessage(pr.getMessage());
        dto.setCreationDate(pr.getCreationDate());
        dto.setRequestResultStatus(pr.getRequestResultStatus());
        dto.setSize(pr.getSize());
        dto.setSubmittedDisseminationDate(pr.getSubmittedDisseminationDate());
        dto.setUri(pr.getUri());
        return dto;
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
    

}
