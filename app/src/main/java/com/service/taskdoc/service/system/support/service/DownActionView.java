package com.service.taskdoc.service.system.support.service;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

public class DownActionView {

    private int originalHeight;
    private boolean open = false;

    public DownActionView(int originalHeight){
        this.originalHeight = originalHeight;
    }

    public boolean isOpen(){
        return this.open;
    }

    public boolean animationDown(LinearLayout billChoices){

        ValueAnimator valueAnimator;

        if (!open) {
            billChoices.setVisibility(View.VISIBLE);
            billChoices.setEnabled(true);
            valueAnimator = ValueAnimator.ofInt(0, originalHeight);
            open = true;
        } else {
            valueAnimator = ValueAnimator.ofInt(originalHeight, 0);
            open = false;

            AlphaAnimation anim = new AlphaAnimation(1, 0);
            anim.setDuration(200);        // 에니메이션 동작 주기
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    billChoices.setVisibility(View.INVISIBLE);
                    billChoices.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            billChoices.startAnimation(anim);
        }
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                billChoices.getLayoutParams().height = value.intValue();
                billChoices.requestLayout();
            }
        });

        valueAnimator.start();
        return open;
    }

    public void animationClose(LinearLayout billChoices){
        if(open){
            open = false;
            ValueAnimator valueAnimator = ValueAnimator.ofInt(originalHeight, 0);

            AlphaAnimation anim = new AlphaAnimation(1, 0);
            anim.setDuration(200);        // 에니메이션 동작 주기
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    billChoices.setVisibility(View.INVISIBLE);
                    billChoices.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            billChoices.startAnimation(anim);
            valueAnimator.setDuration(200);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    billChoices.getLayoutParams().height = value.intValue();
                    billChoices.requestLayout();
                }
            });
            valueAnimator.start();
        }
    }
}
