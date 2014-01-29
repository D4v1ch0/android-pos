package rp3.pos;

import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.Transaction;
import rp3.pos.transaction.TransactionEditActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TransactionDetailActivity extends rp3.app.BaseActivity implements TransactionDetailFragment.TransactionDetailListener {

	private long transactionId;
	private final String STATE_TRANSACTIONID = "transactionId";
	private TransactionDetailFragment transactionDetailFragment;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        setDataBaseParameters(DbOpenHelper.class);               
        
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {            
            
            transactionId = getIntent().getLongExtra(TransactionDetailFragment.ARG_ITEM_ID,0);                                    
            transactionDetailFragment = TransactionDetailFragment.newInstance(transactionId);            
            
            getFragmentManager().beginTransaction()
                    .add(R.id.content_transaction_detail, transactionDetailFragment)
                    .commit();
        }
        else{
        	transactionId = savedInstanceState.getLong(STATE_TRANSACTIONID);
        	transactionDetailFragment = (TransactionDetailFragment)getFragmentManager().
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
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	super.onCreateOptionsMenu(menu);
    	getMenuInflater().inflate(R.menu.activity_transaction_detail, menu);
    	/*
    	MenuItem discard = menu.findItem(R.id.action_discard);
    	MenuItem edit = menu.findItem(R.id.action_edit);
    	
    	discard.setVisible(true);
    	discard.setVisible(true);
    	*/
    	return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {    	
    	
    	switch(item.getItemId())
    	{
    		case android.R.id.home:
    			Intent home = new Intent(this,TransactionListActivity.class);    			    		
    			startActivity(home);
    			finish();    			
    			return true;
    		case R.id.action_edit:
    			Intent intent = new Intent(this,TransactionEditActivity.class);
    			intent.putExtra(TransactionEditActivity.EXTRA_TRANSACTIONID, transactionId);    			
    			startActivity(intent);
    			finish();
    			return true;
    		case R.id.action_discard:    			
    			transactionDetailFragment.beginDelete();    			
    			return true;
    	}
    	
    	return true;
    }



	@Override
	public void onDeleteSuccess(Transaction transaction) {		
		startActivity(new Intent(this,TransactionListActivity.class));
		finish();
	}
    
    
}
