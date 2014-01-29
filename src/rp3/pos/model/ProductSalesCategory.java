package rp3.pos.model;

import java.util.ArrayList;
import java.util.List;

import rp3.data.entity.EntityBase;
import rp3.db.QueryDir;
import rp3.db.sqlite.DataBase;
import rp3.pos.db.Contract;
import rp3.util.CursorUtils;
import android.database.Cursor;

public class ProductSalesCategory extends EntityBase<ProductSalesCategory> {

	private int productSalesCategoryId;
	private int salesCategoryId;
	private int productId;
	private short position;
	private boolean isFavorite;
	private short favoritePosition;
	private Product product;
	
	@Override
	public long getID() {
		return productSalesCategoryId;
	}
	
	@Override
	public String getTableName(){
		return Contract.ProductSalesCategory.TABLE_NAME;
	}
	
	@Override
	public void setValues() {
		clearValues();
		setValue(Contract.ProductSalesCategory.COLUMN_ISFAVORITE, isFavorite());
		setValue(Contract.ProductSalesCategory.COLUMN_POSITION, getPosition());
		setValue(Contract.ProductSalesCategory.COLUMN_FAVORITEPOSITION, getFavoritePosition());
		setValue(Contract.ProductSalesCategory.COLUMN_PRODUCTID, getProductId());
		setValue(Contract.ProductSalesCategory.COLUMN_SALESCATEGORYID, getSalesCategoryId());		
	}
	
	public int getProductSalesCategoryId() {
		return productSalesCategoryId;
	}
	public void setProductSalesCategoryId(int productSalesCategoryId) {
		this.productSalesCategoryId = productSalesCategoryId;
	}
	public int getSalesCategoryId() {
		return salesCategoryId;
	}
	public void setSalesCategoryId(int salesCategoryId) {
		this.salesCategoryId = salesCategoryId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public short getPosition() {
		return position;
	}
	public void setPosition(short position) {
		this.position = position;
	}
	public boolean isFavorite() {
		return isFavorite;
	}
	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
	public short getFavoritePosition() {
		return favoritePosition;
	}
	public void setFavoritePosition(short favoritePosition) {
		this.favoritePosition = favoritePosition;
	}
	
	public Product getProduct(){
		return product;
	}
	public void setProduct(Product product){
		this.product = product;
	}
	
	private static List<ProductSalesCategory> getListFromCursor(Cursor c){
		List<ProductSalesCategory> list = new ArrayList<ProductSalesCategory>();
		while(c.moveToNext()){
			ProductSalesCategory item = new ProductSalesCategory();
			
			item.setFavorite( CursorUtils.getBoolean(c, Contract.ProductSalesCategory.FIELD_ISFAVORITE) );
			item.setFavoritePosition( CursorUtils.getShort(c, Contract.ProductSalesCategory.FIELD_FAVORITEPOSITION) );
			item.setPosition( CursorUtils.getShort(c, Contract.ProductSalesCategory.FIELD_POSITION ) );
			item.setProductId( CursorUtils.getInt(c, Contract.ProductSalesCategory.FIELD_PRODUCTID ) );
			item.setSalesCategoryId( CursorUtils.getInt(c, Contract.ProductSalesCategory.FIELD_SALESCATEGORYID ) );
			item.setProductSalesCategoryId( CursorUtils.getInt(c, Contract.ProductSalesCategory._ID ) );
			
			Product p = new Product();
			p.setDescription( CursorUtils.getString(c, Contract.ProductSalesCategory.FIELD_PRODUCT_DESCRIPTION) );
			p.setPrice( CursorUtils.getDouble( c, Contract.ProductSalesCategory.FIELD_PRODUCT_PRICE));
			p.setSku( CursorUtils.getString( c, Contract.ProductSalesCategory.FIELD_PRODUCT_SKU));
			p.setProductId( item.getProductId() );
			
			item.setProduct(p);
			
			list.add(item);
		}
		
		return list;
	}
	
	public static List<ProductSalesCategory>  getFavorites(DataBase db){
		Cursor c = getProductFavoriteCursor(db);
		return getListFromCursor(c);
	}
	
	public static List<ProductSalesCategory> getProductSalesCategoryByCategory(DataBase db, int salesCategoryId){
		Cursor c = getProductSalesCategoryCursor(db,salesCategoryId);
		return getListFromCursor(c);
	}
	
	public static Cursor getProductSalesCategoryCursor(DataBase db,int salesCategoryId){
		return db.rawQuery( QueryDir.getQuery(Contract.ProductSalesCategory.QUERY_PRODUCT_SALESCATEGORY_BY_CATEGORY),salesCategoryId);
	}
	
	public static Cursor getProductFavoriteCursor(DataBase db){
		return db.rawQuery( QueryDir.getQuery(Contract.ProductSalesCategory.QUERY_PRODUCT_FAVORITE_CATEGORY));
	}

}