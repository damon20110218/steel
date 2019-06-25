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
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.from.FrontStorage;
import cn.four.steel.bean.to.MainStorage;
import cn.four.steel.service.SteelStoreInService;
import cn.four.steel.util.SteelExporter;

@RestController
public class SteelStoreInController {
	private static Logger logger = LoggerFactory.getLogger(SteelStoreInController.class);
	@Autowired
	private SteelStoreInService steelStorageService;

	// 入库新增 及修改
	@RequestMapping("/store/update")
	public String storeSteel(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
		// analyze json object data
		try {
			JSONArray array = jsonParam.getJSONArray("storage");
			if (array != null && array.size() != 0) {
				List<FrontStorage> fss = new ArrayList<>();
				for (int i = 0; i < array.size(); i++) {
					FrontStorage fs = array.getJSONObject(i).toJavaObject(FrontStorage.class);
					fss.add(fs);
				}
				steelStorageService.store(fss);
			}
			return "Success";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}

	@RequestMapping("/store/query")
	public List<MainStorage> queryStorage(String storageNo, String clientNo, String year, String month) {
		try {
			return steelStorageService.queryStorage(storageNo, clientNo, year, month);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping("/store/export")
	public void exportStorage(String storageNo, String clientNo, String year, String month,
			HttpServletResponse response) {
		try {
			List<MainStorage> stores = steelStorageService.queryStorage(storageNo, clientNo, year, month);
			Date now = new Date();
			String fileName = "storeIn_" + now + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				Workbook wb = SteelExporter.exportStorage(stores);
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

	@RequestMapping("/store/show")
	public List<FrontStorage> showSingleStorage(String storageNo) {
		try {
			return steelStorageService.queryStorageById(storageNo);
		} catch (Exception e) {
			logger.error("show single storage:" + storageNo + ", " + e.getMessage());
			return null;
		}
	}
}
