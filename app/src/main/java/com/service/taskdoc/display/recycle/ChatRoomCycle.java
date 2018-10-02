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
import com.service.taskdoc.database.business.ChatRoomInfo;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.UserInfoVO;

import org.w3c.dom.Text;

import java.util.List;

public class ChatRoomCycle extends RecyclerView.Adapter<ChatRoomCycle.ViewHolder> {

    private List<ChatRoomInfo> list;
    private OnClickListener onClickListener;

    private final String MAIN="MAIN";
    private final String SUB="SUB";

    public ChatRoomCycle(List<ChatRoomInfo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_chatroom, viewGroup, false);
        return new ChatRoomCycle.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChatRoomInfo vo = list.get(i);

        LinearLayout linear = viewHolder.linear;
        TextView users = viewHolder.users;
        TextView date = viewHolder.date;
        TextView mode = viewHolder.mode;
        TextView alarm = viewHolder.alarm;

        users.setText("");
        alarm.setText("");

        for (UserInfoVO v : vo.getUserList()) {
            if (users.getText().equals("")) {
                users.setText(v.getUname());
            } else {
                users.setText(users.getText().toString() + ", " + v.getUname());
            }
        }

        if (vo.getChatRoomVO().getCrmode() == 1){
            mode.setText(MAIN);
        } else if(vo.getChatRoomVO().getCrmode() == 2){
            mode.setText(SUB);
        }

        if (vo.getAlarm() > 0) alarm.setText(vo.getAlarm()+"");

        date.setText(vo.getChatRoomVO().getCrdate());

        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(vo);
                }
            }
        });

        linear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onLongClick();
                }
                return false;
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linear;
        private TextView users;
        private TextView date;
        private TextView alarm;
        private TextView mode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linear = itemView.findViewById(R.id.linear);
            users = itemView.findViewById(R.id.users);
            date = itemView.findViewById(R.id.date);
            alarm = itemView.findViewById(R.id.alarm);
            mode = itemView.findViewById(R.id.mode);
        }
    }

    public interface OnClickListener {
        public void onClick(ChatRoomInfo chatRoomInfo);

        public void onLongClick();
    }
}
