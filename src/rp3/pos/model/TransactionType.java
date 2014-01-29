package rp3.pos.model;

import java.util.ArrayList;
import java.util.List;

import rp3.db.sqlite.DataBase;
import rp3.pos.db.Contract;
import rp3.util.Format;
import android.content.ContentValues;
import android.database.Cursor;

public class TransactionType {
	
	
	public static final int TYPE_ORDER = 1;	
	public static final int TYPE_SALES = 2;
	
	public static final int PREFERENCE_PRODUCT_INSERT_ACTION = 1;
	public static final int PREFERENCE_PRODUCT_INSERT_INLINE = 2;	
	
	private int transactionTypeId;
	private String name;
	private boolean active;
	private int position;
	private short productoInsertModePreference;
	
	public int getTransactionTypeId() {
		return transactionTypeId;
	}
	public void setTransactionTypeId(int transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}	
	public short getProductoInsertModePreference() {
		return productoInsertModePreference;
	}
	public void setProductoInsertModePreference(short productoInsertModePreference) {
		this.productoInsertModePreference = productoInsertModePreference;
	}	
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public static boolean updateProductInsertModePreference(DataBase db, int transactionTypeId, short preference){
		ContentValues values = new ContentValues();
		values.put(Contract.TransactionType.COLUMN_PRODUCT_INSERT_MODE_PREFERENCE, preference);
		
		return db.update(Contract.TransactionType.TABLE_NAME, values, transactionTypeId) != 0;		
	}
	
	public static TransactionType getTransactionType(DataBase db, int type){
		Cursor c = db.query(Contract.TransactionType.TABLE_NAME, 
				new String[] { Contract.TransactionType._ID,
							   Contract.TransactionType.FIELD_NAME,
							   Contract.TransactionType.FIELD_ACTIVE,
							   Contract.TransactionType.FIELD_PRODUCT_INSERT_MODE_PREFERENCE}, 
	            Contract.TransactionType._ID + " = ?",
	            String.valueOf(type));
		
		c.moveToFirst();
		
		TransactionType returnValue = new TransactionType();
		returnValue.setTransactionTypeId( c.getInt(c.getColumnIndex(Contract.TransactionType._ID)));
		returnValue.setName( c.getString(c.getColumnIndex(Contract.TransactionType.FIELD_NAME)) );
		returnValue.setActive( Format.getBooleanFromDataBaseInteger( 
				c.getInt(c.getColumnIndex(Contract.TransactionType.FIELD_ACTIVE)) ));
		
		returnValue.setProductoInsertModePreference( c.getShort(c.getColumnIndex(Contract.TransactionType.FIELD_PRODUCT_INSERT_MODE_PREFERENCE)));
		
		return returnValue;
	}
	
	public static List<TransactionType> getActiveTransactionTypes(DataBase db, boolean includeAll)
	{		
		Cursor c = db.query(Contract.TransactionType.TABLE_NAME, 
				new String[] { Contract.TransactionType._ID,
							   Contract.TransactionType.FIELD_NAME}, 
	            Contract.TransactionType.COLUMN_ACTIVE + " = ?",
	            DataBase.DATABASE_TRUE_VALUE);
		
		int _position = 1;
		List<TransactionType> list = new ArrayList<TransactionType>();
		if(includeAll){
			TransactionType allT = new TransactionType();
			allT.setPosition(_position++);
			allT.setName("Todo");
			allT.setTransactionTypeId(0);
			list.add(allT);
		}		
		
		 if (c.moveToFirst()) {
		        do {
		            TransactionType tt = new TransactionType();
		            tt.setTransactionTypeId(c.getInt(c.getColumnIndex(Contract.TransactionType._ID)));
		            tt.setName(c.getString(c.getColumnIndex(Contract.TransactionType.FIELD_NAME)));
		            tt.setActive(true);
		            tt.setPosition(_position++);
		            list.add(tt);
		        } while (c.moveToNext());
		    }
		 //db.close();
		 return list;
	}	
}
