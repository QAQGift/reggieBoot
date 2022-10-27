package com.reggieboot.servicce.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggieboot.common.CustomException;
import com.reggieboot.dto.SetmealDto;
import com.reggieboot.entity.Setmeal;
import com.reggieboot.entity.SetmealDish;
import com.reggieboot.mapper.SetMealMapper;
import com.reggieboot.servicce.SetMealDishService;
import com.reggieboot.servicce.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        /*保存套餐信息setmeal表*/
        this.save(setmealDto);
        /*保存套餐和菜品关联信息setmealDish表*/
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(setmealDishes);
    }
    /*删除套餐，删除套餐和菜品关联表中的信息*/
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        /*查询状态，看是否可以删除*/
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count > 0){
            /*不能删除，抛出异常*/
            throw new CustomException("有套餐正在售卖，不能删除");
        }

        /*能删除，先删除套餐表中数据*/
        this.removeByIds(ids);
        /*后删除套餐菜品关系表中数据*/
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setMealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    public SetmealDto getWithDish(Long id) {
        /*获取setmeal信息*/
        Setmeal byId = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(byId,setmealDto);
        /*获取setmealDish信息*/
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setMealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }


}
