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
		String insertSql = "insert into steel_price(price_code, price, price_date, priceType) values(?,?,?,?)";
		String futureSql = "insert into steel_future_price(price_code, price, price_date) values(?,?,?,?)";
		List<Object> params = new ArrayList<Object>();
		if("1".equals(price.getPriceType())){
			params.add(price.getPriceCode());  // 钢材规格ID
			params.add(price.getPrice());  // 钢材价格
			params.add(new Date());  // 价格录入日期
			params.add(price.getPriceType());
			jdbcTemplate.update(futureSql, params.toArray());
		} else {
			String querySQL = "select count(*) from steel_price where price_code = ?";
			params.add(price.getPriceCode());
			Long cnt = jdbcTemplate.queryForObject(querySQL, params.toArray(), Long.class);
			params.clear();
			if(cnt == 0){
				params.add(price.getPriceCode());  // 钢材规格ID
				params.add(price.getPrice());  // 钢材价格
				params.add(new Date());  // 价格录入日期
				params.add(price.getPriceType());
				jdbcTemplate.update(insertSql, params.toArray());
			} else{
				String updateSQL = "update steel_price set price = ?, price_date = ? where price_code = ?";
				params.add(price.getPrice());  // 钢材价格
				params.add(new Date());  // 价格录入日期
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
		String querySQL = "select ss.thickness, sc.steel_name, sp.price from steel_specs ss, steel_category sc, steel_price sp"
				+ "where ss.category_id = sc.category_id and ss.price_code = sp.price_code(+)"
				+ "and sp.price_date = ? and sp.price_type = 2 order by sc.steel_name";
		List<Object> params = new ArrayList<Object>();
		params.add(new Date());
		List<Map<String,Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
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
