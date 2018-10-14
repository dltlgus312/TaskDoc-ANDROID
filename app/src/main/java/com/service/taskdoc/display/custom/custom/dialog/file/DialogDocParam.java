package com.service.taskdoc.display.custom.custom.dialog.file;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.FeedBackVO;
import com.service.taskdoc.database.transfer.FileVO;
import com.service.taskdoc.display.custom.custom.dialog.chat.DialogChaParam;
import com.service.taskdoc.display.custom.custom.dialog.task.DialogTaskPicker;
import com.service.taskdoc.display.recycle.FeedBackCycle;
import com.service.taskdoc.display.recycle.FileCycle;
import com.service.taskdoc.service.network.restful.service.DocumentService;
import com.service.taskdoc.service.network.restful.service.FeedBackService;
import com.service.taskdoc.service.network.restful.service.FileService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.service.KeyboardManager;

import java.util.ArrayList;
import java.util.List;

public class DialogDocParam extends AlertDialog.Builder {

    private DocumentService documentService;

    private FileService fileService;

    private FeedBackService feedBackService;

    Context context;

    public DialogDocParam(Context context) {
        super(context);
        this.context = context;
    }

    // 파일 업로드 파라미터 생성자.
    public DialogDocParam(Context context, List<FileCycle.Item> items) {
        super(context);

        EditText title = new EditText(context);
        EditText contents = new EditText(context);

        title.setHint(R.string.title);
        contents.setHint(R.string.contents);
        title.setSingleLine();
        title.requestFocus();

        FileCycle fileCycle = new FileCycle(items);
        RecyclerView recyclerView = new RecyclerView(context);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileCycle);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(title);
        linearLayout.addView(contents);
        linearLayout.addView(recyclerView);

        setTitle("파일 업로드");
        setView(linearLayout);

