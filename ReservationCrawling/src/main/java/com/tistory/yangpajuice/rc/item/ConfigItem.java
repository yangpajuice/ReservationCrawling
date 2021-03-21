package com.tistory.yangpajuice.rc.item;

public class ConfigItem implements IDataItem {
	private String sectId = "";
	private String keyId = "";
	private int seq = 0;
	private String value = "";
	
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
}
