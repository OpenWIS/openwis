package org.openwis.metadataportal.model.catalog;
/**
 * A POJO that describes a catalog in GeoNetwork. <P>
 * Explanation goes here. <P>
 * 
 */
public class Catalog {

	private String catalogSize;
	private int nbMetadata;
	public Catalog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Catalog(String catalogSize, int nbMetadata) {
		super();
		this.catalogSize = catalogSize;
		this.nbMetadata = nbMetadata;
	}

	public String getCatalogSize() {
		return catalogSize;
	}
	public void setCatalogSize(String catalogSize) {
		this.catalogSize = catalogSize;
	}
	public int getNbMetadata() {
		return nbMetadata;
	}
	public void setNbMetadata(int nbMetadata) {
		this.nbMetadata = nbMetadata;
	}

}
