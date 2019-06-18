package cn.four.steel.bean.from;

public class FrontOrder {
	private Long orderId;
	private String orderNo;
	private String clientNo;
	private Long clientId;
	private Long specId;
	private Double clientAmount;
	private Double price;
	private String SteelCalcAmount;
	private String comment;
	private String unit;
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
	public String getClientNo() {
		return clientNo;
	}
	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getSteelCalcAmount() {
		return SteelCalcAmount;
	}
	public void setSteelCalcAmount(String steelCalcAmount) {
		SteelCalcAmount = steelCalcAmount;
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
}
