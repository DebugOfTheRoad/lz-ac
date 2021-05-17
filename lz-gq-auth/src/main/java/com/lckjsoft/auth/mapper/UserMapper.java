package com.lckjsoft.auth.mapper;


import com.lckjsoft.common.model.OAuthUser;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 *
 * @author uid40330
 */
@Mapper
public interface UserMapper {
    /**
     * 添加用户
     * @param name
     * @param password
     */
    @SelectKey(keyProperty = "id", resultType = String.class, before = true, statement = "select replace(uuid(), '-', '')")
    @Options(keyProperty = "id", useGeneratedKeys = true)
    @Insert("INSERT INTO `oauth_user` (`id`, `enabled`, `name`, `password`, `locked`, `oauthed`) "
            + "VALUES (#{id,jdbcType=VARCHAR}, 1, #{name,jdbcType=VARCHAR}, #{password,jdbcType=VARCHAR}, 0, 1)")
    void add(@Param("name") String name, @Param("password") String password);

    /**
     * 通过用户名获取用户
     * @param name
     * @return
     */
    @Select("SELECT * FROM oauth_user WHERE name = #{name,jdbcType=VARCHAR} ")
    OAuthUser findByUserName(@Param("name") String name);

    /**
     * 验证clientid
     * @param clientId
     * @return
     */
    @Select("SELECT 1 FROM oauth_client_details WHERE client_id = #{clientId,jdbcType=VARCHAR}")
    Integer checkClientId(@Param("clientId") String clientId);

    /**
     * checkname
     * @param name
     * @return
     */
    @Select("SELECT 1 FROM oauth_user WHERE name = #{name,jdbcType=VARCHAR}")
    Integer checkName(@Param("name") String name);

    /**
     * 设置有效时间
     * @param validtime
     * @param name
     */
    @Update("UPDATE `oauth_user` SET validtime = #{validtime,jdbcType=DATE} WHERE name = #{name,jdbcType=VARCHAR} ")
    void validtime(@Param("validtime") Date validtime, @Param("name") String name);

    /**
     *
     * @param name
     */
    @Update("UPDATE `oauth_user` SET locked = 1 WHERE name = #{name,jdbcType=VARCHAR} ")
    void locked(@Param("name") String name);

    /**
     * 获取用户的权限
     * @param userid
     * @return
     */
    @Select("select oauth_permission.url from oauth_role_permission \n"
            + "INNER JOIN oauth_role on (oauth_role_permission.roleid = oauth_role.id AND oauth_role.enabled = 1) \n"
            + "INNER JOIN oauth_permission on oauth_role_permission.permissionid = oauth_permission.id \n"
            + "where oauth_role_permission.roleid in (select oauth_role_user.roleid from oauth_role_user where oauth_role_user.userid = #{userid,jdbcType=VARCHAR})" )
    List<String> getPermissions(@Param("userid") String userid);

    /**
     *获取用户的角色
     * @param userid
     * @return
     */
    @Select("select GROUP_CONCAT(oauth_role.`name`) names from oauth_role where oauth_role.enabled =1 "
            + "and oauth_role.id in (select oauth_role_user.roleid from oauth_role_user where oauth_role_user.userid = #{userid,jdbcType=VARCHAR})" )
    String getRoleNames(@Param("userid") String userid);

    /**
     * 根据用户名获取userid
     * @param name
     * @return
     */
    @Select("SELECT id FROM `oauth_user` WHERE name = #{name,jdbcType=VARCHAR}")
    String getId(@Param("name") String name);
}
