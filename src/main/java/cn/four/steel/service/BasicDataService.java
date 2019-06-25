package cn.four.steel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.four.steel.bean.from.Client;
import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.bean.to.SteelSpecication;

@Service
public class BasicDataService {
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public List<SteelCategory> listAllCategory(Long categoryId){
		String sql = "Select * From steel_category Where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if( categoryId != null){
			sql += "and category_id = ?";
			params.add(categoryId);
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params.toArray());
		List<SteelCategory> categories =  new ArrayList<>();
		if(list != null){
			for(Map<String, Object> map : list){
				SteelCategory category = new SteelCategory();
				Long categId = Long.valueOf(String.valueOf(map.get("category_id")));
				category.setCategoryId(categId);
				category.setLength(Long.valueOf(String.valueOf(map.get("length"))));
				category.setWidth(Long.valueOf(String.valueOf(map.get("width"))));
				category.setSteelName(String.valueOf(map.get("steel_name")));
				category.setSpecs(loadSpecs(null, categId));
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
		String updateSQL = "update client_info set client_name = ?, create_date= ?, client_mark = ?, mobile = ? "
				+ " telephone = ?, email = ?, address = ?, contact_person = ? where client_id = ? ";
		List<Object> params = new ArrayList<Object>();
		if(client.getClientId() != null){
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
	}
	public void delClient(Long clientId){
		String delSQL = "delete from client_info where client_id = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(clientId);
		jdbcTemplate.update(delSQL, params.toArray());
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
		String querySQL = "select ss.thickness, sc.length, sc.width, sp.density, sk.price from steel_specs ss, steel_category sc, steel_parameter sp, steel_price sk"
				+ " where ss.categort_id = sc.category_id and ss.category_id = sp.category_id and ss.price_code = sk.price_code"
				+ " and ss.spec_id = ? and sk.prcie_date = ?";
		List<Object> params = new ArrayList<Object>();
		Date now = new Date();
		params.add(specId);
		params.add(now);
		Map<String, Object> map = jdbcTemplate.queryForMap(querySQL, params.toArray());
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
