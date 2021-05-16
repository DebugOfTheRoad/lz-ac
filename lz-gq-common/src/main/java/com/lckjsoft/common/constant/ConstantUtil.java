package com.lckjsoft.common.constant;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 22:45
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
public class ConstantUtil {
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int ERROR = 500;

    /** 缓存超时/秒s */
    public static final int CACHE_TIMEOUT_DEFAULT = 7200;
    /** 分隔符 */
    public static final String SPLITE = ":";

    /** session-key-存放用户对象 */
    public static final String SESSION_USER = "OAUTH2_CURRENT_USER";
    /** 请求头存储用户信息key */
    public static final String LOGGER_TRACE_ID = "traceId";


    public final static String RESOURCE_TYPE_MENU = "menu";
    public final static String RESOURCE_TYPE_BTN = "button";
    public static final Integer EX_TOKEN_ERROR_CODE = 40101;
    // 用户token异常
    public static final Integer EX_USER_INVALID_CODE = 40101;
    public static final Integer EX_USER_PASS_INVALID_CODE = 40001;
    // 客户端token异常
    public static final Integer EX_CLIENT_INVALID_CODE = 40131;
    public static final Integer EX_CLIENT_FORBIDDEN_CODE = 40331;
    public static final Integer EX_OTHER_CODE = 500;
    public static final String CONTEXT_KEY_USER_ID = "currentUserId";
    public static final String CONTEXT_KEY_USERNAME = "currentUserName";
    public static final String CONTEXT_KEY_USER_NAME = "currentUser";
    public static final String CONTEXT_KEY_USER_TOKEN = "currentUserToken";
    public static final String JWT_KEY_USER_ID = "userId";
    public static final String JWT_KEY_NAME = "name";
    public static final String JWT_ID = "id";

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";
}
