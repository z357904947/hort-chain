package pers.zp.shorturlsystem.service;

import pers.zp.shorturlsystem.model.ShortUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShortUrlService {

    /**
     * 将长URL转为短url
     * @param url
     * @return
     */
    Mono<ShortUrl> longUrlToShortStr(String url);


    /**
     * 通过短地址参数获取对于的长地址
     * @param str
     * @return
     */
    Mono<ShortUrl> getUrlByShortStr(String str);


    /**
     * 实时系统短链数据总量
     * @return
     */
    public Flux<Integer> sumData();

}
