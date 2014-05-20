package com.doridori.lib.db.utils;

/**
 * User: doriancussen
 */
public enum SQLiteDataTypes
{
    NULL("NULL"), INTEGER("INTEGER"), REAL("REAL"), TEXT("TEXT"), BLOB("BLOB");

    private final String mString;

    SQLiteDataTypes(String string)
    {
        mString = string;
    }

    @Override
    public String toString()
    {
        return mString;
    }
}
