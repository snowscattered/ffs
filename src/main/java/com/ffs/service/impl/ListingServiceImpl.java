package com.ffs.service.impl;

import com.ffs.mapper.ListingMapper;
import com.ffs.po.Listing;
import com.ffs.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ListingServiceImpl 实现了 ListingService
 * 详细信息在 ListingService 接口中说明
 * @author hoshinosena
 * @version 1.0
 */
@Service
public class ListingServiceImpl implements ListingService {
    @Autowired
    ListingMapper listingMapper;

    @Override
    public Listing findListing(int lid) {
        return listingMapper.selectListingByLid(lid);
    }

    @Override
    public List<Listing> findListingsByOid(int oid) {
        return listingMapper.selectListingsByOid(oid);
    }

    @Override
    public List<Listing> findListings() {
        return listingMapper.selectListings();
    }

    @Override
    public int addListing(Listing listing) {
        try {
            return listingMapper.insertListing(listing);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int addListings(List<Listing> listings) {
        int i = 0,
            j = listings.size();
        for (; i<j; i++) {
            try {
                if (listingMapper.insertListing(listings.get(i)) == 0) {
                    listingMapper.deleteListing_oid(listings.get(0).oid);
                    return 0;
                }
            } catch (Exception e) {
                listingMapper.deleteListing_oid(listings.get(0).oid);
                return 0;
            }
        }

        return j;
    }

    @Override
    public int delListing(int lid) {
        return listingMapper.deleteListing_lid(lid);
    }
}
