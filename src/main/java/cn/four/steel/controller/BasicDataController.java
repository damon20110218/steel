package cn.four.steel.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.DataGridResult;
import cn.four.steel.bean.from.Client;
import cn.four.steel.bean.to.Price;
import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.bean.to.SteelSpecication;
import cn.four.steel.cache.BaseDataCache;
import cn.four.steel.service.BasicDataService;
import cn.four.steel.util.SteelUtil;

@RestController
public class BasicDataController {
	private static Logger logger = LoggerFactory.getLogger(BasicDataController.class);
	@Autowired
	private BasicDataService basicDataService;
	@Autowired
	private BaseDataCache baseDataCache;

	@RequestMapping(value = "/basic/category", method = RequestMethod.POST)
	public List<SteelCategory> category() {
		// 采用缓存数据
		Map<Long, SteelCategory> m = baseDataCache.getCatetories();
		List<SteelCategory> categories = new ArrayList<>();
		if (m.size() != 0) {
			for(Entry<Long, SteelCategory> e :m.entrySet()){
				categories.add(e.getValue());
			}
		} else {
			categories = basicDataService.listAllCategory(null);
		}
		return categories;
	}
	@RequestMapping(value = "/basic/price", method = RequestMethod.POST)
	public List<List<Price>> categoryforPrice(){
		List<List<Price>> res = new ArrayList<>();
		try{
			List<Price> p = basicDataService.specForPriceCode();
			res = SteelUtil.convert(p);
			return res;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return res;
		}
	}
	@RequestMapping(value = "/basic/spec", method = {RequestMethod.POST, RequestMethod.GET})
	public List<SteelSpecication> spec(String categoryId, HttpServletRequest request) {
		List<SteelSpecication> specs = baseDataCache.getSteelSpecicationByCategory(Long.valueOf(categoryId));
		if(specs == null || specs.size() == 0){
			specs = basicDataService.loadSpecs(null, Long.valueOf(categoryId));
		}
		return specs;
	}

	@RequestMapping(value = "/basic/list_client", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public DataGridResult<Client> client(String clientName, String clientType, String page, String rows) {
		 List<Client> clients = basicDataService.matchClient(clientName, clientType);
		 DataGridResult<Client> result = new DataGridResult<Client>();
		 result.setRows(clients);
		 result.setTotal(100L);
		 return result;
	}
	
	@RequestMapping(value = "/basic/fuzzyMatch", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public List<Client> fuzzyMatch(String companyName, String clientType){
		 List<Client> clients = basicDataService.matchClient(companyName, clientType);
		 return clients;
	}
	// 维护客户公司信息(新增和修改)
	@RequestMapping(value = "/basic/update_client", method = {RequestMethod.POST, RequestMethod.GET})
	public String clientAddAndUpdate(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
		try {
			Client client = jsonParam.toJavaObject(Client.class);
			basicDataService.updateClient(client);
			return "Success";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "Failed";
		}

	}

	@RequestMapping(value = "/basic/del_client", method = RequestMethod.POST)
	public String clientDel(String clientId) {
		try {
			basicDataService.delClient(Long.valueOf(clientId));
			return "Success";
		} catch (Exception e) {
			return "Failed";
		}
	}
	@RequestMapping(value = "/basic/validate_client", method = RequestMethod.POST)
	public String clientValidation(String clientType, String clientName) {
		try {
			if(basicDataService.validateClient(clientType, clientName)){
				return "Success";
			} else {
				return "Failed";
			}
		} catch (Exception e) {
			return "Failed";
		}
	}
}
