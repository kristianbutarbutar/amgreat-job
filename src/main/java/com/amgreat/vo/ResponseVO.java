package com.amgreat.vo;

public class ResponseVO extends VO {
	private ResponseVO next = null;
	public ResponseVO getNext() { return next; }
	public void setNext(ResponseVO next) { this.next = next; }
	public ResponseVO getColumAt( int colNo ) {
		ResponseVO searchOn = this; ResponseVO col = null;
		if( searchOn != null && colNo == 1) {
			return searchOn;
		} else if( searchOn != null && colNo > 1 ) {
			col = searchOn.getNext(); int i = 2 ;
			while( col != null ) {
				if( colNo == i ) break;
				col = searchOn.getNext(); i++;
			}
			return col;
		}
		return col;
	}
}
