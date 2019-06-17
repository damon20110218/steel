package cn.four.steel.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
public class SteelStorageController {
	 @RequestMapping("/hello")
	  public String storeSteel(@RequestBody JSONObject jsonParam,  HttpServletRequest request){		
		 // analyze json object data
		 
		 return "Success";
	 }
}
