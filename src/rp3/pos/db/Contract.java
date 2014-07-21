package rp3.pos.db;

import android.provider.BaseColumns;
import rp3.db.sqlite.DataBase;

public final class Contract {

	public static abstract class ProductExt implements BaseColumns {
        public static final String TABLE_NAME = "tbProductExt";
        public static final String COLUMN_ID = "docid";
        public static final String COLUMN_SKU = "Sku";
        public static final String COLUMN_DESCRIPTION = "Description";                        
    }
	
	public static abstract class SalesCategory implements BaseColumns{
		public static final String TABLE_NAME = "tbSalesCategory";        
        
		public static final String COLUMN_NAME = "Name";
		public static final String COLUMN_ORDER = "Position";
		
		public static final String FIELD_NAME = COLUMN_NAME;
		public static final String FIELD_ORDER = COLUMN_ORDER;
	}
	
	public static abstract class ProductSalesCategory implements BaseColumns{
		public static final String TABLE_NAME = "tbProductSalesCategory";                
		
		public static final String COLUMN_PRODUCTID = "ProductId";
		public static final String COLUMN_SALESCATEGORYID = "SalesCategoryId";
		public static final String COLUMN_POSITION = "Position";
		public static final String COLUMN_ISFAVORITE = "IsFavorite";
		public static final String COLUMN_FAVORITEPOSITION = "FavoritePosition";
		
		public static final String FIELD_PRODUCTID = COLUMN_PRODUCTID;
		public static final String FIELD_SALESCATEGORYID = COLUMN_SALESCATEGORYID;
		public static final String FIELD_POSITION = COLUMN_POSITION;
		public static final String FIELD_ISFAVORITE = COLUMN_ISFAVORITE;
		public static final String FIELD_FAVORITEPOSITION = COLUMN_FAVORITEPOSITION;
		
		public static final String FIELD_PRODUCT_DESCRIPTION = Contract.ProductExt.TABLE_NAME + "_" + Contract.ProductExt.COLUMN_DESCRIPTION;
		public static final String FIELD_PRODUCT_SKU = Contract.ProductExt.TABLE_NAME + "_" + Contract.ProductExt.COLUMN_SKU;
		public static final String FIELD_PRODUCT_PRICE = Contract.Product.TABLE_NAME + "_" + Contract.Product.COLUMN_PRICE;
		
		public static final String QUERY_PRODUCT_SALESCATEGORY_BY_CATEGORY = "ProductSalesCategoryById";
		public static final String QUERY_PRODUCT_FAVORITE_CATEGORY = "ProductFavoriteCategory";
		
	}
	
	/* Inner class that defines the table contents */
    public static abstract class Product implements BaseColumns {
        public static final String TABLE_NAME = "tbProduct";        
        public static final String COLUMN_PRICE = "Price";
        
        public static final String FIELD_PRICE = COLUMN_PRICE;        
        public static final String FIELD_NAME = ProductExt.TABLE_NAME + "_" + ProductExt.COLUMN_DESCRIPTION;
        public static final String FIELD_SKU = ProductExt.TABLE_NAME + "_" + ProductExt.COLUMN_SKU;
        
//        private static final String ALIAS_RANKCOLUMN = "rank";
//        private static final String ALIAS_QUERY_SEARCH_SUB = "tbProductSearch";

        public static final String QUERY_PRODUCT_SEARCH = "SimpleProductSearch";
        public static final String QUERY_PRODUCT_BY_ID = "ProductById";
        		
//        private static final String QUERY_PRODUCT_SEARCH_SUB = 		
//        		"SELECT " +
//        		ProductExt.TABLE_NAME + "." + ProductExt.COLUMN_ID + DataBase.COMMA_SEP +
//        		"matchinfo( " + ProductExt.TABLE_NAME + ") AS " + ALIAS_RANKCOLUMN +
//        		" FROM " + ProductExt.TABLE_NAME + 
//        		" WHERE " + ProductExt.TABLE_NAME + " MATCH ? " +
//        		" ORDER BY rank DESC LIMIT 30 OFFSET 0 ";	
//        
//        private static final String QUERY_PRODUCT_MAIN_FIELDS = 
//        		ProductExt.TABLE_NAME + "." + ProductExt.COLUMN_ID + " AS " + ProductExt._ID + DataBase.COMMA_SEP +
//        		ProductExt.TABLE_NAME + "." + ProductExt.COLUMN_SKU + " AS " +
//        			FIELD_SKU + DataBase.COMMA_SEP +
//        		ProductExt.TABLE_NAME + "." + ProductExt.COLUMN_DESCRIPTION + " AS " +
//        			FIELD_NAME;
//        
//        public static final String QUERY_PRODUCT_SEARCH = 
//        		"SELECT " +        		        		        				
//        		QUERY_PRODUCT_MAIN_FIELDS +
//        		" FROM " + ProductExt.TABLE_NAME + 
//        		" INNER JOIN (" + QUERY_PRODUCT_SEARCH_SUB + ") AS " + ALIAS_QUERY_SEARCH_SUB +
//        		" ON " + ProductExt.TABLE_NAME + "." + ProductExt.COLUMN_ID + " = " + 
//        				 ALIAS_QUERY_SEARCH_SUB + "." + ProductExt.COLUMN_ID +
//        		" ORDER BY " + ALIAS_QUERY_SEARCH_SUB + "." + ALIAS_RANKCOLUMN + " DESC";
    }      
    
