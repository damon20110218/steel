package cn.four.steel.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.from.FrontOrder;
import cn.four.steel.service.SteelOrderService;

@RestController
public class SteelOrderController {
	private static Logger logger = LoggerFactory.getLogger(SteelOrderController.class);
	@Autowired
	private SteelOrderService steelOrderService;

	@RequestMapping(value = "/order/update", method = RequestMethod.POST)
	public String orderUpdate(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
		try {
			JSONArray array = jsonParam.getJSONArray("storage");
			if (array != null && array.size() != 0) {
				List<FrontOrder> orders = new ArrayList<>();
				for (int i = 0; i < array.size(); i++) {
					FrontOrder order = array.getJSONObject(i).toJavaObject(FrontOrder.class);
					orders.add(order);
				}
				steelOrderService.updateOrder(orders);
			}
			return "Success";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}
	@RequestMapping(value = "/order/query", method = RequestMethod.POST)
	public List<FrontOrder> queryOrder(String search, String year, String month, String isSale, String isOut) {
		try {
			return steelOrderService.queryOrder(search, year, month, isSale, isOut);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	@RequestMapping(value = "/order/show", method = RequestMethod.POST)
	public void showSingleOrder(String orderNo){
	}
}
