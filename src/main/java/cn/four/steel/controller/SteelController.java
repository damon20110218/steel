package cn.four.steel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SteelController {
	 @RequestMapping("/hello")
	    public String hello() {
	        return "Hello Spring Boot!";
	    }
}
