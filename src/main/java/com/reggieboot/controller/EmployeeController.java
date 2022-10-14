package com.reggieboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggieboot.common.R;
import com.reggieboot.entity.Employee;
import com.reggieboot.servicce.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /*员工登录*/
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /*密码进行加密*/
        String password =  employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        /*根据提交的用户名查询数据库*/
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        /*如果没有查询到返回登陆失败结果*/
        if (emp == null){
            return R.error("登陆失败");
        }
        /*密码比较*/
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }
        /*查看员工账号状态*/
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        /*登陆成功*/
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    /*新增员工*/
    @PostMapping
    public R<String> save (HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工{}",employee.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id"+id);
        /*设置初始密码123456，然后进行加密处理*/
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/
        employeeService.save(employee);
        return R.success("新增员工成功");

    }
    /*员工信息分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {} , name = {}",page,pageSize,name);
        /*构造page对象,分页构造器*/
        Page pageInfo = new Page(page,pageSize);
        /*构造条件构造器*/
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        /*添加排序条件*/
        lambdaQueryWrapper.orderByDesc(Employee::getCreateTime);
        /*执行查询*/
        employeeService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }
    /*根据id修改员工信息*/
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("修改员工信息"+employee.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id"+id);

       /* employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));*/
        employeeService.updateById(employee);
        return R.success("信息修改成功");
    }
    /*根据id查询员工信息*/
    @GetMapping("/{id}")
    /*变量在url中*/
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据ID查询员工信息"+id);
        Employee emp = employeeService.getById(id);
        if (emp != null) {
            return R.success(emp);
        }
        return R.error("不存在");
    }
}
