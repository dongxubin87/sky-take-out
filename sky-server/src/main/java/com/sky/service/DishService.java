package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;

public interface DishService {


    /**
     * add new dish with flavor
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);
}
