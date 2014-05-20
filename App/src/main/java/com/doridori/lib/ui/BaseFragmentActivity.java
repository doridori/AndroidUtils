package com.doridori.lib.ui;

import android.support.v4.app.FragmentActivity;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Adds some common useful functionality
 *
 * User: doriancussen
 * Date: 05/11/2012
 */
public abstract class BaseFragmentActivity extends FragmentActivity
{
    private Queue<Runnable> mUiQueue = new LinkedList<Runnable>();

    private boolean mIsPaused = true;

    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPaused = false;
        clearUiQueue();
    }

    private void clearUiQueue(){
        while(mUiQueue.size() > 0){
            mUiQueue.poll().run();
        }
    }

    /**
     * Will perform the passed in op or if the activity is paused it will queue it until the activity is resumed. This is useful
     * for perfroming fragment transactions as a result of an asyncronous method that may return while the activity is in the
     * background. Make sure to handle the case where the activity may be killed in the background before this ui operation is run
     * and then resumed from savedInstanceState by the platform (can do with saving state via Bundle OR checking some model state in onCreate).
     *
     * @param uiOp
     */
    protected void doUiOp(Runnable uiOp){
        if(mIsPaused){
            mUiQueue.add(uiOp);
        }else{
            uiOp.run();
        }
    }
}
