package pers.zp.shorturlsystem.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.zp.shorturlsystem.dao.ShortUrlInfoDao;
import pers.zp.shorturlsystem.model.ShortUrl;
import pers.zp.shorturlsystem.service.ShortUrlService;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class ShortUrlServiceImplTest {

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    ShortUrlInfoDao shortUrlInfoDao;

    @Test
    void longUrlToShortStr() throws InterruptedException {

        Mono<ShortUrl> shortUrlMono = shortUrlService.longUrlToShortStr("sdd77777777888ddddddggfdgfga" +
                "sffffffffffffsdasasdf89776666666666664ghghjghjsdsdg");
        System.out.println("线程============="+Thread.currentThread().getName());
        shortUrlMono.subscribe(a->
                {
                    System.out.println("线程============="+Thread.currentThread().getName());
                    System.out.println(a);
                });

        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    void query() throws InterruptedException {

        Mono<ShortUrl> byShortUrlInt = shortUrlInfoDao.findByShortUrlInt(1919949416)
                .switchIfEmpty( Mono.error(()->new RuntimeException("没有数据！")))
                .filter(a->a.getTargetUrl().equals("sssssssssss"))
                .switchIfEmpty( shortUrlInfoDao.findByShortUrlInt(1008492702))
                ;
        byShortUrlInt.subscribe(
                a->
                {
                    System.out.println("线程============="+Thread.currentThread().getName());
                    System.out.println(a);
                }
        );
        TimeUnit.SECONDS.sleep(5);
    }
}