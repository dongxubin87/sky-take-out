package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

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
    List<ShoppingCart> showShoppingCart();


    /**
     * clean cart
     */
    void cleanShoppingCart();


    /**
     * Decrement the cart item quantity by 1
     * @param shoppingCartDTO
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
