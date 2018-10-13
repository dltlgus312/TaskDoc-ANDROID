package com.service.taskdoc.display.recycle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.display.custom.ganttchart.Line;
import com.service.taskdoc.service.system.support.service.KeyboardManager;

import java.util.ArrayList;
import java.util.List;

public class DecisionItemMakeCycle extends RecyclerView.Adapter<DecisionItemMakeCycle.ViewHolder> {

    private final String SUCCESS = "확인";
    private final String CANCLE = "취소";
    private final String MODIFY = "수정";
    private final String DELETE = "삭제";

    private static final int VIEW_LIST = 0;
    private static final int VIEW_ADD = 1;

    private List<DecisionItemVO> vos;

    private List<MemoVO> memoVos;

    private android.support.v7.app.AlertDialog dialog;

    public DecisionItemMakeCycle() {
        vos = new ArrayList<>();
    }

    public DecisionItemMakeCycle(List<MemoVO> memoVos) {
        this.memoVos = memoVos;
    }

    @Override
    public int getItemViewType(int position) {
        if (vos != null){
            if (position == vos.size()) {
                return VIEW_ADD;
            } else {
                return VIEW_LIST;
            }
        } else {
            if (position == memoVos.size()) {
                return VIEW_ADD;
            } else {
                return VIEW_LIST;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        getItemViewType(i);
        if (vos != null && vos.size() == i || memoVos != null && memoVos.size() == i) {
            // 추가 버튼
            LinearLayout linearLayout = viewHolder.linearLayout;
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (vos != null) {
                        DecisionItemVO vo = new DecisionItemVO();
                        vo.setDsisequence(vos.size() + 1);
                        vos.add(vo);
                    } else {
                        MemoVO vo = new MemoVO();
                        memoVos.add(vo);
                    }

                    if (addClickRePositionListener != null)
                        addClickRePositionListener.onClick();
                }
            });
        } else {
            // 목록 보여주기
            DecisionItemVO vo = null;
            MemoVO memoVo = null;

            if (vos != null)
                vo = vos.get(i);
            else memoVo = memoVos.get(i);

            EditText contents = viewHolder.contents;
            TextView modify = viewHolder.modify;
            TextView delete = viewHolder.delete;
            TextView date = viewHolder.date;

            date.setVisibility(View.GONE);

            contents.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    }
                }
            });

            if (vo != null && vo.getDsilist() == null || memoVo != null && memoVo.getMcontents() == null) {
                contents.setText("");
                contents.requestFocus();
                new KeyboardManager().show(contents.getContext());
                setModifyMode(modify, delete, contents); //새로 작성
            } else {
                if (vo != null)
                    contents.setText(vo.getDsilist());
                else
                    contents.setText(memoVo.getMcontents());

                setSuccessMode(modify, delete, contents, null);
            }


            DecisionItemVO finalVo = vo;
            MemoVO finalMemoVo = memoVo;
            contents.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    //Enter key Action
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        if (finalVo != null)
                            addElementsParam(modify, delete, contents, finalVo);
                        else
                            addElementsParam(modify, delete, contents, finalMemoVo);
                        return true;
                    }
                    return false;
                }
            });

            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // 수정 버튼
                    if (modify.getText().toString().equals(MODIFY)) {
                        setModifyMode(modify, delete, contents);
                    } else { // 수정완료 버튼
                        if (finalVo != null)
                            addElementsParam(modify, delete, contents, finalVo);
                        else
                            addElementsParam(modify, delete, contents, finalMemoVo);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (delete.getText().toString().equals(DELETE)) {
                        if (finalVo != null)
                            vos.remove(finalVo);
                        else{
                            if (sucessClickListener != null) sucessClickListener.onDelete(memoVos, finalMemoVo);
                        }

                        notifyDataSetChanged();
                    } else {
                        if (finalVo != null)
                            setSuccessMode(modify, delete, contents, null);
                        else
                            setSuccessMode(modify, delete, contents, finalMemoVo);
                    }
                }
            });
        }
    }


    public void setModifyMode(TextView modify, TextView delete, EditText contents) {
        modify.setText(SUCCESS);
        delete.setText(CANCLE);
        contents.setEnabled(true);
    }

    public void setSuccessMode(TextView modify, TextView delete, EditText contents, MemoVO vo) {
        modify.setText(MODIFY);
        delete.setText(DELETE);
        contents.setEnabled(false);

        if(sucessClickListener != null && vo != null) sucessClickListener.onClick(vo, contents.getText().toString());
    }

    public boolean addElementsParam(TextView modify, TextView delete, EditText contents, DecisionItemVO vo) {
        String s = contents.getText().toString();
        if (s.equals("")) {
            Toast.makeText(dialog.getContext(),
                    "항목 이름이 너무 짧습니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            setSuccessMode(modify, delete, contents, null);
            vo.setDsilist(s);
            return true;
        }
    }

    public boolean addElementsParam(TextView modify, TextView delete, EditText contents, MemoVO vo) {
        String s = contents.getText().toString();
        if (s.equals("")) {
            Toast.makeText(dialog.getContext(),
                    "내용이 너무 짧습니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            setSuccessMode(modify, delete, contents, vo);
            return true;
        }
    }


    /*
     * Creates Parameter Data
     * */
    public List<DecisionItemVO> getVos() {
        List<DecisionItemVO> vos = new ArrayList<>();

        for (DecisionItemVO vo : this.vos) {
            if (vo.getDsilist() != null && !vo.getDsilist().equals("")) {
                vos.add(vo);
            }
        }

        return vos;
    }

    public void setVos(List<DecisionItemVO> vos) {
        this.vos = vos;
    }

    public android.support.v7.app.AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(android.support.v7.app.AlertDialog dialog) {
        this.dialog = dialog;
    }


    /*
     * Add Click Re Position Listener
     * */
    private AddClickRePositionListener addClickRePositionListener;

    private SucessClickListener sucessClickListener;

    public SucessClickListener getSucessClickListener() {
        return sucessClickListener;
    }

    public void setSucessClickListener(SucessClickListener sucessClickListener) {
        this.sucessClickListener = sucessClickListener;
    }

    public AddClickRePositionListener getAddClickRePositionListener() {
        return addClickRePositionListener;
    }

    public void setAddClickRePositionListener(AddClickRePositionListener addClickRePositionListener) {
        this.addClickRePositionListener = addClickRePositionListener;
    }

    public interface AddClickRePositionListener {
        void onClick();
    }

    public interface SucessClickListener{
        void onClick(MemoVO vo, String text);
        void onDelete(List<MemoVO> memos, MemoVO vo);
    }


    /*
     * Necessary Override Method
     * */
    @Override
    public int getItemCount() {
        if (vos != null)
            return vos.size() + 1;
        else
            return memoVos.size() + 1;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_LIST) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_memo, viewGroup, false);
            return new DecisionItemMakeCycle.ViewHolder(view);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_button, viewGroup, false);
            return new DecisionItemMakeCycle.ViewHolder(view, true);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private EditText contents;
        private TextView date;
        private TextView modify;
        private TextView delete;

        private LinearLayout linearLayout;

        boolean empty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.contents = itemView.findViewById(R.id.contents);
            this.date = itemView.findViewById(R.id.date);
            this.modify = itemView.findViewById(R.id.modify);
            this.delete = itemView.findViewById(R.id.delete);
        }

        public ViewHolder(@NonNull View itemView, boolean empty) {
            super(itemView);
            this.empty = empty;
            this.linearLayout = itemView.findViewById(R.id.linear);
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
    }

}
