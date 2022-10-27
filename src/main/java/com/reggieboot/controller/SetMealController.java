package com.reggieboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggieboot.common.R;
import com.reggieboot.dto.SetmealDto;
import com.reggieboot.entity.Category;
import com.reggieboot.entity.Setmeal;
import com.reggieboot.entity.SetmealDish;
import com.reggieboot.servicce.CategoryService;
import com.reggieboot.servicce.SetMealDishService;
import com.reggieboot.servicce.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    public SetMealService setMealService;
    @Autowired
    public SetMealDishService setMealDishService;
    @Autowired
    public CategoryService categoryService;
    /*新增套餐*/
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐"+setmealDto.toString());
        setMealService.saveWithDish(setmealDto);
        return R.success("新增成功");
    }
    /*套餐信息分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> setmealDtoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDtoLambdaQueryWrapper.like(name !=null,Setmeal::getName,name);
        setmealDtoLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(setmealPage,setmealDtoLambdaQueryWrapper);
        /*对象拷贝*/
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            /*对象拷贝普通属性*/
            BeanUtils.copyProperties(item, setmealDto);
            /*分类id*/
            Long categoryId = item.getCategoryId();
            /*根据id查询category对象*/
            Category categoryById = categoryService.getById(categoryId);
            if (categoryById != null) {
                String category = categoryById.getName();
                setmealDto.setCategoryName(category);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(setmealDtos);
        return R.success(dtoPage);
    }
    /*删除套餐*/
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("删除套餐"+ids);
        setMealService.removeWithDish(ids);
        return R.success("删除成功");
    }
    @PostMapping("/status/{status}")
    public R<String> status(@RequestParam List<Long> ids,@PathVariable int status){
        List<Setmeal> setmeals= new ArrayList<>();
        for (Long id : ids) {
            Setmeal byId = setMealService.getById(id);
            byId.setStatus(status);
            setmeals.add(byId);
        }
        setMealService.updateBatchById(setmeals);
        return R.success("修改成功");
    }
    /*菜品回显回显setmealDto*/
    @GetMapping("/{id}")
    public R<SetmealDto> msg(@PathVariable Long id){
        SetmealDto withDish = setMealService.getWithDish(id);
        return R.success(withDish);
    }
    /*更新套餐*/
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        /*更新setmeal*/
        setMealService.updateById(setmealDto);
        /*更新setmealDish,先删除，插入*/
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setMealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(collect);
        return R.success("更新成功");
    }
    /*根据条件查询套餐数据*/
    @GetMapping("/list")
    public R<List<Setmeal>> list (Long id,int statue){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null,Setmeal::getCategoryId,id);
        queryWrapper.eq(Setmeal::getStatus,statue);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setMealService.list(queryWrapper);
        return R.success(list);
    }
}
