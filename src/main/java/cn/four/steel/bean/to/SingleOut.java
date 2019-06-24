package cn.four.steel.bean.to;

public class SingleOut {
	private Long outId;
	private String orderNo;
	private Long specId;
	private Double clientAmount;
	private String unit;
	private String steelCalcAmount;
	private Double actualAmount;
	public Long getOutId() {
		return outId;
	}
	public void setOutId(Long outId) {
		this.outId = outId;
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
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getSteelCalcAmount() {
		return steelCalcAmount;
	}
	public void setSteelCalcAmount(String steelCalcAmount) {
		this.steelCalcAmount = steelCalcAmount;
	}
	public Double getActualAmount() {
		return actualAmount;
	}
	public void setActualAmount(Double actualAmount) {
		this.actualAmount = actualAmount;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
}