    public static abstract class ClientExt {
    	public static final String TABLE_NAME = "tbClientExt";
    	public static final String COLUMN_ID = "docid";
    	public static final String COLUMN_CARID = "CardId";
        public static final String COLUMN_NAMES = "Names";
        public static final String COLUMN_LASTNAMES = "LastNames";  
    }
    
    public static abstract class Client implements BaseColumns {
        public static final String TABLE_NAME = "tbClient";
        public static final String COLUMN_ADDRESS = "Address";        
        
        public static final String FIELD_FULLNAME = "Fullname";
        
        public static final String FIELD_ADDRESS = COLUMN_ADDRESS;
        
        public static final String FIELD_CARDID = ClientExt.TABLE_NAME + ClientExt.COLUMN_CARID;
        public static final String FIELD_LASTNAMES = ClientExt.TABLE_NAME + ClientExt.COLUMN_LASTNAMES;
        public static final String FIELD_NAMES = ClientExt.TABLE_NAME + ClientExt.COLUMN_NAMES;
        
        private static final String ALIAS_RANKCOLUMN = "rank";
        private static final String ALIAS_QUERY_SEARCH_SUB = "tbProductSearch";
        
        public static final String QUERY_CLIENT_BY_ID = 
        		"SELECT " +        		        		        				
        		Client.TABLE_NAME + "." + Client._ID + " AS " + Client._ID + DataBase.COMMA_SEP +
        		ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_CARID + " AS " + FIELD_CARDID + DataBase.COMMA_SEP +
        		ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_LASTNAMES + " AS " + 
        			FIELD_LASTNAMES + DataBase.COMMA_SEP +
        		ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_NAMES + " AS " + 
        			FIELD_NAMES + DataBase.COMMA_SEP +        			
        		Client.TABLE_NAME + "." + Client.COLUMN_ADDRESS + " AS " + Client.COLUMN_ADDRESS +
        		" FROM " + Client.TABLE_NAME +
        		" INNER JOIN " + ClientExt.TABLE_NAME +
        		" ON " + Client.TABLE_NAME + "." + Client._ID + " = " + ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_ID +
        		" WHERE " + Client.TABLE_NAME + "." + Client._ID + " = ?"; 
        
        private static final String QUERY_CLIENT_SEARCH_SUB = 		
        		"SELECT " +
        		ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_ID + DataBase.COMMA_SEP +
        		"matchinfo( " + ClientExt.TABLE_NAME + ") AS " + ALIAS_RANKCOLUMN +
        		" FROM " + ClientExt.TABLE_NAME + 
        		" WHERE " + ClientExt.TABLE_NAME + " MATCH ? " +
        		" ORDER BY rank DESC LIMIT 30 OFFSET 0 ";	
        
        public static final String QUERY_CLIENT_SEARCH = 
        		"SELECT " +        		        		        				
        		ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_ID + " AS " + Client._ID + DataBase.COMMA_SEP +
        		ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_CARID + " AS " + Client.FIELD_CARDID + DataBase.COMMA_SEP +
        		ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_NAMES  + " || ' ' || " + ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_LASTNAMES + 
        			" AS " +  Client.FIELD_FULLNAME +        		
        		" FROM " + ClientExt.TABLE_NAME + 
        		" INNER JOIN (" + QUERY_CLIENT_SEARCH_SUB + ") AS " + ALIAS_QUERY_SEARCH_SUB +
        		" ON " + ClientExt.TABLE_NAME + "." + ClientExt.COLUMN_ID + " = " + 
        				 ALIAS_QUERY_SEARCH_SUB + "." + ClientExt.COLUMN_ID +
        		" ORDER BY " + ALIAS_QUERY_SEARCH_SUB + "." + ALIAS_RANKCOLUMN + " DESC";
    }
    
