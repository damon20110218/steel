package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.four.steel.bean.from.FrontStorage;
import cn.four.steel.bean.to.MainStorage;

@Transactional
@Service
public class SteelStoreInService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void store(List<FrontStorage> fss) {
		String insertSQL = "insert steel_storage(store_date, client_no, store_no, steel_amount, steel_factory,"
				+ " year, month, spec_id, client_id, price, cash_amount) values(?,?,?,?,?,?,?,?,?,?,?,?)";

		String updateSQL = "update steel_storage set client_no = ?, store_no = ?, steel_amount = ?"
				+ " steel_factory = ?, spec_id = ?, client_id = ?, price = ?, cash_amount = ? where storage_id = ?";
		String categorySQL = "select sc.steel_name, ss.thickness from steel_specs ss, steel_category sc where ss.category_id = sc.category_id and ss.spec_id = ?";
		String inventorySQL = "select store_in from steel_inventory where inventory_date = ? and thickness = ? and steel_name = ?";
		String updateInSQL = "update steel_inventory set store_in = ? where inventory_date = ? and thickness = ? and steel_name = ?";
		String insertInSQL = "insert into steel_inventory(inventory_date, store_in, year, month, steel_name, thickness) values(?,?,?,?,?,?)";
		List<Object> params = new ArrayList<Object>();
		Date now = new Date();
		for (int i = 0; i < fss.size(); i++) {
			FrontStorage fs = fss.get(i);
			// 获取种类与规格信息
			params.clear();
			params.add(fs.getSpecId());
			Map<String, Object> m = jdbcTemplate.queryForMap(categorySQL, params.toArray());
			String steelName = String.valueOf(m.get("steel_name"));
			Double thickness = Double.valueOf(String.valueOf(m.get("thickness")));
			
			params.clear();
			if(fs.getStorageId() != null){
				// Firstly query store date, then query today's store, lastly update inventory position
				String storeDateSQL = "select store_date, steel_amount from steel_storage where storage_id = ?";
				params.add(fs.getStorageId());
				m = jdbcTemplate.queryForMap(storeDateSQL, params.toArray());
				Date storeDate = (Date) m.get("store_date");
				Double steelAmount = Double.valueOf(String.valueOf(m.get("steel_amount")));
				params.clear();
				params.add(storeDate);
				params.add(thickness);
				params.add(steelName);
				m = jdbcTemplate.queryForMap(inventorySQL, params.toArray());
				Object o = m.get("store_in");
				Double storeIn = Double.valueOf(String.valueOf(o));
				Double lastAmount = storeIn - steelAmount + fs.getAmount();
				params.clear();
				params.add(lastAmount);
				params.add(storeDate);
				params.add(thickness);
				params.add(steelName);
				jdbcTemplate.update(updateInSQL, params.toArray());
				//入库
				params.clear();
				params.add(fs.getClientNo());
				params.add(fs.getStorageNo());
				params.add(fs.getAmount());
				params.add(fs.getFactory());
				params.add(fs.getSpecId());
				params.add(fs.getClientId());
				params.add(fs.getPrice());
				params.add(fs.getCashAmount());
				params.add(fs.getStorageId());
				jdbcTemplate.update(updateSQL, params.toArray());
			} else {
				// 入库
				params.add(now);
				params.add(fs.getClientNo());
				params.add(fs.getStorageNo());
				params.add(fs.getAmount());
				params.add(fs.getFactory());
				params.add(now.getYear());
				params.add(now.getMonth() + 1);
				params.add(fs.getSpecId());
				params.add(fs.getClientId());
				params.add(fs.getPrice());
				params.add(fs.getCashAmount());
				jdbcTemplate.update(insertSQL, params.toArray());
				
				// Firstly query today's store in, lastly update inventory position
				params.clear();
				params.add(now);
				params.add(thickness);
				params.add(steelName);
				m = jdbcTemplate.queryForMap(inventorySQL, params.toArray());
				Object o = m.get("store_in");
				if(o == null){  // 当日此规格未入库过
					params.clear();
					params.add(now);
					params.add(fs.getAmount());
					params.add(now.getYear());
					params.add(now.getMonth() + 1);
					params.add(steelName);
					params.add(thickness);
					jdbcTemplate.update(insertInSQL, params.toArray());
				} else {
					Double storeIn = Double.valueOf(String.valueOf(o));
					Double lastAmount = storeIn + fs.getAmount();
					params.clear();
					params.add(lastAmount);
					params.add(now);
					params.add(thickness);
					params.add(steelName);
					jdbcTemplate.update(updateInSQL, params.toArray());
				}
			}
			
		}
	}

	public List<MainStorage> queryStorage(String storageNo, String clientNo, String year, String month) {
		String querySQL = "select storage_id, store_no, client_no, cash_amount, steel_factory from steel_storage where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (storageNo != null && !"".equals(storageNo)) {
			querySQL += " and storage_no like ?";
			params.add("%" + storageNo + "%");
		}
		if (clientNo != null && !"".equals(clientNo)) {
			querySQL += " and client_no like ?";
			params.add("%" + clientNo + "%");
		}
		if (year != null && !"".equals(year)) {
			querySQL += " and year = ?";
			params.add(year);
		}
		if (month != null && !"".equals(month)) {
			querySQL += " and month like ?";
			params.add(month);
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<MainStorage> stores = new ArrayList<>();
		if (list != null) {
			for (Map<String, Object> m : list) {
				MainStorage ms = new MainStorage();
				ms.setStorageId(Long.valueOf(String.valueOf(m.get("storage_id"))));
				ms.setStoreNo(String.valueOf(m.get("store_no")));
				ms.setClientNo(String.valueOf(m.get("client_no")));
				ms.setCashAmount(Double.valueOf(String.valueOf(m.get("cash_amount"))));
				ms.setFactory(String.valueOf(m.get("steel_factory")));
				stores.add(ms);
			}
		}
		return stores;
	}
	
	public List<FrontStorage> queryStorageById(String storageNo){
		String querySQL = "select storage_id, store_no, client_no, cash_amount, steel_factory, steel_amount, "
				+ "price, spec_id, client_id  from steel_storage where store_no = ? ";
		List<Object> params = new ArrayList<Object>();
		params.add(storageNo);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<FrontStorage> stores = new ArrayList<>();
		if (list != null) {
			for (Map<String, Object> m : list) {
				FrontStorage ms = new FrontStorage();
				ms.setStorageId(Long.valueOf(String.valueOf(m.get("storage_id"))));
				ms.setStorageNo(String.valueOf(m.get("store_no")));
				ms.setClientNo(String.valueOf(m.get("client_no")));
				ms.setCashAmount(Double.valueOf(String.valueOf(m.get("cash_amount"))));
				ms.setFactory(String.valueOf(m.get("steel_factory")));
				ms.setAmount(Double.valueOf(String.valueOf(m.get("steel_amount"))));
				ms.setPrice(Double.valueOf(String.valueOf(m.get("price"))));
				ms.setSpecId(Long.valueOf(String.valueOf(m.get("spec_id"))));
				ms.setClientId(Long.valueOf(String.valueOf(m.get("client_id"))));
				stores.add(ms);
			}
		}
		return stores;
	}
	
	public void todayStorage(){
		String storeInSQL = "";
		String storeOutSQL = "";
	}
}
