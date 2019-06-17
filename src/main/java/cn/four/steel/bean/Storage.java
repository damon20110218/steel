package cn.four.steel.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Storage {
	@Id
	private Long storeId;

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
}
