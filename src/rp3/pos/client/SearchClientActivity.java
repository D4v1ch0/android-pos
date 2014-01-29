package rp3.pos.client;

import rp3.pos.R;
import rp3.pos.db.Contract;
import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.Client;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class SearchClientActivity extends rp3.app.SearchActivity {
	
	SimpleCursorAdapter client_adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setDataBaseParameters(DbOpenHelper.class);		
		
		String[] client_fields = {
		         Contract.Client.FIELD_CARDID,
		         Contract.Client.FIELD_FULLNAME
		         };

		int[] client_toViews = {
		    		 R.id.textView_client_cardid, 
		    		 R.id.textView_client_fullname};
		
		client_adapter = new SimpleCursorAdapter(this, 
	   	        R.layout.rowlist_client_search, null, client_fields, client_toViews,0);
		
		setQueryHint(getText(R.string.hint_search_client));	
		setAdapter(client_adapter);						
	}
	
	@Override
	protected boolean onQueryTextChange(String newText) {
		client_adapter.changeCursor(
				Client.getClientSearchCursor(getDataBase(), newText));
		
		return true;
	}
	
	@Override
	protected boolean onQueryTextSubmit(String query) {
		client_adapter.changeCursor(
				Client.getClientSearchCursor(getDataBase(), query));
		
		return true;	
	}
}
