package com.service.taskdoc.display.recycle;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Chating;
import com.service.taskdoc.database.transfer.ChatContentsVO;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.custom.ganttchart.Line;

import java.util.List;

public class ChatingCycle extends RecyclerView.Adapter<ChatingCycle.ViewHolder> {

    private List<Chating> chatContentsList;
    private List<DocumentVO> documentList;
    private List<DecisionVO> decisionList;
    private List<ChatRoomVO> chatRoomList;

    private OnClickListener onClickListener;

    public ChatingCycle(List<Chating> chatContentsList, List<DocumentVO> documentList, List<DecisionVO> decisionList, List<ChatRoomVO> chatRoomList) {
        this.chatContentsList = chatContentsList;
        this.documentList = documentList;
        this.decisionList = decisionList;
        this.chatRoomList = chatRoomList;
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

        Chating vo = chatContentsList.get(i);

        LinearLayout position = viewHolder.position;
        LinearLayout contents = viewHolder.contents;
        LinearLayout who = viewHolder.who;
        CardView card = viewHolder.card;
        TextView name = viewHolder.name;
        TextView permission = viewHolder.permission;
        TextView date = viewHolder.date;


        /*
        * 초기화
        * */
        contents.removeAllViews();



        /*
        * 필수 데이터
        * */
        name.setText(vo.getUserInfos().getName());
        permission.setText(vo.getUserInfos().getPermission());
        date.setText(vo.getChatContentsVO().getCdate());

        if (vo.getUserInfos().getId().equals(UserInfo.getUid())) {
            position.setGravity(Gravity.RIGHT);
            who.setVisibility(View.GONE);
            card.setCardBackgroundColor(Color.YELLOW);
        } else {
            position.setGravity(Gravity.LEFT);
            who.setVisibility(View.VISIBLE);
            if (vo.getUserInfos().getPermission().equals(Projects.OWNER))
                card.setCardBackgroundColor(Color.GREEN);
            else
                card.setCardBackgroundColor(Color.WHITE);
        }



        /*
        * 선택 데이터
        * */
        if (vo.getChatContentsVO().getDmcode() != 0) {
            for (DocumentVO dvo : documentList) {
                if (vo.getChatContentsVO().getDmcode() == dvo.getDmcode()) {
                    Button button = documentSettingUi(dvo.getDmtitle(), "파일", contents);
                    button.setText("OPEN");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onClickListener!=null) onClickListener.onDocClick(card, dvo);
                        }
                    });
                }
            }
        } else if (vo.getChatContentsVO().getCrcoderef() != 0) {
            for (ChatRoomVO cvo : chatRoomList){
                if (vo.getChatContentsVO().getCrcoderef() == cvo.getCrcode()){
                    Button button = documentSettingUi(cvo.getFctitle(), "회의록", contents);
                    if (cvo.getCrclose() == 1)
                        button.setText("회의 결과 확인");
                    else
                        button.setText("회의 참석");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onClickListener!=null) onClickListener.onChaClick(card, cvo);
                        }
                    });
                }
            }
        } else if (vo.getChatContentsVO().getDscode() != 0) {
            for (DecisionVO dvo : decisionList){
                if (vo.getChatContentsVO().getDscode() == dvo.getDscode()){
                    Button button = documentSettingUi(dvo.getDstitle(), "투표", contents);
                    if (dvo.getDscode() == 1)
                        button.setText("투표 결과 확인");
                    else
                        button.setText("투표 하기");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onClickListener!=null) onClickListener.onDecClick(card, dvo);
                        }
                    });
                }
            }
        } else {
            TextView text = new TextView(card.getContext());
            text.setText(vo.getChatContentsVO().getCcontents());
            contents.addView(text);
        }

    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public Button documentSettingUi(String title, String mode, ViewGroup viewGroup){
        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        TextView modeView = new TextView(viewGroup.getContext());
        TextView titleView = new TextView(viewGroup.getContext());
        Button button = new Button(viewGroup.getContext());

        modeView.setText("TYPE : " + mode);
        titleView.setText("TITLE : " + title);

        button.setBackgroundResource(R.drawable.style_default_button);
        modeView.setGravity(Gravity.CENTER);
        titleView.setGravity(Gravity.CENTER);

        linearLayout.addView(modeView);
        linearLayout.addView(titleView);
        linearLayout.addView(button);

        viewGroup.addView(linearLayout);

        return button;
    }

    @Override
    public int getItemCount() {
        return chatContentsList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout position;
        private LinearLayout who;
        private CardView card;
        private TextView name;
        private TextView permission;
        private TextView date;
        private LinearLayout contents;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            position = itemView.findViewById(R.id.position);
            contents = itemView.findViewById(R.id.contents);
            who = itemView.findViewById(R.id.who);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.name);
            permission = itemView.findViewById(R.id.permission);
            date = itemView.findViewById(R.id.date);
        }
    }


    public interface OnClickListener {
        void onDocClick(View view, DocumentVO vo);
        void onChaClick(View view, ChatRoomVO vo);
        void onDecClick(View view, DecisionVO vo);
    }
}