    public static abstract class TransactionType implements BaseColumns {
        public static final String TABLE_NAME = "tbTransactionType";         
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_ACTIVE = "Active";
        public static final String COLUMN_DEFAULT = "IsDefault";
        public static final String COLUMN_PRODUCT_INSERT_MODE_PREFERENCE = "ProductoInsertModePreference";
        
        public static final String FIELD_NAME = COLUMN_NAME;
        public static final String FIELD_ACTIVE = COLUMN_ACTIVE;
        public static final String FIELD_DEFAULT = COLUMN_DEFAULT;
        public static final String FIELD_PRODUCT_INSERT_MODE_PREFERENCE = COLUMN_PRODUCT_INSERT_MODE_PREFERENCE;
    }
            
    public static abstract class TransactionExt implements BaseColumns {
    	public static final String TABLE_NAME = "tbTransactionExt";
    	public static final String COLUMN_ID = "docid";
    	public static final String COLUMN_CODE = "Code";
    	public static final String COLUMN_CLIENT_NAMES = "ClientNames";
        public static final String COLUMN_CLIENT_CARID = "ClientCardId";
        public static final String COLUMN_DETAIL_RESUME = "DetailResume";
        public static final String COLUMN_TRANSACTIONTYPE_NAME = "TransactionTypeName";
    }
    
    public static abstract class TransactionDetail implements BaseColumns {
    	public static final String TABLE_NAME = "tbTransactionDetail";
    	
    	public static final String COLUMN_TRANSACTIONID = "TransactionId";    	
        public static final String COLUMN_PRODUCTID = "ProductId";
        public static final String COLUMN_QUANTITY = "Quantity";
        public static final String COLUMN_DISCOUNT = "Discount";
        public static final String COLUMN_TAX = "Taxes";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_SUBTOTALBEFORETAXES = "SubtotalBeforeTaxes";
        public static final String COLUMN_SUBTOTAL = "Subtotal";
        public static final String COLUMN_TOTAL = "Total";        
        
        public static final String FIELD_TRANSACTIONID = COLUMN_TRANSACTIONID;    	
        public static final String FIELD_PRODUCTID = COLUMN_PRODUCTID;
        public static final String FIELD_QUANTITY = COLUMN_QUANTITY;
        public static final String FIELD_PRICE = COLUMN_PRICE;
        public static final String FIELD_DISCOUNT = COLUMN_DISCOUNT;        
        public static final String FIELD_TAX = COLUMN_TAX;
        public static final String FIELD_SUBTOTALBEFORETAXES = COLUMN_SUBTOTALBEFORETAXES;
        public static final String FIELD_SUBTOTAL = COLUMN_SUBTOTAL;
        public static final String FIELD_TOTAL = COLUMN_TOTAL;
        
        public static final String FIELD_PRODUCT_SKU = ProductExt.TABLE_NAME + ProductExt.COLUMN_SKU;
        public static final String FIELD_PRODUCT_DESCRIPTION = ProductExt.TABLE_NAME + ProductExt.COLUMN_DESCRIPTION;
        
        private static final String QUERY_TRANSACTIONDET_MAIN = 
        		"SELECT " +
        		TABLE_NAME + "." + _ID + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_TRANSACTIONID + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_PRODUCTID + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_QUANTITY + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_PRICE + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_SUBTOTAL + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_DISCOUNT + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_SUBTOTALBEFORETAXES + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_TAX + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_TOTAL + DataBase.COMMA_SEP +        		
        		ProductExt.TABLE_NAME + "." + ProductExt.COLUMN_SKU + " AS " +
        			FIELD_PRODUCT_SKU + DataBase.COMMA_SEP +
        		ProductExt.TABLE_NAME + "." + ProductExt.COLUMN_DESCRIPTION + " AS " +
        			FIELD_PRODUCT_DESCRIPTION +
        		" FROM " + TABLE_NAME + 
        		" INNER JOIN " + Product.TABLE_NAME + 
        		" ON " + TABLE_NAME + "." + COLUMN_PRODUCTID + " = " + Product.TABLE_NAME + "." + Product._ID +
        		" INNER JOIN " + ProductExt.TABLE_NAME + 
        		" ON " + Product.TABLE_NAME + "." + Product._ID + " = " + ProductExt.TABLE_NAME + "." + ProductExt.COLUMN_ID;        		
        
        public static final String QUERY_TRANSACTIONDET = 
        		QUERY_TRANSACTIONDET_MAIN +
        		" WHERE " + TABLE_NAME + "." + COLUMN_TRANSACTIONID + " = ?";
        
        
        public static final String QUERY_TRANSACTIONDET_BY_ID =  
        		QUERY_TRANSACTIONDET_MAIN +
        		" WHERE " + TABLE_NAME + "." + _ID + " = ?";        		
    }
    
