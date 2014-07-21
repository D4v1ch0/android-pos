package rp3.pos.transaction;

import rp3.pos.R;
import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.TransactionDetail;
import rp3.pos.transaction.TransactionEditItemFragment.TransactionEditItemListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class TransactionEditItemActivity extends rp3.app.BaseActivity implements TransactionEditItemListener {

	public static final String EXTRA_TRANSACTION_DETAIL_ID = "transactionDetailId";
	private long transactionDetailId = 0;
	
	TransactionEditItemFragment transactionEditItemFragment;
	
	public static Intent newIntent(Context c, long transactionDetailId){
		Intent intent = new Intent(c,TransactionEditItemActivity.class);
		intent.putExtra(TransactionEditItemActivity.EXTRA_TRANSACTION_DETAIL_ID, transactionDetailId);
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		super.setDataBaseParameters(DbOpenHelper.class);
		
		setContentView(R.layout.activity_transaction_edit_item, R.menu.activity_transaction_edit_item);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		if(intent!=null && intent.hasExtra(EXTRA_TRANSACTION_DETAIL_ID))
			transactionDetailId = intent.getLongExtra(EXTRA_TRANSACTION_DETAIL_ID, 0);
				
				
		if(getCurrentFragmentManager().findFragmentById(R.id.content_transaction_edit_item)==null){
			transactionEditItemFragment = TransactionEditItemFragment.newInstance(transactionDetailId);			
			setFragment(R.id.content_transaction_edit_item, transactionEditItemFragment);
		}
	}
			
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_discard:
			
			transactionEditItemFragment.beginDeleteDetail();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemEditAcceptAction(TransactionDetail transactionDetail) {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onItemEditCancelAction(TransactionDetail transactionDetail) {
		setResult(RESULT_CANCELED);
		finish();
	}
		
	@Override
	public void onItemEditDiscardAction(TransactionDetail transactionDetail) {		
		setResult(RESULT_OK);
		finish();
	}
}
