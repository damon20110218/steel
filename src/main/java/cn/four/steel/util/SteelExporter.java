package cn.four.steel.util;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.four.steel.bean.from.FrontOrder;
import cn.four.steel.bean.from.FrontSale;
import cn.four.steel.bean.to.MainOut;
import cn.four.steel.bean.to.MainStorage;
import cn.four.steel.bean.to.SingleOut;

public class SteelExporter {
	private static Logger logger = LoggerFactory.getLogger(SteelExporter.class);

	public static void createHeader(HSSFWorkbook wb, HSSFSheet sheet, String[] titles) {
		HSSFRow row1 = sheet.createRow(0);
		// 表头
		for (int i = 0; i < titles.length; i++) {
			HSSFCell cell = row1.createCell(i);// 创建标题第一列
			cell.setCellValue(titles[i]);
		}
	}

	public static HSSFWorkbook exportStorage(List<MainStorage> stores, String[] titles) {
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			HSSFSheet sheet = wb.createSheet();
			createHeader(wb, sheet, titles);
			if (stores != null && stores.size() != 0) {
				for (int i = 0; i < stores.size(); i++) {
					MainStorage store = stores.get(i);
					HSSFRow row = sheet.createRow(i + 1);
					HSSFCell storeDateCell = row.createCell(0);
					storeDateCell.setCellValue(store.getStoreDate());
					HSSFCell storeNoCell = row.createCell(1);
					storeNoCell.setCellValue(store.getStoreNo());
					HSSFCell clientNoCell = row.createCell(2);
					clientNoCell.setCellValue(store.getClientNo());
					HSSFCell amountCell = row.createCell(3);
					amountCell.setCellValue(store.getCashAmount());
					HSSFCell factoryCell = row.createCell(4);
					factoryCell.setCellValue(store.getFactory());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return wb;
	}
	
	public static HSSFWorkbook exportMainOrder(List<FrontOrder> orders, String[] titles) {
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			HSSFSheet sheet = wb.createSheet();
			createHeader(wb, sheet, titles);
			if(orders != null && orders.size() != 0){
				for(int i = 0 ; i < orders.size() ;i ++){
					FrontOrder order = orders.get(i);
					HSSFRow row = sheet.createRow(i + 1);
					HSSFCell orderDateCell = row.createCell(0);
					orderDateCell.setCellValue(order.getOrderDate());
					HSSFCell orderNoCell = row.createCell(1);
					orderNoCell.setCellValue(order.getOrderNo());
					HSSFCell clientCell = row.createCell(2);
					clientCell.setCellValue(order.getClientName()); 
					HSSFCell priceCell = row.createCell(3);
					priceCell.setCellValue(order.getPrice());
					HSSFCell isOutCell = row.createCell(4);
					isOutCell.setCellValue(order.getIsOut()); // TODO 映射转换
					HSSFCell isSaleCell = row.createCell(5);
					isSaleCell.setCellValue(order.getIsSale()); // TODO 映射转换
					HSSFCell commentCell = row.createCell(6);
					commentCell.setCellValue(order.getComment()); 
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return wb;	
	}
	public static HSSFWorkbook exportSingleOrder(List<FrontOrder> orders, String[] titles) {
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			HSSFSheet sheet = wb.createSheet();
			createHeader(wb, sheet, titles);
			if(orders != null && orders.size() != 0){
				for(int i = 0 ; i < orders.size() ;i ++){
					FrontOrder order = orders.get(i);
					HSSFRow row = sheet.createRow(i + 1);
					HSSFCell orderNoCell = row.createCell(0);
					orderNoCell.setCellValue(order.getOrderNo());
					HSSFCell clientNoCell = row.createCell(1);
					clientNoCell.setCellValue(order.getAccountNo());
					HSSFCell categoryCell = row.createCell(2);
					categoryCell.setCellValue(order.getSpecId()); // TODO 种类
					HSSFCell specCell = row.createCell(3);
					specCell.setCellValue(order.getSpecId()); // TODO 规格
					HSSFCell amountCell = row.createCell(4);
					amountCell.setCellValue(order.getClientAmount()); 
					HSSFCell unitCell = row.createCell(5);
					unitCell.setCellValue(order.getUnit()); 
					HSSFCell priceCell = row.createCell(6);
					priceCell.setCellValue(order.getPrice()); 
					HSSFCell calcAmountCell = row.createCell(7);
					calcAmountCell.setCellValue(order.getSteelCalcAmount()); 
					HSSFCell commentCell = row.createCell(8);
					commentCell.setCellValue(order.getComment()); 
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return wb;	
	}
	
	public static HSSFWorkbook exportMainSale(List<FrontSale> sales, String[] titles) {
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			HSSFSheet sheet = wb.createSheet();
			createHeader(wb, sheet, titles);
			if(sales != null && sales.size() != 0){
				for(int i = 0 ; i < sales.size() ;i ++){
					FrontSale sale = sales.get(i);
					HSSFRow row = sheet.createRow(i + 1);
					HSSFCell saleDateCell = row.createCell(0);
					saleDateCell.setCellValue(sale.getSaleDate());
					HSSFCell saleNoCell = row.createCell(1);
					saleNoCell.setCellValue(sale.getSaleNo());
					HSSFCell amountCell = row.createCell(2);
					amountCell.setCellValue(sale.getCashAmount());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return wb;	
	}
	public static HSSFWorkbook exportMainOut(List<MainOut> outs, String[] titles) {
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			HSSFSheet sheet = wb.createSheet();
			createHeader(wb, sheet, titles);
			if(outs != null && outs.size() != 0){
				for(int i = 0 ; i < outs.size() ;i ++){
					MainOut out = outs.get(i);
					HSSFRow row = sheet.createRow(i + 1);
					HSSFCell outDateCell = row.createCell(0);
					outDateCell.setCellValue(out.getOutDate());
					HSSFCell orderNoCell = row.createCell(1);
					orderNoCell.setCellValue(out.getOrderNo());
					HSSFCell categotyCell = row.createCell(2);
					categotyCell.setCellValue(out.getCategoryDesc());
					HSSFCell specCell = row.createCell(2);
					specCell.setCellValue(out.getSpecDesc());
					HSSFCell actualAmountCell = row.createCell(2);
					actualAmountCell.setCellValue(out.getActualAmount());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return wb;	
	}
	
	public static HSSFWorkbook exportSingleOut(List<SingleOut> outs, String[] titles) {
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			HSSFSheet sheet = wb.createSheet();
			createHeader(wb, sheet, titles);
			if(outs != null && outs.size() != 0){
				for(int i = 0 ; i < outs.size() ;i ++){
					SingleOut out = outs.get(i);
					HSSFRow row = sheet.createRow(i + 1);
					HSSFCell orderNoCell = row.createCell(0);
					orderNoCell.setCellValue(out.getOrderNo());
					HSSFCell categotyCell = row.createCell(1);
					categotyCell.setCellValue(out.getSpecId()); // TODO 种类
					HSSFCell specCell = row.createCell(2);
					specCell.setCellValue(out.getSpecId());// TODO 规格
					HSSFCell amountCell = row.createCell(3);
					amountCell.setCellValue(out.getClientAmount());
					HSSFCell unitCell = row.createCell(4);
					unitCell.setCellValue(out.getUnit());
					HSSFCell calcAmountCell = row.createCell(5);
					calcAmountCell.setCellValue(out.getSteelCalcAmount());
					HSSFCell actualAmountCell = row.createCell(6);
					actualAmountCell.setCellValue(out.getActualAmount());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return wb;	
	}
}
