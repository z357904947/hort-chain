package pers.zp.shorturlsystem.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 短链实体
 */
@Data
@Table("short_url_info")
@NoArgsConstructor
public class ShortUrl {
    @Id
    private Integer id;

    private String shortUrl ;

    private Integer shortUrlInt;

    private String targetUrl;

    @CreatedDate
    private LocalDateTime createTime;

    public ShortUrl(String shortUrl, Integer shortUrlInt, String targetUrl) {
        this.shortUrl = shortUrl;
        this.shortUrlInt = shortUrlInt;
        this.targetUrl = targetUrl;
    }
}
