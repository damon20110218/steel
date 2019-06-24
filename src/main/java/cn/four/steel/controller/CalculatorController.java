package cn.four.steel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.four.steel.service.BasicDataService;

@RestController
public class CalculatorController {
	private static Logger logger = LoggerFactory.getLogger(CalculatorController.class);
	
	@Autowired 
	private BasicDataService basicDateService;
	
	@RequestMapping(value = "/calc/standart_amount")
	public String calcStandardAmount(String clientWidth, String clientAmount, String standardLength) {
		try {
			Long cw = Long.valueOf(clientWidth);
			int ca = Integer.valueOf(clientAmount);
			double sl = Double.valueOf(standardLength);
			int A = (int) (sl / cw);
			int B = (int) (ca / A);
			Long C = Math.floorMod(ca, A) * cw;
			String res = String.valueOf(B) + "张" + String.valueOf(C) + "CM";
			return res;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "";
		}
	}
	
	@RequestMapping(value = "/calc/formula_price")
	public Double calcPrice(String amount , String specId){
		try {
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
