package com.service.taskdoc.database.business.transfer;

public class Task {
	private int code;
	private String title;
	private String color;
	private String sdate;
	private String edate;
	private int percent;
	private int sequence;
	private int refference;
	private int refpcode;
	private int reftcode;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSdate() {
		return sdate;
	}

	public void setSdate(String sdate) {
		this.sdate = sdate;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getRefference() {
		return refference;
	}

	public void setRefference(int refference) {
		this.refference = refference;
	}

	public int getRefpcode() {
		return refpcode;
	}

	public void setRefpcode(int refpcode) {
		this.refpcode = refpcode;
	}

	public int getReftcode() {
		return reftcode;
	}

	public void setReftcode(int reftcode) {
		this.reftcode = reftcode;
	}
}
