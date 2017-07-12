package com.shwetak3e.loading.CustomWidgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.shwetak3e.loading.R;

/**
 * Created by cas on 10-07-2017.
 */

public class SlidingLinearLayout extends LinearLayout {

    private Animation slide_in;
    private Animation slide_out;

    public SlidingLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        slide_in= AnimationUtils.loadAnimation(context, R.anim.slide_up);
        slide_out=AnimationUtils.loadAnimation(context, R.anim.slide_down);
    }


    @Override
    public void setVisibility(int visibility) {

        if(VISIBLE==visibility){

            if(slide_in!=null){
                startAnimation(slide_in);
            }

        }else if(GONE==visibility || INVISIBLE==visibility){
             if(slide_out!=null){
                 startAnimation(slide_out);
             }
        }
        super.setVisibility(visibility);
    }
}
