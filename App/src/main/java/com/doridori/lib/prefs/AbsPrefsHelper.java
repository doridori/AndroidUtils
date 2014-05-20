package com.doridori.lib.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

/**
 * In your project you can extends this class and add your project specific KEYS
 *
 * User: doriancussen
 * Date: 01/11/2012
 */
public abstract class AbsPrefsHelper {

    private static SharedPreferences sSharedPreferences;

    /**
     * Does file IO on calling thread. Can use {@link #getDefaultSharedPrefsAsync(android.content.Context, couk.doridori.android.lib.prefs.AbsPrefsHelper.PrefsLoader)} if on UI
     *
     * @param context
     * @return
     */
    public static synchronized SharedPreferences getDefaultSharedPreferences(Context context){
        if(sSharedPreferences == null)
            sSharedPreferences = context.getSharedPreferences("defaultPrefs", Context.MODE_PRIVATE);

        return sSharedPreferences;
    }

    public static synchronized SharedPreferences.Editor getEditor(Context ctx){
        return getDefaultSharedPreferences(ctx).edit();
    }

    /**
     * Loads off the UI thread
     *
     * @param context
     * @param loader
     */
    public static synchronized void getDefaultSharedPrefsAsync(final Context context, final PrefsLoader loader)
    {
        if(null == sSharedPreferences)
        {
            loader.loaded(sSharedPreferences);
            return;
        }

        new AsyncTask<Void, Void, SharedPreferences>(){
            @Override
            protected SharedPreferences doInBackground(Void... params)
            {
                return getDefaultSharedPreferences(context);
            }

            @Override
            protected void onPostExecute(SharedPreferences prefs)
            {
                sSharedPreferences = prefs;
                loader.loaded(prefs);
            }
        }.execute();

    }

    public static synchronized void asyncCommit(final SharedPreferences.Editor editor){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            editor.apply();
        }else{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    editor.commit();
                    return null;
                }
            }.execute();
        }
    }

    public static synchronized void removePref(Context ctx, String key){
        SharedPreferences.Editor editor = getEditor(ctx);
        editor.remove(key);
        asyncCommit(editor);
    }

    public static synchronized void asyncPutString(Context ctx, String key, String val){
        SharedPreferences.Editor editor = getEditor(ctx);
        editor.putString(key, val);
        asyncCommit(editor);
    }

    public static synchronized void asyncPutBool(Context ctx, String key, Boolean val){
        SharedPreferences.Editor editor = getEditor(ctx);
        editor.putBoolean(key, val);
        asyncCommit(editor);
    }

    public static synchronized String getString(Context context, String key, String defValue){
        return getDefaultSharedPreferences(context).getString(key, defValue);
    }

    public static synchronized boolean getBoolean(Context context, String key, boolean defValue){
        return getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }

    public interface PrefsLoader
    {
        public void loaded(SharedPreferences prefs);
    }
}
