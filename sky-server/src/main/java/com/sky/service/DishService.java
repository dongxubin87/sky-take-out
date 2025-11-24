package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {


    /**
     * add new dish with flavor
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);


    /**
     * DishPageQueryDTO
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * Batch deletion for dishes
     * @param ids
     * @return
     */
    void deleteBatch(List<Long> ids);
}
