package com.cmx.music.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class Rank implements Serializable {

    private Long id;

    private Long songListId;

    private Long consumerId;

    private Integer score;

}
