package com.reggieboot.servicce.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggieboot.common.CustomException;
import com.reggieboot.entity.Category;
import com.reggieboot.entity.Dish;
import com.reggieboot.entity.Setmeal;
import com.reggieboot.mapper.CategoryMapper;
import com.reggieboot.servicce.CategoryService;
import com.reggieboot.servicce.DishService;
import com.reggieboot.servicce.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>  implements CategoryService {
    /*根据id删除分类*/
    @Autowired
    DishService dishService;
    @Autowired
    SetMealService setMealService;
    @Override
    public void remove(Long id) {
        /*定义查询器*/
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        /*查看当前分类是否关联了菜品*/
        lambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(lambdaQueryWrapper);
        if (count > 0){
            throw new CustomException("当前分类已经关联菜品");
            /*已关联菜品*/
        }
        /*查看当前分类是否关联了套餐*/
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId,id);
        int count1 = setMealService.count(lambdaQueryWrapper1);
        if (count1 > 0){
            /*已关联套餐*/
            throw new CustomException("当前分类已经关联套餐");
        }
        /*正常删除*/
        super.removeById(id);
    }
}
