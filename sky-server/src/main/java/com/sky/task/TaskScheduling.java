package com.sky.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class TaskScheduling {
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ? *")
    public void task() {
        LambdaQueryWrapper<Orders>wrapper=new LambdaQueryWrapper<>();
        LocalDateTime now=LocalDateTime.now().minusMinutes(15);
        wrapper.eq(Orders::getStatus,Orders.PENDING_PAYMENT)
                .lt(Orders::getOrderTime,now);
        List<Orders> list=orderMapper.selectList(wrapper);
        if(list!=null&&list.size()>0){
            for(Orders order:list) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("超时");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.updateById(order);
            }
        }
    }
    @Scheduled(cron = "0 0 1 * * ? *")
    public void processDelivery()
    {
        LambdaQueryWrapper<Orders>wrapper=new LambdaQueryWrapper<>();
        LocalDateTime now=LocalDateTime.now().minusHours(1L);
        wrapper.eq(Orders::getStatus, Orders.DELIVERY_IN_PROGRESS)
                .lt(Orders::getOrderTime,now);
         List<Orders> list=orderMapper.selectList(wrapper);
        if(list!=null&&list.size()>0){
            for(Orders order:list) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.updateById(order);
            }
        }

    }


}
