package com.reggieboot.servicce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggieboot.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
