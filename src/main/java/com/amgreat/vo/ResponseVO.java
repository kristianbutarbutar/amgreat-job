package com.amgreat.vo;

public class ResponseVO extends VO {
	private ResponseVO next = null;
	public ResponseVO getNext() { return next; }
	public void setNext(ResponseVO next) { this.next = next; }
	public ResponseVO getColumAt( int colNo ) {
		ResponseVO searchOn = this;
		if( searchOn != null && colNo == 1) {
			return searchOn;
		} else if ( searchOn != null && colNo > 1 ) {
			searchOn = searchOn.getNext(); int i = 2 ;
			while( searchOn != null ) {
				if ( colNo == i ) break;
				searchOn = searchOn.getNext(); i++;
			}
			return searchOn;
		}
		return searchOn;
	}
}
