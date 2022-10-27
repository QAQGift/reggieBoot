package com.reggieboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggieboot.common.R;
import com.reggieboot.dto.DishDto;
import com.reggieboot.entity.Category;
import com.reggieboot.entity.Dish;
import com.reggieboot.entity.DishFlavor;
import com.reggieboot.servicce.CategoryService;
import com.reggieboot.servicce.DishFlavorService;
import com.reggieboot.servicce.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*菜品管理*/
@RequestMapping("/dish")
@RestController
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    /*新增菜品*/
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("新增菜品"+dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("保存成功");
    }
    @GetMapping("/page")
    /*菜品分页展示*/

    public R<Page> page( int page, int pageSize,String name){
        /*分页构造器*/
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        /*条件构造器*/
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        /*      设置过滤条件              name不为空，要判断的数据集，like(name)*/
        lambdaQueryWrapper.like(name != null,Dish::getName,name);
        /*排序条件*/
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        /*  执行分页查询     数据查询后存入，查询条件*/
        dishService.page(pageInfo,lambdaQueryWrapper);
        /*对象拷贝*/
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();/*获取分类id*/
            Category category = categoryService.getById(categoryId);

            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        /*return R.success(pageInfo);*/
        return R.success(dishDtoPage);
    }
    @GetMapping("/{id}")
    /*根据id查询信息*/
    public R<DishDto> get(@PathVariable long id){
        DishDto byIDWithFlavor = dishService.getByIDWithFlavor(id);
        return R.success(byIDWithFlavor);
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("修改菜品");
        dishService.updateDishWithFlavor(dishDto);
        return R.success("更新成功");
    }
    @DeleteMapping
    public R<String> delete(String ids){
        String[] strings = ids.split(",");
        List<Long> list = Arrays.stream(strings).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        dishService.deleteWithFlavor(list);
        return R.success("删除成功");
    }
    @PostMapping("/status/{statue}")
    public R<String> statue(@RequestParam List<Long> ids,@PathVariable Integer statue){
        List<Dish> dishes = new ArrayList<>();
        for (Long id : ids) {
            Dish dishID = dishService.getById(id);
            dishID.setStatus(statue);
            dishes.add(dishID);
        }
        dishService.updateBatchById(dishes);
        return R.success("修改成功");
    }
    /*按条件查询菜品数据*/
   /* @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
            *//*查询对象*//*
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.like(dish.getName() != null,Dish::getName,dish.getName());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        /*查询对象*/
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.like(dish.getName() != null,Dish::getName,dish.getName());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        List<DishDto> dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String name = category.getName();
                dishDto.setCategoryName(name);
            }

            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> list1 = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
