package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.four.steel.bean.to.SingleInventory;
import cn.four.steel.util.SteelUtil;
@Transactional
@Service
public class SteelInventoryService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<SingleInventory> queryInventory(String year, String month){
		String querySQL = "select inventory_date, steel_name, thickness, store_in, store_out from steel_inventory si where 1=1";
		List<Object> params = new ArrayList<Object>();
		if(year != null && !"".equals(year)){
			querySQL += " and year = ?";
			params.add(year);
		}
		if(month != null && !"".equals(month)){
			querySQL += " and month = ?";
			params.add(month);
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<SingleInventory> inventories = new ArrayList<>();
		if(list != null){
			for(Map<String, Object> m : list){
				SingleInventory in = new SingleInventory();
				in.setInventoryDate(SteelUtil.formatDate((Date)m.get("inventory_date"), null));
				in.setSteelName(String.valueOf(m.get("steel_name")));
				in.setThickness(Double.valueOf(String.valueOf(m.get("thickness"))));
				in.setStoreIn(Double.valueOf(String.valueOf(m.get("store_in"))));
				in.setStoreOut(Double.valueOf(String.valueOf(m.get("store_out"))));
				inventories.add(in);
			}
		}
		return inventories;
	}
	public List<SingleInventory> calcAllInventory(String steelName){
		String querySQL = "select thickness, sum(diff) as sum from (select thickness, (store_in-store_out) as diff from steel_inventory si where steel_name = ?) tt group by thickness order by thickness";
		List<Object> params = new ArrayList<Object>();
		params.add(steelName);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<SingleInventory> inventories = new ArrayList<>();
		if(list != null){
			for(Map<String, Object> m : list){
				SingleInventory in = new SingleInventory();
				in.setThickness(Double.valueOf(String.valueOf(m.get("thickness"))));
				in.setInventory(Double.valueOf(String.valueOf(m.get("sum"))));
				inventories.add(in);
			}
		}
		return inventories;
	}
}
