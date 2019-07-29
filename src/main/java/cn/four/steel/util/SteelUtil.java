package cn.four.steel.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.four.steel.bean.to.Price;

public class SteelUtil {
	private static final Logger logger = LoggerFactory.getLogger(SteelUtil.class);
	
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
		List<Price> sub = null; new ArrayList<>();
		for(int i = 0 ;i < prices.size() ;i++){
			Price p = prices.get(i);
			String sn = p.getSteelName();
			if( i ==0){
				sub = new ArrayList<>();
				steelName = sn;
				
			}
			
			if(steelName.equals(sn)){
				sub.add(p);
			} else {
				res.add(sub);
				sub = new ArrayList<>();
				sub.add(p);
				steelName = sn;
			}
			
			if( i == prices.size() -1){
				res.add(sub);
			}
		}
		return res;
	}
	public static boolean compareDigital(double shch, double calypso, int scale) {
		String formater = "#";
		if (scale < 0) {
			logger.warn("scale should be greater than 0" + scale);
		} else {
			for (int i = 0; i < scale; i++) {
				if (i == 0) {
					formater += ".0";
				} else {
					formater += "0";
				}

			}
		}
		DecimalFormat df = new DecimalFormat(formater);
		boolean tof = compareString(df.format(shch), df.format(Math.abs(calypso)));
		double boundary = 1 / BigDecimal.valueOf(1, -scale).doubleValue();
		if(!tof) {
			if (Math.abs(shch - calypso) <= boundary) {
				
			}
		}
		return tof;
	}
	public static boolean compareString(String shch, String calypso) {
		if (shch == null && calypso == null) {
			return true;
		} else if (shch == null && calypso != null) {
			return false;
		} else if (shch != null && calypso == null) {
			return false;
		} else {
			return shch.equals(calypso);
		}
	}
}
