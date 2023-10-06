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
    @Value("${cleanRelay}")
    private static long cleanRelay;
    @Value("${tokenLease}")
    private static long tokenLease;
    @Value("${maxTokenLease}")
    private static long maxTokenLease;
    @Value("${cacheLease}")
    private static long cacheLease;
    private static Timer timer;
    private static long startTime;
    // token -> uid
    private static volatile Map<String, Integer> bind;
    // uid -> user
    private static volatile Map<Integer, Cache> pool;

    private UserCache() {}

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

                    // 自动清理缓存
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // 这样的话直接清除缓存了
//                            Iterator<Cache> iterator = pool.values().iterator();
//                            while (iterator.hasNext()) {
//                                Cache cache = iterator.next();
//                                if (cache.startTime + maxTokenLease < System.currentTimeMillis()) {
//                                    iterator.remove();
//                                } else if (cache.lastTime + tokenLease < System.currentTimeMillis()) {
//                                    iterator.remove();
//                                }
//                            }
                            // 优先清除 token 但保留缓存
                            Iterator<Integer> iterator = bind.values().iterator();
                            while (iterator.hasNext()) {
                                Cache cache = pool.get(iterator.next());
                                if (cache != null) {
                                    long ct = System.currentTimeMillis();
                                    // 移除缓存即移除 token
                                    if (cache.startTime + cacheLease < ct) {
                                        // 没有遍历 pool 可以直接 put
                                        pool.put(cache.user.uid, null);
                                    } else if (cache.startTime + maxTokenLease < ct) {
                                        iterator.remove();
                                    } else if (cache.lastTime + tokenLease < ct) {
                                        iterator.remove();
                                    }
                                }
                            }
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
        return cache == null ? null : cache.user;
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
        if (pool.containsKey(uid)) {
            token = UUID.randomUUID().toString();
            bind.put(token, uid);
        }

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
    public long lastTime;
    public User user;

    public Cache(User user) {
        this.startTime = this.lastTime = System.currentTimeMillis();
        this.user = user;
    }
}
