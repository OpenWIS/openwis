package org.openwis.dataservice.common.domain.entity.cache;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OPENWIS_FILETYPE_FILTERS")
public class FiletypeFilter implements Serializable {
	
	@Id
	@Column(name = "FILETYPE")
	private String filetype;
	
	@Column(name = "INCLUDE")
	private boolean include;

	
	public FiletypeFilter(){
	}
	
	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public boolean isInclude() {
		return include;
	}

	public void setInclude(boolean include) {
		this.include = include;
	}
}