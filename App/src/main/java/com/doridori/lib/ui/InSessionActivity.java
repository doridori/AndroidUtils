package com.doridori.lib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.doridori.lib.model.ISession;
import com.doridori.lib.util.XLog;


/**
 * <p>This activity provides alt lifecycle methods so if the session call fails your activity will not have its own
 * init functionality called in the traditional lifecycle methods.</p>
 *
 * <p>Make sure when using that any dealloc you do in your closing lifecycle methods has null checks as the corresponding setup methods may not have been called if the session was not validated</p>
 *
 * User: doriancussen
 * Date: 09/11/2012
 */
public abstract class InSessionActivity extends FragmentActivity
{

    private boolean mIsSessionValid;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(null == getSession() || !getSession().isSessionValid()){
            //session invalid - jump back to session start activity
            XLog.w("Session INVALID - jumping back to start activity");
            mIsSessionValid = false;
            Intent intent = new Intent(this, getSessionStartActivityClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            mIsSessionValid = true;
            onCreatePostSessionCheck(savedInstanceState);
        }
    }

    @Override
    protected final void onStart() {
        super.onStart();
        if(mIsSessionValid){
            onStartPostSessionCheck();
        }
    }

    @Override
    protected final void onResume() {
        super.onResume();
        if(mIsSessionValid){
            onResumePostSessionCheck();
        }
    }

    protected final void onPostResume(){
        super.onPostResume();
        if(mIsSessionValid){
            onPostResumePostSessionCheck();
        }
    }

    protected void onCreatePostSessionCheck(Bundle savedInstanceState){};
    protected void onStartPostSessionCheck(){};
    protected void onResumePostSessionCheck(){};
    protected void onPostResumePostSessionCheck(){};

    /**
     * @return return the class of the activity that initiates the session here
     */
    protected abstract Class getSessionStartActivityClass();
    protected abstract ISession getSession();
}
