package cn.four.steel.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.cache.BaseDataCache;
import cn.four.steel.service.BasicDataService;

@RestController
public class CalculatorController {
	private static Logger logger = LoggerFactory.getLogger(CalculatorController.class);
	
	@Autowired 
	private BasicDataService basicDateService;
	@Autowired
	private BaseDataCache baseDataCache;
	
	@RequestMapping(value = "/calc/standart_amount", method = {RequestMethod.POST, RequestMethod.GET})
	public String calcStandardAmount( @RequestBody JSONObject json) {
		try {
			String clientWidth = json.getString("clientWidth");
			String clientAmount = json.getString("clientAmount");
			String categoryId = json.getString("categoryId");
			Long cw = Long.valueOf(clientWidth);
			int ca = Integer.valueOf(clientAmount);
			SteelCategory sc = baseDataCache.getSteelCategory(Long.valueOf(categoryId));
			double sl = sc.getLength();
			int A = (int) (sl / cw);
			int B = (int) (ca / A);
			Long C = Math.floorMod(ca, A) * cw;//all clientWidth mod 10 = 0
			C = C/10 + (C%10 > 0 ? 1L:0L);// convert to cm , iand need to upper
			String res = String.valueOf(B) + "张";
			if(C != 0L) {
				res += String.valueOf(C) + "cm";
			}
			return res;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "计算失败";
		}
	}
	
	@RequestMapping(value = "/calc/formula_price")
	public Double calcPrice(@RequestBody JSONObject json){
		try {
			String amount = json.getString("amount");
			String specId = json.getString("specId");
			Long a = Long.valueOf(amount);
			double singleSteelPrice = basicDateService.loadSingleSteelPrice(Long.valueOf(specId));
			double formulaPrice = a * singleSteelPrice;
			return formulaPrice;
		} catch(Exception e){
			logger.error(e.getMessage());
			return 0.0;
		}
	}
}
