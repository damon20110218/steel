package cn.four.steel.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

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
public class BaseDataCache  implements CommandLineRunner {
	
	private static Logger logger = LoggerFactory.getLogger(BaseDataCache.class);
	@Override
	public void run(String... args) throws Exception {
		logger.info("start to load all basic steel data into memory");
	}
}
