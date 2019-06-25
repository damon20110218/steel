package cn.four.steel.util;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cn.four.steel.bean.to.MainStorage;

public class SteelExporter {

	public static HSSFWorkbook exportStorage(List<MainStorage> stores) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row1 = sheet.createRow(0);
		row1.setHeightInPoints(50);// 设备标题的高度
		String[] titles = new String[] { "入库日期", "入库单号", "客户单号", "金额", "钢厂" };
		// 表头
		for (int i = 0; i < titles.length; i++) {
			HSSFCell cell = row1.createCell(i);// 创建标题第一列
			cell.setCellValue(titles[i]);
		}
		if(stores != null && stores.size() != 0){
			for(int i = 0; i < stores.size() ; i ++){
				
			}
		}
		return wb;
	}
}
