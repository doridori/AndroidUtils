/*
 * Copyright 2011 Dorian Cussen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doridori.lib.app;

import android.os.Handler;

import de.greenrobot.event.EventBus;

/**
 * As there is no global callback for the state of the app i.e. is the app in the foreground or not you can have a base class
 * call this in onStart and onStop and it will do a global event for anyone who is interested. Useful for implementing some
 * form of security when the app moves to the foreground
 *
 * This works when moving from one activity to the next as onStart of the next activity is called before onStop of the current activity. However on rotation
 * onStop is called before onStart of the next, which breaks this count. I have introduced a delay timer for after {@link #activityStopped()} is called to get around this.
 *
 * See http://stackoverflow.com/questions/4414171/how-to-detect-when-an-android-app-goes-to-the-background-and-come-back-to-the-fo for a discussion on this
 *
 * If targeting 14+ a global Activity lifecycle counter can be registered via Application.registerOnActivityLifecycleListener() but the below timer will still need to be used.
 * See http://steveliles.github.io/is_my_android_app_currently_foreground_or_background.html
 */
public class ActivityCounter
{
    //==================================================================================================
    // SINGLETON
    //==================================================================================================

    private static final ActivityCounter sInstance = new ActivityCounter();


    public static ActivityCounter getInstance()
    {
        return sInstance;
    }

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
                EventBus.getDefault().post(new ApplicationMovedIntoBackgroundEvent());
            }
        }
    };

    private int mStartedCount = 0;


    //==================================================================================================
    // CONSTRUCTOR
    //==================================================================================================

    private ActivityCounter()
    {
        mHandler = new Handler();
    }

    //==================================================================================================
    // LIFECYCLE HOOKS
    //==================================================================================================

    public void activityStarted()
    {
        if(mStartedCount == 0)
            EventBus.getDefault().post(new ApplicationMovedIntoForegroundEvent());

        mStartedCount++;
    }

    public void activityStopped()
    {
        mHandler.postDelayed(mActivityStoppedRunnable, BACKGROUND_EVENT_DELAY_MS);
    }

    //==================================================================================================
    // EVENTS
    //==================================================================================================

    public class ApplicationMovedIntoBackgroundEvent{}
    public class ApplicationMovedIntoForegroundEvent{}
}
