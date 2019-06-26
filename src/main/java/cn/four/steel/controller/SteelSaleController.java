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

import cn.four.steel.bean.from.FrontSale;
import cn.four.steel.bean.to.SingleSale;
import cn.four.steel.service.SteelSaleService;
import cn.four.steel.util.SteelExporter;

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
	@RequestMapping("/sale/export")
	public void exportSale(String saleNo, String year, String month,
			HttpServletResponse response) {
		try {
			List<FrontSale> sales = steelSaleService.querySale(saleNo, year, month);
			Date now = new Date();
			String fileName = "main_sale_" + now + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "销售日期", "销售单号", "金额" };
				Workbook wb = SteelExporter.exportMainSale(sales, titles);
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
