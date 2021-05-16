package com.lckjsoft.common.context;

import com.lckjsoft.common.constant.ConstantUtil;
import com.lckjsoft.common.util.StringHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 23:19
 * @Modified By:
 * @Modified Date:      2021/5/16
 */

public class BaseContextHandler {
    public static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static Object get(String key){
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            threadLocal.set(map);
        }
        return map.get(key);
    }

    public static String getUserID(){
        Object value = get(ConstantUtil.CONTEXT_KEY_USER_ID);
        return returnObjectValue(value);
    }

    public static String getUsername(){
        Object value = get(ConstantUtil.CONTEXT_KEY_USERNAME);
        return returnObjectValue(value);
    }


    public static String getName(){
        Object value = get(ConstantUtil.CONTEXT_KEY_USER_NAME);
        return StringHelper.getObjectValue(value);
    }

    public static String getToken(){
        Object value = get(ConstantUtil.CONTEXT_KEY_USER_TOKEN);
        return StringHelper.getObjectValue(value);
    }
    public static void setToken(String token){set(ConstantUtil.CONTEXT_KEY_USER_TOKEN,token);}

    public static void setName(String name){set(ConstantUtil.CONTEXT_KEY_USER_NAME,name);}

    public static void setUserID(String userID){
        set(ConstantUtil.CONTEXT_KEY_USER_ID,userID);
    }

    public static void setUsername(String username){
        set(ConstantUtil.CONTEXT_KEY_USERNAME,username);
    }

    private static String returnObjectValue(Object value) {
        return value==null?null:value.toString();
    }

    public static void remove(){
        threadLocal.remove();
    }

//    @RunWith(MockitoJUnitRunner.class)
//    public static class UnitTest {
//        private Logger logger = LoggerFactory.getLogger(UnitTest.class);
//
//        @Test
//        public void testSetContextVariable() throws InterruptedException {
//            BaseContextHandler.set("test", "main");
//            new Thread(()->{
//                BaseContextHandler.set("test", "moo");
//
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                assertEquals(BaseContextHandler.get("test"), "moo");
//                logger.info("thread one done!");
//            }).start();
//            new Thread(()->{
//                BaseContextHandler.set("test", "moo2");
//                assertEquals(BaseContextHandler.get("test"), "moo2");
//                logger.info("thread two done!");
//            }).start();
//
//            Thread.sleep(5000);
//            assertEquals(BaseContextHandler.get("test"), "main");
//            logger.info("main one done!");
//        }
//
//        @Test
//        public void testSetUserInfo(){
//            BaseContextHandler.setUserID("test");
//            assertEquals(BaseContextHandler.getUserID(), "test");
//            BaseContextHandler.setUsername("test2");
//            assertEquals(BaseContextHandler.getUsername(), "test2");
//        }
//    }
}
