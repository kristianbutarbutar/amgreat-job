package com.amgreat.vo;

public class CommandVO {
	private String cmdName;
	private String uri;
	private String tabelName;
	private String columnName;
	private String cmdStatus;
	
	
	public String getCmdStatus() {
		return cmdStatus;
	}
	public void setCmdStatus(String cmdStatus) {
		this.cmdStatus = cmdStatus;
	}
	
	public String getTabelName() {
		return tabelName;
	}
	public void setTabelName(String tabelName) {
		this.tabelName = tabelName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getCmdName() {
		return cmdName;
	}
	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
}
