package com.esri.sde.sdk.client;

public class SeColumnDefinition {
	
    public SeColumnDefinition(String s,int i,int j,int k, boolean b) throws SeException {}
	
	public final static /* GEOT-947 final*/ int TYPE_SMALLINT = 0;
	public final static /* GEOT-947 final*/ int TYPE_INTEGER = 1;
	public final static /* GEOT-947 final*/ int TYPE_FLOAT = 2;
	public final static /* GEOT-947 final*/ int TYPE_DOUBLE = 3;
	public final static /* GEOT-947 final*/ int TYPE_STRING = 4;
	public final static /* GEOT-947 final*/ int TYPE_DATE = 5;
	public final static /* GEOT-947 final*/ int TYPE_SHAPE = 6;
	public final static /* GEOT-947 final*/ int TYPE_INT16 = 7;
	public final static /* GEOT-947 final*/ int TYPE_INT32 = 8;
	public final static /* GEOT-947 final*/ int TYPE_INT64 = 9;
	public final static /* GEOT-947 final*/ int TYPE_FLOAT32 = 10;
	public final static /* GEOT-947 final*/ int TYPE_FLOAT64 = 11;
	public final static /* GEOT-947 final*/ int TYPE_BLOB = 12;
	public final static /* GEOT-947 final*/ int TYPE_RASTER = 13;
	public final static /* GEOT-947 final*/ int TYPE_NSTRING = 14;
	public final static /* GEOT-947 final*/ int TYPE_UUID = 15;
    public final static int TYPE_CLOB = 1;
    public final static int TYPE_NCLOB = 1;

	
	
	public String getName() { return null; }
	public int getType() { return 0; }
	public int getSize() { return 0; }
	public int getScale() { return 0; }
	public short getRowIdType() { return 0; }
	
	

}
