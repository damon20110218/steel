package cn.four.steel.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import cn.four.steel.bean.from.Client;
import cn.four.steel.bean.to.SingleInventory;
import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.bean.to.SteelSpecication;
import cn.four.steel.service.BasicDataService;

/**
 * 
 * All rights Reserved, Designed By www.gtja.net
 * @Title:  BaseDateCache.java   
 * @Package cn.four.steel.cache   
 * @Description:   维护基本数据在内存中
 * @author: Damon Zhaojin     
 * @date:   2019年6月15日 下午3:13:31   
 * @version V1.0 
 * @Copyright: 2019 www.gtja.net Inc. All rights reserved.
 */
@Component
public class BaseDataCache  implements CommandLineRunner {
	
	private static Logger logger = LoggerFactory.getLogger(BaseDataCache.class);
	@Autowired
	private BasicDataService basicDataService;

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	private Map<Long, SteelCategory> steelCatrgoryMap = new HashMap<Long, SteelCategory>(16);
	private Map<Long, SteelSpecication> steelSpcMap = new HashMap<Long, SteelSpecication>(64);
	private Double[] density = new Double[11];
	List<Client> companyList= new ArrayList<Client>();
	Object companyListLock = new Object();
	List<Client> customerList= new ArrayList<Client>();
	Object customerListLock = new Object();
	
	
	@Override
	public void run(String... args) throws Exception {
		logger.info("start to load all basic steel data into memory");
		
		loadSteelCategory();
		
		loadSteelSpecication();
		
		loadCompanyList();
		
		loadCustomerList();
		
		loadDensity();
	}
	public Map<Long, SteelCategory> getCatetories(){
		return steelCatrgoryMap;
	}
	public Map<Long, SteelSpecication> getSpecs(){
		return steelSpcMap;
	}
	public Long getCategoryId (Long specId){
		return steelSpcMap.get(specId).getCategoryId();
	}
	private void loadSteelCategory() {
		List<SteelCategory> steelCatrgoryList = basicDataService.listAllCategory(null); 
		for(SteelCategory steelCatrgory: steelCatrgoryList) {
			steelCatrgoryMap.put(steelCatrgory.getCategoryId(), steelCatrgory);
		}
	}
	
	private void loadSteelSpecication() {
		List<SteelSpecication> steelSpecicationList = basicDataService.loadSpecs(null, null); 
		for(SteelSpecication steelSpecication: steelSpecicationList) {
			steelSpcMap.put(steelSpecication.getSpecId(), steelSpecication);
		}
	}
	
	private void loadCompanyList() {
		synchronized(companyListLock) {
			companyList = basicDataService.matchClient(null, "1");
		}
	}
	
	private void loadCustomerList() {
		synchronized(customerListLock) {
			customerList = basicDataService.matchClient(null, "2");
		}
	}
	
	private void loadDensity() {
		String querySQL = "select category_id, density from steel_parameter";
		List<Object> params = new ArrayList<Object>();
		List<Map<String, Object>> list = jdbcTemplate.queryForList(querySQL, params.toArray());
		if(list != null){
			for(Map<String, Object> m : list){
				density[(int)m.get("category_id")] = (Double) m.get("density");
			}
		}
	}
	
	public SteelCategory getSteelCategory(Long categoryId) {
		return steelCatrgoryMap.get(categoryId);
	}
	
	public SteelSpecication getSteelSpecication(Long SpecId) {
		return steelSpcMap.get(SpecId);
	}
	
	public List<SteelSpecication> getSteelSpecicationByCategory(Long categoryId) {
		return steelCatrgoryMap.get(categoryId).getSpecs();
	}
	
	public List<Client> getCompanyList(String name) {
		List<Client> rst = new ArrayList<Client>();
		synchronized(companyListLock) {
			for(Client company: companyList) {
				if(company.getClientName().startsWith(name)) {
					rst.add(company);
				}
			}
		}
		return rst;
	}
	
	public List<Client> getCustomerList(String name) {
		List<Client> rst = new ArrayList<Client>();
		synchronized(customerListLock) {
			for(Client customer: customerList) {
				if(customer.getClientName().startsWith(name)) {
					rst.add(customer);
				}
			}
		}
		return rst;
	}
	
	public void updateClient(Client client) {
		if(client.getClientType() == null || client.getClientId() == null)
			return;
		if("1".equals(client.getClientType())) {
			synchronized(companyListLock) {
				for(int i = 0; i< companyList.size(); i++) {
					if(companyList.get(i).getClientId() == client.getClientId()) {
						companyList.remove(i);
						companyList.add(client);
						return;
					}
				}
			}
		}else {
			synchronized(customerListLock) {
				for(int i = 0; i< customerList.size(); i++) {
					if(customerList.get(i).getClientId() == client.getClientId()) {
						customerList.remove(i);
						customerList.add(client);
						return;
					}
				}
			}
		}
	}
	
	public void addClient(Client client) {
		if(client.getClientType() == null )
			return;
		if("1".equals(client.getClientType())) {
			synchronized(companyListLock) {
				companyList.add(client);
			}
		}else {
			synchronized(customerListLock) {
				customerList.add(client);
			}
		}
	}
	
	public void delClient(Long clientID) {
		synchronized(companyListLock) {
			synchronized(customerListLock) {
				for(int i = 0; i< companyList.size(); i++) {
					if(companyList.get(i).getClientId() == clientID) {
						companyList.remove(i);
						return;
					}
				}
				for(int i = 0; i< customerList.size(); i++) {
					if(customerList.get(i).getClientId() == clientID) {
						customerList.remove(i);
						return;
					}
				}
			}
		}
	}
	
	public double getDensityByCategory(Long category_id) {
		return density[category_id.intValue()];
	}
	
}
