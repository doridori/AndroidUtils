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

import de.greenrobot.event.EventBus;

/**
 * As there is no global callback for the state of the app i.e. is the app in the foreground or not you can have a base class
 * call this in onStart and onStop and it will do a global event for anyone who is interested. Useful for implementing some
 * form of security when the app moves to the foreground
 */
public class ActivityCounter
{
    //==================================================================================================
    // SINGLETON
    //==================================================================================================

    private static final ActivityCounter sInstance = new ActivityCounter();

    private ActivityCounter() { /*singleton*/ }

    public static ActivityCounter getsInstance()
    {
        return sInstance;
    }

    //==================================================================================================
    // CLASS
    //==================================================================================================

    public int mStartedCount = 0;

    public void activityStarted()
    {
        if(mStartedCount == 0)
            EventBus.getDefault().post(new ApplicationMovedIntoForegroundEvent());

        mStartedCount++;
    }

    public void activityStopped()
    {
        mStartedCount--;

        if(mStartedCount == 0)
        {
            EventBus.getDefault().post(new ApplicationMovedIntoBackgroundEvent());
        }
    }

    public class ApplicationMovedIntoBackgroundEvent{}
    public class ApplicationMovedIntoForegroundEvent{}
}
