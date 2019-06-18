package cn.four.steel.bean.to;

import java.util.ArrayList;
import java.util.List;

public class SteelCategory {
	private Long categoryId;
	private String steelName;
	private String steelCode;
	private Long length;
	private Long width;
	private List<SteelSpecication> specs;
	public SteelCategory(){
		specs = new ArrayList<>();
	}
	public void addSpec(SteelSpecication spec){
		specs.add(spec);
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getSteelName() {
		return steelName;
	}
	public void setSteelName(String steelName) {
		this.steelName = steelName;
	}
	public String getSteelCode() {
		return steelCode;
	}
	public void setSteelCode(String steelCode) {
		this.steelCode = steelCode;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
	public Long getWidth() {
		return width;
	}
	public void setWidth(Long width) {
		this.width = width;
	}
	public List<SteelSpecication> getSpecs() {
		return specs;
	}
	public void setSpecs(List<SteelSpecication> specs) {
		this.specs = specs;
	}
}
