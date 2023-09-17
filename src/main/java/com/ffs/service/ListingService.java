package com.ffs.service;

import com.ffs.po.Listing;

import java.util.List;

/**
 * ListingService 封装了代理类 ListingMapper
 * 并在执行相应方法前进行数据检查确保返回值是预期的
 * @author hoshinosena
 * @version 1.0
 */
public interface ListingService {
    /**
     * 透过 lid 查找 listing
     * @param lid listing 的 lid 字段
     * @return null 或 listing 引用
     * @author hoshinosena
     */
    Listing findListing(int lid);

    /**
     * 透过 oid 查找 listing
     * @param oid listing 的 oid 字段
     * @return 非 null 的 List<Listing> 引用
     * @author hoshinosena
     */
    List<Listing> findListingsByOid(int oid);

    /**
     * 获取所有 listing
     * @return 非 null 的 List<Listing> 引用
     * @author hoshinosena
     */
    List<Listing> findListings();

    /**
     * 添加 listing
     * @param listing 非 null 引用
     * @return 1: 添加成功
     *         0: 来自数据库的错误
     * @author hoshinosena
     */
    int addListing(Listing listing);

    /**
     * 批量添加 listing, 添加失败时自动回滚操作
     * @param listings 非 null 的 List<Listing>
     *                 引用
     * @return ~0: 添加成功
     *          0: 添加失败
     * @author hoshinosena
     */
    int addListings(List<Listing> listings);

    /**
     * 删除 listing
     * @param lid listing 的 lid 字段
     * @return 1: 删除成功
     *         0: 来自数据库的错误
     * @author hoshinosena
     */
    int delListing(int lid);
}
