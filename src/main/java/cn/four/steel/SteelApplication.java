package cn.four.steel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.four.steel.cache.BaseDataCache;
@EnableTransactionManagement
@SpringBootApplication
public class SteelApplication {

	public static void main(String[] args) {
		SpringApplication.run(SteelApplication.class, args);
	}
	
	@Bean 
	public BaseDataCache baseDataCache(){
		return new BaseDataCache();
	}
}

