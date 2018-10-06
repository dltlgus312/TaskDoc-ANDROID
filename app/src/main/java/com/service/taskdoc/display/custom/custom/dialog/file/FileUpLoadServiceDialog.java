package com.service.taskdoc.display.custom.custom.dialog.file;

import android.content.Context;
import android.widget.Toast;

import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.custom.custom.dialog.task.DialogTaskPicker;
import com.service.taskdoc.display.recycle.FileCycle;
import com.service.taskdoc.service.system.support.listener.FileUpLoadDialogListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUpLoadServiceDialog {

    private Context context;



    public FileUpLoadServiceDialog(Context context) {
        this.context = context;
    }



    /*
     * Getter Setter
     * */

    private int crcode;
    private int pcode;

    public int getCrcode() {
        return crcode;
    }

    public FileUpLoadServiceDialog setCrcode(int crcode) {
        this.crcode = crcode;
        return this;
    }

    public int getPcode() {
        return pcode;
    }

    public FileUpLoadServiceDialog setPcode(int pcode) {
        this.pcode = pcode;
        return this;
    }




    /*
    * Event Listener
    * */

    private FileUpLoadDialogListener fileUpLoadDialogListener;

    public FileUpLoadDialogListener getFileUpLoadDialogListener() {
        return fileUpLoadDialogListener;
    }

    public FileUpLoadServiceDialog setFileUpLoadDialogListener(FileUpLoadDialogListener fileUpLoadDialogListener) {
        this.fileUpLoadDialogListener = fileUpLoadDialogListener;
        return this;
    }







    /*
    * Service
    * */

    private List<FileCycle.Item> itemList;
    private String title, contents;
    private Task task;

    public void docUploadProcess() {
        DialogFilePicker dialog = new DialogFilePicker(context);
        dialog.setOnPositiveClick(new DialogFilePicker.OnPositiveClick() {
            @Override
            public void selectFilePath(List<FileCycle.Item> items) {
                // 오류 처리
                if (items.size() == 0) {
                    Toast.makeText(context, "선택된 파일이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (items.size() > 4) {
                    Toast.makeText(context, "파일은 최대 4개 까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                itemList = items;
                docParameter();
            }
        });
        dialog.show();
    }

    private void docParameter() {
        // DOCUMENT 파라미터 셋팅
        DialogDocParam uploadDialog = new DialogDocParam(context, itemList);
        uploadDialog.setOnPositiveClick(new DialogDocParam.OnPositiveClick() {
            @Override
            public void getImformation(String t, String c) {
                if (t.length() == 0) {
                    Toast.makeText(context, "이름이 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                title = t;
                contents = c;

                if (crcode == 1) {
                    // 메인톡방에선 업무를 선택
                    taskFicker();
                } else {
                    // 서브, 회의모드에선 즉시 업로드
                    uploadFile();
                }
            }
        });
        uploadDialog.show();
    }

    private void taskFicker() {
        DialogTaskPicker dialogTaskPicker = new DialogTaskPicker(context, pcode);
        dialogTaskPicker.setOnPositiveClick(new DialogTaskPicker.OnPositiveClick() {
            @Override
            public void getTask(Task t) {
                if (t == null) {
                    Toast.makeText(context, "선택된 업무가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                task = t;
                uploadFile();
            }
        });
        dialogTaskPicker.show();
    }

    private void uploadFile() {
        DocumentVO vo = new DocumentVO();
        vo.setCrcode(crcode);
        if (task != null) {
            vo.setTcode(task.getCode());
        }
        vo.setUid(UserInfo.getUid());
        vo.setDmtitle(title);
        vo.setDmcontents(contents);

        List<MultipartBody.Part> multiPartList = new ArrayList<>();
        for (FileCycle.Item item : itemList) {
            File file = new File(item.getPath() + File.separator + item.getName());

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", item.getName(), requestFile);

            multiPartList.add(body);
        }

        if (fileUpLoadDialogListener !=null) fileUpLoadDialogListener.formatDate(multiPartList, vo);
    }

}
