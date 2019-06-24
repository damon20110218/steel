package cn.four.steel.bean.from;

public class PagePrice implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9177583169242016623L;
	private String priceCode;
	private String specName;
	private double price;
	private String priceType;
	public String getPriceCode() {
		return priceCode;
	}
	public void setPriceCode(String priceCode) {
		this.priceCode = priceCode;
	}
	public String getSpecName() {
		return specName;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
}
