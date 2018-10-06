package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.MemoVO;

import java.util.List;

public class MemoCycle extends RecyclerView.Adapter<MemoCycle.ViewHolder> {

    private List<MemoVO> memos;
    private OnClickListener onClickListener;

    private final String SUCCESS = "확인";
    private final String CANCLE = "취소";
    private final String MODIFY = "수정";
    private final String DELETE = "삭제";

    public MemoCycle(List<MemoVO> memos) {
        this.memos = memos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_memo, viewGroup, false);
        return new MemoCycle.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        MemoVO memo = memos.get(i);

        EditText contents = viewHolder.contents;
        TextView date = viewHolder.date;
        TextView modify = viewHolder.modify;
        TextView delete = viewHolder.delete;

        contents.setText(memo.getMcontents());
        date.setText(memo.getMdate());

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modify.getText().toString().equals(MODIFY)) {
                    modify.setText(SUCCESS);
                    delete.setText(CANCLE);
                    contents.setEnabled(true);
                    contents.setFocusable(true);
                } else {
                    modify.setText(MODIFY);
                    delete.setText(DELETE);
                    contents.setEnabled(false);
                    contents.setFocusable(false);
                    onClickListener.onModifyClick(memo, contents.getText().toString());
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delete.getText().toString().equals(DELETE)) {
                    onClickListener.onDeleteClick(memo);
                } else {
                    modify.setText(MODIFY);
                    delete.setText(DELETE);
                    contents.setEnabled(false);
                    contents.setFocusable(false);
                }
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public List<MemoVO> getMemos(){
        return memos;
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private EditText contents;
        private TextView date;
        private TextView modify;
        private TextView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.contents = itemView.findViewById(R.id.contents);
            this.date = itemView.findViewById(R.id.date);
            this.modify = itemView.findViewById(R.id.modify);
            this.delete = itemView.findViewById(R.id.delete);
        }
    }

    public interface OnClickListener {
        void onModifyClick(MemoVO memo, String changeText);
        void onDeleteClick(MemoVO memo);
    }
}
