package com.sky.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {

        shoppingCartService.addCart(shoppingCartDTO);
        return Result.success();
    }
    @GetMapping("/list")
    public Result<List<ShoppingCart>>list()
    {
        Long userId= BaseContext.getCurrentId();
        shoppingCartService.list(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId));
        return Result.success(shoppingCartService.list());
    }
    @DeleteMapping("/clean")
    public Result clear() {
         Long userId= BaseContext.getCurrentId();
         shoppingCartService.remove(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId));
         return  Result.success();
    }


}
