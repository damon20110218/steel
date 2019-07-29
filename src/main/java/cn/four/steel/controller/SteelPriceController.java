package cn.four.steel.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.DataGridResult;
import cn.four.steel.bean.from.FrontSale;
import cn.four.steel.bean.from.PagePrice;
import cn.four.steel.bean.to.Price;
import cn.four.steel.service.SteelPriceService;
import cn.four.steel.util.SteelExporter;
import cn.four.steel.util.SteelUtil;

@RestController
public class SteelPriceController {
	@Autowired
	private SteelPriceService steelPriceService;

	private Logger logger = LoggerFactory.getLogger(SteelPriceController.class);

	@RequestMapping(value = "/price/update", method = { RequestMethod.POST, RequestMethod.GET })
	public String addPrice(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
		logger.info("Receive price from front client" + jsonParam);
		try {
			JSONArray array = jsonParam.getJSONArray("prices");
			if (array != null && array.size() != 0) {
				for (int i = 0; i < array.size(); i++) {
					JSONObject json = array.getJSONObject(i);
					String priceCode = json.getString("priceCode");
					if (json.getString("price") == null || "".equals(json.getString("price"))) {
						continue;
					}
					double price = Double.valueOf(json.getString("price"));
					String priceType = json.getString("priceType");
					PagePrice params = new PagePrice();
					params.setPrice(price);
					params.setPriceCode(priceCode);
					params.setPriceType(priceType);
					steelPriceService.addPrice(params);
				}
			}
			return "Success";
		} catch (JSONException e) {
			logger.error(e.getMessage());
			return "Failed";
		}

	}

	@RequestMapping(value = "/price/loadTodayFuturePrice", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Price> loadTodayFuturePrice() {
		return steelPriceService.loadTodayFuturePrice();
	}

	@RequestMapping(value = "/price/loadTodaySalePrice", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Price> loadTodaySalePrice() {
		return steelPriceService.loadTodaySalePrice();
	}

	@RequestMapping(value = "/price/cur_sale_price", method = { RequestMethod.POST, RequestMethod.GET })
	public String getSalePriceBySpecId(String specId) {
		if (specId == null || "".equals(specId)) {
			return "0.000";
		}
		Double salePrice = steelPriceService.findPrice(Long.valueOf(specId));
		return String.format("%.3f", salePrice);
	}

	@RequestMapping(value = "/price/today", method = { RequestMethod.POST, RequestMethod.GET })
	public List<List<Price>> todayPrice() {
		try {
			List<Price> prices = steelPriceService.loadTodayPrice();
			return SteelUtil.convert(prices);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/price/future", method = { RequestMethod.POST, RequestMethod.GET })
	public List<List<Price>> loadFuturePrice() {
		try {
			Date endDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate); // 需要将date数据转移到Calender对象中操作
			calendar.add(calendar.DATE, -60);// 把日期往后增加n天.正数往后推,负数往前移动
			Date startDate = calendar.getTime(); // 这个时间就是日期往后推一天的结果
			List<Price> prices = steelPriceService.loadFuturePrice(SteelUtil.formatDate(startDate, null),
					SteelUtil.formatDate(endDate, null));
			return SteelUtil.convert(prices);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/price/change", method = { RequestMethod.POST, RequestMethod.GET })
	public DataGridResult<Price> queryPriceChange(String year, String month, String categoryId, String specId,
			String page, String rows) {
		DataGridResult<Price> res = new DataGridResult<Price>();
		try {
			int start = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
			int end = Integer.valueOf(page) * Integer.valueOf(rows);
			res = steelPriceService.loadPriceChange(year, month, categoryId, specId, start, end);
			return res;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return res;
		}
	}
	
	@RequestMapping("/price_change/export")
	public void exportPriceChange(String year, String month, String categoryId, String specId,
			HttpServletResponse response) {
		try {
			DataGridResult<Price> prices = steelPriceService.loadPriceChange(year, month, categoryId, specId, null, null);
			Date now = new Date();
			String fileName = "price_change_" + SteelUtil.formatDate(now, "yyyyMMdd HH:mm:ss") + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "种类", "规格", "日期" , "价格" };
				Workbook wb = SteelExporter.exportPriceChange(prices.getRows(), titles);
				wb.write(out);// 将数据写出去
				logger.info("导出" + fileName + "成功！");
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("导出" + fileName + "失败！");
			} finally {
				out.close();
			}
		} catch (Exception e) {
		}
	}
}
