package com.tisawesomeness.minecord.item;

public enum Version {
	V1_8("1.8"), V1_9("1.9"), V1_11("1.11");
	
	private String s;
	private Version(String s) {
		this.s = s;
	}
	public String toString() {
		return s;
	}
}