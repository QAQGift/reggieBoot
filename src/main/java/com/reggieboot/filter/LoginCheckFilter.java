package com.reggieboot.filter;

import com.alibaba.fastjson.JSON;
import com.reggieboot.common.BaseContext;
import com.reggieboot.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*检查是否已经登陆*/
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        /*获取本次请求的uri*/
        String requestURI =  request.getRequestURI();
        /*允许通过的请求*/
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/employee/page",
                "/front/**"
        };
        log.info("拦截到请求"+requestURI);

        /*判断是否需要处理*/
        /*放行*/
        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次不需要处理"+requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        /*判断是否已经登录*/
        /*已登录*/
        if (request.getSession().getAttribute("employee") != " "){
            log.info("用户已经登陆"+request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrent(empId);
            /*    long id = Thread.currentThread().getId();
            log.info("线程id"+id);
*/
            filterChain.doFilter(request,response);
            return;
        }
        /*未登录返回，通过输出流的方式向客户端响应*/
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }
    public boolean check(String[] urls,String requestURI){
        for (String url : urls){
            boolean match = PATH_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
