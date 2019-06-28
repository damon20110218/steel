package cn.four.steel.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping("/index")
	public ModelAndView index() {
		return new ModelAndView("login");
	}

	@RequestMapping("login")
	public String login(String name, String password, HttpServletRequest request) {
		HttpSession session = request.getSession();
		try {
			boolean tof = loginService.validateUser(name, password);
			if (tof) {
				User user = loginService.getUser(name);
				session.setAttribute("user", user);
				return "登录成功";
			} else {
				return "用户名或密码错误!";
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "登录异常!";
		}
	}
}
