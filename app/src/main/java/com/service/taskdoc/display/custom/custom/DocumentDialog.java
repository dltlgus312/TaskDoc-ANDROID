package com.service.taskdoc.display.custom.custom;

import android.telephony.mbms.FileServiceInfo;

import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.service.network.restful.service.FileService;

public class DocumentDialog {

    private DocumentVO vo;

    private FileService service;

    public DocumentDialog(DocumentVO vo){
        this.vo = vo;
        service = new FileService();
    }
}
