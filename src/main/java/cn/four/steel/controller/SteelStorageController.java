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
import cn.four.steel.service.SteelStorageService;

@RestController
public class SteelStorageController {
	private static Logger logger = LoggerFactory.getLogger(SteelStorageController.class);
	@Autowired
	private SteelStorageService steelStorageService;

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
	public void queryStorage(String storageNo, String clientNo){
		
	}

}