        contents.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    new KeyboardManager().hide(context, contents);
                    return true;
                }
                return false;
            }
        });

        setPositiveButton("다음", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new KeyboardManager().hide(context, contents);
                if (onPositiveClick != null) {
                    onPositiveClick.getImformation(title.getText().toString(), contents.getText().toString());
                }
            }
        });

        setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        new KeyboardManager().show(context);

    }

    // 파일 다운로드
    public void showDownLoadInit() {
        List<FileVO> fileVos = new ArrayList<>();
        List<FeedBackVO> feedBackVos = new ArrayList<>();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_file_view, null);

        TextView title = view.findViewById(R.id.title);
        TextView uid = view.findViewById(R.id.id);
        TextView date = view.findViewById(R.id.date);
        TextView contents = view.findViewById(R.id.contents);

        Button feedback = view.findViewById(R.id.feedback);
        Button reposition = view.findViewById(R.id.reposition);
        Button output = view.findViewById(R.id.output);

        RecyclerView recycleFile = view.findViewById(R.id.recycle_file);
        RecyclerView recycleFeed = view.findViewById(R.id.recycle_feedback);

        title.setText(vo.getDmtitle());
        if (vo.getDmcontents() != null && !vo.getDmcontents().equals(""))
            contents.setText(vo.getDmcontents());
        date.setText(vo.getDmdate());
        uid.setText(vo.getUid());

        // 파일 다운로드
        FileCycle fileCycle = new FileCycle(fileVos, true);
        fileCycle.setOnClickDownload(new FileCycle.OnClickDownload() {
            @Override
            public void downloadFile(FileVO vo) {
                fileService.download(vo);
            }
        });

        // 스크롤 막기
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycleFile.setLayoutManager(layoutManager);
        recycleFile.setNestedScrollingEnabled(false);
        recycleFile.setAdapter(fileCycle);

        FeedBackCycle feedBackCycle = new FeedBackCycle(feedBackVos);
        feedBackCycle.setOnClickListener(new FeedBackCycle.OnClickListener() {
            @Override
            public void onDeleteClick(FeedBackVO vo) {
                feedBackService.delete(vo);
            }
        });

        RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };


        recycleFeed.addOnItemTouchListener(mScrollTouchListener);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(context);
        recycleFeed.setLayoutManager(layoutManager2);
        recycleFeed.setAdapter(feedBackCycle);

        // Event
        contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChaParam dialog = new DialogChaParam(context);
                dialog.setOnPositiveClick(new DialogChaParam.OnPositiveClick() {
                    @Override
                    public void getImformation(String title) {
                        vo.setDmcontents(title);
                        documentService.update(vo);
                        documentService.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                contents.setText(vo.getDmcontents());
                            }
                        });
                    }
                });
                dialog.setTitle("내용 입력");
                dialog.show();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChaParam dialog = new DialogChaParam(context);
                dialog.setOnPositiveClick(new DialogChaParam.OnPositiveClick() {
                    @Override
                    public void getImformation(String title) {
                        FeedBackVO fvo = new FeedBackVO();
                        fvo.setDmcode(vo.getDmcode());
                        fvo.setFbcontents(title);
                        fvo.setUid(UserInfo.getUid());

                        feedBackService.insert(fvo);
                    }
                });
                dialog.setTitle("피드백");
                dialog.show();
            }
        });

        reposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogTaskPicker dialogTaskPicker = new DialogTaskPicker(context, pcode);
                dialogTaskPicker.setOnPositiveClick(new DialogTaskPicker.OnPositiveClick() {
                    @Override
                    public void getTask(Task t) {
                        if (t == null) {
                            Toast.makeText(context, "선택된 업무가 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        vo.setTcode(t.getCode());
                        documentService.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                if (fileUpdateListener != null) fileUpdateListener.update(vo);
                            }
                        });
                        documentService.update(vo);

                    }
                });
                dialogTaskPicker.show();
            }
        });

        output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogTaskPicker dialogTaskPicker = new DialogTaskPicker(context, pcode);
                dialogTaskPicker.setOutputTaskGetter(new DialogTaskPicker.OutputTaskGetter() {
                    @Override
                    public void getTask(Task task) {
                        DocumentVO v = new DocumentVO();

                        v.setDmtitle(vo.getDmtitle());
                        v.setDmcontents(vo.getDmcontents());
                        v.setCrcode(vo.getCrcode());
                        v.setDmcode(vo.getDmcode());

                        v.setTcode(task.getCode());
                        v.setUid(UserInfo.getUid());


                        documentService.copy(v);
                        documentService.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                // COPY
                                if (fileUpdateListener != null) fileUpdateListener.insert((DocumentVO) objects[0]);
                            }
                        });
                    }
                });
            }
        });


        if (permision == null || permision.equals(Projects.MEMBER) || crmode != 1 || vo.getDmtitle().contains("OUTPUT")) {
            output.setVisibility(View.GONE);
            reposition.setVisibility(View.GONE);
        }


        // setter
        setTitle("파일 다운로드");
        setView(view);

        setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        // network
        feedBackService = new FeedBackService();
        feedBackService.setList(feedBackVos);
        feedBackService.list(vo.getDmcode());
        feedBackService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                feedBackCycle.notifyDataSetChanged();
                recycleFeed.scrollToPosition(feedBackCycle.getItemCount() - 1);
            }
        });

        fileService = new FileService();
        fileService.list(vo.getDmcode());
        fileService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                fileVos.addAll((List<FileVO>) objects[0]);
                fileCycle.notifyDataSetChanged();
            }
        });

        documentService = new DocumentService(0);
    }





    /*
    * Reference Data
    * */
    int pcode;

    String permision;

    int crmode;

    DocumentVO vo;

    public int getCrmode() {
        return crmode;
    }

    public void setCrmode(int crmode) {
        this.crmode = crmode;
    }

    public int getPcode() {
        return pcode;
    }

    public void setPcode(int pcode) {
        this.pcode = pcode;
    }

    public String getPermision() {
        return permision;
    }

    public void setPermision(String permision) {
        this.permision = permision;
    }

    public DocumentVO getVo() {
        return vo;
    }

    public void setVo(DocumentVO vo) {
        this.vo = vo;
    }






    /*
     * 파일 다운로드 리스너
     * */
    private FileService.FileLoadService fileLoadService;

    public FileService.FileLoadService getFileLoadService() {
        return fileLoadService;
    }

    public void setFileLoadService(FileService.FileLoadService fileLoadService) {
        this.fileLoadService = fileLoadService;

        fileService.setFileLoadService(new FileService.FileLoadService() {
            @Override
            public void fail(String msg) {
                fileLoadService.fail(msg);
            }

            @Override
            public void start(FileVO vo) {
                fileLoadService.start(vo);
            }

            @Override
            public void update(long percent, long total) {
                fileLoadService.update(percent, total);
            }

            @Override
            public void end() {
                fileLoadService.end();
            }
        });
    }







    /*
     * 확인버튼 리스너
     * */
    private OnPositiveClick onPositiveClick;

    public OnPositiveClick getOnPositiveClick() {
        return onPositiveClick;
    }

    public void setOnPositiveClick(OnPositiveClick onPositiveClick) {
        this.onPositiveClick = onPositiveClick;
    }

    public interface OnPositiveClick {
        void getImformation(String title, String contents);
    }




    /*
    * 업데이트 리스너
    * */
    private FileUpdateListener fileUpdateListener;

    public FileUpdateListener getFileUpdateListener() {
        return fileUpdateListener;
    }

    public void setFileUpdateListener(FileUpdateListener fileUpdateListener) {
        this.fileUpdateListener = fileUpdateListener;
    }

    public interface FileUpdateListener{
        void update(DocumentVO vo);
        void insert(DocumentVO vo); // COPY
    }
}
