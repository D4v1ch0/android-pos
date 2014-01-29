package rp3.pos.model;

import java.util.ArrayList;
import java.util.List;

import rp3.db.sqlite.DataBase;
import rp3.pos.db.Contract;
import android.content.ContentValues;
import android.database.Cursor;

public class TransactionDetail {

	private long transactionDetailId;
	private long transactionId;
	private long productId;	
	private double quantity;
	private double subtotal;
	private double price;
	private double discount;
	private double tax;
	private double subtotalBeforeTax;
	private double total;
	private Product product;
	
	public final static String[] SIMPLEFROM_CURSOR = new String[] 
			{
			  Contract.TransactionDetail.COLUMN_QUANTITY,
			  Contract.ProductExt.TABLE_NAME + Contract.ProductExt.COLUMN_DESCRIPTION,
			  Contract.TransactionDetail.COLUMN_DISCOUNT,
			  Contract.TransactionDetail.COLUMN_TOTAL			  
			};
	
	public long getTransactionDetailId() {
		return transactionDetailId;
	}
	public void setTransactionDetailId(long id) {
		this.transactionDetailId = id;
	}
	public long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}	
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
		if(product!=null)
			this.setProductId(product.getProductId());
		else
			this.setProductId(0);
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getSubtotalBeforeTax() {
		return subtotalBeforeTax;
	}
	public void setSubtotalBeforeTax(double subtotalbeforeTax) {
		this.subtotalBeforeTax = subtotalbeforeTax;
	}
	public void calculate()
	{
		this.subtotal = this.price * this.quantity;
		this.subtotalBeforeTax = this.subtotal - discount;
		this.tax = this.subtotalBeforeTax * 0.12;
		this.total = this.subtotalBeforeTax + this.tax;
	}
		
	public static boolean insertFromActiveTransaction(DataBase db, TransactionDetail d)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put(Contract.TransactionDetail.COLUMN_TRANSACTIONID, d.getTransactionId());
			
			setContentValues(d, values);
			
			db.insert(Contract.TransactionDetail.TABLE_NAME, values);
			
			d.setTransactionDetailId(db.getLongLastInsertRowId());
			
			return true;
			
		}catch(Exception ex){
			return false;
		}
	}
	
	public static boolean updateFromActiveTransaction(DataBase db, TransactionDetail d)
	{
		try
		{
			ContentValues values = new ContentValues();
			setContentValues(d, values);
			
			db.update(Contract.TransactionDetail.TABLE_NAME, values, d.getTransactionDetailId());								
			
			return true;
			
		}catch(Exception ex){
			return false;
		}
	}
	
	public static boolean deleteAllFromActiveTransaction(DataBase db,long transactionId){
		return db.delete(Contract.TransactionDetail.TABLE_NAME, 
				Contract.TransactionDetail.COLUMN_TRANSACTIONID + "=?", transactionId) != 0;
	}
	
	public static boolean update(DataBase db, TransactionDetail d){
		ContentValues values = new ContentValues();
		setContentValues(d, values);
		return db.update(Contract.TransactionDetail.TABLE_NAME, values, d.getTransactionDetailId()) != 0;
	}
	
	public static boolean delete(DataBase db,long transactionDetailId){
		return db.delete(Contract.TransactionDetail.TABLE_NAME, transactionDetailId) != 0;
	}
	
	private static void setContentValues(TransactionDetail d,ContentValues values){		
		values.put(Contract.TransactionDetail.COLUMN_DISCOUNT, d.getDiscount());
		values.put(Contract.TransactionDetail.COLUMN_PRODUCTID, d.getProductId());
		values.put(Contract.TransactionDetail.COLUMN_QUANTITY, d.getQuantity());
		values.put(Contract.TransactionDetail.COLUMN_SUBTOTAL, d.getSubtotal());
		values.put(Contract.TransactionDetail.COLUMN_PRICE, d.getPrice());			
		values.put(Contract.TransactionDetail.COLUMN_SUBTOTALBEFORETAXES, d.getSubtotalBeforeTax());
		values.put(Contract.TransactionDetail.COLUMN_TAX, d.getTax());
		values.put(Contract.TransactionDetail.COLUMN_TOTAL, d.getTotal());
	}
	
	public static Cursor getTransactionDetailCursor(DataBase db, long transactionId)
	{
		Cursor c = db.rawQuery(Contract.TransactionDetail.QUERY_TRANSACTIONDET, 
								String.valueOf(transactionId));
		
		return c;
	}
	
	private static TransactionDetail getTransactionDetailFromCurrentCursorPosition(Cursor c){
		
		TransactionDetail td = new TransactionDetail();
        Product product = new Product();
        
        td.setTransactionId( c.getLong(c.getColumnIndex(Contract.TransactionDetail.FIELD_TRANSACTIONID)) );
        td.setTransactionDetailId( c.getLong(c.getColumnIndex(Contract.TransactionDetail._ID)) );
        td.setProductId( c.getLong(c.getColumnIndex(Contract.TransactionDetail.FIELD_PRODUCTID)) );
        td.setQuantity(  c.getDouble(c.getColumnIndex(Contract.TransactionDetail.FIELD_QUANTITY)) );
        td.setDiscount( c.getDouble(c.getColumnIndex(Contract.TransactionDetail.FIELD_DISCOUNT)) );
        td.setSubtotal( c.getDouble(c.getColumnIndex(Contract.TransactionDetail.FIELD_SUBTOTAL)) );
        td.setTax( c.getDouble(c.getColumnIndex(Contract.TransactionDetail.FIELD_TAX)) );
        td.setTotal( c.getDouble(c.getColumnIndex(Contract.TransactionDetail.FIELD_TOTAL)) );
        td.setSubtotalBeforeTax( c.getDouble(c.getColumnIndex(Contract.TransactionDetail.FIELD_SUBTOTALBEFORETAXES)) );
        td.setPrice( c.getDouble(c.getColumnIndex(Contract.TransactionDetail.FIELD_PRICE)) );
        
        product.setProductId( td.getProductId() );
        product.setSku( c.getString(c.getColumnIndex(Contract.TransactionDetail.FIELD_PRODUCT_SKU)) );
        product.setDescription( c.getString(c.getColumnIndex(Contract.TransactionDetail.FIELD_PRODUCT_DESCRIPTION)) );
        
        td.setProduct(product);
        return td;
	}
	
	public static TransactionDetail getById(DataBase db, long transactionDetailId)
	{
		Cursor c = db.rawQuery(Contract.TransactionDetail.QUERY_TRANSACTIONDET_BY_ID, 
								transactionDetailId );
		
		TransactionDetail d = null;
		if (c.moveToFirst()) {
			d = getTransactionDetailFromCurrentCursorPosition(c);
		}
		
		return d;
	}
	
	public static List<TransactionDetail> getTransactionDetails(DataBase db, long transactionId)
	{
		Cursor c = getTransactionDetailCursor(db, transactionId);
		
		List<TransactionDetail> list =  new ArrayList<TransactionDetail>();
		if (c.moveToFirst()) {
	        do {
	            
	        	TransactionDetail td = getTransactionDetailFromCurrentCursorPosition(c);	            
	            list.add(td);
	            
	        } while (c.moveToNext());
	    }

	 return list;
	}

		
}
