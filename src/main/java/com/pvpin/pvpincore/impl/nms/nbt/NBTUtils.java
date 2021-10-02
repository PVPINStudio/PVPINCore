/*
 * The MIT License
 * Copyright © 2020-2021 PVPINStudio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pvpin.pvpincore.impl.nms.nbt;

import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.impl.nms.VersionChecker;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.pvpin.pvpincore.impl.nms.VersionChecker.version;

/**
 * This class is used to do convertions between NMS NBTs, byte[] and Maps.
 *
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class NBTUtils extends NBTIO {
    protected static Class<?> nmsNBTList;
    // This is the super class of NBTTagList and NBTTagIntArray, NBTTagByteArray, etc.
    // Not available in 1.12.

    protected static Class<?> nmsNBTTagString;
    protected static Class<?> nmsNBTTagByte;
    protected static Class<?> nmsNBTTagShort;
    protected static Class<?> nmsNBTTagInt;
    protected static Class<?> nmsNBTTagLong;
    protected static Class<?> nmsNBTNumber;
    protected static Class<?> nmsNBTTagDouble;

    protected static Class<?> nmsNBTTagByteArray;
    protected static Class<?> nmsNBTTagIntArray;
    protected static Class<?> nmsNBTTagLongArray;

    static {
        try {
            if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
                nmsNBTList = Class.forName("net.minecraft.server." + version + ".NBTList");
            } else {
                nmsNBTList = Class.forName("java.lang.System");
                // Get a class that is non null.
            }
            nmsNBTTagString = Class.forName("net.minecraft.server." + version + ".NBTTagString");
            nmsNBTTagByte = Class.forName("net.minecraft.server." + version + ".NBTTagByte");
            nmsNBTTagShort = Class.forName("net.minecraft.server." + version + ".NBTTagShort");
            nmsNBTTagInt = Class.forName("net.minecraft.server." + version + ".NBTTagInt");
            nmsNBTTagLong = Class.forName("net.minecraft.server." + version + ".NBTTagLong");
            nmsNBTNumber = Class.forName("net.minecraft.server." + version + ".NBTNumber");
            nmsNBTTagDouble = Class.forName("net.minecraft.server." + version + ".NBTTagDouble");

            nmsNBTTagByteArray = Class.forName("net.minecraft.server." + version + ".NBTTagByteArray");
            nmsNBTTagIntArray = Class.forName("net.minecraft.server." + version + ".NBTTagIntArray");
            nmsNBTTagLongArray = Class.forName("net.minecraft.server." + version + ".NBTTagLongArray");
        } catch (ClassNotFoundException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to convert an NBTTagCompound to a byte[].
     *
     * @param compound nms NBTTagCompound to be converted
     * @return byte[] data of the NBTTagCompound
     */
    public static byte[] convertNBTToByteArray(Object compound) {
        byte[] ret = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            NBTIO.nmsNBTCompressedStreamTools_write.invoke(null, compound, outputStream);
            ret = outputStream.toByteArray();
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            PVPINLogManager.log(ex);
        }
        return ret;
    }

    /**
     * This method is used to convert a byte[] to an NBTTagCompound.
     *
     * @param bytes byte[] data of an NBTTagCompound
     * @return nms NBTTagCompound converted from the byte[]
     */
    public static Object convertByteArrayToNBT(byte[] bytes) {
        Object ret = null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            ret = NBTIO.nmsNBTCompressedStreamTools_read.invoke(null, inputStream);
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            PVPINLogManager.log(ex);
        }
        return ret;
    }

    /**
     * This method is used to convert an NMS NBT to a Map.<p>
     * All the NBTTagString will be converted to java.lang.String.<p>
     * All the NBTList(including byte array, long array, etc.) will be converted to an ArrayList.<p>
     * All the NBTTagBytes will be converted to bytes.<p>
     * All the NBTTagShorts will be converted to shorts.<p>
     * All the NBTTagInts will be converted to ints.<p>
     * All the NBTTagLongs will be converted to longs.<p>
     * All other numbers(NBTTagFloat, etc.) will be automatically converted to doubles.<p>
     * Note that numbers are ALL converted to doubles in 1.12.<p>
     * The structure of the map will not change, but the NBTTags will be converted.
     *
     * @param compound the compound to be converted
     * @return the result map
     */
    public static Map convertNBTToMap(Object compound) {
        Map result = null;
        try {
            Map<String, Object> temp = (Map<String, Object>) nmsNBTTagCompound.getField("map").get(compound);
            result = new HashMap();
            for (String key : temp.keySet()) {
                Object base = nmsNBTTagCompound_get.invoke(compound, key);
                if (!VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
                    // 1.12 or lower, there is no NBTList.
                    // But there are long arrays, int arrays, byte arrays and TagLists.
                    if (nmsNBTTagList.isAssignableFrom(base.getClass())) {
                        result.put(key, convertNBTToList((List) nmsNBTTagList.getField("list").get(base)));
                        continue;
                    } else if (nmsNBTTagByteArray.isAssignableFrom(base.getClass())) {
                        Field field = nmsNBTTagByteArray.getDeclaredField("data");
                        field.setAccessible(true);
                        result.put(key, Arrays.asList((byte[]) field.get(base)));
                        continue;
                    } else if (nmsNBTTagIntArray.isAssignableFrom(base.getClass())) {
                        Field field = nmsNBTTagIntArray.getDeclaredField("data");
                        field.setAccessible(true);
                        result.put(key, Arrays.asList((int[]) field.get(base)));
                        continue;
                    } else if (nmsNBTTagLongArray.isAssignableFrom(base.getClass())) {
                        Field field = nmsNBTTagLongArray.getDeclaredField("b");
                        // There is no data field.
                        field.setAccessible(true);
                        result.put(key, Arrays.asList((long[]) field.get(base)));
                        continue;
                    }
                }
                if (nmsNBTTagCompound.isAssignableFrom(base.getClass())) {
                    result.put(key, convertNBTToMap(base));
                } else if (nmsNBTList.isAssignableFrom(base.getClass())) {
                    result.put(key, convertNBTToList((List) base));
                } else if (nmsNBTTagString.isAssignableFrom(base.getClass())) {
                    try {
                        result.put(key, nmsNBTTagString.getMethod("asString").invoke(base));
                    } catch (NoSuchMethodException
                            | IllegalAccessException
                            | InvocationTargetException ex) {
                        try {
                            result.put(key, nmsNBTTagString.getMethod("c_").invoke(base));
                        } catch (IllegalAccessException
                                | InvocationTargetException
                                | NoSuchMethodException exc) {
                            PVPINLogManager.log(exc);
                        }
                    }
                    // 1.12

                } else if (nmsNBTTagByte.isAssignableFrom(base.getClass())) {
                    try {
                        result.put(key, nmsNBTTagByte.getMethod("asByte").invoke(base));
                    } catch (NoSuchMethodException
                            | IllegalAccessException
                            | InvocationTargetException exc) {
                        try {
                            result.put(key, nmsNBTTagByte.getMethod("asDouble").invoke(base));
                        } catch (IllegalAccessException
                                | InvocationTargetException
                                | NoSuchMethodException exce) {
                            PVPINLogManager.log(exce);
                        }
                        // 1.12
                    }
                } else if (nmsNBTTagShort.isAssignableFrom(base.getClass())) {
                    try {
                        result.put(key, nmsNBTTagShort.getMethod("asShort").invoke(base));
                    } catch (NoSuchMethodException
                            | IllegalAccessException
                            | InvocationTargetException ex) {
                        try {
                            result.put(key, nmsNBTTagShort.getMethod("asDouble").invoke(base));
                        } catch (IllegalAccessException
                                | InvocationTargetException
                                | NoSuchMethodException excep) {
                            PVPINLogManager.log(excep);
                        }
                        // 1.12
                    }
                } else if (nmsNBTTagInt.isAssignableFrom(base.getClass())) {
                    try {
                        result.put(key, nmsNBTTagInt.getMethod("asInt").invoke(base));
                    } catch (NoSuchMethodException
                            | IllegalAccessException
                            | InvocationTargetException ex) {
                        try {
                            result.put(key, nmsNBTTagInt.getMethod("asDouble").invoke(base));
                        } catch (IllegalAccessException
                                | InvocationTargetException
                                | NoSuchMethodException except) {
                            PVPINLogManager.log(except);
                        }
                        // 1.12
                    }
                } else if (nmsNBTTagLong.isAssignableFrom(base.getClass())) {
                    try {
                        result.put(key, nmsNBTTagLong.getMethod("asLong").invoke(base));
                    } catch (NoSuchMethodException
                            | IllegalAccessException
                            | InvocationTargetException ex) {
                        try {
                            result.put(key, nmsNBTTagLong.getMethod("asDouble").invoke(base));
                        } catch (IllegalAccessException
                                | InvocationTargetException
                                | NoSuchMethodException exception) {
                            PVPINLogManager.log(exception);
                        }
                        // 1.12
                    }
                } else if (nmsNBTNumber.isAssignableFrom(base.getClass())) {
                    result.put(key, base.getClass().getMethod("asDouble").invoke(base));
                } else {
                    throw new RuntimeException("Data type not supported.");
                }
            }
        } catch (InvocationTargetException
                | IllegalAccessException
                | NoSuchMethodException
                | NoSuchFieldException ex) {
            PVPINLogManager.log(ex);
        }
        return result;
    }

    /**
     * This method is used to convert an NBTTagList to an ArrayList.<p>
     * This method also accepts NBTTagByteArray, IntArray, LongArray, etc.<p>
     * All the NBTTagString will be converted to java.lang.String.<p>
     * All the NBTList(including byte array, long array, etc.) will be converted to an ArrayList.<p>
     * All the NBTTagBytes will be converted to bytes.<p>
     * All the NBTTagShorts will be converted to shorts.<p>
     * All the NBTTagInts will be converted to ints.<p>
     * All the NBTTagLongs will be converted to longs.<p>
     * Note that numbers are ALL converted to doubles in 1.12.<p>
     * All other numbers(NBTTagFloat, etc.) will be automatically converted to doubles.<p>
     * This method is for internal use only.
     *
     * @param list the list to be converted
     * @return the result list
     */
    protected static List convertNBTToList(List list) {
        List result = new ArrayList(list.size());
        for (Object action : list) {
            try {
                if (!VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
                    // 1.12 or lower, there is no NBTList.
                    // But there are long arrays, int arrays, byte arrays and TagLists.
                    if (nmsNBTTagList.isAssignableFrom(action.getClass())) {
                        result.add(convertNBTToList((List) nmsNBTTagList.getField("list").get(action)));
                        continue;
                    } else if (nmsNBTTagByteArray.isAssignableFrom(action.getClass())) {
                        Field field = nmsNBTTagByteArray.getDeclaredField("data");
                        field.setAccessible(true);
                        result.add(Arrays.asList((byte[]) field.get(action)));
                        continue;
                    } else if (nmsNBTTagIntArray.isAssignableFrom(action.getClass())) {
                        Field field = nmsNBTTagIntArray.getDeclaredField("data");
                        field.setAccessible(true);
                        result.add(Arrays.asList((int[]) field.get(action)));
                        continue;
                    } else if (nmsNBTTagLongArray.isAssignableFrom(action.getClass())) {
                        Field field = nmsNBTTagLongArray.getDeclaredField("b");
                        // There is no data field.
                        field.setAccessible(true);
                        result.add(Arrays.asList((long[]) field.get(action)));
                        continue;
                    }
                }
                if (nmsNBTTagCompound.isAssignableFrom(action.getClass())) {
                    result.add(convertNBTToMap(action));
                } else if (nmsNBTList.isAssignableFrom(action.getClass())) {
                    result.add(convertNBTToList((List) action));
                } else if (nmsNBTTagString.isAssignableFrom(action.getClass())) {
                    try {
                        result.add(nmsNBTTagString.getMethod("asString").invoke(action));
                    } catch (NoSuchMethodException ex) {
                        result.add(nmsNBTTagString.getMethod("c_").invoke(action));
                    }
                } else if (nmsNBTTagByte.isAssignableFrom(action.getClass())) {
                    try {
                        result.add(nmsNBTTagByte.getMethod("asByte").invoke(action));
                    } catch (NoSuchMethodException ex) {
                        result.add(nmsNBTTagByte.getMethod("asDouble").invoke(action));
                    }
                } else if (nmsNBTTagShort.isAssignableFrom(action.getClass())) {
                    try {
                        result.add(nmsNBTTagShort.getMethod("asShort").invoke(action));
                    } catch (NoSuchMethodException ex) {
                        result.add(nmsNBTTagShort.getMethod("asDouble").invoke(action));
                    }
                } else if (nmsNBTTagInt.isAssignableFrom(action.getClass())) {
                    try {
                        result.add(nmsNBTTagInt.getMethod("asInt").invoke(action));
                    } catch (NoSuchMethodException ex) {
                        result.add(nmsNBTTagInt.getMethod("asDouble").invoke(action));
                    }
                } else if (nmsNBTTagLong.isAssignableFrom(action.getClass())) {
                    try {
                        result.add(nmsNBTTagLong.getMethod("asLong").invoke(action));
                    } catch (NoSuchMethodException ex) {
                        result.add(nmsNBTTagLong.getMethod("asDouble").invoke(action));
                    }
                } else if (nmsNBTNumber.isAssignableFrom(action.getClass())) {
                    result.add(action.getClass().getMethod("asDouble").invoke(action));
                } else {
                    throw new RuntimeException("Data type not supported.");
                }
            } catch (InvocationTargetException
                    | IllegalAccessException
                    | NoSuchMethodException
                    | NoSuchFieldException ex) {
                PVPINLogManager.log(ex);
            }
        }
        return result;
    }

    /**
     * This method is used to convert a map to an NMS NBT.<p>
     * IMPORTANT: Type of the map's keys MUST be String.<p>
     * Type of the map's values MUST be one of the following:<p>
     * 1.Number(Byte, Short, Integer, Long and Double)<p>
     * 2.String<p>
     * 3.Map (whose keys MUST BE strings and values are within the four types)<p>
     * 4.List (values are within the four types)<p>
     * Note that any other number types, like float, etc. will be converted to Double automatically.<p>
     * Note that all elements in one list MUST be of the same type.<p>
     * e.g:<p>
     * [1,2] is valid.<p>
     * [1, "abc"] is invalid.
     *
     * @param map the map to be converted
     * @return NMS NBT converted
     */
    public static Object convertMapToNBT(Map map) {
        Object result = null;
        try {
            result = nmsNBTTagCompound.getConstructor().newInstance();
            final Object finalResult = result;
            map.forEach((key, value) -> {
                try {
                    if (value instanceof Map) {
                        nmsNBTTagCompound_set.invoke(finalResult, key, convertMapToNBT((Map) value));
                    } else if (value instanceof List) {
                        nmsNBTTagCompound_set.invoke(finalResult, key, convertListToNBT((List) value));
                    } else if (value instanceof String) {
                        Constructor cons = nmsNBTTagString.getDeclaredConstructor(String.class);
                        cons.setAccessible(true);
                        nmsNBTTagCompound_set.invoke(finalResult, key, cons.newInstance(value));
                    } else if (value instanceof Byte) {
                        Constructor cons = nmsNBTTagByte.getDeclaredConstructor(byte.class);
                        cons.setAccessible(true);
                        nmsNBTTagCompound_set.invoke(finalResult, key, cons.newInstance(value));
                    } else if (value instanceof Short) {
                        Constructor cons = nmsNBTTagShort.getDeclaredConstructor(short.class);
                        cons.setAccessible(true);
                        nmsNBTTagCompound_set.invoke(finalResult, key, cons.newInstance(value));
                    } else if (value instanceof Integer) {
                        Constructor cons = nmsNBTTagInt.getDeclaredConstructor(int.class);
                        cons.setAccessible(true);
                        nmsNBTTagCompound_set.invoke(finalResult, key, cons.newInstance(value));
                    } else if (value instanceof Long) {
                        Constructor cons = nmsNBTTagLong.getDeclaredConstructor(long.class);
                        cons.setAccessible(true);
                        nmsNBTTagCompound_set.invoke(finalResult, key, cons.newInstance(value));
                    } else if (value instanceof Number) {
                        Constructor cons = nmsNBTTagDouble.getDeclaredConstructor(double.class);
                        cons.setAccessible(true);
                        nmsNBTTagCompound_set.invoke(finalResult, key, cons.newInstance(((Number) value).doubleValue()));
                    } else {
                        throw new RuntimeException("Data type not supported.");
                    }
                } catch (InvocationTargetException
                        | IllegalAccessException
                        | InstantiationException
                        | NoSuchMethodException ex) {
                    PVPINLogManager.log(ex);
                }
            });
        } catch (InvocationTargetException
                | InstantiationException
                | IllegalAccessException
                | NoSuchMethodException ex) {
            PVPINLogManager.log(ex);
        }
        return result;
    }

    /**
     * This method is used to convert a list to an NMS NBTTagList.<p>
     * Type of the list's values should be one of the following:<p>
     * 1.Number(Byte, Short, Integer, Long and Double)<p>
     * 2.String<p>
     * 3.Map (whose keys are strings and values are within the four types)<p>
     * 4.List (values are within the four types)<p>
     * Note that any other number types, like float, etc. will be converted to Double automatically.<p>
     * Note that all the elements of this list MUST BE of the SAME type.<p>
     * e.g:<p>
     * [1,2] is valid.<p>
     * [1, "abc"] is invalid.<p>
     * This method is for internal use only.
     *
     * @param list the list to be converted
     * @return MXLib NBTTagList converted
     */
    protected static Object convertListToNBT(List list) {
        Object result = null;
        try {
            result = nmsNBTTagList.getConstructor().newInstance();
            Method nmsNBTTagList_add = null;
            try {
                nmsNBTTagList_add = nmsNBTTagList.getMethod("add", nmsNBTBase);
                // 1.12.
            } catch (NoSuchMethodException ex) {
                nmsNBTTagList_add = List.class.getMethod("add", Object.class);
                // Above 1.12.
            }
            for (Object action : list) {
                if (action instanceof Map) {
                    nmsNBTTagList_add.invoke(result, convertMapToNBT((Map) action));
                } else if (action instanceof List) {
                    nmsNBTTagList_add.invoke(result, convertListToNBT((List) action));
                } else if (action instanceof String) {
                    Constructor cons = nmsNBTTagString.getDeclaredConstructor(String.class);
                    cons.setAccessible(true);
                    nmsNBTTagList_add.invoke(result, cons.newInstance(action));
                } else if (action instanceof Byte) {
                    Constructor cons = nmsNBTTagByte.getDeclaredConstructor(byte.class);
                    cons.setAccessible(true);
                    nmsNBTTagList_add.invoke(result, cons.newInstance(action));
                } else if (action instanceof Short) {
                    Constructor cons = nmsNBTTagShort.getDeclaredConstructor(short.class);
                    cons.setAccessible(true);
                    nmsNBTTagList_add.invoke(result, cons.newInstance(action));
                } else if (action instanceof Integer) {
                    Constructor cons = nmsNBTTagInt.getDeclaredConstructor(int.class);
                    cons.setAccessible(true);
                    nmsNBTTagList_add.invoke(result, cons.newInstance(action));
                } else if (action instanceof Long) {
                    Constructor cons = nmsNBTTagLong.getDeclaredConstructor(long.class);
                    cons.setAccessible(true);
                    nmsNBTTagList_add.invoke(result, cons.newInstance(action));
                } else if (action instanceof Number) {
                    Constructor cons = nmsNBTTagDouble.getDeclaredConstructor(double.class);
                    cons.setAccessible(true);
                    nmsNBTTagList_add.invoke(result, cons.newInstance(((Number) action).doubleValue()));
                } else {
                    throw new RuntimeException("Data type not supported.");
                }
            }
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException ex) {
            PVPINLogManager.log(ex);
        }
        return result;
    }
}
