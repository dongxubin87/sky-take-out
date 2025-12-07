package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C-side shopping cart apis")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * add new item to cart
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("add new item to cart")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("add new item to cart {}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return  Result.success();
    }

    /**
     * show items in cart
     */
    @GetMapping("/list")
    @ApiOperation("show items in cart")
    public Result<List<ShoppingCartDTO>> list() {

        List<ShoppingCartDTO> list =  shoppingCartService.showShoppingCart();
        return  Result.success(list);
    }

}
