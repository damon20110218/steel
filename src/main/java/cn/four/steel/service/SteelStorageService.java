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
public class SteelStorageService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void store(List<FrontStorage> fss) {
		String insertSQL = "insert steel_storage(store_date, client_no, store_no, steel_amount, steel_factory,"
				+ " year, month, spec_id, client_id, price, cash_amount) values(?,?,?,?,?,?,?,?,?,?,?,?)";

		String updateSQL = "update steel_storage set store_date = ?, client_no = ?, store_no = ?, steel_amount = ?"
				+ " steel_factory = ?, year = ?, month = ?, spec_id = ?, client_id = ?, price = ?, cash_amount = ? where storage_id = ?";
		List<Object> params = new ArrayList<Object>();

		Date now = new Date();
		for (int i = 0; i < fss.size(); i++) {
			FrontStorage fs = fss.get(i);
			params.clear();
			if(fs.getStorageId() != null){
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
				params.add(fs.getStorageId());
				jdbcTemplate.update(updateSQL, params.toArray());
			} else {
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
	
	public List<FrontStorage> queryStorageById(Long storage_id){
		String querySQL = "select storage_id, store_no, client_no, cash_amount, steel_factory, steel_amount, "
				+ "price, spec_id, client_id  from steel_storage where storage_id = ? ";
		List<Object> params = new ArrayList<Object>();
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
}
