package rp3.pos;

import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.Transaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TransactionDetailActivity extends rp3.app.BaseActivity implements TransactionDetailFragment.TransactionDetailListener {

	private long transactionId;
	private final String STATE_TRANSACTIONID = "transactionId";
	private TransactionDetailFragment transactionDetailFragment;
	
	public final static String EXTRA_TRANSACTIONID = "transactionId";
	
	public static Intent newIntent(Context c, long id){
		Intent intent = new Intent(c, TransactionDetailActivity.class);
		intent.putExtra(EXTRA_TRANSACTIONID, id);
//		intent.putExtra(TransactionDetailFragment.ARG_PARENT_SOURCE, 
//        		TransactionDetailFragment.PARENT_SOURCE_LIST);        
        return intent;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        setDataBaseParameters(DbOpenHelper.class);               
        
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {            
            
            transactionId = getIntent().getLongExtra(EXTRA_TRANSACTIONID,0);                                    
            transactionDetailFragment = TransactionDetailFragment.newInstance(transactionId);            
            
            getCurrentFragmentManager().beginTransaction()
                    .add(R.id.content_transaction_detail, transactionDetailFragment)
                    .commit();
        }
        else{
        	transactionId = savedInstanceState.getLong(STATE_TRANSACTIONID);
        	transactionDetailFragment = (TransactionDetailFragment)getCurrentFragmentManager().
        			findFragmentById(R.id.content_transaction_detail);
        }
    }    
    
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	// TODO Auto-generated method stub
    	super.onSaveInstanceState(outState);
    	outState.putLong(STATE_TRANSACTIONID,transactionId);    	
    }    
    
    @Override
	public void onDeleteSuccess(Transaction transaction) {		
		startActivity(new Intent(this,TransactionListActivity.class));
		finish();
	}
    
}
