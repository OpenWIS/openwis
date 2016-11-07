package org.openwis.dataservice.common.domain.entity.request;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.io.FileUtils;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.util.ConfigServiceFacade;

/**
 * The request entity.
 * <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processedRequest")
@Entity
@Table(name = "OPENWIS_PROCESSED_REQUEST")
@SequenceGenerator(name = "PROCESSED_REQUEST_GEN", sequenceName = "PROCESSED_REQUEST_SEQ", initialValue = 1, allocationSize = 1)
@NamedQueries({
		@NamedQuery(name = "ProcessedRequest.FindByRequestResult", //
		query = "SELECT processedrequest FROM ProcessedRequest processedrequest WHERE processedrequest.requestResultStatus = :requestresult)"),
		@NamedQuery(name = "ProcessedRequest.FindLastByDate", //
		query = "SELECT processedrequest FROM ProcessedRequest processedrequest JOIN processedrequest.request request WHERE request.id = :id "
				+ "AND processedrequest.creationDate = "
				+ "(SELECT MAX(processedreq.creationDate) FROM ProcessedRequest processedreq JOIN processedreq.request req WHERE req.id = :id))"),
		@NamedQuery(name = "ProcessedRequest.FindLastRequests", //
		query = "SELECT DISTINCT processedrequest FROM ProcessedRequest processedrequest LEFT JOIN FETCH "
				+ "processedrequest.request request LEFT JOIN FETCH request.productMetadata "
				+ "WHERE request.user = :user AND processedrequest.completedDate IS NOT NULL "
				+ "ORDER BY processedrequest.completedDate DESC))"), //
		@NamedQuery(name = "ProcessedRequest.deleteByRequest", query = "DELETE FROM ProcessedRequest pr WHERE pr.request = :request"),
		@NamedQuery(name = "ProcessedRequest.clearStagingPost", query = "UPDATE FROM ProcessedRequest pr SET pr.uri = NULL"),
		@NamedQuery(name = "ProcessedRequest.clearStagingPostByUri", query = "DELETE FROM ProcessedRequest pr WHERE pr.uri = :uri") })
@SqlResultSetMapping(name = "allPRByUsers", entities={
   @EntityResult(entityClass=ProcessedRequest.class),
   @EntityResult(entityClass=ProductMetadata.class)
})		
public class ProcessedRequest implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6129531917682549074L;

	/** The generated id. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCESSED_REQUEST_GEN")
	@Column(name = "PROCESSED_REQUEST_ID")
	private Long id;

	// @Version
	// This the should be checked in order to have an optimistic lock
	/** The version. */
	private long version;

	/** The creation date. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", nullable = true)
	private Date creationDate;

	/** The submitted dissemination date. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBMITTED_DATE", nullable = true)
	private Date submittedDisseminationDate;

	/** The completed date. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "COMPLETED_DATE", nullable = true)
	private Date completedDate;

	/** The size. */
	@Column(name = "SIZE")
	private long size;

	/** The request result status. */
	@Enumerated(EnumType.STRING)
	@Column(name = "REQUEST_RESULT_STATUS")
	private RequestResultStatus requestResultStatus;

	/** The request. */
	@ManyToOne
	@JoinColumn(name = "REQUEST_ID", referencedColumnName = "REQUEST_ID")
	private Request request;

	/** The uri. */
	@Column(name = "REQUEST_URI", nullable = true)
	private String uri;

	/** The message. */
	@Column(name = "MESSAGE", nullable = true, length = 1024)
	private String message;

	/**
	 * Default constructor. Builds a Request.
	 */
	public ProcessedRequest() {
		//
	}

	/**
	 * Clear staging post.
	 */
	@PostRemove
	public void clearStagingPost() {
		// clear the this.uri;
		if (this.getUri()!= null) {
		   // TODO: This might need to be fixed
			File file = new File(ConfigServiceFacade.getInstance().getString("cache.dir.stagingPost"), this.getUri());
			try {
            FileUtils.deleteDirectory(file);
         } catch (IOException e) {
         }
			this.uri = null;
		}
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
	 * Gets the creation date.
	 * 
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the creation date.
	 * 
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets the submitted dissemination date.
	 * 
	 * @return the submittedDisseminationDate
	 */
	public Date getSubmittedDisseminationDate() {
		return submittedDisseminationDate;
	}

	/**
	 * Sets the submitted dissemination date.
	 * 
	 * @param updateDate
	 *            the new submitted dissemination date
	 */
	public void setSubmittedDisseminationDate(Date updateDate) {
		submittedDisseminationDate = updateDate;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 * 
	 * @param size
	 *            the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Gets the request result status.
	 * 
	 * @return the requestResultStatus
	 */
	public RequestResultStatus getRequestResultStatus() {
		return requestResultStatus;
	}

	/**
	 * Sets the request result status.
	 * 
	 * @param requestResultStatus
	 *            the requestResultStatus to set
	 */
	public void setRequestResultStatus(RequestResultStatus requestResultStatus) {
		this.requestResultStatus = requestResultStatus;
	}

	/**
	 * Gets the request.
	 * 
	 * @return the request.
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Description goes here.
	 * 
	 * @param reqst
	 *            the request
	 */
	public void setRequest(Request reqst) {
		request = reqst;
	}

	/**
	 * Returns <code>true</code> if the processed request in in progress,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the processed request in in progress,
	 *         <code>false</code> otherwise.
	 */
	public boolean isInProgress() {
		return (requestResultStatus != null
				&& !requestResultStatus
						.equals(RequestResultStatus.DISSEMINATED) && !requestResultStatus
					.equals(RequestResultStatus.FAILED));
	}

	/**
	 * Returns <code>true</code> if the processed request is failed,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the processed request is failed,
	 *         <code>false</code> otherwise.
	 */
	public boolean isFailed() {
		return (requestResultStatus != null && requestResultStatus
				.equals(RequestResultStatus.FAILED));
	}

	/**
	 * Returns <code>true</code> if the processed request is complete,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the processed request is complete,
	 *         <code>false</code> otherwise.
	 */
	public boolean isComplete() {
		return (requestResultStatus != null && requestResultStatus
				.equals(RequestResultStatus.DISSEMINATED));
	}

	/**
	 * To string.
	 * 
	 * @return the string {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ProcessedRequest: ");
		sb.append(getId());
		String sep = ", ";
		sb.append(sep);
		sb.append(getCreationDate());
		sb.append(sep);
		sb.append(getSize());
		sb.append(sep);
		sb.append(getRequestResultStatus());
		sb.append(sep);
		sb.append(getCreationDate());
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Sets the uri.
	 * 
	 * @param uri
	 *            the uri to set.
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Gets the uri.
	 * 
	 * @return the uri.
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the completedDate.
	 * 
	 * @param completedDate
	 *            the completedDate to set.
	 */
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	/**
	 * Gets the completedDate.
	 * 
	 * @return the completedDate.
	 */
	public Date getCompletedDate() {
		return completedDate;
	}

	/**
	 * Sets the version.
	 * 
	 * @param version
	 *            the version to set.
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * Gets the version.
	 * 
	 * @return the version.
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
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

}
