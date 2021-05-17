package com.lckjsoft.auth.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PermissionMapper {

    /**
     * 添加一个权限
     * @param name
     * @param url
     * @param method
     */
    @SelectKey(keyProperty = "id", resultType = String.class, before = true, statement = "select replace(uuid(), '-', '')")
    @Options(keyProperty = "id", useGeneratedKeys = true)
    @Insert("INSERT INTO `oauth_permission` (`id`, `name`, `url`, `method`) VALUES (#{id,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{url,jdbcType=VARCHAR}, #{method,jdbcType=VARCHAR}) ")
    void add(@Param("name") String name, @Param("url") String url, @Param("method") String method);

    /**
     * 检查权限url是否存在
     * @param url
     * @return
     */
    @Select("SELECT 1 FROM `oauth_permission` WHERE url = #{url,jdbcType=VARCHAR}")
    Integer checkUrl(@Param("url") String url);

    /**
     * url换取对应的permissionid
     * @param url
     * @return
     */
    @Select("SELECT id FROM `oauth_permission` WHERE url = #{url,jdbcType=VARCHAR}")
    String getId(@Param("url") String url);
}