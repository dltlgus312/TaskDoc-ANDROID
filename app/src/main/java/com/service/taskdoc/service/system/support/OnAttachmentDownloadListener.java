package com.service.taskdoc.service.system.support;

public interface OnAttachmentDownloadListener {
    void onAttachmentDownloadedSuccess();
    void onAttachmentDownloadedError();
    void onAttachmentDownloadedFinished();
    void onAttachmentDownloadUpdate(int percent);
}