package rp3.pos.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rp3.db.sqlite.DataBase;
import rp3.pos.BuildConfig;
import rp3.pos.db.Contract;
import rp3.util.CursorUtils;
import rp3.util.Format;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class Transaction {

	public static final int STATE_DEFAULT = 0;
	public static final int STATE_VALID = 1;
	
	private String transactionCode;
	private String transactionDate;
	private int transactionNumber;
	private int transactionTypeId;
	private long transactionId;
	private Integer clientId;
	private double total;
	private double discount;
	private double taxes;
	private double subtotal;
	private double quantity;
	private double subtotalBeforeTax;
	private String clientFullName;
	private String clientCardId;	
	private String detailResume;
	private String transactionTypeName;
	private int state;
	
	private TransactionType transactionType;
	private List<TransactionDetail> transactionDetails;
	private Cursor transactionDetailCursor;		
	
	private Client client;
	
	public boolean isEmpty(){
		return this.getTransactionDetails().size() == 0 && (clientId == null || clientId == 0);
	}
	
	public int getTransactionTypeId() {
		return transactionTypeId;
	}
	public void setTransactionTypeId(int transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}
	public long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}	
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public Date getTransactionDate() {
		return Format.getDateFromDataBaseString(transactionDate);
	}
	public String getTransactionDateString() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public void setTransactionDate(Date date) {
		this.transactionDate = Format.getDataBaseString(date);
	}
	public int getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public Integer getClientId() {
		return clientId;
	}
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getClientFullName() {
		return clientFullName;
	}
	public void setClientFullName(String clientFullName) {
		this.clientFullName = clientFullName;
	}
	public String getClientCardId() {
		return clientCardId;
	}
	public void setClientCardId(String clientCardId) {
		this.clientCardId = clientCardId;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public double getTaxes() {
		return taxes;
	}
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}	
	public double getQuantity() {
		return quantity;
	}
	public void setSubtotalBeforeTax(double subtotalBeforeTax) {
		this.subtotalBeforeTax = subtotalBeforeTax;
	}	
	public double getSubtotalBeforeTax() {
		return subtotalBeforeTax;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getDetailResume() {
		return detailResume;
	}
	public void setDetailResume(String detailResume) {
		this.detailResume = detailResume;
	}	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
		if(transactionType!=null){
			setTransactionTypeId(transactionType.getTransactionTypeId());
			setTransactionTypeName(transactionType.getName());
		}
		else{
			setTransactionTypeId(0);
			setTransactionTypeName(null);
		}
	}
	public String getTransactionTypeName() {
		return transactionTypeName;
	}
	public void setTransactionTypeName(String transactionTypeName) {
		this.transactionTypeName = transactionTypeName;
	}	
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
		if(client!=null){
			setClientId(client.getId());
			setClientFullName(client.getFullName());
			setClientCardId(client.getCardId());
		}
		else{
			setClientId(null);
			setClientFullName(null);
			setClientCardId(null);
		}
	}
	public List<TransactionDetail> getTransactionDetails() {
		if(transactionDetails == null) transactionDetails = new ArrayList<TransactionDetail>();
		return transactionDetails;
	}
	public void setTransactionDetails(List<TransactionDetail> transactionDetails) {
		this.transactionDetails = transactionDetails;
	}
	public Cursor getTransactionDetailCursor() {
		return transactionDetailCursor;
	}
	public void setTransactionDetailCursor(Cursor transactionDetailCursor) {
		this.transactionDetailCursor = transactionDetailCursor;
	}	
	
	public void addDetail(TransactionDetail newDetail){
		TransactionDetail old = findDetailByProductId(newDetail.getProductId());
		if(old!=null){
			old.setQuantity( old.getQuantity() + newDetail.getQuantity());
			old.calculate();
		}
		else
		{
			getTransactionDetails().add(newDetail);
			newDetail.calculate();
		}
	}
	
	private TransactionDetail findDetailByProductId(long l){
		TransactionDetail result = null;
		for(TransactionDetail detail : transactionDetails){
			if(detail.getProductId() == l) {
				result = detail;
				break;
			}
		}
		return result;
	}
	
	public void calculate(){
		quantity = 0;
		discount = 0;
		subtotal = 0;
		subtotalBeforeTax = 0;
		
		StringBuilder resume = new StringBuilder();
		for(TransactionDetail detail : transactionDetails){
			quantity += detail.getQuantity();
			subtotal += detail.getSubtotal();
			discount += detail.getDiscount();
			subtotalBeforeTax += detail.getSubtotalBeforeTax();
			total += detail.getTotal();
			
			resume.append(Format.getDefaultNumberFormat(detail.getQuantity()) + " " + detail.getProduct().getDescription() + ", ");
		}
		if(resume.length()>0)
			setDetailResume( resume.substring(0,resume.length()-2) );		
		else
			setDetailResume( null );
		
		taxes = subtotalBeforeTax * 0.12;
		total = subtotalBeforeTax + taxes;
	}
	
	public static Transaction getTransaction(DataBase db, long transactionId, boolean includeDetail)
	{		
		String query = Contract.Transaction.QUERY_TRANSACTION_BY_ID;
		 
		Cursor c = db.rawQuery(query, transactionId );
		Transaction transaction = null;
		if(c.moveToFirst())
		{
			transaction =  new Transaction();
			transaction.setTransactionCode( CursorUtils.getString(c,Contract.Transaction.FIELD_TRANSACTION_CODE) );
			transaction.setClientFullName( CursorUtils.getString(c,Contract.Transaction.FIELD_CLIENT_NAMES) );
			transaction.setClientCardId( CursorUtils.getString(c,Contract.Transaction.FIELD_CLIENT_CARDID) );
			transaction.setTotal( CursorUtils.getDouble(c,Contract.Transaction.FIELD_TOTAL) );
			transaction.setTransactionDate( CursorUtils.getString(c,Contract.Transaction.FIELD_TRANSACTIONDATE) );
			transaction.setQuantity( CursorUtils.getDouble(c,Contract.Transaction.FIELD_QUANTITY) );
			transaction.setTaxes(CursorUtils.getDouble(c,Contract.Transaction.FIELD_TAXES) );
			transaction.setDiscount(CursorUtils.getDouble(c,Contract.Transaction.FIELD_DISCOUNT) );
			transaction.setSubtotal(CursorUtils.getDouble(c,Contract.Transaction.FIELD_SUBTOTAL) );
			transaction.setSubtotalBeforeTax(CursorUtils.getDouble(c,Contract.Transaction.FIELD_SUBTOTALBEFORETAXES) );
			
			transaction.setTransactionId( CursorUtils.getLong(c,Contract.Transaction._ID) );
			transaction.setTransactionTypeId( CursorUtils.getInt(c,Contract.Transaction.FIELD_TRANSACTIONTYPEID) );
			
			transaction.setDetailResume( CursorUtils.getString(c,Contract.Transaction.FIELD_DETAIL_RESUME) );
			
			TransactionType type = new TransactionType();
			type.setTransactionTypeId(transaction.getTransactionTypeId());
			type.setName( CursorUtils.getString(c,Contract.Transaction.FIELD_TRANSACTIONTYPE_DESCRIPTION) );
			
			transaction.setTransactionType(type);
			
			if(includeDetail)
			{
				List<TransactionDetail> detail = TransactionDetail.getTransactionDetails(db, transactionId);
				transaction.setTransactionDetails(detail);
			}
		}
		return transaction;
	}
	
	public static int getNextTransactionNumber(DataBase db,int transactionTypeId){
		int value = 0;
		value = db.queryMaxInt(Contract.Transaction.TABLE_NAME, 
				Contract.Transaction.COLUMN_TRANSACTIONNUMBER,
				Contract.Transaction.COLUMN_TRANSACTIONTYPEID + " = ?",
				transactionTypeId);
		
		return value;
	}
	public static boolean insertOrUpdate(DataBase db, Transaction t){
		
		if(t.getTransactionId() == 0){
			if(!t.isEmpty())
				return insert(db, t);
			else
				return true;
		}
		else
			return update(db, t);
	}
	
	public static boolean insert(DataBase db, Transaction t){		
		boolean result = false;
		db.beginTransaction();
		
		try
		{				
			ContentValues values = new ContentValues();
			
			t.setTransactionNumber(getNextTransactionNumber(db, t.getTransactionTypeId()));
			
			setTransactionContentValues(t, values);
			
			db.insert(Contract.Transaction.TABLE_NAME, values);
			
			long _idTransaction = db.getLongLastInsertRowId();
			t.setTransactionId(_idTransaction);
			
			values = new ContentValues();
			values.put(Contract.TransactionExt.COLUMN_ID, _idTransaction);
			
			setTransactionExtContentValues(t, values);
			
			db.insert(Contract.TransactionExt.TABLE_NAME, values);
			
			for(TransactionDetail d : t.getTransactionDetails()){
				d.setTransactionId(_idTransaction);
				if(!TransactionDetail.insertFromActiveTransaction(db, d))
				{					
					if(BuildConfig.DEBUG)
						Log.e(t.getClass().getName(), "Error inserting transaction detail");
					throw new Exception("Error inserting transaction detail");
				}
			}
										
			db.commitTransaction();
			
			result = true;
			
		}catch(Exception ex)
		{
			if(BuildConfig.DEBUG)
				Log.e(t.getClass().getName(), "Error inserting transaction");
			result = false;
		}
		finally
		{			
			db.endTransaction();
		}
		
		return result;
	}
	
	public static boolean update(DataBase db, Transaction t){
		db.beginTransaction();
		boolean result = false;
		try
		{
			ContentValues values =  new ContentValues();
			setTransactionContentValues(t, values);
			
			db.update(Contract.Transaction.TABLE_NAME, values, t.getTransactionId());
			
			values = new ContentValues();			
			
			setTransactionExtContentValues(t, values);
			
			db.update(Contract.TransactionExt.TABLE_NAME, values, 
					Contract.TransactionExt.COLUMN_ID + " = ?",t.getTransactionId());
			
			for(TransactionDetail d : t.getTransactionDetails()){		
				boolean success = false;
				if(d.getTransactionDetailId() == 0)
				{
					d.setTransactionId(t.getTransactionId());
					success = TransactionDetail.insertFromActiveTransaction(db, d);
				}
				else
					success = TransactionDetail.updateFromActiveTransaction(db, d);
				
				if(!success)
				{					
					if(BuildConfig.DEBUG)
						Log.e(t.getClass().getName(), "Error updating transaction detail");
					throw new Exception("Error updating transaction detail");
				}
			}
										
			db.commitTransaction();
			
			result = true;
			
		}catch(Exception ex){
			result = false;
			db.rollBackTransaction();
		}
		finally{
			db.endTransaction();
		}
		return result;
	}
	
	public static boolean delete(DataBase db, long transactionId){
		if(transactionId == 0) return true;
		
		db.beginTransaction();
		boolean result = false;
		try
		{
			TransactionDetail.deleteAllFromActiveTransaction(db, transactionId);			
			db.delete(Contract.Transaction.TABLE_NAME, transactionId);
										
			db.commitTransaction();			
			result = true;
			
		}catch(Exception ex){
			result = false;
			db.rollBackTransaction();
		}
		finally{
			db.endTransaction();
		}
		return result;
	}
	
	private static void setTransactionContentValues(Transaction t,ContentValues values){
		values.put(Contract.Transaction.COLUMN_CLIENTID, t.getClientId());
		values.put(Contract.Transaction.COLUMN_DISCOUNT, t.getDiscount());
		values.put(Contract.Transaction.COLUMN_QUANTITY, t.getQuantity());
		values.put(Contract.Transaction.COLUMN_SUBTOTAL, t.getSubtotal());
		values.put(Contract.Transaction.COLUMN_SUBTOTALBEFORETAXES, t.getSubtotalBeforeTax());
		values.put(Contract.Transaction.COLUMN_TAXES, t.getTaxes());
		values.put(Contract.Transaction.COLUMN_TOTAL, t.getTotal());
		values.put(Contract.Transaction.COLUMN_TRANSACTIONDATE, t.getTransactionDateString());
		values.put(Contract.Transaction.COLUMN_TRANSACTIONNUMBER, t.getTransactionNumber());
		values.put(Contract.Transaction.COLUMN_TRANSACTIONTYPEID, t.getTransactionTypeId());
		values.put(Contract.Transaction.COLUMN_STATE, t.getState());
	}
	
	private static void setTransactionExtContentValues(Transaction t,ContentValues values){
		
		values.put(Contract.TransactionExt.COLUMN_CLIENT_CARID, t.getClientCardId());
		values.put(Contract.TransactionExt.COLUMN_CLIENT_NAMES, t.getClientFullName());
		values.put(Contract.TransactionExt.COLUMN_CODE, t.getTransactionCode());
		values.put(Contract.TransactionExt.COLUMN_DETAIL_RESUME, t.getDetailResume());
		values.put(Contract.TransactionExt.COLUMN_TRANSACTIONTYPE_NAME, t.getTransactionTypeName());
	}
	
	public static Cursor getTransactionSearchCursor(DataBase db, String termSearch)
	{		
		String query = Contract.Transaction.QUERY_TRANSACTION_SEARCH;
		
		Cursor c = db.rawQuery(query, "*" + termSearch + "*" );		
		return c;
	}
	
	public static Cursor getTransactionCursor(DataBase db, int transactionType)
	{		
		Cursor c = null;
		String query = null;
		if(transactionType != 0){			
			query = Contract.Transaction.QUERY_TRANSACTION_BY_TYPE;
			c = db.rawQuery(query, transactionType );
		}else
		{
			query = Contract.Transaction.QUERY_TRANSACTION;
			c = db.rawQuery(query);
		}
		return c;
	}
	
	
}
