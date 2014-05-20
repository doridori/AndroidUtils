package com.doridori.lib;

import android.app.Activity;
import android.os.Bundle;

import com.example.androidutils.app.R;

public class StartActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
