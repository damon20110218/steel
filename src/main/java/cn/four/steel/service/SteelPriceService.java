package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.four.steel.bean.from.PagePrice;
import cn.four.steel.bean.to.Price;
import cn.four.steel.util.SteelUtil;
@Transactional
@Service
public class SteelPriceService {
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public void addPrice(PagePrice price){
		String insertSql = "insert into steel_price(price_code, price, price_date, price_type) values(?,?,?,?)";
		String futureSql = "insert into steel_future_price(price_code, price, price_date, price_type) values(?,?,?,?)";
		List<Object> params = new ArrayList<Object>();
		Date now = new Date();
		if("1".equals(price.getPriceType())){
			// TODO 当日期货价格会保存多条记录
			if(price.getPrice() == null){
				return;
			}
			String querySQL = "select count(*) from steel_future_price where price_code = ? and price_date = ?";
			params.add(price.getPriceCode());
			params.add(SteelUtil.formatDate(now , null));
			Long cnt = jdbcTemplate.queryForObject(querySQL, params.toArray(), Long.class);
			if(cnt == 0){
				params.clear();
				params.add(price.getPriceCode()); // 钢材规格ID
				params.add(price.getPrice()); // 钢材价格
				params.add(now); // 价格录入日期
				params.add(price.getPriceType());
				jdbcTemplate.update(futureSql, params.toArray());
			} else{
				String updateSQL = "update steel_future_price set price = ? where price_code = ? and price_date = ?";
				params.add(price.getPrice());  // 钢材价格
				params.add(price.getPriceCode());  // 钢材规格ID
				params.add(SteelUtil.formatDate(now , null));  
				jdbcTemplate.update(updateSQL, params.toArray());
			}
		} else {
			if(price.getPrice() == null){
				return;
			}
			String querySQL = "select count(*) from steel_price where price_code = ?";
			params.add(price.getPriceCode());
			Long cnt = jdbcTemplate.queryForObject(querySQL, params.toArray(), Long.class);
			params.clear();
			if(cnt == 0){
				params.add(price.getPriceCode());  // 钢材规格ID
				params.add(price.getPrice());  // 钢材价格
				params.add(now);  // 价格录入日期
				params.add(price.getPriceType());
				jdbcTemplate.update(insertSql, params.toArray());
			} else{
				String updateSQL = "update steel_price set price = ?, price_date = ? where price_code = ?";
				params.add(price.getPrice());  // 钢材价格
				params.add(now);  // 价格录入日期
				params.add(price.getPriceCode());  // 钢材规格ID
				jdbcTemplate.update(updateSQL, params.toArray());
			}
		}
	}
	
	public double findPrice(long specId){
		return 0;
	}
	public double findPrice(long specId, Date date){
		return 0;
	}
	public List<Price> loadTodayPrice(){
		String querySQL = " select distinct sp.price, ss.thickness, sc.steel_name from steel_price sp, steel_specs ss, steel_category sc "
						 +" where sp.price_code = ss.price_code and ss.category_id = sc.category_id order by sc.steel_name, thickness";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(querySQL);
		List<Price> prices = new ArrayList<>();
		if(list != null){
			for(Map<String,Object> m : list){
				Price price = new Price();
				price.setPrice(String.valueOf(m.get("price")));
				price.setSteelName(String.valueOf(m.get("steel_name")));
				price.setThickness(String.valueOf(m.get("thickness")));
				prices.add(price);
			}
		}
		return prices;
	}
	
	public List<Price> loadFuturePrice(Date startDate, Date endDate){
		String querySQL = "select ss.thickness, sc.steel_name, sp.price, sp.price_date from steel_specs ss, steel_category sc, steel_future_price sp"
				+ "where ss.category_id = sc.category_id and ss.price_code = sp.price_code(+)"
				+ "and sp.price_date >= ? and sp.price_date <= ? order by sc.steel_name";
		List<Object> params = new ArrayList<Object>();
		params.add(startDate);
		params.add(endDate);
		List<Map<String,Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<Price> prices = new ArrayList<>();
		if(list != null){
			for(Map<String,Object> m : list){
				Price price = new Price();
				price.setPrice(String.valueOf(m.get("price")));
				price.setSteelName(String.valueOf(m.get("steel_name")));
				price.setThickness(String.valueOf(m.get("thickness")));
				price.setPriceDate(SteelUtil.formatDate((Date) m.get("price_date"), null));
				prices.add(price);
			}
		}
		return prices;
	}
}
