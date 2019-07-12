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

import cn.four.steel.bean.from.FrontOut;
import cn.four.steel.bean.to.MainOut;
import cn.four.steel.bean.to.SingleOut;
import cn.four.steel.cache.BaseDataCache;
import cn.four.steel.controller.SteelOrderController;
import cn.four.steel.util.SteelUtil;
@Transactional
@Service
public class SteelOutService {
	
	private static Logger logger = LoggerFactory.getLogger(SteelOutService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	@Autowired
	private BaseDataCache baseDataCache;

	public void updateOut(List<FrontOut> outs) {
		String insertSQL = "insert into steel_outbound(out_date, order_no, actual_amount, year, month) "
				+ "values(?,?,?,?,?)";
		String updateOrderSQL = "update steel_order set is_out = ? where order_no = ?";
		String updateSQL = "update steel_outbound set order_no =?, "
				+ "actual_amount = ? where out_id = ?";
		String getSpecIdByOrderNo = "select spec_id from steel_order where order_no = ?";
		String categorySQL = "select sc.steel_name, ss.thickness from steel_specs ss, steel_category sc "
				+ "where ss.category_id = sc.category_id and ss.spec_id = ?";
		String inventorySQL = "select store_out from steel_inventory where inventory_date = ? and thickness = ? and steel_name = ?";
		String updateInSQL = "update steel_inventory set store_out = ? where inventory_date = ? and thickness = ? and steel_name = ?";
		String insertInSQL = "insert into steel_inventory(inventory_date, store_out, year, month, steel_name, thickness) values(?,?,?,?,?,?)";
		
		String storeDateSQL = "select out_date, actual_amount from steel_outbound where out_id = ?";
		
		Date now = new Date();
		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < outs.size(); i++) {
			FrontOut out = outs.get(i);
			// 获取种类规格信息
			params.clear();
			params.add(out.getOrderNo());
			Map<String, Object> m = jdbcTemplate.queryForMap(getSpecIdByOrderNo, params.toArray());
			Long specId =  Long.parseLong(String.valueOf(m.get("spec_id")));
			Long categoryId = baseDataCache.getCategoryId(specId);
			String steelName = baseDataCache.getSteelCategory(categoryId).getSteelName();
			Double thickness = baseDataCache.getSteelSpecication(specId).getThickness();
			
		
			params.clear();
			if (out.getOutId() == null) {
				// 出库
				params.add(now);
				params.add(out.getOrderNo());
				params.add(out.getActualAmount());
				params.add(now.getYear() + 1900);
				params.add(now.getMonth() + 1);
				logger.info("insert steel_outbound, insertSQL" +insertSQL +  ";params" + params.toString());
				jdbcTemplate.update(insertSQL, params.toArray());
				
				// Firstly query today's store in, lastly update inventory position
				params.clear();
				params.add(now);
				params.add(thickness);
				params.add(steelName);
				logger.info("updateOut: inventorySQL: " +inventorySQL +  ";params" + params.toString());
				try{  // queryForMap 无记录抛异常
				   m = jdbcTemplate.queryForMap(inventorySQL, params.toArray());
				} catch(Exception e){
					logger.warn(e.toString());
				}
				if(!m.containsKey("store_out")){  // 当日此规格未入库过
					params.clear();
					params.add(now);
					params.add(out.getActualAmount());
					params.add(now.getYear()  + 1900);
					params.add(now.getMonth() + 1);
					params.add(steelName);
					params.add(thickness);
					logger.info("updateOut: insertInSQL: " +insertInSQL +  ";params" + params.toString());
					jdbcTemplate.update(insertInSQL, params.toArray());
				} else {
					Double storeOut = Double.valueOf(String.valueOf(m.get("store_out")));
					Double lastAmount = storeOut + out.getActualAmount();
					params.clear();
					params.add(lastAmount);
					params.add(now);
					params.add(thickness);
					params.add(steelName);
					logger.info("updateOut: updateInSQL: " +inventorySQL +  ";params" + params.toString());
					jdbcTemplate.update(updateInSQL, params.toArray());
				}
			} else {
				// Firstly query store date, then query today's store, lastly update inventory position
				
				params.add(out.getOutId());
				
				logger.info("updateOut: storeDateSQL: " +storeDateSQL +  ";params" + params.toString());
				m = jdbcTemplate.queryForMap(storeDateSQL, params.toArray());
				
				Date storeDate = (Date) m.get("out_date");
				Double steelAmount = 0.0;
				if(m.get("actual_amount") != null)
					Double.valueOf(String.valueOf(m.get("actual_amount")));
				params.clear();
				params.add(storeDate);
				params.add(thickness);
				params.add(steelName);
				
				logger.info("updateOut: inventorySQL: " +inventorySQL +  ";params" + params.toString());
				m = jdbcTemplate.queryForMap(inventorySQL, params.toArray());
				
				Double storeOut = 0.0;
				if(m.get("store_out") != null)
					storeOut = Double.valueOf(String.valueOf(m.get("store_out")));
				Double lastAmount = storeOut - steelAmount + out.getActualAmount();
				params.clear();
				params.add(lastAmount);
				params.add(storeDate);
				params.add(thickness);
				params.add(steelName);
				logger.info("updateOut: updateInSQL: " +updateInSQL +  ";params" + params.toString());
				jdbcTemplate.update(updateInSQL, params.toArray());
				
				// 出库
				params.clear();
				params.add(out.getOrderNo());
				params.add(out.getActualAmount());
				params.add(out.getOutId());
				logger.info("updateOut: updateSQL: " +updateSQL +  ";params" + params.toString());
				jdbcTemplate.update(updateSQL, params.toArray());
			}
			params.clear();
			params.add("1");
			params.add(out.getOrderNo());
			logger.info("updateOut: updateOrderSQL: " +updateOrderSQL +  ";params" + params.toString());
			jdbcTemplate.update(updateOrderSQL, params.toArray());
		}
	}

