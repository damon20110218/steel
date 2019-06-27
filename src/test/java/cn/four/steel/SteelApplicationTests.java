package cn.four.steel;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import cn.four.steel.bean.to.Price;
import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.bean.to.SteelSpecication;
import cn.four.steel.cache.BaseDataCache;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SteelApplicationTests {
	
	
	
	@Autowired
    private TestRestTemplate restTemplate;
	

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private BaseDataCache baseDataCache;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testCache1() {
		System.out.println("testCache getCatetories !");
		Map<Long, SteelCategory> m = baseDataCache.getCatetories();
		TestCase.assertEquals(m.size(), 10);
	}
	
	@Test
	public void testCache2() {
		System.out.println("testCache getSpecs!");
		Map<Long, SteelSpecication> m = baseDataCache.getSpecs();
		TestCase.assertEquals(m.size(), 104);
	}
	
	@Test
	public void testCache3() {
		System.out.println("testCache getSteelCategory!");
		SteelCategory s = baseDataCache.getSteelCategory(1L);
		TestCase.assertEquals(s.getSteelName(), "热轧板");
		TestCase.assertEquals(s.getLength(),  new Long(4000));
		TestCase.assertEquals(s.getWidth(),  new Long(1500));
		TestCase.assertEquals(s.getSpecs().size(),  14);
		
	}
	
	@Test
	public void testCache4() {
		System.out.println("testCache getSteelSpecication!");
		SteelSpecication s = baseDataCache.getSteelSpecication(1L);
		TestCase.assertEquals(s.getCategoryId(), new Long(1));
		TestCase.assertEquals(s.getThickness(),  new Double(1.5));
	}
	@Test
	public void testCache5() {
		System.out.println("testCache getSteelSpecicationByCategory!");
		List<SteelSpecication> l = baseDataCache.getSteelSpecicationByCategory(1L);

		TestCase.assertEquals(l.size(), 14);
	}
	
	@Test
	public void testCache6() {
		System.out.println("testCache getDensityByCategory!");
		double l = baseDataCache.getDensityByCategory(1L);

		TestCase.assertEquals(l, 7850.0);
		
		 l = baseDataCache.getDensityByCategory(7L);

		TestCase.assertEquals(l, 2710.0);
	}
	
	@Test
    public void testCalculate1() throws Exception {
		
		System.out.println("testCache testCalculate1!");
		String turl =  "/calc/standart_amount";
		
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("clientWidth", "150");
		paramMap.add("clientAmount", "30");
		paramMap.add("standardLength", "4000");
		String result = restTemplate.postForObject(turl, paramMap, String.class);
		TestCase.assertEquals(result, "1张60cm");
		paramMap.clear();
		paramMap.add("clientWidth", "90");
		paramMap.add("clientAmount", "5000");
		paramMap.add("standardLength", "2000");
		result = restTemplate.postForObject(turl, paramMap, String.class);
		TestCase.assertEquals(result, "227张54cm");
		paramMap.clear();
		paramMap.add("clientWidth", "60");
		paramMap.add("clientAmount", "2500");
		paramMap.add("standardLength", "3000");
		result = restTemplate.postForObject(turl, paramMap, String.class);
		TestCase.assertEquals(result, "50张");
		paramMap.clear();
		paramMap.add("clientWidth", "1600");
		paramMap.add("clientAmount", "2500");
		paramMap.add("standardLength", "3000");
		result = restTemplate.postForObject(turl, paramMap, String.class);
		TestCase.assertEquals(result, "2500张");
		paramMap.clear();
		paramMap.add("clientWidth", "90");
		paramMap.add("clientAmount", "3300");
		paramMap.add("standardLength", "3000");
		result = restTemplate.postForObject(turl, paramMap, String.class);
		TestCase.assertEquals(result, "100张");
    }
	
	@Test
    public void testCalculate2() throws Exception {
		
		System.out.println("testCache testCalculate2!");
		String turl =  "/calc/formula_price";
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("amount", "150");
		paramMap.add("specId", "1");
		String result = restTemplate.postForObject(turl, paramMap, String.class);
		TestCase.assertEquals(result, "1张600mm");
	}
	
	@Test
    public void testPrice() throws Exception {
		
		System.out.println("testCache testPrice!");
		String turl =  "/price/update";
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> paramMap = new HashMap<>();
		String sqlString = "select distinct(price_code) from steel_specs";
		Map<String, Double> tmpPirce = new HashMap<String, Double>();
		List<Map<String, Object>> lm = jdbcTemplate.queryForList(sqlString);
		for(int i =0; i < lm.size(); i++) {
			paramMap.clear();
			paramMap.put("priceCode", lm.get(i).get("price_code").toString());
			paramMap.put("priceType", "2");
			String randomVal = formatDouble4(Math.random()*10);
			Double randomValD = Double.parseDouble(randomVal);
			paramMap.put("price", randomVal);
			tmpPirce.put("price", randomValD);
			HttpEntity<Map<String, String>> requestEntity = new HttpEntity<Map<String, String>>(paramMap, requestHeaders);
			String result = restTemplate.postForObject(turl, requestEntity, String.class);
			TestCase.assertEquals(result, "Success");
		}
		String turl2 =  "/price/today";
		String result = restTemplate.postForObject(turl2, paramMap, String.class);
		System.out.println(result);
		
	}
	
	public static String formatDouble4(double d) {
        DecimalFormat df = new DecimalFormat("#.00");

        
        return df.format(d);
    }

	
}

