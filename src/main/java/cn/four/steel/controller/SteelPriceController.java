package cn.four.steel.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import cn.four.steel.bean.from.PagePrice;
import cn.four.steel.bean.to.Price;
import cn.four.steel.service.SteelPriceService;
import cn.four.steel.util.SteelUtil;

@RestController
public class SteelPriceController {
	@Autowired
	private SteelPriceService steelPriceService;

	private Logger logger = LoggerFactory.getLogger(SteelPriceController.class);

	@RequestMapping(value = "/price/update", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	public String addPrice(@RequestBody JSONObject json, HttpServletRequest request) {
		logger.info("Receive price from front client" + json);
		try {
			String priceCode = json.getString("priceCode");
			double price = Double.valueOf(json.getString("price"));
			String priceType = json.getString("priceType");
			PagePrice params = new PagePrice();
			params.setPrice(price);
			params.setPriceCode(priceCode);
			params.setPriceType(priceType);
			steelPriceService.addPrice(params);
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return "Success";
	}

	@RequestMapping(value = "/price/today")
	public List<List<Price>> todayPrice() {
		try {
			List<Price> prices = steelPriceService.loadTodayPrice();
		    return SteelUtil.convert(prices);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping(value = "/price/future")
	public List<List<Price>> loadFuturePrice(){
		try {
			Date endDate = new Date();
			Calendar calendar  =   Calendar.getInstance();
		    calendar.setTime(endDate); //需要将date数据转移到Calender对象中操作
		    calendar.add(calendar.DATE, -60);//把日期往后增加n天.正数往后推,负数往前移动 
		    Date startDate = calendar.getTime();   //这个时间就是日期往后推一天的结果 
			List<Price> prices = steelPriceService.loadFuturePrice(startDate, endDate);
		    return SteelUtil.convert(prices);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
