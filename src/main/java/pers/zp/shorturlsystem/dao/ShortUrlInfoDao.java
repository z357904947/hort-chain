package pers.zp.shorturlsystem.dao;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pers.zp.shorturlsystem.model.ShortUrl;
import reactor.core.publisher.Mono;

/**
 *  继承ReactiveCrudRepository获取基本的响应式增删改查能力
 */
@Repository
public interface ShortUrlInfoDao extends ReactiveCrudRepository<ShortUrl, Integer> {


    /**
     * 通过urlInt获取实体对象
     * @return
     */
    Mono<ShortUrl> findByShortUrlInt(Integer urlInt);


    /**
     * 通过短链字符串获取实体对象
     * @return
     */
    Mono<ShortUrl> findByShortUrl(String shortStr);
}
