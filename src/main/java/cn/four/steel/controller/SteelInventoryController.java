package cn.four.steel.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.four.steel.bean.to.SingleInventory;
import cn.four.steel.service.SteelInventoryService;

@RestController
public class SteelInventoryController {
	private static Logger logger = LoggerFactory.getLogger(SteelInventoryController.class);
	
	@Autowired
	private SteelInventoryService steelInventoryService;
	
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
