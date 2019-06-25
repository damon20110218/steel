package cn.four.steel.bean.to;

public class SingleInventory {
	private String inventoryDate;
	private String steelName;
	private Double thickness;
	private Double storeIn;
	private Double storeOut;
	private Double inventory;
	public String getInventoryDate() {
		return inventoryDate;
	}
	public void setInventoryDate(String inventoryDate) {
		this.inventoryDate = inventoryDate;
	}
	public String getSteelName() {
		return steelName;
	}
	public void setSteelName(String steelName) {
		this.steelName = steelName;
	}
	public Double getThickness() {
		return thickness;
	}
	public void setThickness(Double thickness) {
		this.thickness = thickness;
	}
	public Double getStoreIn() {
		return storeIn;
	}
	public void setStoreIn(Double storeIn) {
		this.storeIn = storeIn;
	}
	public Double getStoreOut() {
		return storeOut;
	}
	public void setStoreOut(Double storeOut) {
		this.storeOut = storeOut;
	}
	public Double getInventory() {
		return inventory;
	}
	public void setInventory(Double inventory) {
		this.inventory = inventory;
	}
}
