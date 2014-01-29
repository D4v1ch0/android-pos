package rp3.pos;

import java.util.List;

import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.Transaction;
import rp3.pos.model.TransactionType;
import rp3.pos.transaction.TransactionEditActivity;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SpinnerAdapter;


public class TransactionListActivity extends rp3.app.BaseActivity
        implements TransactionListFragment.Callbacks, TransactionDetailFragment.TransactionDetailListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
	private final String STATE_CURRENT_TRANSACTIONTYPE = "transactiontype";
	private final String STATE_WAIT_FOR_UPDATE = "waitforupdate";
	
    private boolean mTwoPane;
    private List<TransactionType> transactionTypes;   
    private int transactionTypeSelected;
    private long selectedTransactionId;
    
    private boolean waitUpdate = false;    
    private MenuItem menuItemActionEdit;
    private MenuItem menuItemActionDiscard;
    private TransactionDetailFragment transactionDetailFragment;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);               
        super.setDataBaseParameters(DbOpenHelper.class);               
        setContentView(R.layout.activity_transaction_list);
        
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        
        transactionTypes = TransactionType.getActiveTransactionTypes(getDataBase(),true);
        SpinnerAdapter mSpinnerAdapter = new ArrayAdapter<TransactionType>(this, 
        		R.layout.rowlist_spinner_transactiontype,
        		android.R.id.text1,
        		transactionTypes);

        actionBar.setListNavigationCallbacks(mSpinnerAdapter, 
            	new OnNavigationListener() {

            	  @Override
            	  public boolean onNavigationItemSelected(int position, long itemId) {
            		  if(transactionTypeSelected != position)
            		  {            		  		            		
		            		transactionTypeSelected = position;		            		
		            		refreshTransactions();
		            		
		            		if(mTwoPane)
		            			clearDetail();
            		  }
            		  return true;
            	  }
            	});                
        
        if (findViewById(R.id.content_transaction_detail) != null) {     
            mTwoPane = true;            
            ((TransactionListFragment) getFragmentManager()
                    .findFragmentById(R.id.transaction_list))
                    .setActivateOnItemClick(true);
        }
        
        if(savedInstanceState!=null)
        {
        	transactionTypeSelected =  savedInstanceState.getInt(STATE_CURRENT_TRANSACTIONTYPE);
        	waitUpdate = savedInstanceState.getBoolean(STATE_WAIT_FOR_UPDATE);
        	actionBar.setSelectedNavigationItem(transactionTypeSelected);
        }
        else      
        {
        	transactionTypeSelected = 0; 
        	selectedTransactionId = 0;
        	if(mTwoPane){
        		clearDetailContent();
        	}
        }
    }

	private void refreshTransactions(){
		((TransactionListFragment) getFragmentManager()
				.findFragmentById(R.id.transaction_list)).
				loadTransactions(transactionTypes.get(transactionTypeSelected).getTransactionTypeId());
		
		if(mTwoPane) onItemSelected(String.valueOf(selectedTransactionId));
		
		waitUpdate = false;
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt(STATE_CURRENT_TRANSACTIONTYPE, transactionTypeSelected);
    	outState.putBoolean(STATE_WAIT_FOR_UPDATE, waitUpdate);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId())
    	{
    		case R.id.action_new:
    			waitUpdate = true;
    			int transactionTypeNew = TransactionType.TYPE_ORDER;    			
    			if(transactionTypeSelected != 0){
    				transactionTypeNew = transactionTypes.get(transactionTypeSelected).getTransactionTypeId();
    			}    			
    			 
    			Intent editIntent = new Intent(this, TransactionEditActivity.class);
    			editIntent.putExtra(TransactionEditActivity.EXTRA_TRANSACTIONTYPEID, transactionTypeNew );
                startActivity(editIntent);
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
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
    	getMenuInflater().inflate(R.menu.activity_transaction_list, menu);    	
        
    	
    	SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    	SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        // Assumes current activity is the searchable activity
        if(null!=searchManager ) {   
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        
        menuItemActionEdit = menu.findItem(R.id.action_edit);
        menuItemActionDiscard = menu.findItem(R.id.action_discard);
        
        boolean visibleActionDetail = mTwoPane && selectedTransactionId != 0;
        setVisibleEditActionButtons(visibleActionDetail);
        
    	return super.onCreateOptionsMenu(menu);
    }
   
    @Override
    protected void onStart() {
    	super.onStart();    	
    	if(waitUpdate){
    		refreshTransactions();      		
    	}
    }    
    
    @Override
    protected void onRestart() {    	
    	super.onRestart();    
    	refreshTransactions();
    }        
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	    	
    	switch(requestCode)
    	{
    		case rp3.data.Constants.LOGIN_REQUEST_CODE:    			
    			finishActivity(requestCode);
    		break;
    	}
    	
    }
    
    /**
     * Callback method from {@link TransactionListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {    	
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.        
        	selectedTransactionId = Long.parseLong(id);
        	setVisibleEditActionButtons( selectedTransactionId != 0 );
        	
        	transactionDetailFragment = TransactionDetailFragment.newInstance(selectedTransactionId); 
        	
        	getFragmentManager().beginTransaction()
                    .replace(R.id.content_transaction_detail, 
                    		transactionDetailFragment)
                    .commit();

        } else {
        	waitUpdate = true;
        	
            Intent detailIntent = new Intent(this, TransactionDetailActivity.class);
            detailIntent.putExtra(TransactionDetailFragment.ARG_ITEM_ID, Long.parseLong(id));
            detailIntent.putExtra(TransactionDetailFragment.ARG_PARENT_SOURCE, 
            		TransactionDetailFragment.PARENT_SOURCE_LIST);
            startActivity(detailIntent);
        }
    }
    
    private void setVisibleEditActionButtons(boolean visible){
    	menuItemActionEdit.setVisible(visible);
    	menuItemActionDiscard.setVisible(visible);
    }
    
    private void clearDetailContent(){
    	transactionDetailFragment = TransactionDetailFragment.newInstance(0);
    	getFragmentManager().beginTransaction()
        .replace(R.id.content_transaction_detail, 
        		transactionDetailFragment)
        .commit();
    }
    
    private void clearDetail()
    {
    	setVisibleEditActionButtons(false);
    	
    	clearDetailContent();
    	
    	((TransactionListFragment) getFragmentManager()
                .findFragmentById(R.id.transaction_list))
                .clearActivatedPosition();
    }

	@Override
	public void onDeleteSuccess(Transaction transaction) {
		selectedTransactionId = 0;
		refreshTransactions();		
	}
}
