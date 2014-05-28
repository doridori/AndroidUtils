/**
 Copyright [2011] [Dorian Cussen]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.doridori.lib.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * Sometimes its helpful to be able to grab device pxl data from the context alone
 */
public class DisplayUtils
{
    /**
     * May not be accurate due to system bars / OEMs etc. See http://stackoverflow.com/questions/15055458/detect-7-inch-and-10-inch-tablet-programmatically
     *
     * Could also use getContext().getResources().getConfiguration()#smallestWidth... if targeting api 13+
     *
     * @return
     */
    public static float getSmallestWidthDp(Context context)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getResources().getDisplayMetrics();

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);
        return smallestWidth;
    }

    public static float getCurrentOrientationWidth(Context context)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        //height and width above do not changed based upon orientation so work out which one is the current length
        int orientation = context.getResources().getConfiguration().orientation;
        switch(orientation)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                return Math.max(widthPixels, heightPixels);
            case Configuration.ORIENTATION_PORTRAIT:
                return Math.min(widthPixels, heightPixels);
            default: //fallback
                XLog.w("undefined orientation!");
                return widthPixels;
        }
    }
}
