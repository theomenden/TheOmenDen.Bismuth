package com.theomenden.bismuth.caching.strategies;

import com.theomenden.bismuth.caching.caches.ColorBlendingCache;
import com.theomenden.bismuth.utils.ColorCachingUtils;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseSlice {
    private long key;
    private int size;
    private int salt;
    private int age;

    private AtomicInteger referenceCount = new AtomicInteger();

    public BaseSlice(int size, int salt) {
        this.size = Math.max(size,0);
        this.salt = Math.max(salt, 0);

        this.markCacheAsInvalid();
    }

    public abstract void invalidateCacheData();

    public final long getCacheKey() {
        return key;
    }

    public final int getSize() {
        return size;
    }

    public final int getSalt() {
        return salt;
    }

    public final int getAge() {
        return age;
    }

    public final void setAge(int age) {
        this.age = age;
    }

    public final void setCacheKey(long key) {
        this.key = key;
    }

    public final int getReferenceCount() {
        return referenceCount.get();
    }

    public final void releaseReference() {
        referenceCount.decrementAndGet();
    }

    public final void acquireReference() {
        referenceCount.incrementAndGet();
    }

    public final boolean isInvalid() {
        return ((this.key ^ this.salt)) == ColorCachingUtils.INVALID_CHUNK_KEY;
    }

    public final void markCacheAsInvalid() {
        this.key = ColorCachingUtils.INVALID_CHUNK_KEY ^ salt;
    }
}
