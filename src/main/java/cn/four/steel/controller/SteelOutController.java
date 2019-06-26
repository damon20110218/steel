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

import cn.four.steel.bean.from.FrontOut;
import cn.four.steel.bean.to.MainOut;
import cn.four.steel.bean.to.MainStorage;
import cn.four.steel.bean.to.SingleOut;
import cn.four.steel.service.SteelOutService;
import cn.four.steel.util.SteelExporter;

@RestController
public class SteelOutController {
	private static Logger logger = LoggerFactory.getLogger(SteelOutController.class);
	@Autowired
	private SteelOutService steelOutService;

	@RequestMapping(value = "/out/update", method = RequestMethod.POST)
	public String orderUpdate(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
		try {
			JSONArray array = jsonParam.getJSONArray("out");
			if (array != null && array.size() != 0) {
				List<FrontOut> outs = new ArrayList<>();
				for (int i = 0; i < array.size(); i++) {
					FrontOut out = array.getJSONObject(i).toJavaObject(FrontOut.class);
					outs.add(out);
				}
				steelOutService.updateOut(outs);
			}
			return "Success";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}

	@RequestMapping(value = "/out/query", method = RequestMethod.POST)
	public List<MainOut> queryOut(String saleNo, String year, String month) {
		try {
			return steelOutService.queryOut(saleNo, year, month);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/out/show", method = RequestMethod.POST)
	public List<SingleOut> showSingleOut(String outId) {
		try {
			return steelOutService.showSingleOut(Long.valueOf(outId));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/out/load", method = RequestMethod.POST)
	public List<SingleOut> loadOutBySaleNo(@RequestBody JSONObject jsonParam) {
		try {
			JSONArray array = jsonParam.getJSONArray("orderNos");
			List<String> saleNos = new ArrayList<>();
			if (array != null && array.size() != 0) {
				for (int i = 0; i < array.size(); i++) {
					saleNos.add(array.getJSONObject(i).toJavaObject(String.class));
				}
			}
			return steelOutService.loadOutByOrderNo(saleNos);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	@RequestMapping("/out/exportMain")
	public void exportMainOut(String saleNo, String year, String month,
			HttpServletResponse response) {
		try {
			List<MainOut> outs = steelOutService.queryOut(saleNo, year, month);
			Date now = new Date();
			String fileName = "storeOut_" + now + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "出库日期", "订单单号", "种类", "规格", "实际称重(吨)" };
				Workbook wb = SteelExporter.exportMainOut(outs, titles);
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
	@RequestMapping("/out/exportSingle")
	public void exportSingleOut(String saleNo, String year, String month,
			HttpServletResponse response) {
		try {
			List<SingleOut> outs = steelOutService.showSingleOut(Long.valueOf(1l));
			Date now = new Date();
			String fileName = "storeOut_" + now + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "订单单号", "种类", "规格", "数量", "单位", "钢板张数", "实际称重(吨)" };
				Workbook wb = SteelExporter.exportSingleOut(outs, titles);
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
