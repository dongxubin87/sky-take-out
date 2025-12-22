package com.sky.controller.user;


import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "orders relevant apis")
@Slf4j
public class OrderController {

@Autowired
private OrderService orderService;
    /**
     *  The user makes an order
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
@ApiOperation("The user makes an order")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("the user makes an order {}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);

        return Result.success(orderSubmitVO);
    }

    /**
     * Order payment
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("Order payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("Order payment：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("Generate a prepayment order：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * history orders query
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("history orders query")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     *query order details
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("query order details")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * use cancel orders
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("cancel order")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * place the order again
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("place the order again")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }


    /**
     * Customer is urging the order
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("Customer is urging the order")
    public Result reminder(@PathVariable("id") Long id) {
        orderService.reminder(id);
        return Result.success();
    }

}
