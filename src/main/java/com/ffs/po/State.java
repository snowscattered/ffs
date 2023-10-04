package com.ffs.po;

/**
 * order 表的 state 字段
 * 在 order 类中透过枚举常量表示
 * @author hoshinosena
 * @version 1.1
 */
public enum State {
    unpaid,
    paid,
    received,
    delivering,
    finish,
    // 1.1添加了 cancel 状态
    cancel
}
