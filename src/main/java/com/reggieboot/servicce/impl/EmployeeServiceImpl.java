package com.reggieboot.servicce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggieboot.entity.Employee;
import com.reggieboot.mapper.EmployeeMapper;
import com.reggieboot.servicce.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
