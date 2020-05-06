package com.tistory.yangpajuice.rc.item;

public class AsusFirmwareItem implements IDataItem {
	private String version;
    private String date;
    private String fileByte;
    private String desc;
    
	@Override
	public String getKey() {
		return version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFileByte() {
		return fileByte;
	}

	public void setFileByte(String fileByte) {
		this.fileByte = fileByte;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}	
}
