package com.service.taskdoc.display.transitions.task;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.service.taskdoc.R;

import java.util.ArrayList;
import java.util.List;

public class Chart extends Fragment {

    private LineChart line;
    private PieChart pie;
    private BarChart bar;

    public Chart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task__chart, container, false);

        line = view.findViewById(R.id.line);
        pie = view.findViewById(R.id.pie);
        bar = view.findViewById(R.id.bar);

        pieChart();
        barChart();
        lineChart();

        return view;
    }

    public void pieChart(){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        pieEntries.add(new PieEntry(10, "text1"));
        pieEntries.add(new PieEntry(20, "text2"));
        pieEntries.add(new PieEntry(30, "text3"));
        pieEntries.add(new PieEntry(40, "text4"));

        colors.add(0xff0f0f0f);
        colors.add(0xffffafaf);
        colors.add(0xffafafff);
        colors.add(0xffafffaf);

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);

        pie.setData(pieData);
        pie.setCenterText("업무 분포도");
        pie.invalidate();
    }


    public void barChart(){
        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        ArrayList<BarEntry> barEntries3 = new ArrayList<>();
        ArrayList<BarEntry> barEntries4 = new ArrayList<>();

        barEntries1.add(new BarEntry(1, 10));
        BarDataSet dataSet1 = new BarDataSet(barEntries1, "test1");
        dataSet1.setColor(0xff0f0f0f);

        barEntries2.add(new BarEntry(2, 10));
        BarDataSet dataSet2 = new BarDataSet(barEntries2, "test2");
        dataSet2.setColor(0xffffafaf);

        barEntries3.add(new BarEntry(3, 10));
        BarDataSet dataSet3 = new BarDataSet(barEntries3, "test3");
        dataSet3.setColor(0xffafafff);

        barEntries4.add(new BarEntry(4, 10));
        BarDataSet dataSet4 = new BarDataSet(barEntries4, "test4");
        dataSet4.setColor(0xffafffaf);

        List<IBarDataSet> iBarDataSets = new ArrayList<>();
        iBarDataSets.add(dataSet1);
        iBarDataSets.add(dataSet2);
        iBarDataSets.add(dataSet3);
        iBarDataSets.add(dataSet4);

        BarData data = new BarData(iBarDataSets);

        bar.setData(data);
        bar.invalidate();
    }


    public void lineChart(){

        ArrayList<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 10));
        entries1.add(new Entry(5, 20));
        entries1.add(new Entry(8, 10));
        entries1.add(new Entry(12, 14));

        ArrayList<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(5, 16));
        entries2.add(new Entry(0, 10));
        entries2.add(new Entry(12, 8));
        entries2.add(new Entry(20, 19));

        LineDataSet dataSet1 = new LineDataSet(entries1, "test1");
        dataSet1.setColor(0xff0f0f0f);

        LineDataSet dataSet2 = new LineDataSet(entries2, "test2");
        dataSet1.setColor(0xffffafaf);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);

        LineData data = new LineData(dataSets);

        line.setData(data);
        line.invalidate();
    }
}
