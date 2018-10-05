package com.service.taskdoc.display.custom.ganttchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.service.taskdoc.service.system.support.ConvertDpPixels;

import java.util.List;

public class GanttChart extends ViewGroup {

    public final static int TOPWIDTHMONTHUNIT = 4;
    public final static int TOPWIDTHWEEKUNIT = 7;

    public static float MAXWIDTH;
    public static float MINWIDTH;
    public static float OVERMAXWIDTH;
    public static float OVERMINWIDTH;
    public static float MAXHEIGHT;
    public static float MINHEIGHT;
    public static float OVERMAXHEIGHT;
    public static float OVERMINHEIGHT;
    public static float CHANGEMODE;

    private final int WIDTHDP = 60;
    private final int HEIGHTDP = 40;
    private final int TOPHEIGHTDP = 40;

    /*
     *
     * Main Value
     *
     * */

    float width;
    float height;
    float topHeight;
    float positionX, positionY;
    float floodingX, floodingY;


    /*
     *
     * Primary Color
     *
     * */

    int backgroundColor;
    int lineColor;
    int textColor;
    int redColor;
    int blueColor;
    int toDayColor;

    int lineWidth;


    /*
     *
     * DRAW ELEMENTS
     * ( BACKGROUND, BAR )
     *
     * */

    Line line;
    Data data;




    /*
     *  생성자
     *
     * */

    public GanttChart(Context context) {
        super(context);
        init();

    }

    public GanttChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public GanttChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    protected void onDraw(Canvas canvas) {

        this.line.drawBack(canvas);

        if (onTheChartDrawListener != null) onTheChartDrawListener.drawBack(canvas);

        if (data != null) {
            this.data.draw(canvas);
        }

        this.line.drawLine(canvas);

        if (onTheChartDrawListener != null) onTheChartDrawListener.drawFront(canvas);



        sliding();
        reposition();
        countdown();
        invalidate();
    }

    void init() {
        setWillNotDraw(false);

        backgroundColor = 0xff172F28;
        lineColor = Color.GRAY;
        textColor = Color.WHITE;
        redColor = Color.RED;
        blueColor = Color.BLUE;
        toDayColor = Color.GRAY;

        topHeight = ConvertDpPixels.convertDpToPixel(TOPHEIGHTDP, getContext());
        width = ConvertDpPixels.convertDpToPixel(WIDTHDP, getContext());
        height = ConvertDpPixels.convertDpToPixel(HEIGHTDP, getContext());

        CHANGEMODE = width * 5;

        MAXWIDTH = width * 6;
        MINWIDTH = width;

        MAXHEIGHT = height * 2;
        MINHEIGHT = height;

        OVERMAXWIDTH = MAXWIDTH * 1.2f;
        OVERMINWIDTH = MINWIDTH * 0.8f;

        OVERMAXHEIGHT = MAXHEIGHT * 1.2f;
        OVERMINHEIGHT = MINHEIGHT * 0.8f;

        this.touchListenerChart = new TouchListenerChart(this);
        this.line = new Line(this);

        setOnTouchListener(touchListenerChart);
    }


    /*
     * Count ( SeekBar, DoubleClick )
     * */

    final int DOUBLECLICKCOUNT = 20;
    final int SEEKBARCOUNT = 200;

    int doubleClickCount;
    int seekBarCount;

    void countdown() {
        if (--seekBarCount <= 0 && seekBarListener != null) seekBarListener.hideSeek();
        if (doubleClickCount > 0) doubleClickCount--;
    }


    /*
     *  Touch, Business
     *
     * */

    final int TOUCHSLOP = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    TouchListenerChart touchListenerChart;

    void sliding() {
        if (touchListenerChart.stackX == 0 && touchListenerChart.stackY == 0) return;

        touchListenerChart.stackX *= 0.8f;
        touchListenerChart.stackY *= 0.8f;

        if (!touchListenerChart.moving) {
            positionX += touchListenerChart.stackX;
            positionY += touchListenerChart.stackY;
        }

        if (Math.abs(touchListenerChart.stackX) < 0.01f) touchListenerChart.stackX = 0;
        if (Math.abs(touchListenerChart.stackY) < 0.01f) touchListenerChart.stackY = 0;
    }

    void reposition() {
        // Sliding Animation Move
        floodingAnimationMove();

        // Over Position X, Y;
        overPositionMove();
    }

    void floodingAnimationMove () {
        if (floodingX == 0 && floodingY == 0) return;

        floodingX *= 0.5f;
        floodingY *= 0.5f;

        if (!touchListenerChart.moving) {
            positionX += floodingX;
            positionY += floodingY;
        }

        if (Math.abs(floodingX) < 0.01f) floodingX = 0;
        if (Math.abs(floodingY) < 0.01f) floodingY = 0;
    }

