package com.ffs.mapper;

import com.ffs.po.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import java.util.List;

/**
 * user 表的增删改查
 * 伪删除使用
 * 1.1中 user 添加 image 字段
 * @author hoshinosena
 * @version 1.1
 */
@Mapper
public interface UserMapper {
    @Select("select * from user where del=0 and uid=#{uid}")
    @Result(property="role", column="role", typeHandler=EnumOrdinalTypeHandler.class)
    User selectUserByUid(int uid);

    @Select("select * from user where del=0 and username=#{username}")
    @Result(property="role", column="role", typeHandler=EnumOrdinalTypeHandler.class)
    User selectUserByUsername(String username);

    @Select("select uid,username,name from user where del=0 and role=#{role}")
    @Results({@Result(property="uid", column="uid"),
            @Result(property="username", column="username"),
            @Result(property="name", column="name")})
    List<User> selectUsersByRole(int role);

    @Select("select uid,role,username,name from user where del=0 and name like concat('%',#{name},'%')")
    @Results({@Result(property="uid", column="uid"),
            @Result(property="role", column="role", typeHandler=EnumOrdinalTypeHandler.class),
            @Result(property="username", column="username"),
            @Result(property="name", column="name")})
    List<User> selectUsersByName(String name);

    @Select("select uid,role,username,name from user where del=0")
    @Results({@Result(property="uid", column="uid"),
            @Result(property="role", column="role", typeHandler=EnumOrdinalTypeHandler.class),
            @Result(property="username", column="username"),
            @Result(property="name", column="name")})
    List<User> selectUsers();

    @Insert("insert into user(role,username,password,name,image,tel,address,info,del) "
            + "values(#{_role},#{user.username},#{user.password},#{user.name},#{user.image}#{user.tel},#{user.address},#{user.info},0)")
    int insertUser(User user, int _role);

    @Update("<script>"
            + "update user"
            + "<set>"
            +     "<if test=\"_role != -1\">role=#{_role},</if>"
            +     "<if test=\"user.username != null\">username=#{user.username},</if>"
            +     "<if test=\"user.password != null\">password=#{user.password},</if>"
            +     "<if test=\"user.name != null\">name=#{user.name},</if>"
            +     "<if test=\"user.image != null\">image=#{user.image},</if>"
            +     "<if test=\"user.tel != null\">tel=#{user.tel},</if>"
            +     "<if test=\"user.address != null\">address=#{user.address},</if>"
            +     "<if test=\"user.info != null\">info=#{user.info},</if>"
            + "</set>"
            + "<where>"
            +     "del=0 and uid=#{user.uid}"
            + "</where>"
            + "</script>")
    int updateUser(User user, int _role);

    @Update("update user set username=NULL,del=1 where del=0 and uid=#{uid}")
    int deleteUser(int uid);
}
