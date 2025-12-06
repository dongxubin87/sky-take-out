package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

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


    /**
     * get dish By Id With Flavor
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);


    /**
     * update dish
     * @return
     */

    void updateWithFlavor(DishDTO dishDTO);


    /**
     * get dish by categoryId
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);


    List<DishVO> listWithFlavor(Dish dish);


    /**
     * Enable/Disable dish sale
     * @param status
     * @param id
     * @return
     */
    void startOrStop(Integer status, Long id);
}
