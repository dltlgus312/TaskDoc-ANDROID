package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DocumentVO;

import java.util.List;

public class NavFocusCycle extends RecyclerView.Adapter<NavFocusCycle.ViewHolder>{

    private List<ChatRoomVO> list;

    public NavFocusCycle(List<ChatRoomVO> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_nav_focus, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChatRoomVO vo = list.get(i);

        LinearLayout linearLayout = viewHolder.linearLayout;
        TextView title = viewHolder.title;
        TextView date = viewHolder.date;
        TextView close = viewHolder.close;

        title.setText(vo.getFctitle());
        date.setText(vo.getCrdate());

        if (vo.getCrclose() == 1){
            close.setText("(종료)");
            close.setTextColor(0xaadd0000);
        } else {
            close.setText("(진행)");
            close.setTextColor(0xaa0000dd);
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.onClickItem(vo);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView title;
        private TextView date;
        private TextView close;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            close = itemView.findViewById(R.id.close);
        }
    }


    OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener{
        void onClickItem(ChatRoomVO vo);
    }
}
