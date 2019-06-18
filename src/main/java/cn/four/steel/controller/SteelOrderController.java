package cn.four.steel.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.four.steel.service.SteelOrderService;

@RestController
public class SteelOrderController {
	private static Logger logger = LoggerFactory.getLogger(SteelOrderController.class);
	@Autowired
	private SteelOrderService steelOrderService;

	@RequestMapping(value = "/order/update", method = RequestMethod.POST)
	public void orderUpdate(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
	}
}
