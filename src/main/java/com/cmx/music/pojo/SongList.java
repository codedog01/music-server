package com.cmx.music.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SongList {

    private Integer id;

    private String title;

    private String pic;

    private String style;

    private String introduction;

}
