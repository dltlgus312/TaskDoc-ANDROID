package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.DocumentVO;

import java.util.List;

public class NavFileCycle extends RecyclerView.Adapter<NavFileCycle.ViewHolder> {

    private List<DocumentVO> list;

    public NavFileCycle(List<DocumentVO> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NavFileCycle.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_nav_file, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NavFileCycle.ViewHolder viewHolder, int i) {

        DocumentVO vo = list.get(i);

        LinearLayout linearLayout = viewHolder.linearLayout;
        TextView title = viewHolder.title;
        TextView date = viewHolder.date;
        TextView user = viewHolder.name;

        title.setText(vo.getDmtitle());
        date.setText(vo.getDmdate());
        user.setText(vo.getUid());

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        private TextView date;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
        }
    }
}
