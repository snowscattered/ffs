package com.ffs.util.helper;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class PageChunk
{
    public static <T> PageInfo<T> chunk(String block, int blockSize, Assign<T> Assign)
    {
        List<T> list=Assign.assign();
        if(block!=null)
            PageHelper.startPage(Integer.parseInt(block),blockSize);
        return new PageInfo<>(list);
    }
}
