package cn.four.steel.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import cn.four.steel.bean.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class LoginController {

	Set<User> set = new HashSet<>();
	Map<String, String> userMap = new HashMap<>();

	@RequestMapping("/index")
	public ModelAndView index() {
		return new ModelAndView("login");
	}

	@RequestMapping(value = "/reg", method = RequestMethod.POST)
	public String reg(@ModelAttribute User user) {
		String name = user.getName();
		String password = user.getPassword();
		set.add(user);
		if (userMap.containsKey(name)) {
			return "0";
		} else {
			userMap.put(name, password);
			return "1";
		}
	}

	// @RequestMapping(value = "/login", method = RequestMethod.POST)
	// public String login(@ModelAttribute User user) {
	// // 登录成功后，保存用户在Session中，同时保存其角色role
	// String name = user.getName();
	// String password = user.getPassword();
	// if (!userMap.containsKey(name)) {
	// return "0";
	// } else if (userMap.get(name).equals(password)) {
	// return "1";
	// } else {
	// return "2";
	// }
	//
	// }
	@RequestMapping("login")
	public String login(String name, String password, HttpServletRequest request) {
		HttpSession session = request.getSession();

		if (name.equals("root") && password.equals("root")) {
			User user = new User();
			user.setName(name);
			session.setAttribute("user", user);
			return "登录成功";
		} else {
			return "用户名或密码错误!";
		}
	}
}
