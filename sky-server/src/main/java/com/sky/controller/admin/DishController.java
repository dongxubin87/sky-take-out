package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * dishes management
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "dishes management")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * add new dish
     * @param dishDTO
     * @return Result
     */
    @PostMapping
    @ApiOperation("add new dish")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("add new dish:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * DishPageQueryDTO
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("dish page query")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("dish page query:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * Batch deletion for dishes
     * @param ids
     * @return Result
     */
    @DeleteMapping
    @ApiOperation("Batch deletion for dishes")
    public Result delete(@RequestParam List<Long> ids ) {
    log.info("Batch deletion for dishes:{}", ids);
    dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * dish query by id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("dish query by id")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("get by id:{}", id);
        DishVO dishVO =  dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }


    /**
     * update dish
     * @return
     */

    @PutMapping
    @ApiOperation("update dish")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("update dish:{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }
}
