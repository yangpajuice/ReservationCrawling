package com.tistory.yangpajuice.rc.param;

public class ConfigParam implements IDataParam {
	private String sectId = "";
	private String keyId = "";
	
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
}
