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
import cn.four.steel.bean.from.PagePrice;
import cn.four.steel.bean.to.Price;
import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.cache.BaseDataCache;
import cn.four.steel.util.SteelUtil;

@Transactional
@Service
public class SteelPriceService {

	private static final Logger logger = LoggerFactory.getLogger(SteelPriceService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private BaseDataCache baseDataCache;
	
	public void addPrice(PagePrice price) {
		String insertSql = "insert into steel_price(price_code, price, price_date, price_type, year, month) values(?,?,?,?,?,?)";
		String futureSql = "insert into steel_future_price(price_code, price, price_date, price_type) values(?,?,?,?)";
		List<Object> params = new ArrayList<Object>();
		Date now = new Date();
		try {
			if ("1".equals(price.getPriceType())) {
				// TODO 当日期货价格会保存多条记录
				if (price.getPrice() == null) {
					return;
				}
				String querySQL = "select count(*) from steel_future_price where price_code = ? and price_date = ?";
				params.add(price.getPriceCode());
				params.add(SteelUtil.formatDate(now, null));
				Long cnt = jdbcTemplate.queryForObject(querySQL, params.toArray(), Long.class);
				if (cnt == 0) {
					params.clear();
					params.add(price.getPriceCode()); // 钢材规格ID
					params.add(price.getPrice()); // 钢材价格
					params.add(SteelUtil.formatDate(now, null)); // 价格录入日期
					params.add(price.getPriceType());
					jdbcTemplate.update(futureSql, params.toArray());
				} else {
					String updateSQL = "update steel_future_price set price = ? where price_code = ? and price_date = ?";
					params.clear();
					params.add(price.getPrice()); // 钢材价格
					params.add(price.getPriceCode()); // 钢材规格ID
					params.add(SteelUtil.formatDate(now, null));
					jdbcTemplate.update(updateSQL, params.toArray());
				}
			} else {
				if (price.getPrice() == null) {
					return;
				}
				// 判断 界面提交的价格是否与最新一次价格一致
				String sql = "select price from steel_price where price_code = ?  order by price_date desc limit 1";
				String querySQL = "select count(*) from steel_price where price_code = ? and price_date = ?";
				params.clear();
				params.add(price.getPriceCode());
				Double pc = null;
				try {
					logger.info("addPrice: sql:" + sql + "; params: " + params.toString()); 
					pc = jdbcTemplate.queryForObject(sql, params.toArray(), Double.class);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				if(pc != null){
					if(SteelUtil.compareDigital(pc, price.getPrice(), 3)){
						return;
					}
				}
				params.add(SteelUtil.formatDate(now, null));
				logger.info("addPrice: querySQL:" + querySQL + "; params: " + params.toString()); 
				Long cnt = jdbcTemplate.queryForObject(querySQL, params.toArray(), Long.class);
				params.clear();
				if (cnt == 0) {
					params.add(price.getPriceCode()); // 钢材规格ID
					params.add(price.getPrice()); // 钢材价格
					params.add(SteelUtil.formatDate(now, null)); // 价格录入日期
					params.add(price.getPriceType());
					params.add(now.getYear() + 1900);
					params.add(now.getMonth() + 1);
					jdbcTemplate.update(insertSql, params.toArray());
				} else {
					String updateSQL = "update steel_price set price = ? where price_code = ? and  price_date = ?";
					params.add(price.getPrice()); // 钢材价格
					params.add(price.getPriceCode()); // 钢材规格ID
					params.add(SteelUtil.formatDate(now, null)); // 价格录入日期
					jdbcTemplate.update(updateSQL, params.toArray());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public double findPrice(long specId) {
		String querySQL = "select round(sp.price,3) from steel_specs ss, steel_price sp where ss.price_code = sp.price_code and ss.spec_id = ? "
				+ " order by sp.price_date desc limit 1";
		List<Object> params = new ArrayList<Object>();
		params.add(specId);
		Double p = 0.0;
		try {
			p = jdbcTemplate.queryForObject(querySQL, params.toArray(), Double.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return p;
	}

	public double findPrice(long specId, Date date) {
		return 0;
	}

	public List<Price> loadTodayPrice() {
		String querySQL = " select distinct round(sp.price,3) as price, ss.thickness, sc.steel_name from steel_price sp, steel_specs ss, steel_category sc "
				+ " where sp.price_code = ss.price_code and ss.category_id = sc.category_id order by sc.steel_name, thickness";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL);
		List<Price> prices = new ArrayList<>();
		if (list != null) {
			for (Map<String, Object> m : list) {
				Price price = new Price();
				price.setPrice(String.format("%.3f", m.get("price")));
				price.setSteelName(String.valueOf(m.get("steel_name")));
				price.setThickness(String.valueOf(m.get("thickness")));
				prices.add(price);
			}
		}
		return prices;
	}

	public List<Price> loadFuturePrice(String startDate, String endDate) {
		String querySQL = "select distinct sc.steel_name, round(sp.price ,3) as price, sp.price_date from steel_specs ss, steel_category sc, steel_future_price sp "
				+ " where ss.category_id = sc.category_id and ss.price_code = sp.price_code "
				+ " and sp.price_date >= ? and sp.price_date <= ? order by sc.steel_name, sp.price_date";
		List<Object> params = new ArrayList<Object>();
		params.add(startDate);
		params.add(endDate);
		logger.info("loadFuturePrice querySQL: " + querySQL + "; params: " + params.toString());
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<Price> prices = new ArrayList<>();
		if (list != null) {
			for (Map<String, Object> m : list) {
				Price price = new Price();
				price.setPrice(String.format("%.3f", m.get("price")));
				price.setSteelName(String.valueOf(m.get("steel_name")));
				price.setThickness(String.valueOf(m.get("thickness")));
				price.setPriceDate(SteelUtil.formatDate((Date) m.get("price_date"), null));
				prices.add(price);
			}
		}
		return prices;
	}

	public List<Price> loadTodayFuturePrice() {
		List<Price> prices = new ArrayList<Price>();
		String querySql = "select price_code, round(price,3) as price \n" + "from steel_future_price\n"
				+ "where (price_code, price_Date) in(\n"
				+ "select price_code, max(price_Date) as price_Date from steel_future_price \n"
				+ "group by price_code)\n";

		logger.info("loadTodayFuturePrice querySql:" + querySql);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql);
		if (list != null) {
			for (Map<String, Object> m : list) {
				Price price = new Price();
				price.setPrice(String.format("%.3f", m.get("price")));
				price.setPriceCode(String.valueOf(m.get("price_code")));
				prices.add(price);
			}
		}
		return prices;
	}

	public List<Price> loadTodaySalePrice() {

		List<Price> prices = new ArrayList<Price>();
		String querySql = "select price_code, round(price,3) as price \n" + "from steel_price\n"
				+ "where (price_code, price_Date) in(\n"
				+ "select price_code, max(price_Date) as price_Date from steel_price \n" + "group by price_code)\n";
		logger.info("loadTodaySalePrice querySql:" + querySql);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql);
		if (list != null) {
			for (Map<String, Object> m : list) {
				Price price = new Price();
				price.setPrice(String.format("%.3f", m.get("price")));
				price.setPriceCode(String.valueOf(m.get("price_code")));
				prices.add(price);
			}
		}
		return prices;
	}
	
	public DataGridResult<Price> loadPriceChange(String year, String month, String categoryId, String specId, Integer start, Integer end){
		DataGridResult<Price> res = new DataGridResult<Price>();
		String querySQL = "select s.price, s.price_date, p.thickness, p.category_id from steel_price s, steel_specs p where s.price_code = p.price_code ";
		String cntSQL = "select count(*) from steel_price s, steel_specs p where s.price_code = p.price_code ";
		List<Object> params = new ArrayList<Object>();
		if (year != null && !"".equals(year)) {
			querySQL += " and s.year = ? ";
			cntSQL += " and s.year = ? ";
			params.add(year);
		}
		if (month != null && !"".equals(month)) {
			querySQL += " and s.month = ? ";
			cntSQL += " and s.month = ? ";
			params.add(month);
		}
		if (categoryId != null && !"".equals(categoryId)) {
			querySQL += " and p.category_id = ? ";
			cntSQL += " and p.category_id = ? ";
			params.add(categoryId);
		}
		if (specId != null && !"".equals(specId)) {
			querySQL += " and p.spec_id = ? ";
			cntSQL += " and p.spec_id = ? ";
			params.add(specId);
		}
		querySQL += " order by s.price_code, s.price_date ";
		if(start != null){
			querySQL += " limit " + start + "," + end;
		}
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		List<Price> prices = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (Map<String, Object> m : list) {
				Price price = new Price();
				price.setPrice(String.valueOf(m.get("price")));
				price.setPriceDate(SteelUtil.formatDate((Date) m.get("price_date"), null));
				price.setThickness(String.valueOf(m.get("thickness")));
				SteelCategory c = baseDataCache.getSteelCategory(Long.valueOf(String.valueOf(m.get("category_id"))));
				price.setSteelName(c.getDisplay());
				prices.add(price);
			}
		}
		Long total = jdbcTemplate.queryForObject(cntSQL, params.toArray(), Long.class);
		res.setRows(prices);
		res.setTotal(total);
		return res;
	}
}
