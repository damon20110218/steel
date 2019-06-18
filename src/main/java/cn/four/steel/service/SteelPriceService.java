package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.four.steel.bean.from.PagePrice;

@Service
public class SteelPriceService {
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public void addPrice(PagePrice price){
		String insertSql = "insert into steel_price(spec_id, price, price_date, priceType) values(?,?,?,?)";
		List<Object> params = new ArrayList<Object>();
		params.add(price.getSpecId());  // 钢材规格ID
		params.add(price.getPrice());  // 钢材价格
		params.add(new Date());  // 价格录入日期
		params.add(price.getPriceType());
		jdbcTemplate.update(insertSql, params.toArray());
	}
	
	public double findPrice(long specId){
		return 0;
	}
	public double findPrice(long specId, Date date){
		return 0;
	}
}
