package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.four.steel.bean.from.FrontOrder;
import cn.four.steel.bean.from.FrontOut;
import cn.four.steel.bean.to.MainOut;
import cn.four.steel.bean.to.SingleOut;
import cn.four.steel.util.SteelUtil;

@Service
public class SteelOutService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void updateOut(List<FrontOut> outs) {
		String insertSQL = "insert into steel_outbound(out_date, order_no, spec_id, actual_amount, year, month) "
				+ "values(?,?,?,?,?,?)";
		String updateOrderSQL = "update steel_order set is_out = ? where order_no = ?";
		String updateSQL = "update steel_outbound set out_date = ?, order_no =?, spec_id = ?,"
				+ "actual_amount = ? , year = ?, month = ? where out_id = ?";
		Date now = new Date();
		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < outs.size(); i++) {
			params.clear();
			FrontOut out = outs.get(i);
			if (out.getOutId() == null) {
				params.add(now);
				params.add(out.getOrderNo());
				params.add(out.getSpecId());
				params.add(out.getActualAmount());
				params.add(now.getYear());
				params.add(now.getMonth() + 1);
				jdbcTemplate.update(insertSQL, params.toArray());
			} else {
				params.add(now);
				params.add(out.getOrderNo());
				params.add(out.getSpecId());
				params.add(out.getActualAmount());
				params.add(now.getYear());
				params.add(now.getMonth() + 1);
				params.add(out.getOutId());
				jdbcTemplate.update(updateSQL, params.toArray());
			}
			params.clear();
			params.add("1");
			params.add(out.getOrderNo());
			jdbcTemplate.update(updateOrderSQL, params.toArray());
		}
	}

	public List<MainOut> queryOut(String orderNo, String year, String month) {
		String querySQL = "select o.out_id, o.out_date, o.order_no, o.actual_amount, s.thichness, c.steel_name, c.length, c.width from steel_out o, steel_specs s, steel_category c "
				+ "where o.spec_id = s.spec_id and s.category_id = c.category_id";
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
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<MainOut> outs = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (Map<String, Object> m : list) {
				MainOut out = new MainOut();
				out.setOutId(Long.valueOf(String.valueOf(m.get("out_id"))));
				out.setOrderNo(String.valueOf(m.get("order_no")));
				out.setOutDate(SteelUtil.formatDate((Date) m.get("out_date"), null));
				out.setActualAmount(Double.valueOf(String.valueOf(m.get("actual_amount"))));
				out.setSpec(Double.valueOf(String.valueOf(m.get("spec"))));
				String category = String.valueOf(m.get("steel_name")) + " " + String.valueOf(m.get("length")) + "*"
						+ String.valueOf(m.get("width"));
				out.setCategory(category);
				outs.add(out);
			}
		}
		return outs;
	}

	public List<SingleOut> showSingleOut(Long outId) {
		String showSQL = "select o.order_no, o.spec_id, o.actual_amount, so.client_amount, so.unit, so.steel_calc_amount from steel_out o, steel_order so where out_id = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(outId);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(showSQL, params.toArray());
		List<SingleOut> outs = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (Map<String, Object> m : list) {
				SingleOut out = new SingleOut();
				out.setOutId(outId);
				out.setOrderNo(String.valueOf(m.get("order_no")));
				out.setSpecId(Long.valueOf(String.valueOf(m.get("spec_id"))));
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
