package cn.four.steel.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.four.steel.bean.User;
import cn.four.steel.service.LoginService;

@RestController
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private LoginService loginService;

	@RequestMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("login.html");
	}
	
	@RequestMapping("/index")
	public  ModelAndView index() {
		return new ModelAndView("index.html");
	}

	@RequestMapping("loginReg")
	public String login(String name, String password, HttpServletRequest request) {
		HttpSession session = request.getSession();
		try {
			boolean tof = loginService.validateUser(name, password);
			if (tof) {
				logger.info("validateUser succuss!");
				User user = loginService.getUser(name);
				session.setAttribute("user", user);
				return "succuss";
			} else {
				logger.info("validateUser failed!");
				return "用户名或者密码错误";
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "校验失败";
		}
	}
}