    void overPositionMove(){
        if (!touchListenerChart.moving && positionX > 0) {
            if ((positionX /= 2) < 0.1f) {
                positionX = 0;
            }
        }

        if (!touchListenerChart.moving && positionY > 0) {
            if ((positionY /= 2) < 0.1f) {
                positionY = 0;
            }
        }
    }

    BarItem onClick(float x, float y) {
        return data.onClick(x, y);
    }

    void onLongClick(float x, float y) {
        data.onLongClick(x, y);
    }



    /*
     *  Listener
     *
     * */

    SeekBarListener seekBarListener;

    OnTheBarDrawClickListener onTheBarDrawClickListener;

    OnBarClickListener onBarClickListener;

    OnTheChartDrawListener onTheChartDrawListener;

    public void setSeekBarListener(SeekBarListener seekBarListener) {
        this.seekBarListener = seekBarListener;
    }

    public SeekBarListener getSeekBarListener() {
        return seekBarListener;
    }

    public OnTheBarDrawClickListener getOnTheBarDrawListener() {
        return onTheBarDrawClickListener;
    }

    public void setOnTheBarDrawClickListener(OnTheBarDrawClickListener onTheBarDrawClickListener) {
        this.onTheBarDrawClickListener = onTheBarDrawClickListener;
        if (data != null){
            this.data.setOnTheBarDrawClickListener(new OnTheBarDrawClickListener() {
                @Override
                public void itemSelect(BarItem barItem, List<OnTheBarItem> onTheBarItemItem) {
                    if(onTheBarDrawClickListener!=null) onTheBarDrawClickListener.itemSelect(barItem, onTheBarItemItem);
                }
            });
        }
    }

    public OnBarClickListener getOnBarClickListener() {
        return onBarClickListener;
    }

    public void setOnBarClickListener(OnBarClickListener onBarClickListener) {
        this.onBarClickListener = onBarClickListener;
        if (data != null){
            this.data.setOnBarClickListener(new OnBarClickListener() {
                @Override
                public void itemSelect(BarItem barItem) {
                    if (onBarClickListener != null) onBarClickListener.itemSelect(barItem);
                }
            });
        }
    }

    public OnTheChartDrawListener getOnTheChartDrawListener() {
        return onTheChartDrawListener;
    }

    public void setOnTheChartDrawListener(OnTheChartDrawListener onTheChartDrawListener) {
        this.onTheChartDrawListener = onTheChartDrawListener;
    }

    /*
    *  Service
    * */

    public void setFloodingX(float floodingX){
        this.floodingX = floodingX;
    }

    public void setFloodingY(float floodingY){
        this.floodingY = floodingY;
    }

    public void resetSeekbarCount() {
        seekBarCount = SEEKBARCOUNT;
    }

    public void goToDay() {
        setFloodingX(-(data.getPositionX(data.getToDay()) - getWidth() / 2));
    }



    /*
    *  Getter, Setter
    * */

    public void setData(Data data) {
        this.data = data;
        this.data.setChart(this);
        if (onTheBarDrawClickListener != null){
            this.data.setOnTheBarDrawClickListener(new OnTheBarDrawClickListener() {
                @Override
                public void itemSelect(BarItem barItem, List<OnTheBarItem> onTheBarItemItem) {
                    if(onTheBarDrawClickListener!=null) onTheBarDrawClickListener.itemSelect(barItem, onTheBarItemItem);
                }
            });
        }

        if (onBarClickListener != null){
            this.data.setOnBarClickListener(new OnBarClickListener() {
                @Override
                public void itemSelect(BarItem barItem) {
                    if (onBarClickListener != null) onBarClickListener.itemSelect(barItem);
                }
            });
        }
    }

    public Data getDate(){
        return data;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    public void setLineColor(int color) {
        lineColor = color;
    }

    public void setTextColor(int color) {
        textColor = color;
    }

    public void setToDayColor(int color) {
        toDayColor = color;
    }

    public int getToDayColor() {
        return toDayColor;
    }

    public void setLineStrokeWidth(int width){
        lineWidth = width;
    }

    public float getLineStrokeWidth(){
        return lineWidth;
    }

    public void setIntervalWidth(float width){
        if (touchListenerChart != null)
            touchListenerChart.repositionWidth(width / this.width, TouchListenerChart.CENTERZOOM);
    }

    public float getIntervalWidth(){
        return width;
    }

    public float getIntervalHeight() {
        return height;
    }

    public void setIntervalHeight(float height) {
        height = height;
    }


    /*
     *  Listener Interface
     *
     * */

    public interface SeekBarListener {
        void showSeek();

        void hideSeek();

        void setSeekPosition(int width);
    }

    public interface OnTheBarDrawClickListener{
        void itemSelect(BarItem barItem, List<OnTheBarItem> onTheBarItemItems);
    }

    public interface OnTheChartDrawListener {
        void drawFront(Canvas canvas);
        void drawBack(Canvas canvas);
    }

    public interface OnBarClickListener{
        void itemSelect(BarItem barItem);
    }

}