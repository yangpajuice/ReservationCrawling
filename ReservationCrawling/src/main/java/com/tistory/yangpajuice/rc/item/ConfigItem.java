package com.tistory.yangpajuice.rc.item;

public class ConfigItem implements IDataItem {
	private String sectId = "";
	private String keyId = "";
	private int seq = 0;
	private String value = "";
	private String value2 = "";
	private String value3 = "";
	
	@Override
	public String getKey() {
		return sectId + "+" + keyId + "+" + seq;
	}

	public String getSectId() {
		return sectId;
	}

	public void setSectId(String sectId) {
		this.sectId = sectId;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
	}
}
