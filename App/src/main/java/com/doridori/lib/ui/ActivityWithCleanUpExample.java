package com.doridori.lib.ui;

import android.app.Activity;
import android.os.Handler;

/**
 * Helper for performing cleanup for when the Activity is moved away from. Uses a timer as no-other way to differentiate from config-changes
 */
public abstract class ActivityWithCleanUpExample extends Activity
{
    //==================================================================================================
    // FIELDS
    //==================================================================================================

    private static final long BACKGROUND_EVENT_DELAY_MS = 2000;

    private Handler mHandler;

    private Runnable mActivityStoppedRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mStartedCount--;

            if(mStartedCount == 0)
            {
                doCleanUp();
            }
        }
    };

    private int mStartedCount = 0;

    //==================================================================================================
    // LIFECYCLE HOOKS
    //==================================================================================================

    @Override
    public void onStart()
    {
        super.onStart();
        mStartedCount++;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(mHandler == null) mHandler = new Handler();
        mHandler.postDelayed(mActivityStoppedRunnable, BACKGROUND_EVENT_DELAY_MS);
    }

    //==================================================================================================
    // ABSTRACT METHODS
    //==================================================================================================

    protected abstract void doCleanUp();
}


