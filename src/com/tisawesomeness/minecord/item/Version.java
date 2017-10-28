package com.tisawesomeness.minecord.item;

public enum Version {
	V1_8("Since 1.8"),
	VBEFORE_1_9("Before 1.9"),
	V1_9("Since 1.9"),
	V1_10("Since 1.10"),
	V1_9_USE_1_10("Since 1.9, Usable since 1.10"),
	V1_11("Since 1.11"),
	V1_12("Since 1.12");
	
	private String s;
	private Version(String s) {
		this.s = s;
	}
	public String toString() {
		return s;
	}
}