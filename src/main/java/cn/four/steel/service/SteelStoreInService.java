package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.four.steel.bean.from.FrontStorage;
import cn.four.steel.bean.to.MainStorage;
import cn.four.steel.util.SteelUtil;

@Transactional
@Service
public class SteelStoreInService {
	
	private static final Logger logger = LoggerFactory.getLogger(SteelStoreInService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void store(List<FrontStorage> fss) {
		String insertSQL = "insert steel_storage(store_date, client_no, store_no, steel_amount, steel_factory,"
				+ " year, month, spec_id, client_id, price, cash_amount) values(?,?,?,?,?,?,?,?,?,?,?)";

		String updateSQL = "update steel_storage set client_no = ?, store_no = ?, steel_amount = ?,"
				+ " steel_factory = ?, spec_id = ?, client_id = ?, price = ?, cash_amount = ? where storage_id = ?";
		String categorySQL = "select sc.steel_name, ss.thickness from steel_specs ss, steel_category sc where ss.category_id = sc.category_id and ss.spec_id = ?";
		String inventoryCntSQl = "select count(*) from steel_inventory where inventory_date = ? and thickness = ? and steel_name = ?";
		String inventorySQL = "select store_in from steel_inventory where inventory_date = ? and thickness = ? and steel_name = ?";
		String updateInSQL = "update steel_inventory set store_in = ? where inventory_date = ? and thickness = ? and steel_name = ?";
		String insertInSQL = "insert into steel_inventory(inventory_date, store_in, year, month, steel_name, thickness) values(?,?,?,?,?,?)";
		List<Object> params = new ArrayList<Object>();
		Long cnt = 0L;
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
				params.add(SteelUtil.formatDate(storeDate, null));
				params.add(thickness);
				params.add(steelName);
				m = jdbcTemplate.queryForMap(inventorySQL, params.toArray());
				Object o = m.get("store_in");
				Double storeIn = Double.valueOf(String.valueOf(o));
				Double lastAmount = storeIn - steelAmount + fs.getAmount();
				params.clear();
				params.add(lastAmount);
				params.add(SteelUtil.formatDate(storeDate, null));
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
				params.add(now.getYear() + 1900);
				params.add(now.getMonth() + 1);
				params.add(fs.getSpecId());
				params.add(fs.getClientId());
				params.add(fs.getPrice());
				params.add(fs.getCashAmount());
				jdbcTemplate.update(insertSQL, params.toArray());
				
				// Firstly query today's store in, lastly update inventory position
				params.clear();
				params.add(SteelUtil.formatDate(now, null));
				params.add(thickness);
				params.add(steelName);
				//
				try{  // queryForMap 无记录抛异常
					cnt = jdbcTemplate.queryForObject(inventoryCntSQl, params.toArray(), Long.class);
				} catch(Exception e){
					
				}
				if(cnt == 0L){  // 当日此规格未入库 也为出库过
					params.clear();
					params.add(now);
					params.add(fs.getAmount());
					params.add(now.getYear()  + 1900);
					params.add(now.getMonth() + 1);
					params.add(steelName);
					params.add(thickness);
					jdbcTemplate.update(insertInSQL, params.toArray());
				} else {
					try{  // queryForMap 无记录抛异常
						m = jdbcTemplate.queryForMap(inventorySQL, params.toArray());
					} catch(Exception e){
						logger.warn(e.toString());
					}
					Object o = m.get("store_in");
					Double storeIn = Double.valueOf(String.valueOf(o));
					Double lastAmount = storeIn + fs.getAmount();
					params.clear();
					params.add(lastAmount);
					params.add(SteelUtil.formatDate(now, null));
					params.add(thickness);
					params.add(steelName);
					jdbcTemplate.update(updateInSQL, params.toArray());
				}
			}
			
		}
	}

	public List<MainStorage> queryStorage(String storageNo, String clientNo, String year, String month) {
		String querySQL = "select storage_id, store_no, client_no, cash_amount, steel_factory, store_date from steel_storage where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (storageNo != null && !"".equals(storageNo)) {
			querySQL += " and store_no like ?";
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
			querySQL += " and month = ?";
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
				ms.setStoreDate(SteelUtil.formatDate((Date)m.get("store_date"), null));
				stores.add(ms);
			}
		}
		return stores;
	}
	
	public List<FrontStorage> queryStorageById(String storageNo, String clientNo){
		String querySQL = "select storage_id, store_no, client_no, cash_amount, steel_factory, steel_amount, "
				+ "price, spec_id, client_id  from steel_storage where store_no = ? ";
		List<Object> params = new ArrayList<Object>();
		params.add(storageNo);
		if(clientNo !=null && !"".equals(clientNo)){
			querySQL += "and client_no = ?";
			params.add(clientNo);
		}
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
	
	public String generateStoreNo(){
		String querySQL = "select store_no from steel_storage where store_date = ? order by store_no desc";
		List<Object> params = new ArrayList<Object>();
		Date now = new Date();
		params.add(SteelUtil.formatDate(now, null));
		String newStoreNo = "";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		
		if(list == null || list.size() == 0){
			newStoreNo = "RK" + SteelUtil.formatDate(now, null) + "01";
		} else {
			Map<String, Object> m = list.get(0);
			String curMaxStoreNo = String.valueOf(m.get("store_no"));
			Long l = Long.valueOf(curMaxStoreNo.substring(10));
			String str = String.format("%02d", l+1);
			newStoreNo = "RK" + SteelUtil.formatDate(now, null) + str;
		}
		return newStoreNo;
	}
}
