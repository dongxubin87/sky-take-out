package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

import java.util.List;

public interface ShoppingCartService {
    /**
     * add new item to cart
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);


    /**
     * query the cart
     * @return
     */
    List<ShoppingCartDTO> showShoppingCart();
}
