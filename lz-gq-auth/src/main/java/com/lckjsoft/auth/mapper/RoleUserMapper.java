package com.lckjsoft.auth.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface RoleUserMapper {
    /**
     * 添加用户角色
     * @param roleid
     * @param userid
     */
    @SelectKey(keyProperty = "id", resultType = String.class, before = true, statement = "select replace(uuid(), '-', '')")
    @Options(keyProperty = "id", useGeneratedKeys = true)
    @Insert("INSERT INTO `oauth_role_user` (`id`, `roleid`, `userid`) VALUES (#{id,jdbcType=VARCHAR}, #{roleid,jdbcType=VARCHAR}, #{userid,jdbcType=VARCHAR}) ")
    void add(@Param("roleid") String roleid, @Param("userid") String userid);

    /**
     * 检查用户角色是否存在
     * @param roleid
     * @param userid
     * @return
     */
    @Select("SELECT 1 FROM `oauth_role_user` WHERE roleid = #{roleid,jdbcType=VARCHAR} AND userid = #{userid,jdbcType=VARCHAR}")
    Integer checkOne(@Param("roleid") String roleid, @Param("userid") String userid);


}