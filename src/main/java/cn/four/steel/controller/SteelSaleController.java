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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.DataGridResult;
import cn.four.steel.bean.from.FrontSale;
import cn.four.steel.bean.to.SingleSale;
import cn.four.steel.cache.BaseDataCache;
import cn.four.steel.service.SteelSaleService;
import cn.four.steel.util.SteelExporter;
import cn.four.steel.util.SteelUtil;

@RestController
public class SteelSaleController {
	private static Logger logger = LoggerFactory.getLogger(SteelSaleController.class);
	@Autowired
	private SteelSaleService steelSaleService;
	
	@Autowired
	private BaseDataCache baseDataCache;

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
				if(steelSaleService.checkSaleClient(sales))
					steelSaleService.updateSale(sales);
				else
					return "repeat";
			}
			return "Success";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}

	@RequestMapping(value = "/sale/query", method = RequestMethod.POST)
	@ResponseBody
	public DataGridResult<FrontSale> querySale(String saleNo, String clientId, String year, String month, String page, String rows) {
		DataGridResult<FrontSale> result = new DataGridResult<FrontSale>();
		try {
			int start = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
			int end = Integer.valueOf(page) * Integer.valueOf(rows);
			result = steelSaleService.querySale(saleNo, clientId, year, month, start, end);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return result;
		}
	}
	@RequestMapping(value="/sale/saleNo", method = RequestMethod.POST)
	public String generateStoreNo(){
		try {
			String storeNo = steelSaleService.generateSaleNo();
			return storeNo;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}
	@RequestMapping(value = "/sale/show", method = RequestMethod.POST)
	public List<SingleSale> showSingleSale(String saleNo) {
		List<SingleSale> sales = new ArrayList<>();
		try {
			 sales = steelSaleService.showSingleSale(saleNo);
			 for(SingleSale sale : sales){
					Long categoryId = baseDataCache.getCategoryId(sale.getSpecId());
					sale.setDisplay(baseDataCache.getSteelCategory(categoryId).getDisplay());
					sale.setThickness(baseDataCache.getSteelSpecication(sale.getSpecId()).getThickness() + " mm");
			 }
			 return sales;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping(value = "/sale/load", method = RequestMethod.POST)
	public List<SingleSale> loadSaleByOrderNo(@RequestBody JSONObject jsonParam) {
		List<SingleSale> sales = new ArrayList<>();
		try {
			String array = jsonParam.getString("orderNos");
			List<String> orderNos = new ArrayList<>();
			if (array != null && !"".equals(array)) {
				String[] ons = array.split(",");
				for(int i = 0; i < ons.length; i++){
					orderNos.add(ons[i]);
				}
			}
			sales = steelSaleService.loadSaleByOrderNo(orderNos);
			for(SingleSale sale : sales){
				Long categoryId = baseDataCache.getCategoryId(sale.getSpecId());
				sale.setDisplay(baseDataCache.getSteelCategory(categoryId).getDisplay());
				sale.setThickness(baseDataCache.getSteelSpecication(sale.getSpecId()).getThickness() + "mm");
			}
			return sales;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return sales;
		}
	}
	@RequestMapping("/sale/export")
	public void exportSale(String saleNo, String clientId, String year, String month,
			HttpServletResponse response) {
		try {
			DataGridResult<FrontSale> sales = steelSaleService.querySale(saleNo, clientId, year, month, null, null);
			Date now = new Date();
			String fileName = "main_sale_" + SteelUtil.formatDate(now, "yyyyMMdd HH:mm:ss") + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "销售日期", "销售单号", "金额" };
				Workbook wb = SteelExporter.exportMainSale(sales.getRows(), titles);
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
	@RequestMapping("/sale/singleExport")
	public void exportSingleSale(@RequestBody JSONObject jsonParam, HttpServletResponse response){
		try {
			// 解析数据
			JSONArray array = jsonParam.getJSONArray("sales");
			List<FrontSale> sales = new ArrayList<>();
			if (array != null && array.size() != 0) {
				for (int i = 0; i < array.size(); i++) {
					FrontSale sale = array.getJSONObject(i).toJavaObject(FrontSale.class);
					sales.add(sale);
				}
			}
			Date now = new Date();
			String fileName = "main_sale_" + SteelUtil.formatDate(now, "yyyyMMdd HH:mm:ss") + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "订单单号", "客户款号", "种类", "规格", "数量", "单位", "单价", "金额" };
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
			logger.info("exportSingleSale 导出 失败！");
		}
	}
}
