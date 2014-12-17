package com.doridori.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

/**
 * Class to work around the annoying queuing of multiple taps for Buttons. Could have a time attribute.
 *
 * This is useful as sometimes (on some platform / device combos) you can quickly tap on a button and end up
 * starting an activity more than one. The reuests seem to be queued before the action has taken place so
 * solutions like disabling the buttons or setting singleTop do not seem to work :(
 * 
 * Alternativly a better solution maybe to put this in a OnClickListener subclass and then could use with any view
 *
 * @author dori
 */
public class SafeButton extends Button {
    public SafeButton(Context context) {
        super(context);
    }

    public SafeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SafeButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(new OnClickListenerWrapper(l));
    }

    private class OnClickListenerWrapper implements OnClickListener{

        private static final long DEFAULT_MIN_INTERVAL = 1500;
        private long mLastClickTime = 0;
        private final OnClickListener mListener;

        public OnClickListenerWrapper(OnClickListener listener){
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            if(currentTime - mLastClickTime > DEFAULT_MIN_INTERVAL){
                mListener.onClick(v);
                mLastClickTime = currentTime;
            }
        }
    }
}
