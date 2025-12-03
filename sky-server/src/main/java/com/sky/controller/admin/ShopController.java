package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@Api(tags = "Shop relevant apis")
@RequestMapping("/admin/shop")
public class ShopController {

    private static final String KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * set shop status
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("set shop status")
    public Result setStatus(@PathVariable Integer status ){
        log.info("set shop status: {}",status == 1 ? "shop in open" : "shop not in open");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("get shop status")
    public Result<Integer> getStatus(){
        Integer status =  (Integer)redisTemplate.opsForValue().get(KEY);
        log.info("shop status: {}", status == 1 ? "shop in open" : "shop not in open");
        return Result.success(status);
    }
}
