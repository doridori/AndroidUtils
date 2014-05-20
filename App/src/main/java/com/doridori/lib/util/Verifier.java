package com.doridori.lib.util;

import android.content.Intent;

/**
 * @author Dorian Cussen
 *         Date: 29/11/2012
 */
public class Verifier
{
    //====================================================================================================
    // String checking
    //====================================================================================================

    /**
     * checks if
     *
     * @return false if any of the strings are null or empty
     */
    public static boolean isNotNullAndNotEmpty(String... strings)
    {
        for(String string : strings)
        {
            if(string == null || string.isEmpty())
                return false;
        }

        return true;
    }

    public static void throwIfAnyNullOrEmpty(String... strings) throws NullPointerException
    {
        for (int i = 0; i < strings.length; i++)
        {
            if (null == strings[i])
            {
                String errorMsg = "Arg with index " + i + " == null!!";
                XLog.e(errorMsg);
                throw new NullPointerException(errorMsg);
            }
            else if(strings[i].isEmpty())
            {
                String errorMsg = "Arg with index " + i + " is Empty!!";
                XLog.e(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }
    }

    //====================================================================================================
    // Object checking
    //====================================================================================================

    public static boolean isAllNotNull(Object... objects)
    {
        for(Object object : objects)
        {
            if(null == object)
                return false;
        }

        return true;
    }

    public static void throwIfAnyNull(Object... objects)
    {
        for (int i = 0; i < objects.length; i++)
        {
            if (null == objects[i])
            {
                String errorMsg = "Arg with index " + i + " == null!!";
                XLog.e(errorMsg);
                throw new NullPointerException(errorMsg);
            }
        }
    }

    public static void throwIfAllNull(Object... objects)
    {
        if(objects.length == 0)
        {
            throw new NullPointerException("no objects passed in");
        }

        boolean allNull = true;

        for (int i = 0; i < objects.length; i++)
        {
            if (null != objects[i])
            {
                allNull = false;
            }
        }

        if(allNull)
            throw new NullPointerException("All NULL!");
    }

    //====================================================================================================
    // Intent key checking
    //====================================================================================================

    public static void throwIfIntentKeysMissing(Intent intent, String... keys)
    {
        if(null == intent.getExtras())
            throw new RuntimeException("No intent extras at all!");

        StringBuilder builder = new StringBuilder();
        boolean anyMissing = false;

        for (String key : keys)
        {
            if (!intent.getExtras().containsKey(key))
            {
                anyMissing = true;
                XLog.w("Intent extra missing with key : "+key);
                builder.append(key+" ,");
            }
        }

        if(anyMissing)
            throw new RuntimeException("Intent key(s) missing : "+builder.toString());
    }
}
