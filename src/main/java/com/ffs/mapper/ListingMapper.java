package com.ffs.mapper;

import com.ffs.po.Listing;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * listing 表的增删查
 * @author hoshinosena
 * @version 1.0
 */
@Mapper
public interface ListingMapper {
    @Select("select * from listing where lid=#{lid}")
    Listing selectListingByLid(int lid);

    @Select("select * from listing where oid=#{oid}")
    List<Listing> selectListingsByOid(int oid);

    @Select("select * from listing")
    List<Listing> selectListings();

    @Insert("insert into listing(uid,oid,pid,amount)"
            + "values(#{uid},#{oid},#{pid},#{amount})")
    int insertListing(Listing listing);

    @Delete("delete from listing where lid=#{lid}")
    int deleteListing_lid(int lid);

    @Delete("delete from listing where oid=#{oid}")
    int deleteListing_oid(int oid);
}
