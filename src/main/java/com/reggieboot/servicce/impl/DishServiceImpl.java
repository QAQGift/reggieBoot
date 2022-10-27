package com.reggieboot.servicce.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggieboot.dto.DishDto;
import com.reggieboot.entity.Dish;
import com.reggieboot.entity.DishFlavor;
import com.reggieboot.mapper.DishMapper;
import com.reggieboot.servicce.DishFlavorService;
import com.reggieboot.servicce.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Transactional
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    DishFlavorService dishFlavorService;
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        /*保存到dish表中*/
        this.save(dishDto);
        /*保存到dishFlavor表中*/
        Long dishid = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
           item.setDishId(dishid);
           return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIDWithFlavor(Long id) {
        /*查询菜品信息*/
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        /*查询口味信息 dish_flavor表中*/
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;

    }

    @Override
    @Transactional
    public void updateDishWithFlavor(DishDto dishDto) {
        /*更新dish*/
        this.updateById(dishDto);
        /*更新dish-flavor*/
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        /*先清理后保存*/
    }

    @Override
    public Boolean deleteWithFlavor(List<Long> ids) {
        this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.removeByIds(ids);

        return null;
    }
}
