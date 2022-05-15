-- auto-generated definition
create table short_url_info
(
    id            int auto_increment
        primary key,
    short_url     varchar(10) not null comment '短串64位转换后的字符串表示',
    target_url    text        not null comment '对应的目标url',
    short_url_int int         not null comment '短串10进制表示',
    create_time   timestamp   null,
    constraint short_url_info_pk_2
        unique (short_url_int),
    constraint short_url_info_pk_3
        unique (short_url)
)
    comment '短链信息表';

