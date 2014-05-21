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

    public AspectImageView(Context context)
    {
        super(context);
        throw new RuntimeException("Use a constructor with attrs");
    }

    public AspectImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        getCustomAttrs(context, attrs);
    }

    public AspectImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        getCustomAttrs(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() * mHeightFactor));
    }

    private void getCustomAttrs(Context context, AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AspectImageView);

        // get state layout res id's if present, else use default
        mHeightFactor = array.getFloat(
                R.styleable.AspectImageView_heightFactor,
                -1);

        if(mHeightFactor <= 0)
            throw new RuntimeException("you must set the 'heightFactor' xml attr and is must be greater than 0");

        array.recycle();
    }
}
