package com.tisawesomeness.minecord.util;

import java.lang.reflect.Array;

// Adapted from apache lang3
public class ArrayUtils {

    /**
     * Adds all elements from two arrays into a new array.
     * @param arr first array
     * @param arr2 second array
     * @param <T> array type
     * @return new array with the elements of the first array, then the elements of the second
     */
    public static <T> T[] addAll(T[] arr, T[] arr2) {
        @SuppressWarnings("unchecked")
        T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(), arr.length + arr2.length);
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        System.arraycopy(arr2, 0, newArr, arr.length, arr2.length);
        return newArr;
    }

    /**
     * Creates a new array with the element at the given index removed.
     * @param arr array
     * @param index index
     * @param <T> array type
     * @return a new array
     * @throws IndexOutOfBoundsException if the index is out of bounds ({@code (index < 0 || arr.length <= index})
     */
    public static <T> T[] remove(T[] arr, int index) {
        int length = arr.length;
        if (index < 0 || length <= index) {
            throw new IndexOutOfBoundsException("Index must be between 0 and " + length + " but was " + index);
        }

        @SuppressWarnings("unchecked")
        T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(), length - 1);
        System.arraycopy(arr, 0, newArr, 0, index);
        if (index < length - 1) {
            System.arraycopy(arr, index + 1, newArr, index, length - index - 1);
        }
        return newArr;
    }

    /**
     * Checks if the array contains the given value.
     * @param arr array
     * @param val value to find
     * @param <T> array type
     * @return whether the array contains the value
     */
    public static <T> boolean contains(T[] arr, T val) {
        return indexOf(arr, val) != -1;
    }

    /**
     * Finds the index of the given value in the array.
     * @param arr array
     * @param val value to find
     * @param <T> array type
     * @return the index of the value in the array, or -1 if not found
     */
    public static <T> int indexOf(T[] arr, T val) {
        for (int i = 0; i < arr.length; i++) {
            if (val.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }

}
