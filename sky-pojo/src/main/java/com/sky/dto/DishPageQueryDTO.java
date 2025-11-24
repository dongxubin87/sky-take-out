package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DishPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;

    // categoryId
    private Integer categoryId;

    //status 0-> disable 1 -> enable
    private Integer status;

}
