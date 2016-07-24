package com.sh.net;

/*
 * DataContainer의 Single Data 기본형
 */
public class DataItem {
	private String key;
	private String val;
	
	public DataItem(String key, String val)
	{
		this.key = key;
		this.val = val;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public String getValue()
	{
		return val;
	}
}
