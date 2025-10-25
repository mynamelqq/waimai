package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(value = "setmeal", key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealService.save(setmeal);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<Page<Setmeal>> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        Wrapper<Setmeal> wrapper = setmealService.pageQuery(setmealPageQueryDTO);
        Page<Setmeal> page = setmealService.page(new Page<>(setmealPageQueryDTO.getPage(),
                        setmealPageQueryDTO.getPageSize()),
                wrapper);

        return Result.success(page);
    }

}
