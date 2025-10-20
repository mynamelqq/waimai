package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.Result;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Resource
    private DishService dishService;
    @Resource
    private DishFlavorService dishFlavorService;

    @PostMapping
    public Result save(@RequestBody DishDTO dto) {
        dishService.saveWithFlavor(dto);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<Page<Dish>> pageDishDTO(DishPageQueryDTO pageQueryDTO) {
        Page<Dish> page = dishService.page(new Page<>(pageQueryDTO.getPage(), pageQueryDTO.getPageSize()),
                dishService.getPageDTO(pageQueryDTO));
        return Result.success(page);
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        dishService.deleteDish(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> getDishDTO(@PathVariable Long id) {
        DishVO dishVO = new DishVO();

        BeanUtils.copyProperties(dishService.seletOne(id), dishVO);
        List<DishFlavor> lst = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId, id));
        dishVO.setFlavors(lst);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        List<DishFlavor> lst = dto.getFlavors();
        for (DishFlavor df : lst) {
            df.setDishId(dish.getId());
        }
        dishService.updateById(dish);
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId, dto.getId()));
        dishFlavorService.saveBatch(lst);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.listDishByCategoryId(categoryId);
        return Result.success(list);
    }
}
