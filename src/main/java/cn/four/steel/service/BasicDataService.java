package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.four.steel.bean.from.Client;
import cn.four.steel.bean.to.Price;
import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.bean.to.SteelSpecication;
import cn.four.steel.cache.BaseDataCache;

@Transactional
@Service
public class BasicDataService {
	private static Logger logger = LoggerFactory.getLogger(BasicDataService.class);
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private BaseDataCache baseDataCache;
	
	public List<Price> specForPriceCode(){
		String sql = "select price_code, max(thickness) thickness, max(steel_name) steel_name from (select ss.thickness,ss.price_code,sc.steel_name from steel_specs ss, steel_category sc where ss.category_id = sc.category_id) tt group by price_code order by steel_name, thickness";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		List<Price> prices = new ArrayList<>();
		if(list != null ){
			for(Map<String, Object> m : list){
				Price price = new Price();
				price.setPriceCode(String.valueOf(m.get("price_code")));
				price.setThickness(String.valueOf(m.get("thickness")));
				price.setSteelName(String.valueOf(m.get("steel_name")));
				prices.add(price);
			}
		}
		return prices;
	}
	
	public List<SteelCategory> listAllCategory(Long categoryId){
		String sql = "Select * From steel_category Where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if( categoryId != null){
			sql += "and category_id = ?";
			params.add(categoryId);
		}
		sql += " order by steel_name";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params.toArray());
		List<SteelCategory> categories =  new LinkedList<>();
		if(list != null){
			for(Map<String, Object> map : list){
				SteelCategory category = new SteelCategory();
				Long categId = Long.valueOf(String.valueOf(map.get("category_id")));
				category.setCategoryId(categId);
				category.setLength(Long.valueOf(String.valueOf(map.get("length"))));
				category.setWidth(Long.valueOf(String.valueOf(map.get("width"))));
				category.setSteelName(String.valueOf(map.get("steel_name")));
				category.setSpecs(loadSpecs(null, categId));
				category.setDisplay(category.getSteelName() + category.getLength() + "*" + category.getWidth());
				categories.add(category);
			}
		}
		return categories;
	}
	
	public List<SteelSpecication> loadSpecs(Long specId, Long categoryId){
		String sql = "Select * from steel_specs where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if( categoryId != null){
			sql += " and category_id = ? ";
			params.add(categoryId);
		}
		if( specId != null){
			sql += " and spec_id = ? ";
			params.add(specId);
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params.toArray());
		List<SteelSpecication> specs =  new ArrayList<>();
		if(list != null){
			for(Map<String, Object> map : list){
				SteelSpecication spec = new SteelSpecication();
				spec.setSpecId(Long.valueOf(String.valueOf(map.get("spec_id"))));
				spec.setCategoryId(Long.valueOf(String.valueOf(map.get("category_id"))));
				spec.setThickness(Double.valueOf(String.valueOf(map.get("thickness"))));
				spec.setPriceCode(String.valueOf(map.get("price_code")));
				specs.add(spec);
			}
		}
		return specs;
	}
	
	public List<Client> matchClient(String clientName, String clientType){
		String sql = "Select client_id, client_name, telephone, mobile, email, address, contact_person, client_mark From client_info where 1=1 and client_type = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(clientType);
		if(clientName != null ){
			sql += " and  client_name like ? ";
			params.add("%" + clientName + "%");
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params.toArray());
		List<Client> clients = new ArrayList<>();
		if(list != null){
			for(Map<String, Object> m : list){
				Client client = new Client();
				client.setClientId(Long.valueOf(String.valueOf(m.get("client_id"))));
				client.setClientName(String.valueOf(m.get("client_name")));
				client.setTelephone(String.valueOf(m.get("telephone")));
				client.setMobile(String.valueOf(m.get("mobile")));
				client.setEmail(String.valueOf(m.get("email")));
				client.setAddress(String.valueOf(m.get("address")));
				client.setContactPerson(String.valueOf(m.get("contact_person")));
				client.setClientMark(String.valueOf(m.get("client_mark")));
				client.setClientType(clientType);
				clients.add(client);
			}
		}
		return clients;
	}
	public void updateClient(Client client){
		String insertSQL = "insert into client_info(client_name, create_date, client_type, client_mark,"
				+ " mobile, telephone, email, address, contact_person) values(?,?,?,?,?,?,?,?,?)";
		String updateSQL = "update client_info set client_name = ?, create_date= ?, client_mark = ?, mobile = ?, "
				+ " telephone = ?, email = ?, address = ?, contact_person = ? where client_id = ? ";
		List<Object> params = new ArrayList<Object>();
		logger.info("ClientId:" + client.getClientId());
		if(client.getClientId() == null){
			params.add(client.getClientName());
			params.add(new Date());
			params.add(client.getClientType());
			params.add(client.getClientMark());
			params.add(client.getMobile());
			params.add(client.getTelephone());
			params.add(client.getEmail());
			params.add(client.getAddress());
			params.add(client.getContactPerson());
			jdbcTemplate.update(insertSQL, params.toArray());
		} else {
			params.add(client.getClientName());
			params.add(new Date());
			params.add(client.getClientMark());
			params.add(client.getMobile());
			params.add(client.getTelephone());
			params.add(client.getEmail());
			params.add(client.getAddress());
			params.add(client.getContactPerson());
			params.add(client.getClientId());
			jdbcTemplate.update(updateSQL, params.toArray());
		}
		
		baseDataCache.updateClient(client);
	}
	public void delClient(Long clientId){
		String delSQL = "delete from client_info where client_id = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(clientId);
		jdbcTemplate.update(delSQL, params.toArray());
		baseDataCache.delClient(clientId);
	}
	public boolean validateClient(String clientType, String clientName){
		String sql = "select count(*) from client_info where client_type= ? and client_name = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(clientType);
		params.add(clientName);
		Long cnt = jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
		if(cnt == 0 ){
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 
	* @Title: loadSingleSteelPrice  
	* @Description: 计算出单个钢板的理论价格  
	* @param specId
	* @return    参数  
	* double    返回类型  
	* @throws
	 */
	public double loadSingleSteelPrice(Long specId){
		String querySQL = "SELECT\n" + 
				"	ss.thickness,\n" + 
				"	sc.length,\n" + 
				"	sc.width,\n" + 
				"	sp.density,\n" + 
				"	sk.price\n" + 
				"FROM\n" + 
				"	steel_specs ss,\n" + 
				"	steel_category sc,\n" + 
				"	steel_parameter sp,\n" + 
				"	steel_price sk\n" + 
				"WHERE\n" + 
				"	ss.category_id = sc.category_id\n" + 
				"AND ss.category_id = sp.category_id\n" + 
				"AND ss.price_code = sk.price_code\n" + 
				"AND ss.spec_id = ?\n" + 
				"AND (sk.price_code, sk.price_id) IN (\n" + 
				"	SELECT\n" + 
				"		price_code,\n" + 
				"		max(price_id)\n" + 
				"	FROM\n" + 
				"		steel_price\n" + 
				"	WHERE\n" + 
				"		price_type = 2\n" + 
				"	GROUP BY\n" + 
				"		price_code\n" + 
				")";
		List<Object> params = new ArrayList<Object>();
		params.add(specId);
		Map<String, Object> map = null;
		try{
		   map = jdbcTemplate.queryForMap(querySQL, params.toArray());
		} catch(Exception e){
			
		}
		double singleSteelPrice = 0D;
		if(map != null){
			double thickness = Double.valueOf(String.valueOf(map.get("thickness")));
			long length = Long.valueOf(String.valueOf(map.get("length")));
			long width = Long.valueOf(String.valueOf(map.get("width")));
			double density = Double.valueOf(String.valueOf(map.get("density")));
			double price = Double.valueOf(String.valueOf(map.get("price")));
			singleSteelPrice = (length * width * thickness * density * price ) /1000000000;
		}
		return singleSteelPrice;
	}
}
