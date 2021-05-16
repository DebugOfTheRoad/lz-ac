package com.lckjsoft.common.util;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 23:20
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
public class StringHelper {
    public static String getObjectValue(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
