package rp3.pos;

import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.Transaction;
import rp3.pos.model.TransactionType;
import rp3.pos.transaction.TransactionEditActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


public class TransactionListActivity extends rp3.app.NavActivity
        implements TransactionListFragment.TransactionListFragmentListener, TransactionDetailFragment.TransactionDetailListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
	private final String STATE_CURRENT_TRANSACTIONTYPE = "transactiontype";
	private final String STATE_WAIT_FOR_UPDATE = "waitforupdate";
	
	private final static String EXTRA_TRANSACTIONTYPE = "TRANSACTIONTYPE";
	
    private boolean mTwoPane;
    private int currentTrasantionTypeId;
    private long selectedTransactionId;
    
    private TransactionType transactionType;
    private boolean waitUpdate = false;    
    private MenuItem menuItemActionEdit;
    private MenuItem menuItemActionDiscard;
    private TransactionDetailFragment transactionDetailFragment;
    
    public static Intent newIntent(Context c, int transactionTypeId) {
    	Intent i = new Intent(c, TransactionListActivity.class);
    	i.putExtra(EXTRA_TRANSACTIONTYPE, transactionTypeId);
    	return i;
    }
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);               
        super.setDataBaseParameters(DbOpenHelper.class);               
        
        setPaneContentView(R.layout.fragment_transaction);                                    

        if (findViewById(R.id.content_transaction_detail) != null) {     
            mTwoPane = true;            
            ((TransactionListFragment) getCurrentFragmentManager()
                    .findFragmentById(R.id.content_transaction_list))
                    .setActivateOnItemClick(true);
        }
        
        if(savedInstanceState!=null)
        {
        	currentTrasantionTypeId =  savedInstanceState.getInt(STATE_CURRENT_TRANSACTIONTYPE);
        	waitUpdate = savedInstanceState.getBoolean(STATE_WAIT_FOR_UPDATE);        	
        }
        else      
        {
        	 if(getIntent().hasExtra(EXTRA_TRANSACTIONTYPE)){
             	currentTrasantionTypeId = getIntent().getIntExtra(EXTRA_TRANSACTIONTYPE, 0);             	
             }             
        	 
        	selectedTransactionId = 0;
        	if(mTwoPane){
        		clearDetailContent();
        	}
        }
        
        if(currentTrasantionTypeId == 0){
        	transactionType = TransactionType.getDefaultTransactionType(getDataBase());
        	currentTrasantionTypeId = transactionType.getTransactionTypeId();
        }
        else{
        	transactionType = TransactionType.getTransactionType(getDataBase(), currentTrasantionTypeId);
        }
        
        setNavigationSelection(currentTrasantionTypeId);
        setTitle(transactionType.getName());
    }

	private void refreshTransactions(){
		((TransactionListFragment) getCurrentFragmentManager()
				.findFragmentById(R.id.content_transaction_list)).
				loadTransactions(transactionType.getTransactionTypeId());
		
		if(mTwoPane) onTransactionSelected(selectedTransactionId);
		
		waitUpdate = false;
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt(STATE_CURRENT_TRANSACTIONTYPE, currentTrasantionTypeId );
    	outState.putBoolean(STATE_WAIT_FOR_UPDATE, waitUpdate);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId())
    	{
    		case R.id.action_new:
    			waitUpdate = true;
    			int transactionTypeNew = TransactionType.TYPE_ORDER;    			
    			if(currentTrasantionTypeId != 0){
    				transactionTypeNew = transactionType.getTransactionTypeId();
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
    public void onTransactionSelected(long id) {    	
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.        
        	selectedTransactionId = id;
        	setVisibleEditActionButtons( selectedTransactionId != 0 );
        	
        	transactionDetailFragment = TransactionDetailFragment.newInstance(selectedTransactionId); 
        	
        	getCurrentFragmentManager().beginTransaction()
                    .replace(R.id.content_transaction_detail, 
                    		transactionDetailFragment)
                    .commit();

        } else {
        	waitUpdate = true;
        	
            Intent detailIntent = new Intent(this, TransactionDetailActivity.class);
            detailIntent.putExtra(TransactionDetailFragment.ARG_ITEM_ID, id);
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
    	getSupportFragmentManager().beginTransaction()
        .replace(R.id.content_transaction_detail, 
        		transactionDetailFragment)
        .commit();
    }   

	@Override
	public void onDeleteSuccess(Transaction transaction) {
		selectedTransactionId = 0;
		refreshTransactions();		
	}
}
