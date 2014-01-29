package rp3.pos.product;

import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import rp3.app.SearchActivity;
import rp3.pos.R;
import rp3.pos.db.Contract;
import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.Product;

public class ProductSearchActivity extends SearchActivity {

	SimpleCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setDataBaseParameters(DbOpenHelper.class);		
		
		String[] product_fields = {
		         Contract.Product.FIELD_SKU,
		         Contract.Product.FIELD_NAME
		         };

		int[] product_toViews = {
		    		 R.id.textView_product_code, 
		    		 R.id.textView_product_name};
		
		adapter = new SimpleCursorAdapter(this, 
	   	        R.layout.rowlist_transaction_product_search, null, product_fields, product_toViews,0);
		
		setQueryHint(getText(R.string.hint_search_product));	
		setAdapter(adapter);						
	}
	
	@Override
	protected boolean onQueryTextChange(String newText) {
		adapter.changeCursor(
				Product.getProductSearchCursor(getDataBase(), newText));
		
		return true;
	}
	
	@Override
	protected boolean onQueryTextSubmit(String query) {
		adapter.changeCursor(
				Product.getProductSearchCursor(getDataBase(), query));
		
		return true;	
	}
	
}
