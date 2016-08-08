package org.openwis.datasource.server.jaxb.serializer.incomingds;

import java.io.Serializable;
import java.text.MessageFormat;

public class FeedingMessage implements Serializable {
	
	private String fullSourcePath;
	
	private String targetFilename;
	
	private Integer priority;

	
	public FeedingMessage() {
	}

	public String getFullSourcePath() {
		return fullSourcePath;
	}

	public void setFullSourcePath(String fullSourcePath) {
		this.fullSourcePath = fullSourcePath;
	}

	public String getTargetFilename() {
		return targetFilename;
	}

	public void setTargetFilename(String targetFilename) {
		this.targetFilename = targetFilename;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return MessageFormat.format("[SourcePath: {0}, TargetFilename: {1}]", getFullSourcePath(), getTargetFilename());
	}
}