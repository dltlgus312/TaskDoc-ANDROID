package com.service.taskdoc.display.custom.ganttchart;

import android.view.MotionEvent;
import android.view.View;

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

    BarItem refBarItem;

    TouchListenerChart(GanttChart chart) {
        this.chart = chart;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();

                if (refBarItem != null && chart.getOnBarClickListener() == null)
                    refBarItem.modifyClickPosition(touchX, touchY);

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

                if (chart.doubleClickCount != 0) {
                    doubleClickZoom();
                }// 더블 클릭 이벤트
                else if (!moving && !zooming) {
                    doubleClickThread = null;
                    chart.doubleClickCount = chart.DOUBLECLICKCOUNT;
                    refBarItem = chart.onClick(touchX, touchY);
                }

                if (refBarItem != null) refBarItem.setModifyClicked(false);

                moving = false;
                zooming = false;
                zoomingW = false;
                zoomingH = false;
                break;
        }
        return true;
    }

    void move(MotionEvent event) {
        if (event.getPointerCount() == 2 || zooming) return;

        float x = event.getX();
        float y = event.getY();

        float distanceX = x - touchX;
        float distanceY = y - touchY;

        if (refBarItem != null && refBarItem.isModifyClicked()) {
            moving = true;
            int percent = (int) ((x - refBarItem.getLeft()) / refBarItem.getPercentage());
            refBarItem.setPercent(percent);
        } else {
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
        }

        touchX = x;
        touchY = y;
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

    Thread doubleClickThread;

    void doubleClickZoom() {
        if (doubleClickThread == null) {
            doubleClickThread = new Thread(new Runnable() {
                @Override
                public void run() {
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
                }
            });
            doubleClickThread.start();
        }
    }

}
