package rp3.pos.model;

import rp3.db.sqlite.DataBase;
import rp3.pos.db.Contract;
import android.database.Cursor;

public class SalesCategory {

	private int salesCategoryId;
	private String name;
	private short order;
	
	public int getSalesCategoryId() {
		return salesCategoryId;
	}
	public void setSalesCategoryId(int salesCategoryId) {
		this.salesCategoryId = salesCategoryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public short getOrder() {
		return order;
	}
	public void setOrder(short order) {
		this.order = order;
	}
	
	public static Cursor getSalesCategoryCursor(DataBase db){
		return db.query(Contract.SalesCategory.TABLE_NAME, 
				new String[] {
				Contract.SalesCategory._ID,
				Contract.SalesCategory.COLUMN_NAME },
				null, null, null, null, Contract.SalesCategory.COLUMN_ORDER);				
	}
	
}
