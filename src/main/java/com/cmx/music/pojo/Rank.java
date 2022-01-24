package com.cmx.music.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class Rank implements Serializable {

    private Long id;

    private Long songListId;

    private Long consumerId;

    private Integer score;

}
