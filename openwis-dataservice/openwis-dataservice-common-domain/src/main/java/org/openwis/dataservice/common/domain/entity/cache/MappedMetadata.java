package org.openwis.dataservice.common.domain.entity.cache;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

/**
 * Maps every file in the cache to one or more product metadata.
 */
@Entity
@Table(name = "OPENWIS_MAPPED_METADATA")
@SequenceGenerator(name = "MAPPED_METADATA_GEN", sequenceName = "MAPPED_METADATA_SEQ", initialValue = 1, allocationSize = 1)
public class MappedMetadata implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MAPPED_METADATA_GEN")
	@Column(name = "MAPPING_ID")
	private Long mapId;

	@Column(name = "CACHED_FILE_ID")
	@Index(name = "CACHED_FILE_ID_INDEX")
	private Long id;

	@Column(name = "PRODUCT_METADATA_ID")
	private Long productMetadataId;


	public MappedMetadata(){
	}


	public Long getMapId() {
		return mapId;
	}


	public void setMapId(Long mapId) {
		this.mapId = mapId;
	}


	public Long getProductMetadataId() {
		return productMetadataId;
	}


	public void setProductMetadataId(Long productMetadataId) {
		this.productMetadataId = productMetadataId;
	}


	public Long getCachedFileId() {
		return id;
	}


	public void setCachedFileId(Long cachedFileId) {
		this.id = cachedFileId;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}
}