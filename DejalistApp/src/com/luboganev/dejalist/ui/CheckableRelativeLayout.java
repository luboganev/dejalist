package com.luboganev.dejalist.ui;

import com.luboganev.dejalist.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private boolean mChecked;
    private StateListDrawable mSelectionDrawable;

    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    public CheckableRelativeLayout(Context context) {
        this(context, null);
        
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSelectionDrawable = (StateListDrawable) context.getResources().getDrawable(R.drawable.grid_item_fg_selector);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    	mSelectionDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    	mSelectionDrawable.draw(canvas);
    }

    /**********************/
    /** Handle clicks **/
    /**********************/

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    /**************************/
    /** Checkable **/
    /**************************/

    public void toggle() {
        setChecked(!mChecked);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
            setCheckedRecursive(this, checked);
        }
    }

    private void setCheckedRecursive(ViewGroup parent, boolean checked) {
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = parent.getChildAt(i);
            if (v instanceof Checkable) {
                ((Checkable) v).setChecked(checked);
            }

            if (v instanceof ViewGroup) {
                setCheckedRecursive((ViewGroup) v, checked);
            }
        }
    }

    /**************************/
    /** Drawable States **/
    /**************************/

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mSelectionDrawable != null) {
            int[] myDrawableState = getDrawableState();
            mSelectionDrawable.setState(myDrawableState);
            invalidate();
        }
    }
}