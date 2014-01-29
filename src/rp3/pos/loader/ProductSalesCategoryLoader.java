package rp3.pos.loader;

import java.util.List;

import rp3.db.sqlite.DataBase;
import rp3.pos.model.ProductSalesCategory;
import android.content.Context;

public class ProductSalesCategoryLoader extends
		rp3.content.SimpleObjectLoader<List<ProductSalesCategory>> {

	private DataBase db;
	private int salesCategoryId;

	public ProductSalesCategoryLoader(Context context, DataBase db,
			int salesCategoryId) {
		super(context);
		this.db = db;
		this.salesCategoryId = salesCategoryId;
	}

	@Override
	public List<ProductSalesCategory> loadInBackground() {
		List<ProductSalesCategory> result = null;
		if (this.salesCategoryId == 0)
			result = ProductSalesCategory.getFavorites(db);
		else
			result = ProductSalesCategory.getProductSalesCategoryByCategory(db,
					salesCategoryId);
		return result;
	}
	
}