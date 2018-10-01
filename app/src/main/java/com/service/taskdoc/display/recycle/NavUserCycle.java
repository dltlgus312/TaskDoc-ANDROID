package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.UserInfoVO;

import java.util.List;

public class NavUserCycle extends RecyclerView.Adapter<NavUserCycle.ViewHolder>{

    private List<UserInfos> list;

    public NavUserCycle (List<UserInfos> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_nav_user, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        UserInfos vo = list.get(i);

        LinearLayout linearLayout = viewHolder.linearLayout;
        TextView name = viewHolder.name;
        TextView permission = viewHolder.permission;
        TextView state = viewHolder.state;

        name.setText(vo.getName());
        permission.setText(vo.getPermission());
        state.setText(vo.getState());

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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView name;
        private TextView permission;
        private TextView state;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear);
            permission = itemView.findViewById(R.id.permission);
            name = itemView.findViewById(R.id.name);
            state = itemView.findViewById(R.id.state);
        }
    }

}
