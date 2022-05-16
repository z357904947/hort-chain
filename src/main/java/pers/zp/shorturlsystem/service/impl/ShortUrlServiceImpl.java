package pers.zp.shorturlsystem.service.impl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pers.zp.shorturlsystem.cache.DefaultCache;
import pers.zp.shorturlsystem.dao.ShortUrlInfoDao;
import pers.zp.shorturlsystem.model.ShortUrl;
import pers.zp.shorturlsystem.service.ShortUrlService;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


@Service
@Slf4j
public class ShortUrlServiceImpl implements ShortUrlService {
    @Autowired
    ShortUrlInfoDao shortUrlInfoDao;

    @Autowired
    DefaultCache defaultCache;

    /**
     * 当hash冲突时使用
     */
    final static String 后缀字符串 = "jfklsjaifhjaskdj";

    /**
     * 将长链映射成短链
     * @param url
     * @return
     */
    @Override
    public Mono<ShortUrl> longUrlToShortStr(String url) {
        return longUrlToShortStr(url , null);

    }
    public Mono<ShortUrl> longUrlToShortStr(String url,String 后缀) {



        return Mono.just(url)
                        .filter(a->URI.create(url).isAbsolute())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("不是一个有效的长链地址！")))
                        .flatMap(a->Mono.just(getShortUrl(a, 后缀)))
                        .flatMap(shortUrl->shortUrlInfoDao.save(shortUrl).onErrorResume(throwable -> {
                        log.error("异常类{},异常信息{}",throwable.getClass().getName(),throwable.getMessage());
                        if(throwable instanceof DataIntegrityViolationException&& shortUrl.getId()==null){
                            //如果能找到，且长链相等则直接返回
                            return shortUrlInfoDao.findByShortUrlInt(shortUrl.getShortUrlInt())
                                    //如果没有查询到数据，则可能只是保存数据出错了。
                                    .switchIfEmpty( Mono.error(()->new RuntimeException("服务异常！")))
                                    //如果能查询到数据，则判断长链是否相等
                                    .filter(a->{
                                        log.debug("是否相等{}",a.getTargetUrl().equals(url));
                                        return a.getTargetUrl().equals(url);
                                    })
                                    //如果不等，则加上后缀重新获取次短链
                                    //Mono.defer解决Mono中发射的元素即使是空，switchIfEmpty仍然被调用，即longUrlToShortStr被调用的问题。
                                    .switchIfEmpty(Mono.defer(()->longUrlToShortStr(url,后缀字符串)));
                        }
                        return Mono.error(()->new RuntimeException("服务异常！"));
                }));
    }

    private ShortUrl getShortUrl(String url, String 后缀) {
       if(! URI.create(url).isAbsolute())
       {
           throw new IllegalArgumentException("SSSSSSSSSSSSS");
       }
        HashFunction hashFunction = Hashing.murmur3_32_fixed();
        int hashInt ;
        if(后缀 == null)
        {
            hashInt = hashFunction.hashString(url, StandardCharsets.UTF_8).asInt();
        }else
        {
            hashInt = hashFunction.hashString(url + 后缀, StandardCharsets.UTF_8).asInt();
        }

        log.debug("hash int结果{}",hashInt);
        String 短链的64进制表示 = 十进制转64进制表示(hashInt);
        log.debug("hash int结果转换为64进制结果{}",短链的64进制表示);
        //把结果和长链保存到数据库，如果主键重复，则说明hash碰撞，此时先获取该散列值对应的数据，判断其对应的长链是否和本次要转换的长链相等，
        //若不等，说明hash冲突发生，则加上后缀重新散列一次。再保存
        ShortUrl shortUrl = new ShortUrl(短链的64进制表示,hashInt, url);
        return shortUrl;
    }

    /**
     * 根据短链获取长链地址
     * @param str
     * @return
     */
    @SneakyThrows
    @Override
    public Mono<ShortUrl> getUrlByShortStr(String str) {
        //响应式的话没法直接缓存Mono 因为mono 其实是一个操作集，我们要缓存的应该是数据
        //spring cache 不适用
        // 操作redis 有ReactiveRedisTemplate
        // 这里自己实现一个缓存接口，
        return Mono.just((Optional)defaultCache.getCache(str))
                .filter(a->a.isPresent())
                //有值则拿出缓存返回
                .map(a->(ShortUrl)a.get())
                //如果是空值,则从数据库获取
                .switchIfEmpty(Mono.defer(()->shortUrlInfoDao.findByShortUrl(str)
                        //数据库也没有，则给一个空对象放入缓存，防止缓存击穿
                        .defaultIfEmpty( new ShortUrl())
                        .doOnNext(b->{
                            log.debug("没有缓存，加载缓存！");
                            defaultCache.save(str,Optional.of(b));
                        })


                ))
                //如果出错了就直接回退流，给一个空值
                .onErrorResume(throwable->{
                    log.error("非法数据！{}",str);
                    return Mono.empty();
                })
                ;

    }

    /**
     * 自己定义的64位编码
     */
    final static   char[] digits = {
            '0' , '1' , '2' , '3' , '4' ,'5' ,
            '6' , '7' , '8' , '9' , 'a' ,'b' ,
            'c' , 'd' , 'e' , 'f' , 'g' ,'h' ,
            'i' , 'j' , 'k' , 'l' , 'm' ,'n' ,
            'o' , 'p' , 'q' , 'r' , 's' ,'t' ,
            'u' , 'v' , 'w' , 'x' , 'y' ,'z',
            'C' , 'D' , 'E' , 'F' , 'G' ,'H' ,
            'I' , 'J' , 'K' , 'L' , 'M' ,'N' ,
            'O' , 'P' , 'Q' , 'R' , 'S' ,'T' ,
            'U' , 'V' , 'W' , 'X' , 'Y' ,'Z',
            'K' , 'L' , 'M' ,'N'
    };
    public static String  十进制转64进制表示(int i ){

        long num = 0;
        if (i < 0) {
            num =((long)2 * 0x7fffffff) + i + 2;
        } else {
            num =i;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((num / 64) > 0){
            buf[--charPos]= digits[(int)(num % 64)];
            num /=64;
        }
        buf[--charPos] =digits[(int)(num % 64)];
        return new String(buf, charPos,(32 - charPos));



    }
}
