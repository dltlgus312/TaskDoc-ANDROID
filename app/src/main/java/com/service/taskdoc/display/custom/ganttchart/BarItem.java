package com.service.taskdoc.display.custom.ganttchart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class BarItem {

    private final int startTextPosition = 50;

    private boolean division = false;
    private boolean barClicked = false;
    private boolean titleClicked = false;
    private boolean modifyClicked = false;
    private boolean percentDraw = true;
    private boolean clickable = true;


    private float titleTextSize;
    private float titleTextWidth;

    private Paint barColor;
    private Paint background;
    private Paint textPaint;
    private Paint strokePaint;
    private Paint modifyArcPaint;
    private int textColor;

    private float percentTextSize;
    private int highlightColor;

    private float height;

    private RectF modifyArc;

    private float percentage;

    private List<OnTheBarItem> onTheBarItems;

    private List<Integer> arrowList;

    private int depthArrow;

    private final int HIGHLIGHTCOUNT = 100;

    private int count;


    /*
     *
     *  Main Value
     * */

    private float left;
    private float top;
    private float right;
    private float bottom;

    private int percent;
    private String color;
    private String title;
    private Calendar sdate;
    private Calendar edate;


    public BarItem() {
        background = new Paint();
        barColor = new Paint();
        textPaint = new Paint();
        strokePaint = new Paint();
        modifyArcPaint = new Paint();

        textColor = Color.WHITE;
        highlightColor = Color.YELLOW;

        barColor.setStrokeWidth(3);

        background.setColor(0xbbf0f0f0);

        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);

        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);

        modifyArcPaint.setColor(Color.CYAN);
        modifyArcPaint.setStrokeWidth(3);

        depthArrow = 6;
    }

    public BarItem(boolean division) {
        this.division = division;
    }



    /*
     *
     * Draw
     * */

    void drawBar(float left, float top, float right, float bottom, Canvas canvas) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        percentage = (right - left) / 100f;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (0 < count) {
                canvas.drawRoundRect(left-3, top-3, right+3, bottom+3, 10, 10, modifyArcPaint);
                count--;
            }
            canvas.drawRoundRect(left, top, right, bottom, 10, 10, background);
            canvas.drawRoundRect(left, top, left + percentage * percent, bottom, 10, 10, barColor);
        } else {
            if (0 < count) {
                canvas.drawRect(left-3, top-3, right+3, bottom+3, modifyArcPaint);
                count--;
            }
            canvas.drawRect(left, top, right, bottom, background);
            canvas.drawRect(left, top, left + percentage * percent, bottom, barColor);
        }

        if (barClicked && onBarClickListener == null) drawModifyPercent(canvas);
    }

    void drawTitle(Canvas canvas) {
        if (sdate == null) return;

        if (titleTextSize == 0) calculation();
        if (percentDraw) drawPercent(canvas);

        canvas.drawText(title, startTextPosition, ((top + bottom) / 2) + titleTextSize * 0.4f, strokePaint);
        canvas.drawText(title, startTextPosition, ((top + bottom) / 2) + titleTextSize * 0.4f, textPaint);
    }

    void drawArrow(float x, float y, Canvas canvas) {

        canvas.drawLine(left, bottom - 10, left - depthArrow * 10, bottom - 10, barColor);
        canvas.drawLine(left - depthArrow * 10, bottom - 10, left - depthArrow * 10, y, barColor);
        canvas.drawLine(left - depthArrow * 10, y, x, y, barColor);

        canvas.drawLine(x - 10, y - 10, x, y, barColor);
        canvas.drawLine(x - 10, y + 10, x, y, barColor);
    }

    void drawModifyPercent(Canvas canvas) {
        if (onBarClickListener == null) {
            modifyArc = new RectF(left - titleTextSize + percentage * percent,
                    top - titleTextSize * 2, left + titleTextSize + percentage * percent, top);

            canvas.drawArc(modifyArc, 0, 360, false, modifyArcPaint);
            canvas.drawLine(modifyArc.centerX(), top, modifyArc.centerX(), bottom, modifyArcPaint);
        } else {

        }
    }

    void drawPercent(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(percentTextSize);

        if (percent == 100) {
            paint.setColor(highlightColor);
        } else {
            paint.setColor(textColor);
        }
        canvas.drawText(percent + "%", right + (height / 3), bottom, paint);
    }



    /*
     *
     * Click Event
     * */

    boolean clickPosition(float x, float y) {


        boolean click = false;

        if (this.onTheBarItems != null && this.onTheBarItems.size() > 0 && onTheBarDrawClickListener != null) {
            List<OnTheBarItem> list = new ArrayList<>();
            for (OnTheBarItem b : this.onTheBarItems) {
                if (b.onClickPosition(x, y)) {
                    list.add(b);
                }
            }
            if (list.size() > 0) {
                onTheBarDrawClickListener.itemSelect(this, list);
                return false;
            }
        }

        if (isClickable() && x < right && x > left && y < bottom && y > top) {
            if (onBarClickListener != null) {
                onBarClickListener.itemSelect(this);
                textPaint.setColor(Color.GREEN);
                barClicked = true;
                click = true;
            } else {
                textPaint.setColor(Color.CYAN);
                barClicked = true;
                click = true;
            }
        } else if (x > startTextPosition && x < startTextPosition + titleTextWidth && y < bottom && y > top) {
            titleClicked = true;
            click = true;
        }

        return click;
    }

    void modifyClickPosition(float x, float y) {
        if (x > modifyArc.left && x < modifyArc.right && y > modifyArc.top && y < modifyArc.bottom) {
            modifyClicked = true;
        } else modifyClicked = false;
    }

    boolean longClick(float x, float y) {
        boolean click = false;


        return click;
    }

    void closeClick() {
        textPaint.setColor(textColor);
        barClicked = false;
    }

    void closeLongClick() {

    }


    /*
     *
     * Override
     * */
    protected abstract void updatePercent(int percent);


    /*
     *
     * Service
     * */

    boolean isModifyClicked() {
        return modifyClicked;
    }

    void setModifyClicked(boolean modifyClicked) {
        this.modifyClicked = modifyClicked;
    }

    boolean isTitleClicked() {
        return titleClicked;
    }

    void setTitleClicked(boolean titleClicked) {
        this.titleClicked = titleClicked;
    }

    float getPercentage() {
        return percentage;
    }

    void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    void calculation() {
        height = bottom - top;
        titleTextSize = height * 0.5f;
        percentTextSize = titleTextSize * 0.8f;

        textPaint.setTextSize(titleTextSize);
        strokePaint.setTextSize(titleTextSize);
        titleTextWidth = textPaint.measureText(title, 0, title.length());
    }



    /*
     *
     *  Getter, Setter
     * */

    public void setPercent(int percent) {
        if (percent < 0) percent = 0;
        else if (percent > 100) percent = 100;
        this.percent = percent;

        updatePercent(percent);
    }

    public int getPercent() {
        return percent;
    }

    public void setOnTheBarItems(List<OnTheBarItem> onTheBarItems) {
        this.onTheBarItems = onTheBarItems;
    }

    public void addOnTheBarDraws(OnTheBarItem onTheBarItem) {
        if (this.onTheBarItems == null) {
            this.onTheBarItems = new ArrayList<>();
        }
        onTheBarItems.add(onTheBarItem);
    }

    public List<OnTheBarItem> getOnTheBarItems() {
        return onTheBarItems;
    }

    public void addArrowList(int index) {
        if (this.arrowList == null) arrowList = new ArrayList<>();

        arrowList.add(index);
    }

    public List<Integer> getArrowList() {
        return arrowList;
    }

    public Calendar getSdate() {
        return sdate;
    }

    public void setSdate(Calendar sdate) {
        this.sdate = sdate;
    }

    public Calendar getEdate() {
        return edate;
    }

    public void setEdate(Calendar edate) {
        this.edate = edate;
        this.edate.add(Calendar.DATE, 1);
    }

    public void setBarColor(String color) {
        this.color = color;

        int c = 0xff000000;

        c += Integer.parseInt(this.color, 16);
        barColor.setColor(c);
    }

    public void setBarColor(int color) {
        barColor.setColor(color);
    }

    public String getBarColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isBarClicked() {
        return this.barClicked;
    }

    public void setBarClicked(boolean barClicked) {
        this.barClicked = barClicked;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isPercentDraw() {
        return percentDraw;
    }

    public void setPercentDraw(boolean percentDraw) {
        this.percentDraw = percentDraw;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
    }

    public int getDepthArrow() {
        return depthArrow;
    }

    public void setDownDepth(int depthArrow) {
        if (depthArrow == 0) this.depthArrow = 1;
        this.depthArrow = depthArrow;
    }

    public boolean isDivision() {
        return division;
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setHighlightCount() {
        this.count = HIGHLIGHTCOUNT;
    }



    /*
     *
     *  Listener
     * */

    GanttChart.OnTheBarDrawClickListener onTheBarDrawClickListener;

    GanttChart.OnBarClickListener onBarClickListener;

    public GanttChart.OnBarClickListener getOnBarClickListener() {
        return onBarClickListener;
    }

    public void setOnBarClickListener(GanttChart.OnBarClickListener onBarClickListener) {
        this.onBarClickListener = onBarClickListener;
    }

    public GanttChart.OnTheBarDrawClickListener getOnTheBarDrawClickListener() {
        return onTheBarDrawClickListener;
    }

    public void setOnTheBarDrawClickListener(GanttChart.OnTheBarDrawClickListener onBarClickListener) {
        this.onTheBarDrawClickListener = onBarClickListener;
    }

}

