package com.doridori.lib.view.anim;

import android.view.animation.Interpolator;

/**
 * Will act like a normal interpolator thats used with the REVERSE setting. Useful as REPEAT is buggy!
 * http://stackoverflow.com/questions/4480652/android-animation-does-not-repeat
 *
 * User: doriancussen
 * Date: 28/09/2012
 */
public class ReverseInterpolator implements Interpolator {

    private final Interpolator mInterpolator;

    public ReverseInterpolator(Interpolator interpolator){
        mInterpolator = interpolator;
    }

    @Override
    public float getInterpolation(float input) {
        //map value so 0-0.5 = 0-1 and 0.5-1 = 1-0
        if(input <= 0.5){
            return input*2;
        }else{
            return Math.abs(input-1)*2;
        }
    }
}
