package com.service.taskdoc.database.business.transfer;

public class Project {
    private int pcode;
    private String ptitle;
    private String psubtitle;
    private String pedate;
    private String psdate;
    private String ppermission;
    private int pinvite;

    public int getPcode() {
        return pcode;
    }

    public void setPcode(int pcode) {
        this.pcode = pcode;
    }

    public String getPtitle() {
        return ptitle;
    }

    public void setPtitle(String ptitle) {
        this.ptitle = ptitle;
    }

    public String getPsubtitle() {
        return psubtitle;
    }

    public void setPsubtitle(String psubtitle) {
        this.psubtitle = psubtitle;
    }

    public String getPedate() {
        return pedate;
    }

    public void setPedate(String pedate) {
        this.pedate = pedate;
    }

    public String getPsdate() {
        return psdate;
    }

    public void setPsdate(String psdate) {
        this.psdate = psdate;
    }

    public String getPpermission() {
        return ppermission;
    }

    public void setPpermission(String ppermission) {
        this.ppermission = ppermission;
    }

    public int getPinvite() {
        return pinvite;
    }

    public void setPinvite(int pinvite) {
        this.pinvite = pinvite;
    }
}
