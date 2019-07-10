package cn.four.steel.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.from.FrontOrder;
import cn.four.steel.service.SteelOrderService;
import cn.four.steel.util.SteelExporter;

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
	@RequestMapping(value="/order/orderNo", method = RequestMethod.POST)
	public String generateStoreNo(){
		try {
			String orderNo = steelOrderService.generateOrderNo();
			return orderNo;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}
	
	@RequestMapping(value = "/order/query", method = RequestMethod.POST)
	public List<FrontOrder> queryOrder(String orderNo, String clientId, String year, String month, String isSale, String isOut) {
		try {
			return steelOrderService.queryOrder(orderNo, clientId, year, month, isSale, isOut);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/order/show", method = RequestMethod.POST)
	public List<FrontOrder> showSingleOrder(String orderId) {
		try {
			return steelOrderService.showSingleOrder(orderId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	@RequestMapping("/order/exportMain")
	public void exportMainOrder(String orderNo, String clientId, String year, String month, String isSale, String isOut, 
			HttpServletResponse response) {
		try {
			List<FrontOrder> orders = steelOrderService.queryOrder(orderNo, clientId, year, month, isSale, isOut);
			Date now = new Date();
			String fileName = "main_order_" + now + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "日期", "订单单号", "客户", "价格", "是否出库", "是否销售", "备注" };
				Workbook wb = SteelExporter.exportMainOrder(orders, titles);
				wb.write(out);// 将数据写出去
				logger.info("导出" + fileName + "成功！");
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("导出" + fileName + "失败！");
			} finally {
				out.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	@RequestMapping("/order/exportSingle")
	public void exportSingleOrder(String orderNo, String clientId, String year, String month, String isSale, String isOut, 
			HttpServletResponse response) {
		try {
			List<FrontOrder> orders = steelOrderService.queryOrder(orderNo, clientId, year, month, isSale, isOut);
			Date now = new Date();
			String fileName = "single_order_" + now + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "订单单号", "客户款号", "种类", "规格", "重量", "单位", "价格", "钢板张数", "备注" };
				Workbook wb = SteelExporter.exportMainOrder(orders, titles);
				wb.write(out);// 将数据写出去
				logger.info("导出" + fileName + "成功！");
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("导出" + fileName + "失败！");
			} finally {
				out.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
