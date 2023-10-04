package com.ffs.po;

/**
 * review 表的 POJO 类
 * @author hoshinosena
 * @version 1.1
 */
public class Review {
    public int rid;
    public int oid;
    // 1.1修改 pid 为 uid
    // 只能对商家评论
    public int uid;
    public Integer score;
    public String date;
    public String detail;
}
