package cn.four.steel.bean.from;

public class FrontOrder {
	private Long orderId;
	private String orderNo;
	private String accountNo;
	private Long clientId;
	private Long categoryId;
	private Long specId;
	private String clientSpec;
	private Double clientAmount;
	private String price;
	private String cashAmount;
	private String steelCalcAmount;
	private String comment;
	private String unit;
	private String clientName;
	private String isOut;
	private String isSale;
	private String orderDate;
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public Long getSpecId() {
		return specId;
	}
	public void setSpecId(Long specId) {
		this.specId = specId;
	}
	public Double getClientAmount() {
		return clientAmount;
	}
	public void setClientAmount(Double clientAmount) {
		this.clientAmount = clientAmount;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSteelCalcAmount() {
		return steelCalcAmount;
	}
	public void setSteelCalcAmount(String steelCalcAmount) {
		this.steelCalcAmount = steelCalcAmount;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getIsOut() {
		return isOut;
	}
	public void setIsOut(String isOut) {
		this.isOut = isOut;
	}
	public String getIsSale() {
		return isSale;
	}
	public void setIsSale(String isSale) {
		this.isSale = isSale;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getClientSpec() {
		return clientSpec;
	}
	public void setClientSpec(String clientSpec) {
		this.clientSpec = clientSpec;
	}
	public String getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(String cashAmount) {
		this.cashAmount = cashAmount;
	}
}
