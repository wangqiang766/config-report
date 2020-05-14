package com.hxyl.bbp.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 字符串，数组，集合，列表，对象
 * 空与非空判断
 */
public class BbpObjectUtil {

    /**
     * 判断字符串空(针对空字符串情况)
     * @param obj
     * @return
     */
    public static boolean isNull(String obj){
        if(null == obj || obj.length() == 0){
            return true;
        }
        return false;
    }
    /**
     * 判断字符串非空(针对字符串情况)
     * @param obj
     * @return
     */
    public static boolean isNotNull(String obj){
        return !isNull(obj);
    }

    /**
     * 判断对象空
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj){
        if(null == obj){
            return true;
        }
        return false;
    }

    /**
     * 判断对象非空(针对对象情况)
     * @param obj
     * @return
     */
    public static boolean isNotNull(Object obj){
        return !isNull(obj);
    }

    /**
     * 判断List空
     * @param list
     * @return
     */
    public static boolean isNull(List list){
        if(null == list || list.size() == 0){
            return true;
        }
        return false;
    }

    /**
     * 判断List非空
     * @param list
     * @return
     */
    public static boolean isNotNull(List list){
        return !isNull(list);
    }

    /**
     * 判断Map空
     * @param map
     * @return
     */
    public static boolean isNull(Map map){
        if(null == map || map.size() == 0){
            return true;
        }
        return false;
    }
    /**
     * 判断Map非空
     * @param map
     * @return
     */
    public static boolean isNotNull(Map map){
        return !isNull(map);
    }

    /**
     * 判断Set空
     * @param set
     * @return
     */
    public static boolean isNull(Set set){
        if(null == set || set.size() == 0){
            return true;
        }
        return false;
    }
    /**
     * 判断Set非空
     * @param set
     * @return
     */
    public static boolean isNotNull(Set set){
        return !isNull(set);
    }

    /**
     * 判断数组空
     * @param arr
     * @return
     */
    public static boolean isNull(Object[] arr){
        if(null == arr || arr.length == 0){
            return true;
        }
        return false;
    }
    /**
     * 判断数组非空
     * @param arr
     * @return
     */
    public static boolean isNotNull(Object[] arr){
        return !isNull(arr);
    }

    /**
     * 两个非空字符串比较
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean isEqual(String arr1,String arr2){
        if(null == arr1){
            return false;
        }
        return arr1.equals(arr2);
    }
    /**
     * 两个非空数值比较
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean isEqual(Integer arr1,Integer arr2){
        if(null == arr1){
            return false;
        }
        return arr1.equals(arr2);
    }
    /**
     * 两个非空对象比较
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean isEqual(Object arr1,Object arr2){
        if(null == arr1){
            return false;
        }
        return arr1.equals(arr2);
    }

    /**
     * 两个非空数不相等比较
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean isNotEqual(Integer arr1,Integer arr2){
        return !isEqual(arr1,arr2);
    }
    /**
     * 两个字符串不相等比较
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean isNotEqual(String arr1,String arr2){
        return !isEqual(arr1,arr2);
    }
}
