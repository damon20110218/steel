package cn.four.steel;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.four.steel.bean.to.SteelCategory;
import cn.four.steel.bean.to.SteelSpecication;
import cn.four.steel.cache.BaseDataCache;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SteelApplicationTests {
	
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
}

