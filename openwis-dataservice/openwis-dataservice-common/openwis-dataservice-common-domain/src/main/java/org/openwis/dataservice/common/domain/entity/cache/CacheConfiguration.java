package org.openwis.dataservice.common.domain.entity.cache;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "OPENWIS_CACHE_CONFIGURATION")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CacheConfiguration implements Serializable{
	
	@Id
	@Column(name = "OPENWIS_CONFIGURATION_KEY")
	private String key;
	
	@Column(name = "OPENWIS_CONFIGURATION_VALUE")
	private Long value;
	
	
	public CacheConfiguration(){
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
}