package com.amgreat.vo;

public class ListVO extends VO {
	private String id;
	private String cmdName;
	private ListVO next;
	private String selective;
	public String getSelective() { return selective; }
	public void setSelective(String selective) { this.selective = selective; }
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public ListVO getNext() { return next; }
	public void setNext(ListVO next) { this.next = next; }
	public String getCmdName() { return cmdName; }
	public void setCmdName(String cmdName) { this.cmdName = cmdName; }
}
