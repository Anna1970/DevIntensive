package com.softdesign.devintensive.util;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.softdesign.devintensive.R;

public class CollapsingBehavior <V extends  View> extends CoordinatorLayout.Behavior<V> {

    private Context mContext;
    private int mBottomPadding, mTopPadding;

    public CollapsingBehavior(){

    }

    public CollapsingBehavior(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        mContext = context;
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState",super.onSaveInstanceState(parent,child));
        bundle.putInt("mBottomPadding",this.mBottomPadding);
        bundle.putInt("mTopPadding",this.mTopPadding);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            this.mBottomPadding = bundle.getInt("mBottomPadding");
            this.mTopPadding = bundle.getInt("mTopPadding");
        }
        super.onRestoreInstanceState(parent, child, state);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return  dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();

        /*Считываем текущее значение паддингов*/
        int t = child.getPaddingTop(),
            b = child.getPaddingBottom();

        if (t > 0) {mTopPadding = t;}
        if (b > 0) {mBottomPadding = b;}

        if (dependency.getY() + dependency.getHeight() <= getStatusBarHeight()+getActionBarSize()) {

            child.setPadding(child.getPaddingLeft(),0,child.getPaddingRight(),0);

        } else {

            child.setPadding(child.getPaddingLeft(),mTopPadding,child.getPaddingRight(),mBottomPadding);

        }

        /*Устанавливаем новую высоту child*/
        layoutParams.height = layoutParams.height + child.getPaddingTop() - t + child.getPaddingBottom() - b;

        child.setLayoutParams(layoutParams);
        child.setTranslationY(dependency.getY() + dependency.getHeight());

        /*Устанавливаем новый паддинг для NestedScroll*/
        for (int i = 0;i < parent.getChildCount()-1; i++){
            View view = parent.getChildAt(i);
            if (view.getId() == R.id.nested_scroll){
                view.setPadding(view.getPaddingLeft(),layoutParams.height,view.getPaddingRight(),view.getPaddingBottom());
            }
        }

        return true;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private float getActionBarSize() {
        final TypedArray styledAttributes = mContext.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        float actionBarSize = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarSize;
        }

}
