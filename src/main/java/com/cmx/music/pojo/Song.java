package com.cmx.music.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data

@EqualsAndHashCode(callSuper = false)
public class Song {

    private Integer id;

    private Integer singerId;

    private String name;

    private String introduction;

    private Date createTime;

    private Date updateTime;

    private String pic;

    private String lyric;

    private String url;


}
