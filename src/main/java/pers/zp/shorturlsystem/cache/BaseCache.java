package pers.zp.shorturlsystem.cache;

import java.util.concurrent.ExecutionException;

/**
 * 基本的缓存操作
 */
public interface BaseCache<T> {


    void save(String key,Object o);


     T getCache(String key) throws ExecutionException;

    void del(String key);

    void 请求数加一();

    int 获取实时请求数();

    void 更新数据总量(int v);

    int 获取系统总量();

}
