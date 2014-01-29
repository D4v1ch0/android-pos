package rp3.pos.model;

import rp3.db.QueryDir;
import rp3.db.sqlite.DataBase;
import rp3.pos.db.Contract;
import rp3.util.CursorUtils;
import android.database.Cursor;

public class Product {
	
	private long productId;
	private String sku;
	private String description;	
	private double price;
	
	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}
	
	public String getSku()
	{
		return sku;
	}
	
	public void setSku(String sku)
	{
		this.sku = sku;
	}	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public static Product getProduct(DataBase db, int productId){
		Product product = new Product();		
		
		Cursor c = db.rawQuery( QueryDir.getQuery(Contract.Product.QUERY_PRODUCT_BY_ID), productId );
		c.moveToFirst();
		
		product.setSku( CursorUtils.getString(c, Contract.Product.FIELD_SKU) );
		product.setDescription( CursorUtils.getString(c, Contract.Product.FIELD_NAME) );
		product.setPrice( CursorUtils.getDouble(c, Contract.Product.FIELD_PRICE) );
		product.setProductId( CursorUtils.getInt(c, Contract.Product._ID) );
		
		return product;
	}
	
	public static Cursor getProductSearchCursor(DataBase db, String termSearch)
	{		
		String query = QueryDir.getQuery( Contract.Product.QUERY_PRODUCT_SEARCH );
		
		Cursor c = db.rawQuery(query, "*" + termSearch + "*" );
		return c;
	}
}
