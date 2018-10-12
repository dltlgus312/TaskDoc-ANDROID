package com.service.taskdoc.display.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.service.system.support.service.DownActionView;

import java.util.ArrayList;
import java.util.List;

public class TaskCycle extends RecyclerView.Adapter<TaskCycle.ViewHolder> {

    private Tasks tasks;

    private ClickListener clickListener;
    private ChartClickListener chartClickListener;
    private RecycleExpandClickListener recycleExpandClickListener;

    private boolean isGoBack = false;
    private boolean output = false;
    private int contextVisible = View.VISIBLE;
    private int downArrowVisible = View.GONE;

    private List<Task> copyTasks = new ArrayList<>();
    private List<List<Task>> stack = new ArrayList<>();

    private DownActionView downActionView;
    private int originalHeight;


    public TaskCycle(Tasks tasks) {
        this.tasks = tasks;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Task task = copyTasks.get(i);

        CardView cardView = viewHolder.cardView;
        LinearLayout content = viewHolder.content;
        TextView title = viewHolder.title;
        TextView down = viewHolder.down;
        TextView sdate = viewHolder.sdate;
        TextView edate = viewHolder.edate;
        TextView kind = viewHolder.kind;
        ImageView next = viewHolder.next;
        PieChart chart = viewHolder.chart;

        ImageButton expand_button = viewHolder.expand_button;
        LinearLayout expand_linear = viewHolder.expand_linear;
        RecyclerView expand_recycle = viewHolder.expand_recycle;
        TextView expand_add = viewHolder.expand_add;

        title.setText(task.getTitle());
        sdate.setText(task.getSdate());
        edate.setText(task.getEdate());
        drawChart(chart, task);

        /* TITLE COLOR */
        int color = 0xbb000000;
        if(task.getColor() == null)
            color += 0x00f0f0f0;
        else
            color += Integer.parseInt(task.getColor(), 16);
        title.setBackgroundColor(color);

        /* Type */
        boolean isPublic = task.getRefpcode()!=0 ? true:false;
        if (isPublic) {
            kind.setText(Tasks.PUBLIC);
            kind.setTextColor(0xaadd0000);
            expand_button.setVisibility(View.GONE);
            expand_linear.setVisibility(View.GONE);
        } else {
            kind.setText(Tasks.PRIVATE);
            kind.setTextColor(0xaa00aa00);
            expand_button.setVisibility(View.VISIBLE);
            expand_linear.setVisibility(View.GONE);
            expand_recycle.setAdapter(null);
        }

        /* Hierarchy */
        int down_count = downCount(task, isPublic);
        if (down_count != 0) {
            down.setVisibility(View.VISIBLE);
            down.setText(down_count + "");
        } else {
            down.setVisibility(View.GONE);
        }

        /* Methd Mode */
        content.setVisibility(contextVisible);
        next.setVisibility(downArrowVisible);

        if (i == copyTasks.size() - 1) {
            next.setVisibility(View.GONE);
        }

        /* Click Event */
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(task);
            }
        });
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickListener.onLongClick(cardView, task);
                return false;
            }
        });
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartClickListener.onChartClick(task);
            }
        });
        expand_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recycleExpandClickListener != null &&  downActionView.animationDown(expand_linear) && expand_recycle.getAdapter() == null){
                    recycleExpandClickListener.onExpandClick(expand_recycle, task.getCode());
                }
            }
        });
        expand_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycleExpandClickListener.onExpandAddClick(expand_recycle, task.getCode());
            }
        });

    }

    @Override
    public int getItemCount() {
        return copyTasks.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_task, viewGroup, false);
        return new ViewHolder(view);
    }


    /* Business */
    public void init() {

        copyTasks.clear();
        stack.clear();

        if (tasks.isPubilc()) {
            if (output) {
                for (Task t : tasks.getPublicTasks()) {
                    if (tasks.getPublicTasks().get(0).getCode() == t.getRefference() && !t.getTitle().equals(Tasks.OUTPUT)) {
                        copyTasks.add(t);
                    }
                }
            } else {
                for (Task t : tasks.getPublicTasks()) {
                    if (t.getCode() == t.getRefference() && !t.getTitle().equals(Tasks.OUTPUT) && t.getSdate() != null) {
                        copyTasks.add(t);
                    }
                }
            }
        } else {
            for (Task t : tasks.getPrivateTasks()) {
                if (t.getCode() == t.getRefference()) {
                    copyTasks.add(t);
                }
            }
        }

        notifyDataSetChanged();
    }
    public int downCount(Task task, boolean isPublic ){

        int down_count = 0;

        if (isPublic) {
            for(Task t : tasks.getPublicTasks()){
                if (task.getCode() == t.getRefference() && task != t){
                    down_count++;
                }
            }
            for (Task t : tasks.getPrivateTasks()) {
                if (task.getCode() == t.getReftcode() && t.getCode() == t.getRefference()){
                    down_count++;
                }
            }
        } else {
            for (Task t : tasks.getPrivateTasks()) {
                if (task.getCode() == t.getRefference() && task != t){
                    down_count++;
                }
            }
        }
        return down_count;
    }
    public void drawChart(PieChart chart, Task task){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        pieEntries.add(new PieEntry(task.getPercent(), ""));
        pieEntries.add(new PieEntry(100 - task.getPercent(), ""));

        colors.add(0xffffafaf);
        colors.add(0xfff0f0f0);

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        PieData pieData = new PieData(dataSet);

        chart.setData(pieData);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);

        chart.setCenterText(task.getPercent() + "%");
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }
    public void setCopytTask(Task task){
        copyTasks.add(task);
    }
    public void removeCopyTask(Task task){
        copyTasks.remove(task);
    }




    /* Click, Long Click Event */
    public void go(Task task) {

        List<Task> s = new ArrayList<>();
        s.addAll(copyTasks);

        stack.add(s);
        copyTasks.clear();

        List<Task> tasks = new ArrayList<>();
        if (task.getRefpcode() != 0) {
            for (Task t : this.tasks.getPublicTasks()) {
                if (t.getRefference() == task.getCode() && t != task) {
                    if (output) {
                        tasks.add(t);
                    } else if (t.getSdate() != null) {
                        tasks.add(t);
                    }
                }
            }
            for (Task t : this.tasks.getPrivateTasks()) {
                if (t.getReftcode() == task.getCode() && t.getRefference() == t.getCode()) {
                    tasks.add(t);
                }
            }
        } else {
            for (Task t : this.tasks.getPrivateTasks()) {
                if (t.getRefference() == task.getCode() && t != task) {
                    tasks.add(t);
                }
            }
        }

        this.isGoBack = true;
        copyTasks.addAll(tasks);
        notifyDataSetChanged();
    }
    public void goTo(int index) {
        if (index >= stack.size()) return;
        copyTasks.clear();
        copyTasks.addAll(stack.get(index));
        int size = stack.size();
        for (int i = index; i < size; i++) {
            stack.remove(stack.size() - 1);
        }

        if (index == 0) {
            this.isGoBack = false;
        }

        if (index == 0) init();
        notifyDataSetChanged();
    }
    public void back() {
        int index = stack.size() - 1;
        copyTasks.clear();
        copyTasks.addAll(stack.get(index));
        stack.remove(index);

        if (index == 0) {
            this.isGoBack = false;
        }

        notifyDataSetChanged();
    }
    public boolean canGoBack() {
        return this.isGoBack;
    }




    /* Setting */
    public void setContentVisible(int contentVi) {
        this.contextVisible = contentVi;
    }
    public void setOutput(boolean output) {
        this.output = output;
    }
    public void setDownArrowVisible(int downArrowVisible) {
        this.downArrowVisible = downArrowVisible;
    }




    /* Setter */
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    public void setChartClickListener(ChartClickListener chartClickListener) {
        this.chartClickListener = chartClickListener;
    }
    public void setRecycleExpandClickListener(RecycleExpandClickListener recycleExpandClickListener, int originalHeight){
        this.recycleExpandClickListener = recycleExpandClickListener;
        this.originalHeight = originalHeight;
        downActionView = new DownActionView(originalHeight);
    }




    /* Listener */
    public interface ClickListener {
        public void onClick(Task task);

        public void onLongClick(View view, Task task);
    }
    public interface ChartClickListener {
        public void onChartClick(Task Task);
    }
    public interface RecycleExpandClickListener{
        void onExpandClick(RecyclerView recyclerView, int ptcode);
        void onExpandAddClick(RecyclerView recyclerView, int ptcode);
    }




    /* Holder */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private LinearLayout content;
        private TextView title;
        private TextView down;
        private TextView sdate;
        private TextView edate;
        private TextView kind;
        private ImageView next;
        private PieChart chart;

        private ImageButton expand_button;
        private LinearLayout expand_linear;
        private RecyclerView expand_recycle;
        private TextView expand_add;

        public ViewHolder(@NonNull View view) {
            super(view);

            cardView = view.findViewById(R.id.recycle_item_task_card);
            content = view.findViewById(R.id.recycle_item_task_content);
            title = view.findViewById(R.id.recycle_item_task_title);
            down = view.findViewById(R.id.recycle_item_task_down);
            sdate = view.findViewById(R.id.recycle_item_task_sdate);
            edate = view.findViewById(R.id.recycle_item_task_edate);
            kind = view.findViewById(R.id.recycle_item_task_kind);
            next = view.findViewById(R.id.recycle_item_task_next);
            chart = view.findViewById(R.id.recycle_item_task_piechart);

            expand_button = view.findViewById(R.id.recycle_item_task_expand);
            expand_linear = view.findViewById(R.id.recycle_item_task_recycle);
            expand_recycle = view.findViewById(R.id.recycle);
            expand_add = view.findViewById(R.id.add);
        }
    }

}
