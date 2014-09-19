package rp3.pos;

import rp3.app.BaseActivity;
import rp3.pos.model.Transaction;
import rp3.pos.transaction.TransactionEditActivity;
import android.os.Bundle;
import android.app.SearchManager;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;


public class SearchableActivity extends BaseActivity
	implements TransactionListFragment.TransactionListFragmentListener, TransactionDetailFragment.TransactionDetailListener {

	private boolean mTwoPane;
	private String query;
	
	private MenuItem menuItemActionEdit;
    private MenuItem menuItemActionDiscard;
    private long selectedTransactionId;
    
    private TransactionListFragment transactionListFragment;
	private TransactionDetailFragment transactionDetailFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_transaction);				
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
						
	    if(!hasFragment(R.id.content_transaction_list)){
	    	transactionListFragment = TransactionListFragment.newInstance(0,true);
	    	setFragment(R.id.content_transaction_list, transactionListFragment);
	    }else{
	    	transactionListFragment = (TransactionListFragment)getFragment(R.id.content_transaction_list);
	    }	    	    
	    	
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	query = intent.getStringExtra(SearchManager.QUERY);	    	
	    }
	    
	    if (findViewById(R.id.content_transaction_detail) != null) {          
            mTwoPane = true;
	    }
	}
	
	@Override
	protected void onResume() {		
		super.onStart();
		
		if(!TextUtils.isEmpty(query))
			executeSearch(query);
		
		if(mTwoPane){
			transactionListFragment.setActivateOnItemClick(true);
			if(selectedTransactionId!=0)
				onTransactionSelected(selectedTransactionId);
		}
	}
	
	private void executeSearch(String query){
		transactionListFragment.searchTransactions(query);
	}
	
	@Override
	public void onTransactionSelected(long id) {
		
		selectedTransactionId = id;
		
		if (mTwoPane) {      			
			transactionDetailFragment = TransactionDetailFragment.newInstance(selectedTransactionId);
			setVisibleEditActionButtons( selectedTransactionId != 0 );
			
			setFragment(R.id.content_transaction_detail, transactionDetailFragment);			
        } else {           
        	startActivity(TransactionDetailActivity.newIntent(this, id) );            
            this.cancelAnimationTransition();
//            Intent detailIntent = new Intent(this, TransactionDetailActivity.class);
//            detailIntent.putExtra(TransactionDetailFragment.ARG_ITEM_ID, id);
//            detailIntent.putExtra(TransactionDetailFragment.ARG_PARENT_SOURCE, TransactionDetailFragment.PARENT_SOURCE_SEARCH);
//            startActivity(detailIntent);
        }  
	}

	
	private void setVisibleEditActionButtons(boolean visible){
    	menuItemActionEdit.setVisible(visible);
    	menuItemActionDiscard.setVisible(visible);
    }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searchable, menu);		
		
		menuItemActionEdit = menu.findItem(R.id.action_edit);
		menuItemActionDiscard = menu.findItem(R.id.action_discard);	    
	    
		boolean visibleActionDetail = mTwoPane && selectedTransactionId != 0;
        setVisibleEditActionButtons(visibleActionDetail);
        
		MenuItem searchViewItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) searchViewItem.getActionView();

	    searchView.setIconifiedByDefault(false);
	    searchView.setQuery(query, false);
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				executeSearch(query);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	    
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {    	
        switch (item.getItemId()) {
            case android.R.id.home: 
            	this.finish();
                return true;
            case R.id.action_edit:
    			
    			Intent intent = new Intent(this,TransactionEditActivity.class);
    			intent.putExtra(TransactionEditActivity.EXTRA_TRANSACTIONID, selectedTransactionId);    			
    			startActivity(intent);
    			
    			return true;
    		case R.id.action_discard:    			
    			transactionDetailFragment.beginDelete();    			
    			return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onDeleteSuccess(Transaction transaction) {
		selectedTransactionId = 0;
		finish();
	}
}
