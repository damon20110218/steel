package cn.four.steel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SteelOrderService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void updateOrder(){
		
	}
}
