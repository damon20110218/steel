package cn.four.steel.bean;

import java.util.ArrayList;
import java.util.List;

public class DataGridResult<T> {
	private Long total;
	private List<T> rows;

	public DataGridResult() {
		rows = new ArrayList<T>();
		total = 0L;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
