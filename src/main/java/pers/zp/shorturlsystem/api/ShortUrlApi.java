package pers.zp.shorturlsystem.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.zp.shorturlsystem.cache.BaseCache;
import pers.zp.shorturlsystem.model.ShortUrl;
import pers.zp.shorturlsystem.model.请求次数时间间隔统计实体;
import pers.zp.shorturlsystem.service.ShortUrlService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping
public class ShortUrlApi {

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    BaseCache  baseCache;

    /**
     * 长链转为短链
     *
     * @param url
     * @return
     */
    @PostMapping(value = "/hash/shortUrl",consumes = "multipart/form-data")
    public Mono<ShortUrl> createShortUrl( @RequestPart String url)
    {
        return shortUrlService.longUrlToShortStr(url);
    }

    @GetMapping("{shortUrl}" )
    public Mono<ResponseEntity> getUrl(@PathVariable String shortUrl)
    {
        //获取短链对应的长链 ,转换为302重定向。没找到则啥都不干。
        return shortUrlService.getUrlByShortStr(shortUrl)
                //过滤掉缓存中标记的非法数据
                .filter(a->a.getId()!=null)
                .doOnNext(a-> baseCache.请求数加一())
                .map(a->ResponseEntity.status(HttpStatus.FOUND).location(URI.create(a.getTargetUrl())).build())
                ;


    }

    /**
     * 显示实时系统访问量
     * @return
     */
    @GetMapping(value = "/show/real/count" ,produces = "text/event-stream")
    public Flux ss()
    {

        return Flux.generate(()->new 请求次数时间间隔统计实体(-1),(a, sink) -> {

            int count = a.get请求次数();
            int sum = baseCache.获取实时请求数();
            LocalDateTime localDateTime = LocalDateTime.now();
            //取每分内的0 ，10 ，20 ，30，40，50 时的数据
                    System.out.println("生产数据");
            int 余数 = localDateTime.getSecond()%10;
            if(count == -1&&余数!=0)
            {
                //睡眠
                try {
                    TimeUnit.SECONDS.sleep(10-余数);
                    localDateTime = localDateTime.plusSeconds(10-余数);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            int 间隔请求次数 = 0 ;
            int  newCount =  baseCache.获取实时请求数();
            if(count == -1)
            {
                //说明是第一组数据
                间隔请求次数 = newCount - sum;
            }else
            {
                间隔请求次数 = newCount - count;
                localDateTime = a.get统计时间().plusSeconds(10);
            }
            请求次数时间间隔统计实体 返回数据 = new 请求次数时间间隔统计实体();
            返回数据.set请求次数(间隔请求次数);
            返回数据.set统计时间(localDateTime);
            sink.next(返回数据);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 返回数据;
        });
    }


    @GetMapping(value = "/show/sum" ,produces = "text/event-stream")
    public Flux  test()
    {
        //参照create方法的注释写的一个通过事件回调驱动主动通知前端简单方法。
        return shortUrlService.sumData();


    }


}
