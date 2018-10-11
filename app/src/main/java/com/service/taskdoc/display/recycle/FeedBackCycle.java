package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.FeedBackVO;
import com.service.taskdoc.database.transfer.MemoVO;

import java.util.List;

public class FeedBackCycle extends RecyclerView.Adapter<FeedBackCycle.ViewHolder> {

    private List<FeedBackVO> vos;
    private OnClickListener onClickListener;

    private final String SUCCESS = "확인";
    private final String CANCLE = "취소";
    private final String MODIFY = "수정";
    private final String DELETE = "삭제";

    public FeedBackCycle(List<FeedBackVO> vos) {
        this.vos = vos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_memo, viewGroup, false);
        return new FeedBackCycle.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        FeedBackVO vo = vos.get(i);

        EditText contents = viewHolder.contents;
        TextView date = viewHolder.date;
        TextView modify = viewHolder.modify;
        TextView delete = viewHolder.delete;
        TextView id = viewHolder.id;

        modify.setVisibility(View.GONE);
        if (!vo.getUid().equals(UserInfo.getUid()))
            delete.setVisibility(View.GONE);

        id.setText(vo.getUid());
        contents.setText(vo.getFbcontents());
        date.setText(vo.getFbdate());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onDeleteClick(vo);
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @Override
    public int getItemCount() {
        return vos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private EditText contents;
        private TextView date;
        private TextView modify;
        private TextView delete;
        private TextView id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.contents = itemView.findViewById(R.id.contents);
            this.date = itemView.findViewById(R.id.date);
            this.modify = itemView.findViewById(R.id.modify);
            this.delete = itemView.findViewById(R.id.delete);
            this.id = itemView.findViewById(R.id.id);
        }
    }

    public interface OnClickListener {
        void onDeleteClick(FeedBackVO vo);
    }
}
