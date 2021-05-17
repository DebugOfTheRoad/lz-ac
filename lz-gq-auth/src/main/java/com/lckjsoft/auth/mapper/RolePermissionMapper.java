package com.lckjsoft.auth.mapper;

import org.apache.ibatis.annotations.*;

/**
 * @author uid40330
 */
@Mapper
public interface RolePermissionMapper {
    /**
     * 添加角色的权限
     * @param roleid
     * @param permissionid
     */
    @SelectKey(keyProperty = "id", resultType = String.class, before = true, statement = "select replace(uuid(), '-', '')")
    @Options(keyProperty = "id", useGeneratedKeys = true)
    @Insert("INSERT INTO `oauth_role_permission` (`id`, `roleid`, `permissionid`) VALUES (#{id,jdbcType=VARCHAR}, #{roleid,jdbcType=VARCHAR}, #{permissionid,jdbcType=VARCHAR}) ")
    void add(@Param("roleid") String roleid, @Param("permissionid") String permissionid);

    /**
     * 根据角色id、权限id查询一个权限
     * @param roleid
     * @param permissionid
     * @return
     */
    @Select("SELECT 1 FROM `oauth_role_permission` WHERE roleid = #{roleid,jdbcType=VARCHAR} AND permissionid = #{permissionid,jdbcType=VARCHAR}")
    Integer checkOne(@Param("roleid") String roleid, @Param("permissionid") String permissionid);


}
