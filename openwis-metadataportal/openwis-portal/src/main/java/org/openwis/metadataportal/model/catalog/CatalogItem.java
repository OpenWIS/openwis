package org.openwis.metadataportal.model.catalog;

public class CatalogItem {
	
	private String date;
	private String source;
	private int size;
	private int nbRecords;

	public CatalogItem() {
		// TODO Auto-generated constructor stub
	}

	public CatalogItem(String date, String source, int size, int nbRecords) {
		super();
		this.date = date;
		this.source = source;
		this.size = size;
		this.nbRecords = nbRecords;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getNbRecords() {
		return nbRecords;
	}

	public void setNbRecords(int nbRecords) {
		this.nbRecords = nbRecords;
	}

}
