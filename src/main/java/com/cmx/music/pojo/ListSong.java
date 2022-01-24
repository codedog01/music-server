package com.cmx.music.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class ListSong implements Serializable {

    private Integer id;

    private Integer songId;

    private Integer songListId;


}
