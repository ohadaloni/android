package com.theora.M;
/*------------------------------------------------------------*/
public class RowTag {
	/*------------------------------------------------------------*/
	private MmodelRow row;
	private int ordinal; // the first data row is ordinal=1
	private int background;
	private int type;
	public final static int titleRow = 0;
	public final static int headersRow = 1;
	public final static int dataRow = 2;
	
	/*------------------------------------------------------------*/
	public RowTag(int type) {
		this(null, 0, Mview.zebra0, type);
	}
	/*------------------------------------------------------------*/
	public RowTag(MmodelRow row, int ordinal, int background, int type) {
		this.row = row;
		this.ordinal = ordinal;
		this.background = background;
		this.type = type;
	}
	/*------------------------------------------------------------*/
	public int background() {
		return(background);
	}
	/*------------------------------------------------------------*/
	public int ordinal() {
		return(ordinal);
	}
	/*------------------------------------------------------------*/
	public MmodelRow row() {
		return(row);
	}
	/*------------------------------------------------------------*/
	public int type() {
		return(type);
	}
	/*------------------------------------------------------------*/
	public boolean isDataRow() {
		return(type == dataRow);
	}
	/*------------------------------------------------------------*/
	public boolean isHeadersRow() {
		return(type == headersRow);
	}
	/*------------------------------------------------------------*/
}
