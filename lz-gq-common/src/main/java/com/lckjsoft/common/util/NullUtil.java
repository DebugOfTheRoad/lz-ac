package com.lckjsoft.common.util;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 22:44
 * @Modified By:
 * @Modified Date:      2021/5/16
 */

public class NullUtil {

    public static boolean isNull(Object obj){
        return obj==null || "".equals(obj.toString().trim()) || "null".equalsIgnoreCase(obj.toString().trim()) || "undefined".equalsIgnoreCase(obj.toString().trim());
    }

    public static boolean isNull(byte[] data){
        return data==null || data.length==0;
    }

    public static boolean isNull(List<?> list){
        return list==null || list.size()==0;
    }

    public static boolean isNull(Set<?> list){
        return list==null || list.size()==0;
    }

    public static boolean isNull(Map<?,?> map){
        return map==null || map.isEmpty();
    }

    public static boolean isNull(Object[] objs){
        return objs==null || objs.length==0;
    }

    public static boolean isNull(File file){
        return file==null || !file.exists() || file.length()==0;
    }

    public static boolean isNotNull(Object obj){
        return obj!=null && !"".equals(obj.toString().trim()) && !"null".equalsIgnoreCase(obj.toString().trim()) && !"undefined".equalsIgnoreCase(obj.toString().trim());
    }

    public static boolean isNotNull(byte[] data){
        return data!=null && data.length>0;
    }

    public static boolean isNotNull(List<?> list){
        return list!=null && list.size()>0;
    }

    public static boolean isNotNull(Set<?> list){
        return list!=null && list.size()>0;
    }

    public static boolean isNotNull(Map<?,?> map){
        return map!=null && !map.isEmpty();
    }

    public static boolean isNotNull(Object[] objs){
        return objs!=null && objs.length>0;
    }

    public static boolean isNotNull(File file){
        return file!=null && file.exists() && file.length()>0;
    }
}
