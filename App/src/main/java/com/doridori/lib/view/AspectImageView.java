package com.doridori.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.androidutils.app.R;

/**
 * Image view that will have a set aspect ratio as a function of view width
 */
public class AspectImageView extends ImageView
{
    /**
     * the height as a function of width
     */
    private float mHeightFactor;
    private float mWidthFactor;

    public AspectImageView(Context context)
    {
        this(context, null);
    }

    public AspectImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        getCustomAttrs(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mHeightFactor > 0)
            setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() * mHeightFactor));
        if(mWidthFactor > 0)
            setMeasuredDimension((int) (getMeasuredHeight()*mWidthFactor), getMeasuredHeight());
    }

    private void getCustomAttrs(Context context, AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AspectImageView);

        // get state layout res id's if present, else use default
        mHeightFactor = array.getFloat(
                R.styleable.AspectImageView_heightFactor,
                -1);

        mWidthFactor = array.getFloat(
                R.styleable.AspectImageView_widthFactor,
                -1);

        if(mHeightFactor <= 0 && mWidthFactor <= 0)
            throw new RuntimeException("you must set the 'heightFactor' || 'widthFactor' xml attr and it must be greater than 0");

        if(mHeightFactor > 0 && mWidthFactor > 0)
            throw new RuntimeException("you can only set one aspect xml attr");

        array.recycle();
    }
}