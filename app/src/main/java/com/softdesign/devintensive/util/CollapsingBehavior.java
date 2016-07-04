package com.softdesign.devintensive.util;


import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CollapsingBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    private Context mContext;

    public CollapsingBehavior(){

    }

    public CollapsingBehavior(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return  dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        int tempHeight = child.getHeight();
        float depY = dependency.getY()-228;

        if (depY >= 128 && depY <=224){
            tempHeight = (int)(depY/3);
        }
        dependency.setPadding(dependency.getPaddingLeft(),tempHeight,dependency.getPaddingRight(),dependency.getPaddingBottom());
        child.setY(dependency.getY());
        if (depY <= 128) {
            child.setPadding(child.getPaddingLeft(), 0, child.getPaddingRight(), 0);
        } else {
            child.setPadding(child.getPaddingLeft(), 28, child.getPaddingRight(), 28);
        }

        return true;
    }

}
