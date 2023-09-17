package com.ffs.mapper;

import com.ffs.po.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * product 表的增删改查
 * 伪删除使用
 * @author hoshinosena
 * @version 1.0
 */
@Mapper
public interface ProductMapper {
    @Select("select * from product where pid=#{pid}")
    Product selectProductByPid(int pid);

    @Select("select * from product where del=0 and uid=#{uid} order by pid desc")
    List<Product> selectProductsByUid(int uid);

    @Select("select pid,uid,name,image from product where del=0 and name like concat('%',#{name},'%')")
    @Results({@Result(property="pid", column="pid"),
            @Result(property="uid", column="uid"),
            @Result(property="name", column="name"),
            @Result(property="image", column="image")})
    List<Product> selectProductsByName(String name);

    @Select("select pid,uid,name,price,score from product where del=0")
    @Results({@Result(property="pid", column="pid"),
            @Result(property="uid", column="uid"),
            @Result(property="name", column="name"),
            @Result(property="price", column="price"),
            @Result(property="score", column="score")})
    List<Product> selectProducts();

    @Insert("insert into product(uid,name,image,price,score,info,del) "
            + "values(#{uid},#{name},#{image},#{price},0,#{info},0)")
    int insertProduct(Product product);

    @Update("<script>"
            + "update product"
            + "<set>"
            +     "<if test=\"name != null\">name=#{name},</if>"
            +     "<if test=\"image != null\">image=#{image},</if>"
            +     "<if test=\"price != null\">price=#{price},</if>"
            +     "<if test=\"info != null\">info=#{info}</if>"
            + "</set>"
            + "<where>"
            +     "del=0 and pid=#{pid}"
            + "</where>"
            + "</script>")
    int updateProduct(Product product);

    @Update("update product set del=1 where del=0 and pid=#{pid}")
    int deleteProduct(int pid);
}
