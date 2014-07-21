package rp3.pos;

import rp3.pos.db.DbOpenHelper;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class TransactionDetailItemActivity extends rp3.app.BaseActivity {

	public final static String EXTRA_TRANSACTION_DETAIL_ID = "transactionDetailId";
	private long transactionDetailId = 0;
	
	public static Intent newIntent(Context c, long transactionDetailId){
		Intent intent = new Intent(c, TransactionDetailItemActivity.class);
		intent.putExtra(EXTRA_TRANSACTION_DETAIL_ID, transactionDetailId);
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		super.setDataBaseParameters(DbOpenHelper.class);
		
		setContentView(R.layout.activity_transaction_detail_item);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		if(intent!=null && intent.hasExtra(EXTRA_TRANSACTION_DETAIL_ID))
			transactionDetailId = intent.getLongExtra(EXTRA_TRANSACTION_DETAIL_ID, 0);
				
		
		FragmentManager fm = getFragmentManager();
		if(fm.findFragmentById(R.id.content_transaction_detail_item)==null){
			getCurrentFragmentManager().beginTransaction().replace(R.id.content_transaction_detail_item,
					TransactionDetailItemFragment.newInstance(transactionDetailId,false) ).commit();
		}
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}				
	}
	
}
