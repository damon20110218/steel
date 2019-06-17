package cn.four.steel.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.params.PagePrice;
import cn.four.steel.service.SteelPriceService;

@RestController
public class SteelPriceController {
	@Autowired
	private SteelPriceService steelPriceService;
	
	private Logger logger = LoggerFactory.getLogger(SteelPriceController.class);
	@RequestMapping(value = "/addPrice", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=UTF-8")
	public String addPrice(@RequestBody JSONObject json, HttpServletRequest request){
		logger.info("Receive price from front client" + json);
		try {
			long  specId =  Long.valueOf(json.getString("specId"));
			double price = Double.valueOf(json.getString("price"));
			PagePrice params = new PagePrice();
			params.setPrice(price);
			params.setSpecId(specId);
			steelPriceService.addPrice(params);
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return "Success";
	}
}
