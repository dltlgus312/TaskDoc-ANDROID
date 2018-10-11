package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;

import java.util.List;

public class NavDecisionCycle extends RecyclerView.Adapter<NavDecisionCycle.ViewHolder> {

    private List<DecisionVO> list;

    public NavDecisionCycle(List<DecisionVO> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_nav_decision, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DecisionVO vo = list.get(i);

        LinearLayout linearLayout = viewHolder.linearLayout;
        TextView title = viewHolder.title;
        TextView close = viewHolder.close;
        TextView date = viewHolder.date;

        title.setText(vo.getDstitle());
        date.setText(vo.getDsdate());

        if (vo.getDsclose() == 1){
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private TextView title;
        private TextView close;
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear);
            title = itemView.findViewById(R.id.title);
            close = itemView.findViewById(R.id.close);
            date = itemView.findViewById(R.id.date);
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
        void onClickItem(DecisionVO vo);
    }
}
