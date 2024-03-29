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

import cn.four.steel.bean.DataGridResult;
import cn.four.steel.bean.from.FrontSale;
import cn.four.steel.bean.to.SingleSale;
import cn.four.steel.cache.BaseDataCache;
import cn.four.steel.util.SteelUtil;
@Transactional
@Service
public class SteelSaleService {
	private static Logger logger = LoggerFactory.getLogger(SteelSaleService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private BaseDataCache baseDataCache;
	
	public boolean checkSaleClient(List<FrontSale> sales) {
		String querySql = "select count(distinct(client_id)) as num from "
				+ "steel_order where order_no in (";
		for(int i = 0; i < sales.size();i++) {
			querySql += "'" + sales.get(i).getOrderNo() + "'";
			if(i != sales.size()-1)querySql +=",";
		}
		querySql += ")";
		logger.debug("checkSaleClient query string : " + querySql);
		List<Map<String, Object>> rst = jdbcTemplate.queryForList(querySql);
		return "1".equals(String.valueOf(rst.get(0).get("num")));
	}
	
	public void updateSale(List<FrontSale> sales) {
		String insertSQL = "insert into steel_sale(sale_date, sale_no, order_no, sale_amount, unit, price, cash_amount, "
				+ " year, month) "
				+ "values(?,?,?,?,?,?,?,?,?)";
		String updateOrderSQL = "update steel_order set is_sale = ? where order_no = ?";
		String updateSQL = "update steel_sale set sale_date = ?, "
				+ "sale_no = ?, sale_amount =?, unit=?, price=?, "
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
				params.add(sale.getUnit());
				params.add(sale.getPrice());
				params.add(sale.getCashAmount());
				params.add(now.getYear() + 1900);
				params.add(now.getMonth() + 1);
				logger.info("updateSale, insertSQL: "+insertSQL+";params: " + params.toString()); 
				jdbcTemplate.update(insertSQL, params.toArray());
			} else{
				params.add(now);
				params.add(sale.getSaleNo());
				params.add(sale.getSaleAmount());
				params.add(sale.getUnit());
				params.add(sale.getPrice());
				params.add(sale.getCashAmount());
				params.add(now.getYear() + 1900);
				params.add(now.getMonth() + 1);
				params.add(sale.getSaleId());
				jdbcTemplate.update(updateSQL, params.toArray());
			}
			params.clear();
			params.add("1");
			//params.add(sale.getPrice());
			params.add(sale.getOrderNo());
			jdbcTemplate.update(updateOrderSQL, params.toArray());
		}
	}
	
	public DataGridResult<FrontSale> querySale(String saleNo, String clientId, String year, String month, Integer start, Integer end){
		DataGridResult<FrontSale> result = new DataGridResult<FrontSale>();
		String querySQL = "select ss.sale_no as sale_no, sum(ss.cash_amount) as cash_amount, max(ss.sale_date) as sale_date "
				+ "from steel_sale ss,  steel_order so"
				+ "  where ss.order_no = so.order_no ";
		String cntSQL = "select  count(*) from steel_sale ss,  steel_order so where ss.order_no = so.order_no ";
		List<Object> params = new ArrayList<Object>();
		if(year != null && !"".equals(year)){
			querySQL += " and ss.year = ? ";
			cntSQL += " and ss.year = ? ";
			params.add(year);
		}
		if(month != null && !"".equals(month)){
			querySQL += " and ss.month = ? ";
			cntSQL += " and ss.month = ? ";
			params.add(month);
		}
		if(saleNo != null && !"".equals(saleNo)){
			querySQL += " and  ss.sale_no like ? ";
			cntSQL += " and  ss.sale_no like ? ";
			params.add("%" + saleNo + "%");
		}
		if(clientId != null && !"".equals(clientId)) {
			querySQL += " and  so.client_id = ? ";
			cntSQL += " and so.client_id = ? ";
			params.add(clientId);
		}
		querySQL += " group by ss.sale_no ";
		if(start != null){
			querySQL += " limit " + start + "," + end;
		}
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
		Long total = jdbcTemplate.queryForObject(cntSQL, params.toArray(), Long.class);
		result.setRows(sales);
		result.setTotal(total);
		return result;
	}
	
	public List<SingleSale> showSingleSale(String saleNo){
		String showSQL = "select s.sale_id, s.sale_no, o.client_id, o.order_no, o.account_no, o.spec_id, o.client_spec, "
				+ "s.sale_amount, s.cash_amount, s.unit,  s.price, c.client_name, c.contact_person "
				+ "from steel_sale s, steel_order o, client_info c "
				+ "where s.order_no = o.order_no and sale_no = ? and c.client_id = o.client_id";
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
				sale.setClientSpec(String.valueOf(m.get("client_spec")));
				if(null != m.get("price"))
					sale.setPrice(String.format("%.3f", m.get("price")));
				if(null != m.get("sale_amount")) {
					if("张".equals(String.valueOf(m.get("unit")))) {
						sale.setSaleAmount(String.format("%.1f", m.get("sale_amount")));
					}
					else {
						sale.setSaleAmount(String.format("%.3f", m.get("sale_amount")));
					}
				}
				
				sale.setUnit(String.valueOf(m.get("unit")));
				if(null != m.get("cash_amount"))
					sale.setCashAmount(String.format("%.2f", m.get("cash_amount")));
				sale.setUnit(String.valueOf(m.get("unit")));
				sale.setClientName(String.valueOf(m.get("client_name")));
				if(null != m.get("contact_person"))
					sale.setContactPerson(String.valueOf(m.get("contact_person")));
				else
					sale.setContactPerson("");
				sale.setGoodsName(baseDataCache.getCategoryNameBySpecId(sale.getSpecId()));
				sales.add(sale);
			}
		}
		return sales;
	}
	
	public List<SingleSale> loadSaleByOrderNo(List<String> orderNos){
		String showSQL = "select order_no, account_no, spec_id, round(client_amount, 3) as client_amount, client_id, unit, round(price, 3) as price, round(cash_amount, 2) as cash_amount, client_spec from steel_order where order_no = ? ";
		logger.info("loadSaleByOrderNo showSQL : " + showSQL);
		List<Object> params = new ArrayList<Object>();
		List<SingleSale> sales = new ArrayList<>();
		if (orderNos != null) {
			for (String orderNo : orderNos) {
				params.clear();
				params.add(orderNo);
				logger.info("loadSaleByOrderNo params : " + params.toString());
				List<Map<String, Object>> list = jdbcTemplate.queryForList(showSQL, params.toArray());
				if (list != null && list.size() != 0) {
					for (Map<String, Object> m : list) {
						SingleSale sale = new SingleSale();
						sale.setOrderNo(String.valueOf(m.get("order_no")));
						sale.setAccountNo(String.valueOf(m.get("account_no")));
						sale.setSpecId(Long.valueOf(String.valueOf(m.get("spec_id"))));
						sale.setClientId(Long.valueOf(String.valueOf(m.get("client_id"))));
						if(null != m.get("client_amount")) {
							if("张".equals(String.valueOf(m.get("unit")))) {
								sale.setSaleAmount(String.format("%.1f", m.get("client_amount")));
							}
							else {
								sale.setSaleAmount(String.format("%.3f", m.get("client_amount")));
							}
						}
						sale.setUnit(String.valueOf(m.get("unit")));
						sale.setPrice(String.format("%.3f", m.get("price")));
						sale.setCashAmount(String.format("%.2f", m.get("cash_amount")));
						sale.setClientSpec(String.valueOf(m.get("client_spec")));
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
			newStoreNo = "X" + SteelUtil.formatDate(now, null).substring(2) + "01";
		} else {
			Map<String, Object> m = list.get(0);
			String curMaxStoreNo = String.valueOf(m.get("sale_no"));
			Long l = Long.valueOf(curMaxStoreNo.substring(7));
			String str = String.format("%02d", l+1);
			newStoreNo = "X" + SteelUtil.formatDate(now, null).substring(2) + str;
		}
		return newStoreNo;
	}
}
