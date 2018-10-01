package com.service.taskdoc.display.recycle;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.UserInfos;

import java.util.List;

public class UsersCycle extends RecyclerView.Adapter<UsersCycle.ViewHolder>{

    private List<UserInfos> usersInfos;
    private OnClickListener onClickListener;

    private final String INVITE = "참여중";
    private final String NONINVITE = "미참여";

    public UsersCycle(List<UserInfos> userInfos){
        this.usersInfos = userInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_users, viewGroup, false);
        return new UsersCycle.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        UserInfos vo = usersInfos.get(i);

        CardView cardView = viewHolder.cardView;
        TextView id = viewHolder.id;
        TextView name = viewHolder.name;
        TextView state = viewHolder.state;
        TextView phone = viewHolder.phone;
        TextView permission = viewHolder.permission;
        TextView invite = viewHolder.invite;

        id.setText(vo.getId());
        name.setText(vo.getName());
        state.setText(vo.getState());
        phone.setText(vo.getPhone());
        permission.setText(vo.getPermission());

        if(vo.getPermission().equals(Projects.OWNER)){
            permission.setTextColor(Color.RED);

        }else if(vo.getPermission().equals(Projects.MEMBER)){
            permission.setTextColor(Color.BLUE);
        }

        if(vo.getInvite() == 1){
            invite.setText(INVITE);
            invite.setTextColor(Color.GREEN);
        }else if(vo.getInvite() == 0 ){
            invite.setText(NONINVITE);
            invite.setTextColor(Color.GRAY);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener!=null)
                    onClickListener.onClick(cardView, vo);
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onClickListener!=null)
                    onClickListener.onLongClick(cardView, vo);
                return false;
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return usersInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private TextView id;
        private TextView name;
        private TextView state;
        private TextView phone;
        private TextView permission;
        private TextView invite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            state = itemView.findViewById(R.id.state);
            phone = itemView.findViewById(R.id.phone);
            permission = itemView.findViewById(R.id.permission);
            invite = itemView.findViewById(R.id.invite);
        }
    }

    public interface OnClickListener{
        void onClick(CardView view, UserInfos userInfos);
        void onLongClick(CardView view, UserInfos userInfos);
    }
}
