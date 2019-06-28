package cn.four.steel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.four.steel.bean.User;

@Service
public class LoginService {
	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean validateUser(String userName, String password) {
		try {
			String querySQL = "select count(*) from user where user_name = ? and password = ?";
			List<Object> params = new ArrayList<>();
			params.add(userName);
			params.add(password);
			long cnt = jdbcTemplate.queryForObject(querySQL, params.toArray(), Long.class);
			if(cnt == 1){
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	public User getUser(String userName) {
		User user = new User();
		try {
			String querySQL = "select * from user where user_name = ? ";
			List<Object> params = new ArrayList<>();
			params.add(userName);
			Map<String, Object> map = jdbcTemplate.queryForMap(querySQL, params.toArray());
			if(map != null){
				user.setName(String.valueOf(map.get("user_name")));
				user.setRole(String.valueOf(map.get("role")));
			}
			return user;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return user;
		}
	}
}
