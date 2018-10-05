package com.service.taskdoc.display.transitions.methodboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.MethodBoardVO;
import com.service.taskdoc.display.activity.MethodBoardViewActivity;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.MethodBoardService;

public class Contents extends Fragment implements NetworkSuccessWork {

    private MethodBoardVO vo;

    private int mbcode;

    private TextView id;
    private TextView code;
    private TextView date;
    private TextView contents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_method_board_contents, container, false);

        code = view.findViewById(R.id.fragment_trans_method_board_contents_code);
        id = view.findViewById(R.id.fragment_trans_method_board_contents_id);
        date = view.findViewById(R.id.fragment_trans_method_board_contents_date);
        contents = view.findViewById(R.id.fragment_trans_method_board_contents);

        contents.setMovementMethod(ScrollingMovementMethod.getInstance());

        this.vo = ((MethodBoardViewActivity)getActivity()).vo;

        code.setText(vo.getMbcode()+"");
        id.setText(vo.getUid());
        date.setText(vo.getMbdate());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // elements 에서 뒤로가키....때문에 ...
        ((MethodBoardViewActivity) getActivity()).setOnBackPressedListener(null);
        MethodBoardService service = new MethodBoardService();
        service.work(this);
        service.view(vo.getMbcode());
    }

    @Override
    public void work(Object... objects) {
        MethodBoardVO methodBoardVO = (MethodBoardVO) objects[0];

        vo.setMbcontents(methodBoardVO.getMbcontents());
        vo.setMbtitle(methodBoardVO.getMbtitle());
        vo.setPcode(methodBoardVO.getPcode());

        ((MethodBoardViewActivity)getActivity()).setTitle(vo.getMbtitle());

        contents.setText(vo.getMbcontents());
    }
}
