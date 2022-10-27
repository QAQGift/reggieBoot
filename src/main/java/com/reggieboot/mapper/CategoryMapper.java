package com.reggieboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggieboot.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
