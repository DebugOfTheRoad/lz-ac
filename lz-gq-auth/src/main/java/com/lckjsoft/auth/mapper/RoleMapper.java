package com.lckjsoft.auth.mapper;

import org.apache.ibatis.annotations.*;

/**
 * @author uid40330
 */
@Mapper
public interface RoleMapper {

    /**
     * 添加一个角色
     * @param name
     * @param enabled
     */
	@SelectKey(keyProperty = "id", resultType = String.class, before = true, statement = "select replace(uuid(), '-', '')")
	@Options(keyProperty = "id", useGeneratedKeys = true)
    @Insert("INSERT INTO `oauth_role` (`id`, `name`, `enabled`) VALUES (#{id,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{enabled,jdbcType=TINYINT}) ")
    void add(@Param("name") String name, @Param("enabled") Integer enabled);

    /**
     * 验证角色名字是否存在
     * @param name
     * @return
     */
    @Select("SELECT 1 FROM `oauth_role` WHERE name = #{name,jdbcType=VARCHAR}")
    Integer checkName(@Param("name") String name);

    /**
     *  设置角色状态
     * @param enabled
     * @param name
     */
    @Update("UPDATE `oauth_role` SET enabled =#{enabled,jdbcType=TINYINT} WHERE name = #{name,jdbcType=VARCHAR} ")
   	void setEnabled(@Param("enabled") Integer enabled, @Param("name") String name);

    /**
     * 角色名称->id
     * @param name
     * @return
     */
    @Select("SELECT id FROM `oauth_role` WHERE name = #{name,jdbcType=VARCHAR}")
    String getId(@Param("name") String name);
}
