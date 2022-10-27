package com.reggieboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggieboot.common.R;
import com.reggieboot.entity.Category;
import com.reggieboot.servicce.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    /*新增分类*/
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category{}",category);
        categoryService.save(category);
        return R.success("添加成功");
    }
    /*分类的分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        log.info("page = {},pageSize = {}",page,pageSize);
        /*构造page对象,分页构造器*/
        Page<Category> pageInfo = new Page(page,pageSize);
        /*条件构造器，排序条件*/
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();
        /*添加排序条件*/
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        /*执行查询*/
        categoryService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }
    /*根据id删除分类*/
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("id{}",ids);
        categoryService.remove(ids);
        return R.success("删除成功");
    }
    /*根据id修改分类*/
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("根据id修改信息"+category);
        categoryService.updateById(category);

        return R.success("修改成功");
    }
    @GetMapping("/list")
    /*按类型查询分类,根据条件查询分类*/
    public R<List<Category>> listR(Category category){
        /*条件构造器*/
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        /*添加条件*/
        categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        /*添加排序条件*/
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        /*查询*/
        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(list);
    }
}
