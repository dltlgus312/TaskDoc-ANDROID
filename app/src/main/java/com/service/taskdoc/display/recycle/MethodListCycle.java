package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.MethodListVO;

import java.util.List;

public class MethodListCycle extends RecyclerView.Adapter<MethodListCycle.ViewHolder>{

    private List<MethodListVO> list;
    private ClickListener clickListener;

    public MethodListCycle(List<MethodListVO> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_method_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MethodListVO vo = list.get(i);

        CardView card = viewHolder.card;
        TextView title = viewHolder.title;

        title.setText(vo.getMltitle());

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(vo);
            }
        });

        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickListener.onLongClick(vo, card);
                return false;
            }
        });
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView card;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.recycle_item_method_list_card);
            title = itemView.findViewById(R.id.recycle_item_method_list_title);
        }
    }

    public interface ClickListener{
        public void onClick(MethodListVO vo);
        public void onLongClick(MethodListVO vo, View view);
    }
}