	public List<MainOut> queryOut(String orderNo, String year, String month) {
		String querySQL = "select o.out_id, o.out_date, o.order_no, o.actual_amount, so.spec_id "
				+ "from steel_outbound o, steel_order so "
				+ "where so.order_no = o.order_no ";
		List<Object> params = new ArrayList<Object>();
		if (year != null && !"".equals(year)) {
			querySQL += " and o.year = ?";
			params.add(year);
		}
		if (month != null && !"".equals(month)) {
			querySQL += " and o.month = ?";
			params.add(month);
		}
		if (orderNo != null && !"".equals(orderNo)) {
			querySQL += " and o.order_no";
			params.add("%" + orderNo + "%");
			params.add("%" + orderNo + "%");
		}
		logger.info("queryOut: sql:" + querySQL + "params:"+ params);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<MainOut> outs = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (Map<String, Object> m : list) {
				MainOut out = new MainOut();
				out.setOutId(Long.valueOf(String.valueOf(m.get("out_id"))));
				out.setOrderNo(String.valueOf(m.get("order_no")));
				out.setOutDate(SteelUtil.formatDate((Date) m.get("out_date"), null));
				if(m.get("actual_amount") != null)
					out.setActualAmount(Double.valueOf(String.valueOf(m.get("actual_amount"))));
				Long specId = Long.valueOf(String.valueOf(m.get("spec_id")));
				out.setSpecDesc(baseDataCache.getSteelSpecication(specId).getThickness() + "mm");
				out.setCategoryDesc(baseDataCache.getSteelCategory(baseDataCache.getCategoryId(specId)).getDisplay());
				outs.add(out);
			}
		}
		return outs;
	}

	public List<SingleOut> showSingleOut(Long outId) {
		String showSQL = "select o.order_no, so.spec_id, o.actual_amount, so.client_amount, so.unit, so.steel_calc_amount "
				+ "from steel_outbound o, steel_order so where out_id = ? and o.order_no = so.order_no";
		List<Object> params = new ArrayList<Object>();
		params.add(outId);
		logger.info("showSingleOut: sql:" + showSQL + "outId:"+ outId);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(showSQL, params.toArray());
		List<SingleOut> outs = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (Map<String, Object> m : list) {
				SingleOut out = new SingleOut();
				out.setOutId(outId);
				out.setOrderNo(String.valueOf(m.get("order_no")));
				out.setSpecId(Long.valueOf(String.valueOf(m.get("spec_id"))));
				if(m.get("actual_amount") != null)
					out.setActualAmount(Double.valueOf(String.valueOf(m.get("actual_amount"))));
				out.setClientAmount(Double.valueOf(String.valueOf(m.get("client_amount"))));
				out.setUnit(String.valueOf(m.get("unit")));
				out.setSteelCalcAmount(String.valueOf(m.get("steel_calc_amount")));
				outs.add(out);
			}
		}
		return outs;
	}

	public List<SingleOut> loadOutByOrderNo(List<String> orderNos) {
		String showSQL = "select order_no, spec_id, client_amount, unit, steel_calc_amount from steel_order  where order_no = ?";
		List<Object> params = new ArrayList<Object>();
		List<SingleOut> outs = new ArrayList<>();
		if (orderNos != null) {
			for (String orderNo : orderNos) {
				params.clear();
				params.add(orderNo);
				List<Map<String, Object>> list = jdbcTemplate.queryForList(showSQL, params.toArray());
				logger.info("loadOutByOrderNo: sql:" + showSQL + "params:"+ params);
				if (list != null && list.size() != 0) {
					for (Map<String, Object> m : list) {
						SingleOut out = new SingleOut();
						out.setOrderNo(String.valueOf(m.get("order_no")));
						out.setSpecId(Long.valueOf(String.valueOf(m.get("spec_id"))));
						out.setClientAmount(Double.valueOf(String.valueOf(m.get("client_amount"))));
						out.setUnit(String.valueOf(m.get("unit")));
						out.setSteelCalcAmount(String.valueOf(m.get("steel_calc_amount")));
						outs.add(out);
					}
				}
			}
		}
		return outs;
	}
}
