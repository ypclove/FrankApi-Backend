package com.frank.apibackstage.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frank.apibackstage.model.dto.pay.PayCreateRequest;
import com.frank.apibackstage.model.dto.productorder.ProductOrderQueryRequest;
import com.frank.apibackstage.model.entity.ProductInfo;
import com.frank.apibackstage.model.entity.ProductOrder;
import com.frank.apibackstage.model.vo.OrderVo;
import com.frank.apibackstage.model.vo.ProductOrderVo;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.OrderService;
import com.frank.apibackstage.service.ProductOrderService;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.enums.PaymentStatusEnum;
import com.frank.apicommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.frank.apicommon.constant.PayConstant.QUERY_ORDER_STATUS;
import static com.frank.apicommon.enums.PaymentStatusEnum.SUCCESS;

/**
 * @author Frank
 * @date 2024/06/22
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class ProductOrderController {

    @Resource
    private UserService userService;

    @Resource
    private OrderService orderService;

    @Resource
    private ProductOrderService productOrderService;

    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;

    /**
     * 创建订单
     *
     * @param payCreateRequest 订单创建请求
     * @param request          HttpServletRequest
     * @return 订单
     */
    @PostMapping("/create")
    public BaseResponse<ProductOrderVo> createOrder(@Valid @RequestBody PayCreateRequest payCreateRequest,
                                                    HttpServletRequest request) {
        ProductOrderVo productOrderVo = orderService.createOrderByPayType(
                payCreateRequest.getProductId(),
                payCreateRequest.getPayType(),
                userService.getLoginUser(request));
        if (Objects.isNull(productOrderVo)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "订单创建失败");
        }
        return ResultUtils.success(productOrderVo);
    }

    /**
     * 取消订单
     *
     * @param orderId 订单 Id
     * @return 取消订单是否成功
     */
    @PostMapping("/close/{orderId}")
    public BaseResponse<Boolean> closeProductOrder(@PathVariable @Valid
                                                   @NotEmpty(message = "订单 Id 不能为空")
                                                   String orderId) {
        // 判断是否存在
        ProductOrder productOrder = productOrderService.getProductOrderByOrderId(orderId);
        if (Objects.isNull(productOrder)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        // 查询订单
        ProductOrderService orderServiceByPayType = orderService.getProductOrderServiceByPayType(productOrder.getPayType());
        // 更新订单状态
        boolean closedResult = orderServiceByPayType.updateOrderStatusByOrderId(orderId, PaymentStatusEnum.CLOSED.getCode());
        return ResultUtils.success(closedResult);
    }

    /**
     * 删除订单
     *
     * @param orderId 订单 Id
     * @param request HttpServletRequest
     * @return 删除订单是否成功
     */
    @DeleteMapping("/delete/{orderId}")
    public BaseResponse<Boolean> deleteProductOrder(@PathVariable @Valid
                                                    @NotEmpty(message = "订单 Id 不能为空")
                                                    String orderId,
                                                    HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        // 校验数据是否存在
        ProductOrder productOrder = productOrderService.getById(orderId);
        if (Objects.isNull(productOrder)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!productOrder.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(productOrderService.removeById(orderId));
    }

    /**
     * 根据 Id 获取订单
     *
     * @param orderId 订单 Id
     * @return 订单
     */
    @GetMapping("/get/{orderId}")
    public BaseResponse<ProductOrderVo> getProductOrderById(@PathVariable @Valid
                                                            @NotEmpty(message = "订单 Id 不能为空")
                                                            String orderId) {
        ProductOrder productOrder = productOrderService.getById(orderId);
        if (Objects.isNull(productOrder)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        ProductOrderVo productOrderVo = formatProductOrderVo(productOrder);
        return ResultUtils.success(productOrderVo);
    }

    /**
     * 分页获取产品订单列表
     *
     * @param productOrderQueryRequest 产品查询请求
     * @param request                  HttpServletRequest
     * @return 产品订单列表
     */
    @GetMapping("/list/page")
    public BaseResponse<OrderVo> getProductOrderListByPage(ProductOrderQueryRequest productOrderQueryRequest,
                                                           HttpServletRequest request) {
        if (productOrderQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        ProductOrder productOrder = new ProductOrder();
        BeanUtils.copyProperties(productOrderQueryRequest, productOrder);
        String orderName = productOrderQueryRequest.getOrderName();
        String orderNo = productOrderQueryRequest.getOrderNo();
        Integer total = productOrderQueryRequest.getTotal();
        Integer status = productOrderQueryRequest.getStatus();
        String productInfo = productOrderQueryRequest.getProductInfo();
        Integer payType = productOrderQueryRequest.getPayType();
        Integer addPoints = productOrderQueryRequest.getAddPoints();
        long current = productOrderQueryRequest.getCurrent();
        long pageSize = productOrderQueryRequest.getPageSize();

        // 限制爬虫
        if (pageSize > 50) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        UserVO loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<ProductOrder> queryWrapper = new QueryWrapper<>();

        queryWrapper
                .like(StringUtils.isNotBlank(orderName), "orderName", orderName)
                .like(StringUtils.isNotBlank(productInfo), "productInfo", productInfo)
                .eq("userId", userId)
                .eq(StringUtils.isNotBlank(orderNo), "orderNo", orderNo)
                .eq(Objects.isNull(status), "status", status)
                .eq(Objects.isNull(payType), "payType", payType)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total);
        // 未支付的订单前置
        queryWrapper.last("ORDER BY CASE WHEN status = 'NOTPAY' THEN 0 ELSE 1 END, status");
        Page<ProductOrder> productOrderPage = productOrderService.page(new Page<>(current, pageSize), queryWrapper);
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(productOrderPage, orderVo);
        // 处理订单信息
        List<ProductOrderVo> productOrders = productOrderPage
                .getRecords()
                .stream()
                .map(this::formatProductOrderVo)
                .collect(Collectors.toList());
        orderVo.setRecords(productOrders);
        return ResultUtils.success(orderVo);
    }

    /**
     * 查询产品订单状态
     *
     * @param productOrderQueryRequest 产品订单查询请求
     * @return 查询是否成功
     */
    @PostMapping("/query/status")
    public BaseResponse<Boolean> queryOrderStatus(@RequestBody ProductOrderQueryRequest productOrderQueryRequest) {
        if (ObjectUtils.isEmpty(productOrderQueryRequest) || StringUtils.isBlank(productOrderQueryRequest.getOrderNo())) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        String orderNo = productOrderQueryRequest.getOrderNo();
        Boolean data = redisTemplate.opsForValue().get(QUERY_ORDER_STATUS + orderNo);
        if (Boolean.FALSE.equals(data)) {
            return ResultUtils.success(false);
        }
        ProductOrder productOrder = productOrderService.getProductOrderByOrderId(orderNo);
        if (productOrder.getStatus().equals(SUCCESS.getCode())) {
            return ResultUtils.success(true);
        }
        redisTemplate.opsForValue().set(QUERY_ORDER_STATUS + orderNo, false, 5, TimeUnit.MINUTES);
        return ResultUtils.success(false);
    }

    /**
     * 解析订单通知结果
     * 通知频率为 15s/15s/30s/3m/10m/20m/30m/30m/30m/60m/3h/3h/3h/6h/6h - 总计 24h4m
     *
     * @param notifyData 通知数据
     * @param request    HttpServletRequest
     * @return 通知结果
     */
    @PostMapping("/notify/order")
    public String parseOrderNotifyResult(@RequestBody String notifyData, HttpServletRequest request) {
        return orderService.doOrderNotify(notifyData, request);
    }

    /**
     * 将 ProductOrder 转换为 ProductOrderVo，并进行相应字段的格式化和处理
     *
     * @param productOrder 产品订单
     * @return {@link ProductOrderVo}
     */
    private ProductOrderVo formatProductOrderVo(ProductOrder productOrder) {
        ProductOrderVo productOrderVo = new ProductOrderVo();
        BeanUtils.copyProperties(productOrder, productOrderVo);
        ProductInfo prodInfo = JSONUtil.toBean(productOrder.getProductInfo(), ProductInfo.class);
        productOrderVo.setDescription(prodInfo.getDescription());
        productOrderVo.setProductType(prodInfo.getProductType());
        String voTotal = String.valueOf(prodInfo.getTotal());
        // 除以 100 得到以 "￥" 为单位的金额，精度为小数点后两位，四舍五入
        BigDecimal total = new BigDecimal(voTotal).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        productOrderVo.setTotal(total.toString());
        return productOrderVo;
    }
}
