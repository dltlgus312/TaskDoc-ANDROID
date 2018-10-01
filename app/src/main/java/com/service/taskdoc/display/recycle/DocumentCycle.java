package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.DocumentVO;

import java.util.ArrayList;
import java.util.List;

public class DocumentCycle extends RecyclerView.Adapter<DocumentCycle.ViewHolder> {

    private boolean mine = false;
    private boolean isGoBack = false;

    private List<Task> taskList;
    private List<DocumentVO> documentList;

    private List<Task> copyTaskList;
    private List<DocumentVO> copyDocumentList;

    private List<List<Task>> taskStack;
    private List<List<DocumentVO>> documentStack;

    private ClickListener clickListener;

    private final String ALL = "전체";
    private final String FOLDER = "폴더";
    private final String FILE = "파일";
    private final String NAME = "이름";
    private final String DATE = "날짜";

    public DocumentCycle(List<DocumentVO> documentList) {
        this.documentList = documentList;
        mine = true;
    }

    public DocumentCycle(Tasks tasks, List<DocumentVO> documentList) {
        this.taskList = tasks.getPublicTasks();
        this.documentList = documentList;

        copyTaskList = new ArrayList<>();
        copyDocumentList = new ArrayList<>();
        taskStack = new ArrayList<>();
        documentStack = new ArrayList<>();

        for (Task t : taskList) {
            if (t.getCode() == t.getRefference()) {
                copyTaskList.add(t);
            }
        }

        mine = false;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_file, viewGroup, false);
        return new DocumentCycle.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        LinearLayout linearLayout = viewHolder.linearLayout;
        ImageView img = viewHolder.img;
        TextView name = viewHolder.name;

        linearLayout.setOnClickListener(null);

        if (mine) {
            DocumentVO document = documentList.get(i);
            img.setImageResource(R.drawable.img_file_doc);
            name.setText(document.getDmtitle());

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.fileClick(document);
                }
            });
        } else {
            if (i <= copyTaskList.size() - 1) {
                // 폴더
                Task task = copyTaskList.get(i);

                boolean empty = isEmpty(task);
                if (empty) {
                    img.setImageResource(R.drawable.img_folder_empty);
                } else {
                    img.setImageResource(R.drawable.img_folder_fill);
                }
                name.setText(task.getTitle());

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.folderClick(task);
                    }
                });
            } else {
                // 파일
                DocumentVO document = copyDocumentList.get(i - copyTaskList.size());
                img.setImageResource(R.drawable.img_file_doc);
                name.setText(document.getDmtitle());

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.fileClick(document);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        int size = 0;

        if (mine) {
            size += documentList.size();
        } else {
            size += copyTaskList.size();
            size += copyDocumentList.size();
        }

        return size;
    }


    public boolean canGoBack() {
        return isGoBack;
    }

    public void go(Task task) {

        // 스텍 저장
        List<DocumentVO> vos = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        vos.addAll(copyDocumentList);
        tasks.addAll(copyTaskList);
        taskStack.add(tasks);
        documentStack.add(vos);

        // 새로운 리스트 구성
        copyTaskList.clear();
        copyDocumentList.clear();

        for (Task t : taskList) {
            if (task.getCode() == t.getRefference() && task != t) {
                copyTaskList.add(t);
            }
        }

        for (DocumentVO vo : documentList) {
            if (task.getCode() == vo.getTcode()) {
                copyDocumentList.add(vo);
            }
        }

        isGoBack = true;
        notifyDataSetChanged();
    }

    public void back() {
        copyTaskList.clear();
        copyDocumentList.clear();

        int index = taskStack.size() - 1;

        copyTaskList.addAll(taskStack.get(index));
        copyDocumentList.addAll(documentStack.get(index));

        taskStack.remove(index);
        documentStack.remove(index);

        notifyDataSetChanged();

        if (taskStack.size() == 0){
            this.isGoBack = false;
        }
    }

    public void goTo(int index) {
        if (index >= taskStack.size()) return;
        copyTaskList.clear();
        copyDocumentList.clear();

        copyTaskList.addAll(taskStack.get(index));
        copyDocumentList.addAll(documentStack.get(index));

        int size = taskStack.size();
        for (int i = index; i < size; i++) {
            taskStack.remove(taskStack.size() - 1);
            documentStack.remove(documentStack.size() - 1);
        }

        notifyDataSetChanged();
    }

    public boolean isEmpty(Task task) {
        for (Task t : taskList) {
            if (task.getCode() == t.getRefference() && task != t) {
                return false;
            }
        }

        for (DocumentVO vo : documentList) {
            if (task.getCode() == vo.getTcode()) {
                return false;
            }
        }

        return true;
    }

    public void search(String string, String mode){
        
        switch (mode){
            case ALL :
                break;
            case FOLDER :
                break;
            case FILE :
                break;
            case NAME :
                break;
            case DATE :
                break;
            default: break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private ImageView img;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
        }
    }

    public interface ClickListener {
        void folderClick(Task task);

        void folderLongClick(Task task);

        void fileClick(DocumentVO vo);

        void fileLongClick(DocumentVO vo);
    }
}
