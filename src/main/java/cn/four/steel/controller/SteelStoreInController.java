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
import cn.four.steel.bean.from.FrontStorage;
import cn.four.steel.bean.to.MainStorage;
import cn.four.steel.cache.BaseDataCache;
import cn.four.steel.service.SteelStoreInService;
import cn.four.steel.util.SteelExporter;
import cn.four.steel.util.SteelUtil;

@RestController
public class SteelStoreInController {
	private static Logger logger = LoggerFactory.getLogger(SteelStoreInController.class);
	@Autowired
	private SteelStoreInService steelStorageService;
	@Autowired
	private BaseDataCache baseDataCache;
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
	@RequestMapping(value="/store/storeNo", method = RequestMethod.POST)
	public String generateStoreNo(){
		try {
			String storeNo = steelStorageService.generateStoreNo();
			return storeNo;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}
	@RequestMapping(value="/store/query", method = RequestMethod.POST)
	@ResponseBody
	public DataGridResult<MainStorage> queryStorage(String storeNo, String clientNo, String year, String month, String page, String rows) {
		DataGridResult<MainStorage> result = new DataGridResult<MainStorage>();
		try {
			int start = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
			int end = Integer.valueOf(page) * Integer.valueOf(rows);		
			result = steelStorageService.queryStorage(storeNo, clientNo, year, month,  start,  end);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return result;
		}
	}

	@RequestMapping("/store/export")
	public void exportStorage(String storageNo, String st, String clientNo, String year, String month,
			HttpServletResponse response) {
		try {
			DataGridResult<MainStorage> stores = steelStorageService.queryStorage(storageNo, clientNo, year, month, null, null);
			Date now = new Date();
			String fileName = "storeIn_" + SteelUtil.formatDate(now, "yyyyMMdd HH:mm:SS") + ".xls";
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
			OutputStream out = response.getOutputStream();
			try {
				String[] titles = new String[] { "入库日期", "入库单号", "客户单号", "金额", "钢厂" };
				Workbook wb = SteelExporter.exportStorage(stores.getRows(), titles);
				wb.write(out);// 将数据写出去
				logger.info("导出" + fileName + "成功！");
			} catch (Exception e) {
				logger.info("导出" + fileName + "失败！");
			} finally {
				out.close();
			}
		} catch (Exception e) {
		}
	}

	@RequestMapping(value="/store/show", method = RequestMethod.POST)
	@ResponseBody
	public List<FrontStorage> showSingleStorage(String storageNo, String clientNo) {
		try {
			List<FrontStorage> stores = steelStorageService.queryStorageById(storageNo, clientNo);
			for(FrontStorage store : stores){
				store.setCategoryId(baseDataCache.getCategoryId(store.getSpecId()));
			}
			return stores;
		} catch (Exception e) {
			logger.error("show single storage:" + storageNo + ", " + e.getMessage());
			return null;
		}
	}
}
