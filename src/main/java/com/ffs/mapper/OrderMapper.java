package com.ffs.mapper;

import com.ffs.po.Order;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import java.util.List;

/**
 * order 表的增删改查
 * 伪删除使用
 * @author hoshinosena
 * @version 1.0
 */
@Mapper
public interface OrderMapper {
    @Select("select * from _order where del=0 and oid=#{oid}")
    @Result(property="state", column="state", typeHandler=EnumOrdinalTypeHandler.class)
    Order selectOrderByOid(int oid);

    @Select("select * from _order where del=0 and tid=#{tid}")
    @Result(property="state", column="state", typeHandler=EnumOrdinalTypeHandler.class)
    Order selectOrderByTid(String tid);

    @Select("select * from _order where del=0 and bid=#{bid} order by oid desc")
    @Result(property="state", column="state", typeHandler=EnumOrdinalTypeHandler.class)
    List<Order> selectOrderByBid(int bid);

    @Select("select * from _order where del=0 and sid=#{sid} order by oid desc")
    @Result(property="state", column="state", typeHandler=EnumOrdinalTypeHandler.class)
    List<Order> selectOrderBySid(int sid);

    @Select("select * from _order where del=0 and did=#{did} order by oid desc")
    @Result(property="state", column="state", typeHandler=EnumOrdinalTypeHandler.class)
    List<Order> selectOrderByDid(int did);

    @Select("select * from _order where del=0 order by oid desc")
    @Result(property="state", column="state", typeHandler=EnumOrdinalTypeHandler.class)
    List<Order> selectOrders();

    @Insert("insert into _order(state,bid,sid,did,tid,date,info,del) "
            + "values(0,#{bid},#{sid},null,'',#{date},#{info},0)")
    int insertOrder(Order order);

    @Update("<script>"
            + "update _order"
            + "<set>"
            +     "<if test=\"state != -1\">state=#{state},</if>"
            +     "<if test=\"order.sid != null\">sid=#{order.sid},</if>"
            +     "<if test=\"order.did != null\">did=#{order.did},</if>"
            +     "<if test=\"order.tid != null\">tid=#{order.tid},</if>"
            +     "<if test=\"order.info != null\">info=#{order.info},</if>"
            + "</set>"
            + "<where>"
            +     "del=0 and oid=#{order.oid}"
            + "</where>"
            + "</script>")
    int updateOrder(Order order, int state);

    @Update("update _order set del=1 where del=0 and oid=#{oid}")
    int deleteOrder(int oid);
}
