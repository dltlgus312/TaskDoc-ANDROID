package com.service.taskdoc.display.recycle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.RecoverySystem;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.transfer.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectCycle extends RecyclerView.Adapter<ProjectCycle.ViewHolder> {

    private Map<String, List<Project>> projectMap;
    private Context context;

    private final String NONPARTIC = "NON PARTIC";

    public ProjectCycle(Map<String, List<Project>> projectMap, Context context) {
        this.projectMap = projectMap;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_project, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        List<Project> projectList = new ArrayList<>();
        projectList.addAll(projectMap.get(Projects.PROJECTLIST));
        projectList.addAll(projectMap.get(Projects.PROJECTINVITELIST));

        Project project = projectList.get(i);

        CardView cardView = viewHolder.cardView;
        RelativeLayout theme = viewHolder.theme;
        TextView title = viewHolder.title;
        TextView subTitle = viewHolder.subTitle;
        TextView sdate = viewHolder.sdate;
        TextView edate = viewHolder.edate;
        TextView permision = viewHolder.permision;

        title.setText(project.getPtitle());
        subTitle.setText(project.getPsubtitle());
        sdate.setText(project.getPsdate());
        edate.setText(project.getPedate());

        if(project.getPinvite() == 0){
            permision.setText(NONPARTIC);
            theme.setBackgroundResource(R.drawable.style_project_invite);
        }else {
            String perm = project.getPpermission();
            perm = perm.toUpperCase();
            if(perm.equals(Projects.OWNER)){
                theme.setBackgroundResource(R.drawable.style_project_owner);
            }else if(perm.equals(Projects.MEMBER)){
                theme.setBackgroundResource(R.drawable.style_project_member);
            }
            permision.setText(perm);
        }

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                pListener.setLongClick(project, view);
                return false;
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pListener.setOnClick(project, view);
            }
        });

    }

    public int getItemCount() {
        int size = 0;
        size = projectMap.get(Projects.PROJECTLIST).size();
        size += projectMap.get(Projects.PROJECTINVITELIST).size();
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private RelativeLayout theme;
        private TextView title;
        private TextView subTitle;
        private TextView sdate;
        private TextView edate;
        private TextView permision;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.recycle_item_project_card);
            theme  = itemView.findViewById(R.id.recycle_item_project_theme);
            title = itemView.findViewById(R.id.recycle_item_project_title);
            subTitle = itemView.findViewById(R.id.recycle_item_project_sub_title);
            sdate = itemView.findViewById(R.id.recycle_item_project_sdate);
            edate = itemView.findViewById(R.id.recycle_item_project_edate);
            permision = itemView.findViewById(R.id.recycle_item_project_permision);

        }
    }

    private Listener pListener;

    public interface Listener {
        public void setOnClick(Project project, View view);

        public void setLongClick(Project project, View view);
    }

    public void setClick(Listener pListener) {
        this.pListener = pListener;
    }
}
