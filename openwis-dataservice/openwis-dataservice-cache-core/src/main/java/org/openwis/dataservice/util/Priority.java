package org.openwis.dataservice.util;

public enum Priority {
	LOW,HIGH;
	
	public static Priority getPriorityFromValue(int priorityValue){
		return ((priorityValue == 1 || priorityValue == 2)? Priority.HIGH : Priority.LOW);
	}
}