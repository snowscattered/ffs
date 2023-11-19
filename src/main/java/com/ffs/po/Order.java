package com.ffs.po;

import java.util.List;

/**
 * order 表的 POJO 类
 * @author hoshinosena
 * @version 1.0
 */
public class Order {
    public int oid;
    public State state;
    public Integer bid;
    public Integer sid;
    public Integer did;
    public String tid;
    public String date;
    public String info;
    public String bn;
    public String bt;
    public String ba;
    public String sn;
    public String st;
    public String sa;
    public String dn;
    public String dt;
    public List<Listing> listings;

    @Override
    public String toString()
    {
        return "Order{" +
                "oid=" + oid +
                ", state=" + state +
                ", bid=" + bid +
                ", sid=" + sid +
                ", did=" + did +
                ", tid='" + tid + '\'' +
                ", date='" + date + '\'' +
                ", info='" + info + '\'' +
                ", bn='" + bn + '\'' +
                ", bt='" + bt + '\'' +
                ", ba='" + ba + '\'' +
                ", sn='" + sn + '\'' +
                ", st='" + st + '\'' +
                ", sa='" + sa + '\'' +
                ", dn='" + dn + '\'' +
                ", dt='" + dt + '\'' +
                ", listings=" + listings +
                '}';
    }
}
