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

import cn.four.steel.bean.from.FrontSale;
import cn.four.steel.bean.to.SingleSale;
import cn.four.steel.service.SteelSaleService;

@RestController
public class SteelSaleController {
	private static Logger logger = LoggerFactory.getLogger(SteelSaleController.class);
	@Autowired
	private SteelSaleService steelSaleService;

	@RequestMapping(value = "/sale/update", method = RequestMethod.POST)
	public String orderUpdate(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
		try {
			JSONArray array = jsonParam.getJSONArray("sales");
			if (array != null && array.size() != 0) {
				List<FrontSale> sales = new ArrayList<>();
				for (int i = 0; i < array.size(); i++) {
					FrontSale sale = array.getJSONObject(i).toJavaObject(FrontSale.class);
					sales.add(sale);
				}
				steelSaleService.updateSale(sales);
			}
			return "Success";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}

	@RequestMapping(value = "/sale/query", method = RequestMethod.POST)
	public List<FrontSale> querySale(String saleNo, String year, String month) {
		try {
			return steelSaleService.querySale(saleNo, year, month);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/sale/show", method = RequestMethod.POST)
	public List<SingleSale> showSingleSale(String saleNo) {
		try {
			return steelSaleService.showSingleSale(saleNo);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping(value = "/sale/load", method = RequestMethod.POST)
	public List<SingleSale> loadSaleByOrderNo(@RequestBody JSONObject jsonParam) {
		try {
			JSONArray array = jsonParam.getJSONArray("orderNos");
			List<String> saleNos = new ArrayList<>();
			if (array != null && array.size() != 0) {
				for (int i = 0; i < array.size(); i++) {
					saleNos.add(array.getJSONObject(i).toJavaObject(String.class));
				}
			}
			return steelSaleService.loadSaleByOrderNo(saleNos);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
