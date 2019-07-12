package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.four.steel.bean.from.FrontSale;
import cn.four.steel.bean.to.SingleSale;
import cn.four.steel.util.SteelUtil;
@Transactional
@Service
public class SteelSaleService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void updateSale(List<FrontSale> sales) {
		String insertSQL = "insert into steel_sale(sale_date, sale_no, order_no, sale_amount, cash_amount, "
				+ " year, month) "
				+ "values(?,?,?,?,?,?,?)";
		String updateOrderSQL = "update steel_order set is_sale = ?, price = ? where order_no = ?";
		String updateSQL = "update steel_sale set sale_date = ?, "
				+ "sale_no = ?, sale_amount =?,"
				+ "cash_amount = ?, year = ?, month = ? where sale_id = ?";
		Date now = new Date();
		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < sales.size(); i++) {
			params.clear();
			FrontSale sale = sales.get(i);
			if(sale.getSaleId() == null){
				params.add(now);
				params.add(sale.getSaleNo());
				params.add(sale.getOrderNo());
				params.add(sale.getSaleAmount());
				params.add(sale.getCashAmount());
				params.add(now.getYear() + 1900);
				params.add(now.getMonth() + 1);
				jdbcTemplate.update(insertSQL, params.toArray());
			} else{
				params.add(now);
				params.add(sale.getSaleNo());
				params.add(sale.getSaleAmount());
				params.add(sale.getCashAmount());
				params.add(now.getYear() + 1900);
				params.add(now.getMonth() + 1);
				params.add(sale.getSaleId());
				jdbcTemplate.update(updateSQL, params.toArray());
			}
			params.clear();
			params.add("1");
			params.add(sale.getPrice());
			params.add(sale.getOrderNo());
			jdbcTemplate.update(updateOrderSQL, params.toArray());
		}
	}
	
	public List<FrontSale> querySale(String saleNo, String year, String month){
		String querySQL = "select sale_no, sum(cash_amount) as cash_amount, max(sale_date) as sale_date from steel_sale where 1=1";
		List<Object> params = new ArrayList<Object>();
		if(year != null && !"".equals(year)){
			querySQL += " and year = ?";
			params.add(year);
		}
		if(month != null && !"".equals(month)){
			querySQL += " and month = ?";
			params.add(month);
		}
		if(saleNo != null && !"".equals(saleNo)){
			querySQL += " and  sale_no like ?";
			params.add("%" + saleNo + "%");
		}
		querySQL += " group by sale_no";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<FrontSale> sales = new ArrayList<>();
		if(list != null && list.size() != 0){
			for(Map<String, Object> m : list){
				FrontSale sale = new FrontSale();
				sale.setSaleNo(String.valueOf(m.get("sale_no")));
				sale.setSaleDate(SteelUtil.formatDate((Date)m.get("sale_date"), null));
				sale.setCashAmount(Double.valueOf(String.valueOf(m.get("cash_amount"))));
				sales.add(sale);
			}
		}
		return sales;
	}
	
	public List<SingleSale> showSingleSale(String saleNo){
		String showSQL = "select s.sale_id, s.sale_no, o.client_id, o.order_no, o.account_no, o.spec_id, price, s.sale_amount, s.cash_amount, o.unit "
				+ "from steel_sale s, steel_order o where s.order_no = o.order_no and sale_no = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(saleNo);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(showSQL, params.toArray());
		List<SingleSale> sales = new ArrayList<>();
		if(list != null && list.size() != 0){
			for(Map<String, Object> m : list){
				SingleSale sale = new SingleSale();
				sale.setSaleId(Long.valueOf(String.valueOf(m.get("sale_id"))));
				sale.setSaleNo(saleNo);
				sale.setClientId(Long.valueOf(String.valueOf(m.get("client_id"))));
				sale.setOrderNo(String.valueOf(m.get("order_no")));
				sale.setAccountNo(String.valueOf(m.get("account_no")));
				sale.setSpecId(Long.valueOf(String.valueOf(m.get("spec_id"))));
				sale.setPrice(Double.valueOf(String.valueOf(m.get("price"))));
				sale.setSaleAmount(Double.valueOf(String.valueOf(m.get("sale_amount"))));
				sale.setCashAmount(Double.valueOf(String.valueOf(m.get("cash_amount"))));
				sale.setUnit(String.valueOf(m.get("unit")));
				sales.add(sale);
			}
		}
		return sales;
	}
	
	public List<SingleSale> loadSaleByOrderNo(List<String> orderNos){
		String showSQL = "select order_no, account_no, spec_id, client_id, unit, price from steel_order where order_no = ? ";
		List<Object> params = new ArrayList<Object>();
		List<SingleSale> sales = new ArrayList<>();
		if (orderNos != null) {
			for (String orderNo : orderNos) {
				params.clear();
				params.add(orderNo);
				List<Map<String, Object>> list = jdbcTemplate.queryForList(showSQL, params.toArray());
				if (list != null && list.size() != 0) {
					for (Map<String, Object> m : list) {
						SingleSale sale = new SingleSale();
						sale.setOrderNo(String.valueOf(m.get("order_no")));
						sale.setAccountNo(String.valueOf(m.get("account_no")));
						sale.setSpecId(Long.valueOf(String.valueOf(m.get("spec_id"))));
						sale.setClientId(Long.valueOf(String.valueOf(m.get("client_id"))));
						sale.setUnit(String.valueOf(m.get("unit")));
						sale.setPrice(Double.valueOf(String.valueOf(m.get("price"))));
						sales.add(sale);
					}
				}
			}
		}
		return sales;
	}
	
	public String generateSaleNo(){
		String querySQL = "select sale_no from steel_sale where sale_date = ? order by sale_no desc";
		List<Object> params = new ArrayList<Object>();
		Date now = new Date();
		params.add(SteelUtil.formatDate(now, null));
		String newStoreNo = "";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		
		if(list == null || list.size() == 0){
			newStoreNo = "XS" + SteelUtil.formatDate(now, null) + "01";
		} else {
			Map<String, Object> m = list.get(0);
			String curMaxStoreNo = String.valueOf(m.get("sale_no"));
			Long l = Long.valueOf(curMaxStoreNo.substring(10));
			String str = String.format("%02d", l+1);
			newStoreNo = "XS" + SteelUtil.formatDate(now, null) + str;
		}
		return newStoreNo;
	}
}
