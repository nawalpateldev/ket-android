package com.ketgrouponline.Storage;

import com.ketgrouponline.Bean.NumberBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameCacheManager {
    private static GameCacheManager instance;
    private static final long DEFAULT_TTL_MS = 15 * 60 * 1000; // 15 minutes TTL

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private GameCacheManager() {}

    public static synchronized GameCacheManager getInstance() {
        if (instance == null) {
            instance = new GameCacheManager();
        }
        return instance;
    }

    public static class CacheEntry {
        private final List<NumberBean> numbers;
        private final long timestamp;

        public CacheEntry(List<NumberBean> numbers, long timestamp) {
            this.numbers = numbers;
            this.timestamp = timestamp;
        }

        public List<NumberBean> getNumbers() {
            return numbers;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    public void put(String key, List<NumberBean> numbers) {
        if (key == null || numbers == null) return;
        List<NumberBean> copy = new ArrayList<>();
        for (NumberBean item : numbers) {
            NumberBean bean = new NumberBean();
            bean.setSid(item.getSid());
            bean.setDigit(item.getDigit());
            bean.setValue(item.getValue());
            copy.add(bean);
        }
        cache.put(key, new CacheEntry(copy, System.currentTimeMillis()));
    }

    public List<NumberBean> get(String key) {
        if (key == null) return null;
        CacheEntry entry = cache.get(key);
        if (entry == null) return null;

        List<NumberBean> copy = new ArrayList<>();
        for (NumberBean item : entry.getNumbers()) {
            NumberBean bean = new NumberBean();
            bean.setSid(item.getSid());
            bean.setDigit(item.getDigit());
            bean.setValue(item.getValue());
            copy.add(bean);
        }
        return copy;
    }

    public boolean isExpired(String key) {
        if (key == null) return true;
        CacheEntry entry = cache.get(key);
        if (entry == null) return true;
        return (System.currentTimeMillis() - entry.getTimestamp()) > DEFAULT_TTL_MS;
    }

    public void clear(String key) {
        if (key != null) {
            cache.remove(key);
        }
    }

    public void clearAll() {
        cache.clear();
    }
}
