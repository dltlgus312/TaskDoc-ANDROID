package com.service.taskdoc.display.custom.custom.dialog.file;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.FileVO;
import com.service.taskdoc.service.network.restful.service.FileService;

public class FileDownLoadServiceDialog implements FileService.FileLoadService {

    private Context context;

    private DocumentVO vo;

    android.app.AlertDialog dialog;

    AlertDialog.Builder arcBuilder;
    AlertDialog arcDialog;
    ProgressBar arcProgressBar;

    AlertDialog.Builder barBuilder;
    AlertDialog barDialog;
    ProgressBar barProgressBar;

    LinearLayout botLinear;
    LinearLayout parentsLinear;
    TextView percentView;
    TextView titleView;
    TextView totalView;

    public FileDownLoadServiceDialog(Context context){
        this.context = context;
        init();
    }

    public void init(){



        /*
         * Arc Dial
         * */
        arcProgressBar = new ProgressBar(context);

        arcBuilder = new AlertDialog.Builder(context);
        arcBuilder.setView(arcProgressBar);

        arcDialog = arcBuilder.create();
        arcDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // 투명
        arcDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // 어두운배경 삭제
        arcDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, // 밖의 View 터치 가능
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        arcDialog.setCancelable(false); // back버튼 동작 x


        /*
         * Bar Dial
         * */
        barProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);

        percentView = new TextView(context);
        totalView = new TextView(context);
        titleView = new TextView(context);

        percentView.setTextColor(Color.BLACK);
        totalView.setTextColor(Color.BLACK);
        titleView.setTextColor(Color.BLACK);
        titleView.setGravity(Gravity.CENTER);

        botLinear = new LinearLayout(context);
        botLinear.setOrientation(LinearLayout.HORIZONTAL);
        botLinear.setGravity(Gravity.CENTER);

        parentsLinear = new LinearLayout(context);
        parentsLinear.setOrientation(LinearLayout.VERTICAL);


        botLinear.addView(percentView);
        botLinear.addView(totalView);

        parentsLinear.addView(titleView);
        parentsLinear.addView(barProgressBar);
        parentsLinear.addView(botLinear);

        barBuilder = new AlertDialog.Builder(context);
        barBuilder.setView(parentsLinear);

        barDialog = barBuilder.create();
        barDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        barDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        barDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        barDialog.setCancelable(false);
    }

    public void showFileData(){
        DialogDocParam dialogDocParam = new DialogDocParam(context);

        dialogDocParam.setPcode(pcode);
        dialogDocParam.setPermision(permision);
        dialogDocParam.setVo(vo);
        dialogDocParam.setCrmode(crmode);
        dialogDocParam.setFileUpdateListener(new DialogDocParam.FileUpdateListener() {
            @Override
            public void update(DocumentVO vo) {
                if (fileUpdateListener != null) fileUpdateListener.update(vo);
            }

            @Override
            public void insert(DocumentVO vo) {
                if (fileUpdateListener != null) fileUpdateListener.insert(vo);
            }
        });
        dialogDocParam.showDownLoadInit();

        dialogDocParam.setFileLoadService(this);
        dialog = dialogDocParam.show();
    }


    /*
    * Data Ref
    * */
    String permision;

    int crmode;

    int pcode;

    DialogDocParam.FileUpdateListener fileUpdateListener;

    public DocumentVO getVo() {
        return vo;
    }

    public FileDownLoadServiceDialog setVo(DocumentVO vo) {
        this.vo = vo;
        return this;
    }

    public String getPermision() {
        return permision;
    }

    public FileDownLoadServiceDialog setPermision(String permision) {
        this.permision = permision;
        return this;
    }

    public int getCrmode() {
        return crmode;
    }

    public FileDownLoadServiceDialog setCrmode(int crmode) {
        this.crmode = crmode;
        return this;
    }

    public int getPcode() {
        return pcode;
    }

    public FileDownLoadServiceDialog setPcode(int pcode) {
        this.pcode = pcode;
        return this;
    }

    public DialogDocParam.FileUpdateListener getFileUpdateListener() {
        return fileUpdateListener;
    }

    public FileDownLoadServiceDialog setFileUpdateListener(DialogDocParam.FileUpdateListener fileUpdateListener) {
        this.fileUpdateListener = fileUpdateListener;
        return this;
    }

    /*
    * Event Listener
    * */
    @Override
    public void start(FileVO vo) {
        dialog.dismiss();
        arcDialog.show();
        Toast.makeText(context, "\""+vo.getFname() +"\""+ " 파일 다운로드", Toast.LENGTH_SHORT).show();

        titleView.setText(vo.getFname());
    }

    @Override
    public void update(long percent, long total) {
        if (arcDialog.isShowing()){
            arcDialog.dismiss();

            barProgressBar.setMax((int) total);
            totalView.setText(" / "+ barProgressBar.getMax()+" KB");
            barDialog.show();
        }

        barProgressBar.setProgress((int) percent);
        percentView.setText(barProgressBar.getProgress()+" KB");
    }

    @Override
    public void end() {
        barDialog.dismiss();
        Toast.makeText(context, "파일 다운로드 완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fail(String msg) {
        arcDialog.dismiss();
        Toast.makeText(context, "파일 다운로드 실패. \n 실패사유: " + msg, Toast.LENGTH_SHORT).show();
    }

    public AlertDialog getArcDialog() {
        return arcDialog;
    }

    public void setArcDialog(AlertDialog arcDialog) {
        this.arcDialog = arcDialog;
    }

    public AlertDialog getBarDialog() {
        return barDialog;
    }

    public void setBarDialog(AlertDialog barDialog) {
        this.barDialog = barDialog;
    }
}
