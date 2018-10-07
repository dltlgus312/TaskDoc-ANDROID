package com.service.taskdoc.database.transfer;

public class DecisionVO {
	private int dscode;
	private String dstitle;
	private String dsdate;
	private int dsclose;
	private int crcode;
	private int tcode;
	
	
	public int getDscode() {
		return dscode;
	}
	public void setDscode(int dscode) {
		this.dscode = dscode;
	}
	public String getDstitle() {
		return dstitle;
	}
	public void setDstitle(String dstitle) {
		this.dstitle = dstitle;
	}
	public String getDsdate() {
		return dsdate;
	}
	public void setDsdate(String dsdate) {
		this.dsdate = dsdate;
	}
	public int getDsclose() {
		return dsclose;
	}
	public void setDsclose(int dsclose) {
		this.dsclose = dsclose;
	}
	public int getCrcode() {
		return crcode;
	}
	public void setCrcode(int crcode) {
		this.crcode = crcode;
	}
	public int getTcode() {
		return tcode;
	}
	public void setTcode(int tcode) {
		this.tcode = tcode;
	}
}
