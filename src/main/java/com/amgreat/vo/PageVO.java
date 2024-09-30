package com.amgreat.vo;

public class PageVO {
	private String id;
	private String html;
	private String cmdName;
	private String pageLabel;
	private String viewType;
	private String par;
	
	public String getPar() { return par; }
	public void setPar(String par) { this.par = par; }
	public String getViewType() { return viewType; }
	public void setViewType(String viewType) { this.viewType = viewType; }
	public String getPageLabel() { return pageLabel; }
	public void setPageLabel(String pageLabel) { this.pageLabel = pageLabel; }
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getHtml() { return html; }
	public void setHtml(String html) { this.html = html; }
	public String getCmdName() { return cmdName; }
	public void setCmdName(String cmdName) { this.cmdName = cmdName; }
}
