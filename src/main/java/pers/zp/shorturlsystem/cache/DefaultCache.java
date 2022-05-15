package pers.zp.shorturlsystem.cache;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认的缓存实现
 * 本来手写的一个的，但是既然已经引入了Guava 那就直接用google 大神的轮子不必自己写的香吗
 * 生产环境替换为redis 等其他缓存实现
 */
@Service
public class DefaultCache<T>  implements BaseCache<T>{

    /**
     * 请求数量
     */
    AtomicInteger requestCount = new AtomicInteger(0);


    private LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Object>() {
                @Override
                public Object load(String key) throws Exception {
                    //这里必须让我初始化一个值，还不能为null ，不然就报错

                    return Optional.empty();//当缓存过期会在这生成值
                }
            });


    @Override
    public void save(String key, Object o) {
        cache.put(key,o);
    }

    @Override
    public T getCache(String key) throws ExecutionException {
        Object o = cache.get(key);
        return (T)o;
    }

    @Override
    public void del(String key) {
        cache.invalidate(key);
    }

    @Override
    public void 请求数加一() {
        requestCount.getAndIncrement();
    }

    @Override
    public int 获取实时请求数() {
        return requestCount.get();
    }
}
