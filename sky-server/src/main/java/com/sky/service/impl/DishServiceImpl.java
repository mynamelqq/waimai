package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dish) {
        Dish dishEntity = new Dish();
        BeanUtils.copyProperties(dish, dishEntity);

        dishMapper.insert(dishEntity);
        Dish dish1 = dishMapper.selectOne(new LambdaQueryWrapper<Dish>().eq(Dish::getName, dish.getName()));

        Long dishId = dish1.getId();
        List<DishFlavor> list = dish.getFlavors();
        List<DishFlavor> collect = list.stream().map(lst -> {
            lst.setDishId(dishId);
            return lst;
        }).collect(Collectors.toList());

        if (list != null && list.size() > 0) {
            dishFlavorService.saveBatch(collect);
        }
    }

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public LambdaQueryWrapper<Dish> getPageDTO(DishPageQueryDTO dto) {
        Dish dish = new Dish();
//        BeanUtils.copyProperties(dto,dish);
        LambdaQueryWrapper l = new LambdaQueryWrapper<Dish>();
        String name = dto.getName();
        Integer categoryId = dto.getCategoryId();
        Integer status = dto.getStatus();
        lambdaQuery().like(StringUtils.isNotBlank(name), Dish::getName, name)
                .eq(ObjectUtils.isNotEmpty(categoryId), Dish::getCategoryId, categoryId)
                .eq(ObjectUtils.isNotEmpty(status), Dish::getStatus, status);
        return l;
    }

    @Override
    public void deleteDish(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            if (setmealDishMapper.countDishOfSetmeal(id) > 0) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.delete(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        }


    }

    @Override
    public Dish seletOne(Long id) {
        Dish dish = dishMapper.selectOne(id);
        return dish;
    }

    public List<Dish> listDishByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return this.list(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, StatusConstant.ENABLE));
    }

    public List<DishVO> listWithFlavor(Dish dish) {


        List<Dish> dishList = dishMapper.selectList(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getStatus, StatusConstant.ENABLE)
                .eq(ObjectUtils.isNotEmpty(dish.getCategoryId()),Dish::getCategoryId, dish.getCategoryId())
                .eq(StringUtils.isNotBlank(dish.getName()),Dish::getName, dish.getName())
        );

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectList(new LambdaQueryWrapper<DishFlavor>()
                    .eq(DishFlavor::getDishId, d.getId()));

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
