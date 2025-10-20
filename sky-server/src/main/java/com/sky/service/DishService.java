package com.sky.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDTO dish);

    LambdaQueryWrapper<Dish> getPageDTO(DishPageQueryDTO dto);

    void deleteDish(List<Long> ids);

    Dish seletOne(Long id);

    List<Dish> listDishByCategoryId(Long categoryId);

    List<DishVO> listWithFlavor(Dish dish);
}
