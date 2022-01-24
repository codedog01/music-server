package com.cmx.music.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Admin {
    private Integer id;

    private String name;

    private String password;


}
