package pers.zp.shorturlsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class 请求次数时间间隔统计实体 {


//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    LocalDateTime 统计时间;

    Integer 请求次数;


    public 请求次数时间间隔统计实体(Integer 请求次数) {
        this.请求次数 = 请求次数;
    }
}
