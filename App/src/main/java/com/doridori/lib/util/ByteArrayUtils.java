package com.doridori.lib.util;

import java.util.Arrays;
import java.util.Comparator;

public class ByteArrayUtils
{
    public static void sortDecendingSize(byte[]... arrays)
    {
        Arrays.sort(arrays, new Comparator<byte[]>()
        {
            @Override
            public int compare(byte[] lhs, byte[] rhs)
            {
                if (lhs.length > rhs.length)
                    return -1;
                if (lhs.length < rhs.length)
                    return 1;
                else return 0;
            }
        });
    }

    /**
     * This method merges any number of arrays of any count.
     *
     * @param arrays
     * @return merged array
     */
    public static byte[] merge(byte[]... arrays)
    {
        int count = 0;
        for (byte[] array: arrays)
        {
            count += array.length;
        }

        // Create new array and copy all array contents
        byte[] mergedArray = new byte[count];
        int start = 0;
        for (byte[] array: arrays)
        {
            System.arraycopy(array, 0, mergedArray, start, array.length);
            start += array.length;
        }
        return mergedArray;
    }

    /**
     * XORs the bits in an arbitary number of byte arrays.
     *
     * @return The result of XORing the bits in the byte arrays.
     */
    public static byte[] xor(byte[]... arrays)
    {
        if (arrays.length == 0) return null;

        sortDecendingSize(arrays);

        byte[] result = new byte[arrays[0].length];

        for (byte[] array : arrays)
        {
            for (int i = 0; i < array.length; i++)
            {
                result[i] ^= array[i];
            }
        }

        return result;
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
