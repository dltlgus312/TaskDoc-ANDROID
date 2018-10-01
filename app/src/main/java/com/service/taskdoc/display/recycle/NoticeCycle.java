package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.transfer.NoticeVO;
import com.service.taskdoc.service.network.restful.service.NoticeService;

import org.w3c.dom.Text;

import java.util.List;

public class NoticeCycle extends RecyclerView.Adapter<NoticeCycle.ViewHolder> {

    private List<NoticeVO> list;
    private OnClickListener onClickListener;

    public NoticeCycle(List<NoticeVO> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_method, viewGroup, false);
        return new NoticeCycle.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        NoticeVO vo = this.list.get(i);

        TextView title = viewHolder.title;
        TextView date = viewHolder.date;
        TextView name = viewHolder.name;
        TextView number = viewHolder.number;
        LinearLayout linearLayout = viewHolder.linearLayout;

        title.setText(vo.getNtitle());
        date.setText(vo.getNdate());
        name.setText(Projects.OWNER);
        number.setText((i+1)+"");

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(vo);
            }
        });

        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onClickListener.onLongClick(linearLayout, vo);
                return false;
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView date;
        private TextView name;
        private TextView number;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.recycle_item_method_title);
            date = itemView.findViewById(R.id.recycle_item_method_date);
            name = itemView.findViewById(R.id.recycle_item_method_id);
            number = itemView.findViewById(R.id.recycle_item_method_number);
            linearLayout = itemView.findViewById(R.id.recycle_item_method_card);
        }
    }

    public interface OnClickListener{
        public void onClick(NoticeVO vo);
        public void onLongClick(View view, NoticeVO vo);
    }
}
