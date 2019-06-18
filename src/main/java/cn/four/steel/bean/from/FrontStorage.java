package cn.four.steel.bean.from;

public class FrontStorage implements java.io.Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = -1337363428615750782L;
	
	private Long storageId;
	private Long clientId;
	private String clientName;
	private String storageNo;
	private String clientNo;
	private Long specId;
	private Double amount;
	private Double cashAmount;
	private String factory;
	private Double price;
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getStorageNo() {
		return storageNo;
	}
	public void setStorageNo(String storageNo) {
		this.storageNo = storageNo;
	}
	public String getClientNo() {
		return clientNo;
	}
	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}
	public Long getSpecId() {
		return specId;
	}
	public void setSpecId(Long specId) {
		this.specId = specId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(Double cashAmount) {
		this.cashAmount = cashAmount;
	}
	public String getFactory() {
		return factory;
	}
	public void setFactory(String factory) {
		this.factory = factory;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getStorageId() {
		return storageId;
	}
	public void setStorageId(Long storageId) {
		this.storageId = storageId;
	}
}
