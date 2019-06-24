package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.four.steel.bean.from.FrontOrder;

@Service
public class SteelOrderService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void updateOrder(List<FrontOrder> orders) {
		String insertSQL = "insert into steel_order(order_date, order_no, client_id, client_no, spec_id, "
				+ "client_amount, price, steel_calc_amount, comment, unit, year, month) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		String updateSQL = "update steel_order set order_date = ?, "
				+ "order_no =?, client_id =?, client_no=?, spec_id = ?,"
				+ "client_amount = ?, price = ?, steel_calc_amount = ?, comment = ?, unit = ?,"
				+ " year = ?, month = ? where order_id = ?";
		Date now = new Date();
		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < orders.size(); i++) {
			params.clear();
			FrontOrder order = orders.get(i);
			if(order.getOrderId() == null){
				params.add(now);
				params.add(order.getOrderNo());
				params.add(order.getClientId());
				params.add(order.getClientNo());
				params.add(order.getSpecId());
				params.add(order.getClientAmount());
				params.add(order.getPrice());
				params.add(order.getSteelCalcAmount());
				params.add(order.getComment());
				params.add(order.getUnit());
				params.add(now.getYear());
				params.add(now.getMonth() + 1);
				jdbcTemplate.update(insertSQL, params.toArray());
			} else{
				params.add(now);
				params.add(order.getOrderNo());
				params.add(order.getClientId());
				params.add(order.getClientNo());
				params.add(order.getSpecId());
				params.add(order.getClientAmount());
				params.add(order.getPrice());
				params.add(order.getSteelCalcAmount());
				params.add(order.getComment());
				params.add(order.getUnit());
				params.add(now.getYear());
				params.add(now.getMonth() + 1);
				params.add(order.getOrderId());
				jdbcTemplate.update(updateSQL, params.toArray());
			}
		}
	}
	
	public List<FrontOrder> queryOrder(String search, String year, String month, String isSale, String isOut){
		String querySQL = "select s.order_id, s.order_no, c.client_name, s.price, s.is_out, s.is_sale, s.comment from steel_order s, client_info c where s.client_id = c.client_id";
		List<Object> params = new ArrayList<Object>();
		if(year != null && !"".equals(year)){
			querySQL += " and year = ?";
			params.add(year);
		}
		if(month != null && !"".equals(month)){
			querySQL += " and month = ?";
			params.add(month);
		}
		if(search != null && !"".equals(search)){
			querySQL += " and (order_no = ? or client_no = ?)";
			params.add("%" + search + "%");
			params.add("%" + search + "%");
		}
		if(isSale != null && !"".equals(isSale)){
			querySQL += "is_sale = ?";
			params.add(isSale);
		}
		if(isOut != null && !"".equals(isOut)){
			querySQL += "is_out = ?";
			params.add(isOut);
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<FrontOrder> orders = new ArrayList<>();
		if(list != null && list.size() != 0){
			for(Map<String, Object> m : list){
				FrontOrder order = new FrontOrder();
				order.setOrderId(Long.valueOf(String.valueOf(m.get("order_id"))));
				order.setOrderNo(String.valueOf(m.get("order_no")));
				order.setClientName(String.valueOf(m.get("client_name")));
				order.setPrice(Double.valueOf(String.valueOf(m.get("price"))));
				order.setIsOut(String.valueOf(m.get("is_out")));
				order.setIsSale(String.valueOf(m.get("is_sale")));
				order.setComment(String.valueOf(m.get("comment")));
				orders.add(order);
			}
		}
		return orders;
	}
	
	public List<FrontOrder> showSingleOrder(String orderNo){
		String showSQL = "select order_id, order_no, client_id, client_no, spec_id, price, steel_calc_amount, comment, unit from steel_order where order_no = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(orderNo);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(showSQL, params.toArray());
		List<FrontOrder> orders = new ArrayList<>();
		if(list != null && list.size() != 0){
			for(Map<String, Object> m : list){
				FrontOrder order = new FrontOrder();
				order.setOrderId(Long.valueOf(String.valueOf(m.get("order_id"))));
				order.setOrderNo(String.valueOf(m.get("order_no")));
				order.setClientId(Long.valueOf(String.valueOf(m.get("client_id"))));
				order.setClientNo(String.valueOf(m.get("client_no")));
				order.setSpecId(Long.valueOf(String.valueOf(m.get("spec_id"))));
				order.setPrice(Double.valueOf(String.valueOf(m.get("price"))));
				order.setSteelCalcAmount(String.valueOf(m.get("steel_calc_amount")));
				order.setComment(String.valueOf(m.get("comment")));
				order.setUnit(String.valueOf(m.get("unit")));
				orders.add(order);
			}
		}
		return orders;
	}
}
