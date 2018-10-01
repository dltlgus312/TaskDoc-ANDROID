package com.service.taskdoc.display.recycle;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Chating;
import com.service.taskdoc.database.transfer.ChatContentsVO;

import java.util.List;

public class ChatingCycle extends RecyclerView.Adapter<ChatingCycle.ViewHolder> {

    private List<Chating> list;

    public ChatingCycle(List<Chating> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_chating, viewGroup, false);
        return new ChatingCycle.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Chating vo = list.get(i);

        LinearLayout position = viewHolder.position;
        LinearLayout who = viewHolder.who;
        CardView card = viewHolder.card;
        TextView name = viewHolder.name;
        TextView permission = viewHolder.permission;
        TextView date = viewHolder.date;
        TextView contents = viewHolder.contents;

        card.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        name.setText(vo.getUserInfos().getName());
        permission.setText(vo.getUserInfos().getPermission());
        date.setText(vo.getChatContentsVO().getCdate());
        contents.setText(vo.getChatContentsVO().getCcontents());

        if(vo.getUserInfos().getId().equals(UserInfo.getUid())){
            position.setGravity(Gravity.RIGHT);
            who.setVisibility(View.GONE);
            card.setCardBackgroundColor(Color.YELLOW);
        }else {
            position.setGravity(Gravity.LEFT);
            who.setVisibility(View.VISIBLE);
            if(vo.getUserInfos().getPermission().equals(Projects.OWNER))
                card.setCardBackgroundColor(Color.GREEN);
            else
                card.setCardBackgroundColor(Color.WHITE);
        }

        card.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout position;
        private LinearLayout who;
        private CardView card;
        private TextView name;
        private TextView permission;
        private TextView date;
        private TextView contents;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            position = itemView.findViewById(R.id.position);
            who = itemView.findViewById(R.id.who);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.name);
            permission = itemView.findViewById(R.id.permission);
            date = itemView.findViewById(R.id.date);
            contents = itemView.findViewById(R.id.contents);
        }
    }
}
