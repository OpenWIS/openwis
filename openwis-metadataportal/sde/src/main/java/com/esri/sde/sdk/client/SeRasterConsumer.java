package com.esri.sde.sdk.client;

public interface SeRasterConsumer {
	
	public final static int COMPLETETILES = 0;
	public final static int SINGLEFRAMEDONE = 1;
	public final static int STATICIMAGEDONE = 2;
	public final static int IMAGEERROR = 3;
	
	public void setHints(int h);
	public void setScanLines(int l, byte[] d, byte[] b);
	
	public void rasterComplete(int status);

}
