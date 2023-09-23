package com.ffs.mapper;

import com.ffs.po.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * review 表的增删改查
 * 伪删除使用
 * @author hoshinosena
 * @version 1.0
 */
@Mapper
public interface ReviewMapper {
    @Select("select * from review where del=0 and rid=#{rid}")
    Review selectReviewByRid(int rid);

    @Select("select * from review where del=0 and oid=#{oid} and pid=#{pid}")
    Review selectReviewByOP(int oid, int pid);

    @Select("select * from review where del=0 and pid=#{pid} order by rid desc")
    List<Review> selectReviewsByPid(int pid);

    @Select("select * from review where del=0 order by rid desc")
    List<Review> selectReviews();

    @Insert("insert into review(oid,pid,score,detail,date,del) "
            + "values(#{oid},#{pid},#{score},#{detail},#{date},0)")
    int insertReview(Review review);

    @Update("<script>"
            + "update review"
            + "<set>"
            +     "<if test=\"score != null\">score=#{score},</if>"
            +     "<if test=\"detail != null\">detail=#{detail},</if>"
            +     "<if test=\"date != null\">date=#{date},</if>"
            + "</set>"
            + "<where>"
            +     "del=0 and rid=#{rid}"
            + "</where>"
            + "</script>")
    int updateReview(Review review);

    @Update("update review set del=1 where del=0 and rid=#{rid}")
    int deleteReview(int rid);
}
