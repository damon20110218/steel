package cn.four.steel.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.four.steel.bean.to.Price;

public class SteelUtil {

	public String generateStorageNo(){
		return null;
	}
	public static String formatDate(Date date, String pattern){
		if(pattern == null || "".equals(pattern)){
			pattern = "yyyyMMdd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	public static List<List<Price>> convert(List<Price> prices){
		List<List<Price>> res = new ArrayList<List<Price>>();
		if(prices == null || prices.size() == 0){
			return res;
		}
		String steelName = "";
		for(int i = 0 ;i < prices.size() ;i++){
			Price p = prices.get(i);
			String sn = p.getSteelName();
			List<Price> sub = new ArrayList<>();
			if( i ==0){
				steelName = sn;
				sub.add(p);
			} else {
				if(steelName.equals(sn)){
					sub.add(p);
				} else {
					res.add(sub);
					sub = new ArrayList<>();
					sub.add(p);
				}
			}
			if( i == prices.size() -1){
				res.add(sub);
			}
		}
		return res;
	}
}
