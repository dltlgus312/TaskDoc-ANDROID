package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.MethodBoardVO;

import java.util.List;

public class MethodCycle extends RecyclerView.Adapter<MethodCycle.ViewHolder> {

    private List<MethodBoardVO> list;
    private Listener listener;

    public MethodCycle(List<MethodBoardVO> list){
        this.list = list;
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public MethodCycle.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_method, viewGroup, false);
        return new MethodCycle.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        MethodBoardVO vo = list.get(i);

        LinearLayout item = viewHolder.item;
        TextView title = viewHolder.title;
        TextView date = viewHolder.date;
        TextView id = viewHolder.id;
        TextView number = viewHolder.number;

        String day = vo.getMbdate();
        day = day.substring(6, day.length());
        day = day.replace("-", "/");

        title.setText(vo.getMbtitle());
        date.setText(day);
        id.setText(vo.getUid());
        number.setText(vo.getMbcode()+"");

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(vo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item;
        private TextView title;
        private TextView date;
        private TextView id;
        private TextView number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.recycle_item_method_card);
            title = itemView.findViewById(R.id.recycle_item_method_title);
            date = itemView.findViewById(R.id.recycle_item_method_date);
            id = itemView.findViewById(R.id.recycle_item_method_id);
            number = itemView.findViewById(R.id.recycle_item_method_number);

        }
    }

    public interface Listener{
        public void onClick(MethodBoardVO vo);
        public void onLongClick(MethodBoardVO vo);
    }
}
