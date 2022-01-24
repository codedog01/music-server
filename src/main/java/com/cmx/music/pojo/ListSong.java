package com.cmx.music.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class ListSong implements Serializable {

    private Integer id;

    private Integer songId;

    private Integer songListId;


}
