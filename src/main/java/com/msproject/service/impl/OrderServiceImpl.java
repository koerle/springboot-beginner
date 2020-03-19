package com.msproject.service.impl;

import com.msproject.dao.*;
import com.msproject.dataobject.ItemDO;
import com.msproject.dataobject.ItemStockDO;
import com.msproject.dataobject.OrderDO;
import com.msproject.dataobject.SequenceDO;
import com.msproject.error.BusinessException;
import com.msproject.error.EmBusinessError;
import com.msproject.service.OrderService;
import com.msproject.service.PromoService;
import com.msproject.service.UserService;
import com.msproject.service.impl.ItemServiceImpl;
import com.msproject.service.model.ItemModel;
import com.msproject.service.model.OrderModel;
import com.msproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private PromoService promoService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException {
        //校验参数
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息错误");
        }

        UserModel userModel = userService.getUserById(userId);
        if(userModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息错误");
        }
        if(amount<=0 || amount>99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息错误");
        }
        if(promoId!=null){
            if(promoId.intValue()!=itemModel.getPromoModel().getPromoId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀活动不存在");
            }else if(itemModel.getPromoModel().getStatus()!=2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀活动不在进行中");
            }
        }
        //减库存
        Boolean result = itemService.decreaseItemStock(itemId,amount);
        if(!result){
            throw new BusinessException(EmBusinessError.NOT_ENOUGH_AMOUNT);
        }

        //创建订单
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setAmount(amount);
        orderModel.setItemId(itemId);
        if(promoId!=null){
            orderModel.setPrice(itemModel.getPromoModel().getPromoPrice());
        }else{
            orderModel.setPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setTotalPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));
        orderModel.setOrderId(generateOrderId());

        //订单入库
        OrderDO orderDO = this.convertOrderDOFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);
        //更新销量
        itemService.increaseSales(itemId,amount);
        System.out.println(orderModel);
        //返回前端
        return orderModel;
    }

    private OrderDO convertOrderDOFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setPrice(orderModel.getPrice().doubleValue());
        orderDO.setTotalPrice(orderModel.getTotalPrice().doubleValue());
        return orderDO;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderId(){
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间
        LocalDateTime now = LocalDateTime.now();
        String nowtime = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowtime);

        //中间六位为自增序列
        int seq = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceNumber("order");
        seq += sequenceDO.getCurrentValue();
        int current_value = seq+sequenceDO.getStep();
        if(current_value<sequenceDO.getMaxValue()){
            sequenceDO.setCurrentValue(seq+sequenceDO.getStep());
            sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        }
        else{
            sequenceDO.setCurrentValue(sequenceDO.getInitValue());
            sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        }
        String seqStr = String.valueOf(seq);
        for(int i=0;i<6-seqStr.length();i++){
            stringBuilder.append("0");
        }
        stringBuilder.append(seqStr);
        //后2位为分库分表号
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
}
