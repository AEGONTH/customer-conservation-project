package com.adms.util;

import org.apache.commons.lang3.StringUtils;

public class Utilize {

	public String lowerCase(String str) {
		return StringUtils.lowerCase(str);
	}
	
	public String capitalized(String str) {
		return StringUtils.capitalize(str);
	}
	
	public String forceCapitalized(String str) {
		return capitalized(lowerCase(str));
	}
	
	public String upperCase(String str) {
		return StringUtils.upperCase(str);
	}
	
	public boolean isBlank(String str) {
		return StringUtils.isBlank(str);
	}
}
