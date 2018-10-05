package com.service.taskdoc.service.system.support.listener;

import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.custom.custom.dialog.FileUpLoadServiceDialog;

import java.util.List;

import okhttp3.MultipartBody;

public interface FileUpLoadDialogListener {
    void formatDate(List<MultipartBody.Part> multiPartList, DocumentVO vo);
}
