package com.doridori.lib.mapping;

import android.graphics.drawable.Drawable;

/**
 * User: doriancussen
 * Date: 12/11/2012
 */
public class MapUtils {

    /**
     * Useful for map pins. This method is also contained in ItemizedDrawable but sometimes we dont want to instantiate this class and still place pins on a map
     *
     * @param drawable
     */
    public static void setBottomBounds(Drawable drawable){
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        drawable.setBounds(-w / 2, -h, w / 2, 0);
    }

    /**
     * Lots of the google mapping lib methods take microDegrees instead of notmal degrees so this is just a handy converter
     *
     * @param degree
     * @return
     */
    public static int convertToMicroDegrees(double degree){
        return (int)(degree*1e6);
    }
}
