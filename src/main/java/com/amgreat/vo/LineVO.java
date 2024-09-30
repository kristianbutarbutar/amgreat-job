package com.amgreat.vo;

public class LineVO {
	private String line;
	private int recNo;
	private LineVO next;
	
	public int getRecNo() { return recNo; }
	public void setRecNo(int recNo) { this.recNo = recNo; }
	public String getLine() { return line; }
	public void setLine(String line) { this.line = line; }
	public LineVO getNext() { return next; }
	public void setNext(LineVO next) { this.next = next; }
}
