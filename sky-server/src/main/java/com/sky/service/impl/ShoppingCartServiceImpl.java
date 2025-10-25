package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void addCart(ShoppingCartDTO shoppingCartDTO) {
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        String dishFlavor = shoppingCartDTO.getDishFlavor();
        Long userId= BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<ShoppingCart>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(dishId),ShoppingCart::getDishId, dishId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(setmealId),ShoppingCart::getSetmealId, setmealId);
        queryWrapper.eq(StringUtils.isNotBlank(dishFlavor),ShoppingCart::getDishFlavor,dishFlavor);
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        ShoppingCart shoppingCart = shoppingCartMapper.selectOne(queryWrapper);
        if(ObjectUtils.isNotEmpty(shoppingCart)){
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.updateById(shoppingCart);
        }
        else{
            shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
            if(dishId!=null){
                Dish dish = dishMapper.selectById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }
            else{
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setUserId(userId);
            shoppingCartMapper.insert(shoppingCart);
        }
    }
}
