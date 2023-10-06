package com.ffs.cache;

import com.ffs.po.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * user 表的缓存
 * 需透过静态方法手工管理
 * @author hoshinosena
 * @version 1.0
 */
@Component
public class UserCache {
    private static long cleanRelay;
    private static long tokenLease;
    private static long maxTokenLease;
    private static long cacheLease;
    private static Timer tokenTimer;
    private static Timer cacheTimer;
    private static long startTime;
    // token -> uid
    private static volatile Map<String, Integer> bind;
    // uid -> user
    private static volatile Map<Integer, Cache> pool;

    private UserCache(@Value("${cleanRelay}") long cleanRelay,
                      @Value("${tokenLease}") long tokenLease,
                      @Value("${maxTokenLease}") long maxTokenLease,
                      @Value("${cacheLease}") long cacheLease) {
        UserCache.cleanRelay = cleanRelay;
        UserCache.tokenLease = tokenLease;
        UserCache.maxTokenLease = maxTokenLease;
        UserCache.cacheLease = cacheLease;
    }

    /**
     * 静态工厂方法
     * 双重锁使用确保有且仅有一个实例
     * @author hoshinosena
     */
    public static void enable() {
        if (pool == null) {
            synchronized (UserCache.class) {
                if (pool == null) {
                    startTime = System.currentTimeMillis();
                    bind = new ConcurrentHashMap<>();
                    pool = new ConcurrentHashMap<>();

                    // 自动清理 token
                    tokenTimer = new Timer();
                    tokenTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Iterator<Integer> iterator = bind.values().iterator();
                            while (iterator.hasNext()) {
                                Cache cache = pool.get(iterator.next());
                                if (cache != null) {
                                    long ct = System.currentTimeMillis();
                                    if (cache.startTime + maxTokenLease < ct) {
                                        iterator.remove();
                                    } else if (cache.tokenTime + tokenLease < ct) {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }, 0, cleanRelay);
                    // 自动清理缓存
                    cacheTimer = new Timer();
                    cacheTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            pool.values().removeIf(cache -> cache.cacheTime + cacheLease < System.currentTimeMillis());
                        }
                    }, 0, cleanRelay);
                }
            }
        }
    }

    /**
     * 缓存 user
     * 必须先使用工厂方法
     * 是线程安全的(锁使用)
     * @param user 加入 cache 中的 user
     * @author hoshinosena
     */
    public static synchronized void addUser(User user) {
        if (pool == null) {
            return;
        }

        pool.put(user.uid, new Cache(user));
    }

    /**
     * 透过 uid 查找缓存
     * 必须先使用工厂方法
     * 是线程安全的(并发安全的哈希表)
     * @param uid user 的 uid 字段
     * @return null: user 未缓存或未使用工厂方法
     *         user: user 引用
     * @author hoshinosena
     */
    public static User getUser(int uid) {
        if (pool == null) {
            return null;
        }

        Cache cache = pool.get(uid);
        if (cache == null) {
            return null;
        }

        cache.cacheTime = System.currentTimeMillis();
        return cache.user;
    }

    /**
     * 透过 token 查找缓存
     * 必须先使用工厂方法
     * 是线程安全的(并发安全的哈希表)
     * @param token user 的安全令牌
     * @return null: token 不存在或 user 未缓存或未使用工厂方法
     *         user: user 引用
     * @author hoshinosena
     */
    public static User getUser(String token) {
        if (pool == null) {
            return null;
        }

        Integer b = bind.get(token);
        if (b == null) {
            return null;
        }
        Cache cache = pool.get(b);
        if (cache == null) {
            bind.put(token, null);
            return null;
        }

        cache.tokenTime = cache.cacheTime = System.currentTimeMillis();
        return cache.user;
    }

    /**
     * 为缓存的 user 生成或更新 token
     * 必须先使用工厂方法
     * 是线程安全的(锁使用)
     * @param uid user 的 uid 字段
     * @return null: user 未缓存或未使用工厂方法
     *         string: token 字符串
     * @author hoshinosena
     */
    public static synchronized String getToken(int uid) {
        if (pool == null) {
            return null;
        }

        String token = null;
        Cache cache = pool.get(uid);
        if (cache == null) {
            return null;
        }

        token = UUID.randomUUID().toString();
        bind.put(token, uid);
        cache.tokenTime = cache.cacheTime = System.currentTimeMillis();
        return token;
    }

    /**
     * 透过 uid 删除缓存
     * 必须先使用工厂方法
     * 是线程安全的(锁使用)
     * @param uid user 的 uid 字段
     * @author hoshinosena
     */
    public static synchronized void delUser(int uid) {
        if (pool == null) {
            return;
        }

        pool.put(uid, null);
    }

    /**
     * 移除 token 但保留缓存
     * 必须先使用工厂方法
     * 是线程安全的(锁使用)
     * @param token user 的安全令牌
     * @author hoshinosena
     */
    public static synchronized void delUser(String token) {
        if (pool == null) {
            return;
        }

        Integer b = bind.get(token);
        if (b == null) {
            return;
        }
        bind.put(token, null);
        // 保留 cache 缓存
//        Cache cache = pool.get(b);
//        if (cache == null) {
//            return;
//        }
//        pool.put(b, null);
    }
}

class Cache {
    public long startTime;
    public long tokenTime;
    public long cacheTime;
    public User user;

    public Cache(User user) {
        // tokenTime 默认0
        this.startTime = this.cacheTime = System.currentTimeMillis();
        this.user = user;
    }
}
