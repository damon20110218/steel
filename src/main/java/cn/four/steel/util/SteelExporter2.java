package cn.four.steel.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;

public class SteelExporter2 {

	public void export() {
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row1 = sheet.createRow((int) 0);
		row1.setHeightInPoints(50);// 设备标题的高度
		// 第三步创建标题的单元格样式style2以及字体样式headerFont1
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style2.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		HSSFFont headerFont1 = (HSSFFont) wb.createFont(); // 创建字体样式
		headerFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体加粗
		headerFont1.setFontName("黑体"); // 设置字体类型
		headerFont1.setFontHeightInPoints((short) 15); // 设置字体大小
		style2.setFont(headerFont1); // 为标题样式设置字体样式

		HSSFCell cell1 = row1.createCell(0);// 创建标题第一列
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10)); // 合并列标题
		cell1.setCellValue("导出Excel"); // 设置值标题
		cell1.setCellStyle(style2); // 设置标题样式

		// 创建第1行 也就是表头
		HSSFRow row = sheet.createRow((int) 1);
		row.setHeightInPoints(37);// 设置表头高度

		// 第四步，创建表头单元格样式 以及表头的字体样式
		HSSFCellStyle style = wb.createCellStyle();
		style.setWrapText(true);// 设置自动换行
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 创建一个居中格式

		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);

		HSSFFont headerFont = (HSSFFont) wb.createFont(); // 创建字体样式
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体加粗
		headerFont.setFontName("黑体"); // 设置字体类型
		headerFont.setFontHeightInPoints((short) 10); // 设置字体大小
		style.setFont(headerFont); // 为标题样式设置字体样式

		// 第四.一步，创建表头的列
//		for (int i = 0; i < columnNumber; i++) {
//			HSSFCell cell = row.createCell(i);
//			cell.setCellValue(columnName[i]);
//			cell.setCellStyle(style);
//		}

	}
}
