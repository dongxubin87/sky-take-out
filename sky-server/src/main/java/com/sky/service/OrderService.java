package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * The user makes an order
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * order payment
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * successfully pay and update order status
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);


    /**
     * history orders query
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);


    /**
     * query order details
     *
     * @param id
     * @return
     */
    OrderVO details(Long id);


    /**
     * use cancel orders
     *
     * @return
     */
    void userCancelById(Long id) throws Exception;


    /**
     * place the order again
     *
     * @param id
     * @return
     */
    void repetition(Long id);


    /**
     * orders search
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * Statistics of order counts by status
     *
     * @return
     */
    OrderStatisticsVO statistics();


    /**
     * Confirm order
     * @return
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * Reject order
     * @return
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * cancel order
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * Deliver order
     * @return
     */
    void delivery(Long id);


    /**
     * Complete order
     * @return
     */
    void complete(Long id);

    void reminder(Long id);
}
