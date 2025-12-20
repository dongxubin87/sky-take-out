package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Value("${sky.shop.address}")
    private String shopAddress;

    @Value("${sky.baidu.ak}")
    private String ak;
    /**
     * The user makes an order
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        // handle exceptions(address book is null or cart in empty )
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        // query data from the current cart

        Long userId = BaseContext.getCurrentId();

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // insert one item into order
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setAddress(addressBook.getDetail());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        orderMapper.insert(orders);
        // insert items in the cart into order details

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        // clean up the cart
        shoppingCartMapper.deleteByUserId(userId);
        // return submitOrder
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }

    /**
     * pay the order
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // current id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //generate prepaid order
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(),
//                new BigDecimal(0.01),
//                "sky-takeout-order",
//                user.getOpenid()
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("the order was paid");
//        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        Integer OrderPaidStatus = Orders.PAID;
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;

        LocalDateTime check_out_time = LocalDateTime.now();

        String orderNumber = ordersPaymentDTO.getOrderNumber();
        log.info("call updatestatus");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);
        return vo;
    }

    /**
     * after payment, update order status
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // query order by orderId
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }


    /**
     * history orders query
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // set up pagination
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // pagination query
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList();
        // query order details, and pack them into OrderVO
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// order id

                // order details query
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }


    /**
     * query order details
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        // query order by id
        Orders orders = orderMapper.getById(id);

        // query dish and setmeal details
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // pack them in OrderVO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * user canel orders
     *
     * @param id
     * @throws Exception
     */
    @Override
    public void userCancelById(Long id) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    ordersDB.getNumber(), //商户订单号
                    ordersDB.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }


    /**
     * place the order again
     *
     * @param id
     * @return
     */
    @Override
    public void repetition(Long id) {
        // query user by userId
        Long userId = BaseContext.getCurrentId();

        // query order details by id
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // convert order details into cart object
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // copy order details to cart
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // Add multiple cart objects to the database in batch
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * Order search
     *
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(
                ordersPageQueryDTO.getPage(),
                ordersPageQueryDTO.getPageSize()
        );

        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        // For some order statuses, additional dish information needs to be returned.
        // Convert Orders objects to OrderVO objects.
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(), orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // Custom OrderVO response is required to include order dish information
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // Copy common fields from Orders to OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);

                String orderDishes = getOrderDishesStr(orders);

                // Set order dish information into OrderVO and add it to the list
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * Get the dish information string based on order ID
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // Query order dish details (dishes and quantities in the order)
        List<OrderDetail> orderDetailList =
                orderDetailMapper.getByOrderId(orders.getId());

        // Concatenate each dish info into a string (format: Kung Pao Chicken*3;)
        List<String> orderDishList = orderDetailList.stream()
                .map(x -> {
                    String orderDish = x.getName() + "*" + x.getNumber() + ";";
                    return orderDish;
                })
                .collect(Collectors.toList());

        // Combine all dish information of this order into a single string
        return String.join("", orderDishList);
    }
    /**
     * Statistics of order counts by status
     *
     * @return
     */
    public OrderStatisticsVO statistics() {
        // Query the number of orders by status:
        // to be confirmed, confirmed, and in delivery
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress =
                orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        // Populate the queried data into OrderStatisticsVO as the response
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    /**
     * Confirm order
     * @return
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.update(orders);
    }

    /**
     * Reject order
     * @return
     */
    @Override

        public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
            // Query the order by ID
            Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

            // Only existing orders with status = TO_BE_CONFIRMED can be rejected
            if (ordersDB == null ||
                    !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
                throw new OrderBusinessException(
                        MessageConstant.ORDER_STATUS_ERROR);
            }

            // Payment status
            Integer payStatus = ordersDB.getPayStatus();
            if (payStatus == Orders.PAID) {
                // The user has already paid, a refund is required
                String refund = weChatPayUtil.refund(
                        ordersDB.getNumber(),
                        ordersDB.getNumber(),
                        new BigDecimal(0.01),
                        new BigDecimal(0.01)
                );
                log.info("Refund request submitted: {}", refund);
            }

            // Rejecting an order requires a refund.
            // Update order status, rejection reason, and cancellation time by order ID
            Orders orders = new Orders();
            orders.setId(ordersDB.getId());
            orders.setStatus(Orders.CANCELLED);
            orders.setRejectionReason(
                    ordersRejectionDTO.getRejectionReason());
            orders.setCancelTime(LocalDateTime.now());

            orderMapper.update(orders);
        }

    /**
     * cancel order
     * @param ordersCancelDTO
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        // Query the order by ID
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        // Payment status
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == 1) {
            // The user has already paid, a refund is required
            String refund = weChatPayUtil.refund(
                    ordersDB.getNumber(),
                    ordersDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01)
            );
            log.info("Refund request submitted: {}", refund);
        }

        // When the admin cancels the order, a refund is required.
        // Update the order status, cancellation reason, and cancellation time by order ID
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);
    }


    /**
     * Deliver order
     * @return
     */
    @Override
    public void delivery(Long id) {
        // Query the order by ID
        Orders ordersDB = orderMapper.getById(id);

        // Validate that the order exists and its status is CONFIRMED
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // Update the order status to DELIVERY_IN_PROGRESS
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        orderMapper.update(orders);
    }

    /**
     * Complete order
     * @return
     */
    @Override
    public void complete(Long id) {
        // Query the order by ID
        Orders ordersDB = orderMapper.getById(id);

        // Validate that the order exists and its status is DELIVERY_IN_PROGRESS
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // Update the order status to COMPLETED
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    /**
     * Check whether the customer's delivery address is out of the delivery range
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address", shopAddress);
        map.put("output", "json");
        map.put("ak", ak);

        // Get the latitude and longitude of the shop address
        String shopCoordinate = HttpClientUtil.doGet(
                "https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("Failed to parse shop address");
        }

        // Parse response data
        JSONObject location = jsonObject.getJSONObject("result")
                .getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");

        // Shop latitude and longitude
        String shopLngLat = lat + "," + lng;

        map.put("address", address);

        // Get the latitude and longitude of the customer's delivery address
        String userCoordinate = HttpClientUtil.doGet(
                "https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("Failed to parse delivery address");
        }

        // Parse response data
        location = jsonObject.getJSONObject("result")
                .getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");

        // Customer delivery address latitude and longitude
        String userLngLat = lat + "," + lng;

        map.put("origin", shopLngLat);
        map.put("destination", userLngLat);
        map.put("steps_info", "0");

        // Route planning
        String json = HttpClientUtil.doGet(
                "https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("Failed to plan delivery route");
        }

        // Parse response data
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0))
                .get("distance");

        if (distance > 5000) {
            // Delivery distance exceeds 5000 meters
            throw new OrderBusinessException("Out of delivery range");
        }
    }
}