    public static abstract class Transaction implements BaseColumns {
        public static final String TABLE_NAME = "tbTransaction";
        private static final String ALIAS_QUERY_SEARCH_SUB = "tbTransactionSearch";
        private static final String ALIAS_RANKCOLUMN = "rank";
        
        public static final String COLUMN_TRANSACTIONTYPEID = "TransactionTypeId";
        public static final String COLUMN_TRANSACTIONDATE = "TransactionDate";
        public static final String COLUMN_TRANSACTIONNUMBER = "TransactionNumber";
        public static final String COLUMN_CLIENTID = "ClientId";
        public static final String COLUMN_SUBTOTALBEFORETAXES = "SubtotalBeforeTaxes";
        public static final String COLUMN_TOTAL = "Total";
        public static final String COLUMN_SUBTOTAL = "Subtotal";
        public static final String COLUMN_TAXES = "Taxes";
        public static final String COLUMN_DISCOUNT = "Discount";
        public static final String COLUMN_QUANTITY = "Quantity";
        public static final String COLUMN_STATE = "State";
        
        public static final String FIELD_TRANSACTIONTYPEID = COLUMN_TRANSACTIONTYPEID;
        public static final String FIELD_TRANSACTIONDATE = COLUMN_TRANSACTIONDATE;
        public static final String FIELD_TRANSACTIONNUMBER = COLUMN_TRANSACTIONNUMBER;
        public static final String FIELD_CLIENTID = COLUMN_CLIENTID;
        public static final String FIELD_SUBTOTALBEFORETAXES = COLUMN_SUBTOTALBEFORETAXES;
        public static final String FIELD_TOTAL = COLUMN_TOTAL;
        public static final String FIELD_SUBTOTAL = COLUMN_SUBTOTAL;
        public static final String FIELD_TAXES = COLUMN_TAXES;
        public static final String FIELD_DISCOUNT = COLUMN_DISCOUNT;
        public static final String FIELD_QUANTITY = COLUMN_QUANTITY;
        public static final String FIELD_STATE = COLUMN_STATE;
        
        public static final String FIELD_DETAIL_RESUME = Contract.TransactionExt.TABLE_NAME + TransactionExt.COLUMN_DETAIL_RESUME;
        public static final String FIELD_TRANSACTIONTYPE_DESCRIPTION = Contract.TransactionType.TABLE_NAME + Contract.TransactionType.COLUMN_NAME;
        public static final String FIELD_CLIENT_NAMES = TransactionExt.TABLE_NAME + TransactionExt.COLUMN_CLIENT_NAMES;
        public static final String FIELD_TRANSACTION_CODE = Contract.TransactionExt.TABLE_NAME + TransactionExt.COLUMN_CODE;
        public static final String FIELD_CLIENT_CARDID = Contract.TransactionExt.TABLE_NAME + Contract.TransactionExt.COLUMN_CLIENT_CARID;               
        
        private static final String QUERY_TRANSACTION_MAINFIELDS = 
        		"SELECT " + 
        				TABLE_NAME + "." + _ID + DataBase.COMMA_SEP +
        				TransactionExt.TABLE_NAME + "." + TransactionExt.COLUMN_CODE + " AS " +
        					FIELD_TRANSACTION_CODE + DataBase.COMMA_SEP +        				        				
        				TABLE_NAME + "." + COLUMN_TOTAL + DataBase.COMMA_SEP + 
        				TABLE_NAME + "." + COLUMN_TRANSACTIONDATE + DataBase.COMMA_SEP +          				
        				TABLE_NAME + "." + COLUMN_TRANSACTIONTYPEID + DataBase.COMMA_SEP +
        				TransactionExt.TABLE_NAME + "." + TransactionExt.COLUMN_DETAIL_RESUME + " AS " +
        					FIELD_DETAIL_RESUME + DataBase.COMMA_SEP +
        				Contract.TransactionType.TABLE_NAME + "." + Contract.TransactionType.COLUMN_NAME + " AS " + 
        					FIELD_TRANSACTIONTYPE_DESCRIPTION + DataBase.COMMA_SEP +
        				TransactionExt.TABLE_NAME + "." + TransactionExt.COLUMN_CLIENT_NAMES + " AS " +
        					FIELD_CLIENT_NAMES;
        
