package com.service.taskdoc.display.custom.ganttchart;

import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

public class TouchListenerChart implements View.OnTouchListener {

    GanttChart chart;

    static final float EXPANSION = 1.2f;
    static final float COLLAPSE = 0.8f;

    static final int CENTERZOOM = 0;
    static final int TABZOOM = 1;
    static final int POINTZOOM = 2;

    int zoommode;

    float touchX, touchY;
    float touchX2, touchY2;
    float stackX, stackY;
    float centerW, centerH;



    boolean moving = false;
    boolean zooming = false;
    boolean zoomingW = false;
    boolean zoomingH = false;
    boolean downTouch = false;
    boolean threadZooming = false;

    TouchListenerChart(GanttChart chart) {
        this.chart = chart;
    }


    /*
     * Touch Service
     * */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downTouch = true;

                touchX = event.getX();
                touchY = event.getY();

                // 1 새로운 터치가 올 때 마다 카운트다운 시작
                chart.longClickCount = chart.LONGCLICKCOUNT;

                if (chart.isClickedItem()) {
                    if (chart.longClickItem.modifyClickPosition(touchX, touchY)) {
                        if (chart.longClickItem.isMoveClicked())
                            chart.longClickItem.setCenterDate(chart.data.xPositionToDate(touchX));
                    }
                }

                if (chart.seekBarListener != null) {
                    chart.seekBarCount = chart.SEEKBARCOUNT;
                    chart.seekBarListener.showSeek();
                }

                break;
            case 261:
                touchX = event.getX(0);
                touchY = event.getY(0);
                touchX2 = event.getX(1);
                touchY2 = event.getY(1);
                break;
            case MotionEvent.ACTION_MOVE:
                move(event);
                zoom(event);
                break;
            case MotionEvent.ACTION_UP:
                if (!moving && !zooming) {

                    if (chart.doubleClickCount != 0 && !threadZooming) {
                        doubleClickZoom();
                    }

                    if (!chart.isClickedItem() || !chart.longClickItem.isModifyClick()) {
                        if (chart.isClickedItem()) chart.closeClickeditem();
                        else {
                            if (!chart.onClick(touchX, touchY))
                                chart.doubleClickCount = chart.DOUBLECLICKCOUNT;
                        }
                    }
                }
                moving = false;
                zooming = false;
                zoomingW = false;
                zoomingH = false;
                downTouch = false;
                break;
        }
        return true;
    }

    void move(MotionEvent event) {
        if (event.getPointerCount() == 2 || zooming) return;

        if (chart.isClickedItem() && chart.longClickItem.isModifyClick()) {
            actionRefBarITem(event.getX());
        } else {
            actionMoveChart(event.getX(), event.getY());
        }
    }

    void zoom(MotionEvent event) {
        if (event.getPointerCount() == 1 || moving) return;
        zooming = true;
        zoommode = POINTZOOM;

        float nX1 = event.getX(0);
        float nY1 = event.getY(0);
        float nX2 = event.getX(1);
        float nY2 = event.getY(1);

        float calcW = (Math.abs(nX1 - nX2)) / (Math.abs(touchX - touchX2));

        centerW = (touchX + touchX2) / 2;

        repositionWidth(calcW, zoommode);

        touchX = nX1;
        touchX2 = nX2;
        touchY = nY1;
        touchY2 = nY2;
    }


    /*
     * action
     * */
    synchronized void repositionWidth(float ratio, int zoomMode) {

        if (chart.width * ratio < chart.OVERMINWIDTH || chart.width * ratio > chart.OVERMAXWIDTH)
            return;

        chart.width *= ratio;
        chart.positionX *= ratio;

        switch (zoomMode) {
            case CENTERZOOM:
                chart.positionX += chart.getWidth() / 2 - (chart.getWidth() / 2 * ratio);
                break;
            case TABZOOM:
                chart.positionX += touchX - (touchX * ratio);
                break;
            case POINTZOOM:
                chart.positionX += centerW - (centerW * ratio);
                break;
        }
        if (chart.seekBarListener != null)
            chart.seekBarListener.setSeekPosition((int) chart.width);
    }

    void repositionHeight(float ratio, int zoomMode) {

        if (chart.height * ratio < chart.OVERMINHEIGHT || chart.height * ratio > chart.OVERMAXHEIGHT)
            return;

        chart.height *= ratio;
        chart.positionY *= ratio;

        switch (zoomMode) {
            case CENTERZOOM:
                chart.positionY += chart.getHeight() / 2 - (chart.getHeight() / 2 * ratio);
                break;
            case TABZOOM:
                chart.positionY += touchY - (touchY * ratio);
                break;
            case POINTZOOM:
                chart.positionY += centerH - (centerH * ratio);
                break;
        }
    }

    void actionMoveChart(float x, float y) {
        float distanceX = x - touchX;
        float distanceY = y - touchY;

        if (moving || Math.abs(distanceX) >= chart.TOUCHSLOP) {
            moving = true;
            chart.positionX += distanceX;
            stackX += distanceX;
        }

        if (moving || Math.abs(distanceY) >= chart.TOUCHSLOP) {
            moving = true;
            chart.positionY += distanceY;
            stackY += distanceY;
        }

        touchX = x;
        touchY = y;
    }

    void actionRefBarITem(float x) {
        if (chart.longClickItem.isPercentClicked()) {
            moving = true;
            int percent = (int) ((x - chart.longClickItem.getLeft()) / chart.longClickItem.getPercentage());
            chart.longClickItem.setPercent(percent);
        } else if (chart.longClickItem.isMoveClicked()) {
            moving = true;
            Calendar cal = chart.data.xPositionToDate(x);
            int diff = chart.data.diffDay(chart.longClickItem.getCenterDate(), cal);
            chart.longClickItem.moveBar(diff);
            chart.longClickItem.setCenterDate(cal);
        } else if (chart.longClickItem.issDateClicked()) {
            moving = true;
            chart.longClickItem.addSdate(chart.data.xPositionToDate(x));
        } else if (chart.longClickItem.iseDateClicked()) {
            moving = true;
            chart.longClickItem.addEdate(chart.data.xPositionToDate(x));
        }
    }


    /*
     * animation
     * */

    void doubleClickZoom() {
        Thread doubleClickThread = new Thread(new Runnable() {
            @Override
            public void run() {
                threadZooming = true;
                int speed = 30;
                zoommode = TABZOOM;
                if (chart.width <= chart.MINWIDTH) {
                    while (chart.width < chart.MAXWIDTH) {
                        try {
                            Thread.sleep(speed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        repositionWidth(EXPANSION, zoommode);
                    }
                } else {
                    while (chart.width > chart.MINWIDTH) {
                        try {
                            Thread.sleep(speed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        repositionWidth(COLLAPSE, zoommode);
                    }
                }
                threadZooming = false;
            }
        });
        doubleClickThread.start();
    }

}
