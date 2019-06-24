package cn.four.steel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class SteelInventoryService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void queryInventory(String year, String month){
		String querySQL = "select * from steel_inventory si, steel_specs ss, steel_category sc";
	}
}
