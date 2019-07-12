package cn.four.steel.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.four.steel.bean.to.SingleInventory;
import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.cache.BaseDataCache;
import cn.four.steel.service.SteelInventoryService;

@RestController
public class SteelInventoryController {
	private static Logger logger = LoggerFactory.getLogger(SteelInventoryController.class);
	
	@Autowired
	private SteelInventoryService steelInventoryService;
	@Autowired
	private BaseDataCache baseDataCache;
	
	@RequestMapping(value = "/inventory/category")
	public List<SteelCategory> category(){
		Map<Long, SteelCategory> t = baseDataCache.getCatetories();
		List<SteelCategory> res = new ArrayList<>();
		Set<String> s = new HashSet<>();
		for(Entry<Long, SteelCategory> e : t.entrySet()){
			if(!s.contains(e.getValue().getSteelName())){
				res.add(e.getValue());
				s.add(e.getValue().getSteelName());
			}
		}
		return res;
	}
	@RequestMapping(value = "/inventory/query")
	public List<SingleInventory> queryInventory(String year, String month) {
		try {
			return steelInventoryService.queryInventory(year, month);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping(value = "/inventory/calc")
	public List<SingleInventory> calcInventory(String steelName) {
		try {
			return steelInventoryService.calcAllInventory(steelName);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
