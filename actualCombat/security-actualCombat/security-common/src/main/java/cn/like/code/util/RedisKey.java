package cn.like.code.util;

import java.text.MessageFormat;

/**
 * redis key 生成工具类
 * <pre>
 *     纯字符串的StringKey
 *     TEST_STRING("test:string"),
 *     带参数的key拼接
 *     TEST_STRING_ID("test:string:id:{0}"),
 *     多个参数的key拼接
 *     TEST_STRING_ID_NAME("test:string:id:{0}:{1}"),
 * </pre>
 *
 * @author pdd20
 * @date 2021/06/04
 */
public enum RedisKey {

    /** 缓存信息 已经注册过的客户端  */ CACHE_AUTH_CLIENT_ID("cache:auth:client_id:{0}"),
    /** 缓存信息 已经注册过的用户 */ CACHE_AUTH_USER_NAME("cache:auth:user_name:{0}"),

    ;

    private final String value;

    RedisKey(String value) {
        this.value = value;
    }

    /**
     * 构建Key
     *
     * @param params 参数
     * @return {@link String}
     */
    public String buildKey(Object... params) {
        return MessageFormat.format(value, params);
    }
}
