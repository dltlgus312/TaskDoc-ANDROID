package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.VoterVO;

import java.util.ArrayList;
import java.util.List;

public class DecisionItemListCycle extends RecyclerView.Adapter<DecisionItemListCycle.ViewHolder> {

    List<DecisionItemVO> items;
    List<List<VoterVO>> voters;

    List<SelectItem> pickers;

    VoterVO alreadyItemVO;
    VoterVO newItemVO;

    boolean picker;

    public DecisionItemListCycle(List<DecisionItemVO> items, List<List<VoterVO>> voters, boolean picker) {
        this.items = items;
        this.voters = voters;
        this.picker = picker;

        if (picker){
            pickers = new ArrayList<>();

            for (DecisionItemVO vo : items){
                SelectItem item = new SelectItem();
                item.setVo(vo);

                pickers.add(item);
            }

            for (SelectItem item : pickers){
                for (List<VoterVO> voter : voters){
                    if (item.getVo().getDsicode() == voter.get(0).getDsicode()){
                        for (VoterVO vo : voter){
                            if (vo.getUid().equals(UserInfo.getUid())){
                                alreadyItemVO = vo;
                                item.setCheck(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (!picker){
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_decision_item, viewGroup, false);
            return new ViewHolder(view);
        }else {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_decision_select, viewGroup, false);
            return new ViewHolder(view, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (!picker) {
            DecisionItemVO item = items.get(i);
            List<VoterVO> voter = null;


            LinearLayout linearLayout = viewHolder.linearLayout;
            TextView number = viewHolder.number;
            TextView title = viewHolder.title;
            TextView count = viewHolder.count;

            number.setText(item.getDsisequence() + "");
            title.setText(item.getDsilist());
            count.setText("0");

            for (List<VoterVO> v : voters) {
                if (v.get(0).getDsicode() == item.getDsicode()) {
                    count.setText(v.size() + "");
                    voter = v;
                }
            }

            List<VoterVO> finalVoter = voter;
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null)
                        onClickListener.onClickItem(item, finalVoter);
                }
            });
        }else {
            SelectItem item = pickers.get(i);

            LinearLayout linearLayout = viewHolder.linearLayout;
            TextView number = viewHolder.number;
            TextView title = viewHolder.title;
            RadioButton radio = viewHolder.radio;

            number.setText(item.getVo().getDsisequence() + "");
            title.setText(item.getVo().getDsilist());
            radio.setChecked(item.isCheck());

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioClick(item);
                }
            });

            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioClick(item);
                }
            });

        }

    }

    public void radioClick(SelectItem item){
        for (SelectItem i : pickers) i.setCheck(false);
        item.setCheck(true);

        VoterVO vo = new VoterVO();
        vo.setDsicode(item.getVo().getDsicode());
        vo.setUid(UserInfo.getUid());
        newItemVO = vo;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView number;
        TextView title;
        TextView count;

        RadioButton radio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear);
            number = itemView.findViewById(R.id.number);
            title = itemView.findViewById(R.id.title);
            count = itemView.findViewById(R.id.count);
        }

        public ViewHolder(@NonNull View itemView, boolean empty) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear);
            number = itemView.findViewById(R.id.number);
            title = itemView.findViewById(R.id.title);
            radio = itemView.findViewById(R.id.radio);
        }
    }








    /*
     * 클릭 이벤트 리스너
     * */
    private OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClickItem(DecisionItemVO item, List<VoterVO> voters);
    }







    /*
    * 투표할떄..
    * */
    public VoterVO getAlreadyItemVO() {
        return alreadyItemVO;
    }

    public void setAlreadyItemVO(VoterVO alreadyItemVO) {
        this.alreadyItemVO = alreadyItemVO;
    }

    public VoterVO getNewItemVO() {
        return newItemVO;
    }

    public void setNewItemVO(VoterVO newItemVO) {
        this.newItemVO = newItemVO;
    }







    /*
    * Select Picker Object
    * */
    public class SelectItem{
        DecisionItemVO vo;
        boolean check;

        public DecisionItemVO getVo() {
            return vo;
        }

        public void setVo(DecisionItemVO vo) {
            this.vo = vo;
        }

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }
    }
}