        private static final String QUERY_TRANSACTION_MAINFROM = 
        		" FROM " + TABLE_NAME +
        		" INNER JOIN " + Contract.TransactionExt.TABLE_NAME +
        		" ON " + TABLE_NAME + "." + Transaction._ID + " = " + 
        			Contract.TransactionExt.TABLE_NAME + "." + Contract.TransactionExt.COLUMN_ID +
        		" INNER JOIN " + Contract.TransactionType.TABLE_NAME +
        		" ON " + TABLE_NAME + "." + COLUMN_TRANSACTIONTYPEID + " = " + 
        			Contract.TransactionType.TABLE_NAME + "." + Contract.TransactionType._ID; //+
        
        private static final String QUERY_TRANSACTION_PARENT = 
        		QUERY_TRANSACTION_MAINFIELDS +
        		QUERY_TRANSACTION_MAINFROM;
        		
        		//" INNER JOIN " + Contract.Client.TABLE_NAME + 
        		//" ON " + TABLE_NAME + "." + COLUMN_CLIENTID + " = " + Contract.Client.TABLE_NAME + "." + Contract.Client._ID;
        
        private static final String QUERY_TRANSACTION_ADITIONALFIELDS = " " + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + Contract.Transaction.COLUMN_QUANTITY + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + Contract.Transaction.COLUMN_TAXES + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + Contract.Transaction.COLUMN_SUBTOTAL + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + Contract.Transaction.COLUMN_SUBTOTALBEFORETAXES + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + Contract.Transaction.COLUMN_DISCOUNT + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + Contract.Transaction.COLUMN_CLIENTID + DataBase.COMMA_SEP +        		
        		Contract.TransactionExt.TABLE_NAME + "." + Contract.TransactionExt.COLUMN_CLIENT_CARID + " AS " + 
        			FIELD_CLIENT_CARDID + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_TRANSACTIONNUMBER + DataBase.COMMA_SEP +
        		TABLE_NAME + "." + COLUMN_TRANSACTIONTYPEID;        		
        
        private static final String QUERY_FULL_FIELDS = 
        		QUERY_TRANSACTION_MAINFIELDS + 
        		QUERY_TRANSACTION_ADITIONALFIELDS;
        
        private static final String QUERY_TRANSACTION_FULL = 
        		QUERY_FULL_FIELDS;
        
        private static final String QUERY_ORDER_BY = 
        		" ORDER BY " + Transaction.COLUMN_TRANSACTIONDATE + " DESC";
        
        public static final String QUERY_TRANSACTION = 
        		QUERY_TRANSACTION_PARENT +
        		QUERY_ORDER_BY;
        
        private static final String QUERY_FILTER_TRANSACTIONID = " WHERE " +  
        		Contract.Transaction.TABLE_NAME + "." + Contract.Transaction._ID +
        		" = ?";
        
        private static final String QUERY_FILTER_TYPETRANSACTION = " WHERE " +  
        		Contract.TransactionType.TABLE_NAME + "." + Contract.TransactionType._ID +
        		" = ?";
        
        public static final String QUERY_TRANSACTION_BY_TYPE = 
        		QUERY_TRANSACTION_PARENT +
        		QUERY_FILTER_TYPETRANSACTION +
        		QUERY_ORDER_BY;
        
        public static final String QUERY_TRANSACTION_BY_ID = 
        		QUERY_TRANSACTION_FULL +
        		QUERY_TRANSACTION_MAINFROM + 
        		QUERY_FILTER_TRANSACTIONID;
        		
        private static final String QUERY_TRANSACTION_SEARCH_SUB = 		
        		"SELECT " +
        		TransactionExt.COLUMN_ID + DataBase.COMMA_SEP +
        		"matchinfo( " + TransactionExt.TABLE_NAME + ") AS " + ALIAS_RANKCOLUMN +
        		" FROM " + TransactionExt.TABLE_NAME + 
        		" WHERE " + TransactionExt.TABLE_NAME + " MATCH ? " +
        		" ORDER BY rank DESC LIMIT 30 OFFSET 0 ";	
        
        public static final String QUERY_TRANSACTION_SEARCH = 
        		QUERY_TRANSACTION_PARENT + 
        		" INNER JOIN (" + QUERY_TRANSACTION_SEARCH_SUB + ") AS " + ALIAS_QUERY_SEARCH_SUB +
        		" ON " + TransactionExt.TABLE_NAME + "." + TransactionExt.COLUMN_ID + " = " + 
        				 ALIAS_QUERY_SEARCH_SUB + "." + TransactionExt.COLUMN_ID +
        		" ORDER BY " + ALIAS_QUERY_SEARCH_SUB + "." + ALIAS_RANKCOLUMN + " DESC";
    }
    
}