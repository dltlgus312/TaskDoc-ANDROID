package com.service.taskdoc.display.custom.custom.dialog;

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
import com.service.taskdoc.display.activity.ChatingActivity;
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

    public FileDownLoadServiceDialog(Context context, DocumentVO vo) {
        this.context = context;
        this.vo = vo;

        DialogDocParam dialogDocParam = new DialogDocParam(context, vo);
        dialogDocParam.setFileLoadService(this);
        dialog = dialogDocParam.show();


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

        percentView.setTextColor(Color.WHITE);
        totalView.setTextColor(Color.WHITE);
        titleView.setTextColor(Color.WHITE);
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
            totalView.setText(" / "+ barProgressBar.getMax());
            barDialog.show();
        }

        barProgressBar.setProgress((int) percent);
        percentView.setText(barProgressBar.getProgress()+"");
    }

    @Override
    public void end() {
        barDialog.dismiss();
        Toast.makeText(context, "파일 다운로드 완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fail(String msg) {
        Toast.makeText(context, "파일 다운로드 실패. \n 실패사유: " + msg, Toast.LENGTH_SHORT).show();
    }
}
