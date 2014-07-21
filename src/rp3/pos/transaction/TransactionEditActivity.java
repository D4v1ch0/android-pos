package rp3.pos.transaction;

import rp3.pos.R;
import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.Product;
import rp3.pos.model.Transaction;
import rp3.pos.model.TransactionType;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TransactionEditActivity extends rp3.app.BaseActivity 
	implements TransactionEditExtensionFragment.TransactionEditExtensionListener {

	public static final String EXTRA_TRANSACTIONID = "rp3.pos.transactionId";
	public static final String EXTRA_TRANSACTIONTYPEID = "rp3.pos.transactionTypeId";
	
	static final String STATE_TRANSACTIONID = "transactionId";
	static final String STATE_TRANSACTIONTYPEID = "transactionTypeId";		
	
	private TransactionEditMainFragment transactionEditMainFragment;	
	private TransactionEditExtensionFragment transactionEditExtensionFragment;				
	
	private long transactionId;	
	private int transactionTypeId;					
	
	public static Intent newIntent(Context c, long transactionId){
		Intent intent = new Intent(c,TransactionEditActivity.class);
		intent.putExtra(TransactionEditActivity.EXTRA_TRANSACTIONID, transactionId);
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setDataBaseParameters(DbOpenHelper.class);
		setContentView(R.layout.activity_transaction_edit);
		
		setupActionBar();
				
		Intent intent = getIntent();	
		if(intent.hasExtra(EXTRA_TRANSACTIONID)){
			transactionId = intent.getLongExtra(EXTRA_TRANSACTIONID, 0);
		}
		if(intent.hasExtra(EXTRA_TRANSACTIONTYPEID)){
			transactionTypeId = intent.getIntExtra(EXTRA_TRANSACTIONTYPEID, 0);
		} 
												
		if(savedInstanceState!=null){
			transactionId = savedInstanceState.getLong(STATE_TRANSACTIONID);
			transactionTypeId = savedInstanceState.getInt(STATE_TRANSACTIONTYPEID);			
			
			transactionEditMainFragment = ((TransactionEditMainFragment) getCurrentFragmentManager()
					.findFragmentById(R.id.content_transaction_edit_main));
		}
		else
		{			
			if(transactionTypeId==0 && transactionId != 0){
				Transaction t = Transaction.getTransaction(getDataBase(), transactionId, false);
				if(t!=null){
					transactionTypeId = t.getTransactionTypeId();
				}
			}			
					
			beginSetFragment();
			/*Main Fragment*/
			transactionEditMainFragment = TransactionEditMainFragment.newInstance(transactionId, transactionTypeId);			
			setFragment(R.id.content_transaction_edit_main, transactionEditMainFragment);
			
			/*Extension Fragment*/
			if(findViewById(R.id.content_transaction_edit_extension)!=null){
				transactionEditExtensionFragment = TransactionEditExtensionFragment.newInstance();				
				
				setFragment(R.id.content_transaction_edit_extension, transactionEditExtensionFragment);											
			}
			
			endSetFragment();										
		}
		
		this.setTitle(TransactionType.getTransactionType(getDataBase(), transactionTypeId).getName());			
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
			
	@Override
	protected void onSaveInstanceState(Bundle outState) {				
		super.onSaveInstanceState(outState);
		
		outState.putLong(STATE_TRANSACTIONID, transactionId);
		outState.putInt(STATE_TRANSACTIONTYPEID, transactionTypeId);				        
	}

	@Override
	public void addProduct(Product p) {
		transactionEditMainFragment.addProduct(p.getProductId(), p.getSku(), p.getDescription(), p.getPrice(), 1);
	}		
					
		
}
