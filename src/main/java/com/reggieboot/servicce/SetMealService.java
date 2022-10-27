package com.reggieboot.servicce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggieboot.dto.SetmealDto;
import com.reggieboot.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    /*删除套餐*/
    public void removeWithDish(List<Long> ids);
    public SetmealDto getWithDish(Long id);
}
