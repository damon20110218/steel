package cn.four.steel.util;

import javax.annotation.Resource;

import org.omg.PortableInterceptor.Interceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Component
public class SessionConfiguration implements  WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry ){
		    registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
		    //网站配置生成器：添加一个拦截器，拦截路径为整个项目
		}
}