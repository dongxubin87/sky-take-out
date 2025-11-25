package com.sky.mapper;


import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    /**
     * insert flavors
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);


    /**
     * delete dish_flavor by id
     * @param dishId
     */


    @Delete("delete from dish_flavor where dish_id = ${dishId}")
    void deleteByDishId(Long dishId);


    /**
     * batch deletion dish_flavor by id
     * @param ids
     */

    void deleteByDishIds(List<Long> ids);


    /**
     * get dish flavor by dish id
     * @param dishId
     * @return List
     */

    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getbyDishId(Long dishId);
}
