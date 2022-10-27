package com.reggieboot.servicce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggieboot.dto.DishDto;
import com.reggieboot.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    /*同时操作两张表，dish和dishflavor*/
    public void saveWithFlavor(DishDto dishDto);
    /*根据id查询菜品和口味*/
    public DishDto getByIDWithFlavor(Long id);
    /*更新dish和dish_flavor表*/
    public void updateDishWithFlavor(DishDto dishDto);
    /*删除信息*/
    public Boolean deleteWithFlavor(List<Long> ids);
}
