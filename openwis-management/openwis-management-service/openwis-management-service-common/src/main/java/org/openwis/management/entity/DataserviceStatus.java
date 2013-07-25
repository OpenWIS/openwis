package org.openwis.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "OPENWIS_DATASERVICE_STATUS")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class DataserviceStatus {
	
	@Id
	@Column(name = "SERVICE_STATUS_KEY")
	private String key;
	
	@Column(name = "SERVICE_STATUS_VALUE")
	private Long value;
	
	public DataserviceStatus(){
		
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