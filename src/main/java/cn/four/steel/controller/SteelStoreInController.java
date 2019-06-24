package cn.four.steel.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
	public List<MainStorage> queryStorage(String storageNo, String clientNo, String year, String month){
		try{
			return steelStorageService.queryStorage(storageNo, clientNo, year, month);
		}catch(Exception e){
			return null;
		}
	}
	
	@RequestMapping("/store/show")
	public List<FrontStorage> showSingleStorage(String storageNo){
		try{
			return steelStorageService.queryStorageById(storageNo);
		} catch(Exception e){
			logger.error("show single storage:" + storageNo + ", " + e.getMessage());
			return null;
		}
	}
}
