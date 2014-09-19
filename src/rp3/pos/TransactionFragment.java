package rp3.pos;

import java.util.Currency;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import rp3.app.BaseFragment;
import rp3.pos.model.Transaction;
import rp3.pos.model.TransactionType;
import rp3.pos.transaction.TransactionEditActivity;

public class TransactionFragment extends BaseFragment implements TransactionListFragment.TransactionListFragmentListener,
TransactionDetailFragment.TransactionDetailListener{

	public static final String ARG_TRANSACTIONTYPEID = "transactionTypeId";
	
	private TransactionListFragment transactionListFragment;
	private TransactionDetailFragment transactionDetailFragment;
	
	private int transactionTypeId = 0;
	private boolean mTwoPane = false;
	private long selectedTransactionId;
	
	private MenuItem menuItemActionEdit;
    private MenuItem menuItemActionDiscard;
    
	public static TransactionFragment newInstance(int transactionTypeId) {
		TransactionFragment fragment = new TransactionFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TRANSACTIONTYPEID, transactionTypeId);
		fragment.setArguments(args);
		return fragment;
    }
	
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);
		setContentView(R.layout.fragment_transaction, R.menu.fragment_transaction);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);					
				
		if(getArguments().containsKey(ARG_TRANSACTIONTYPEID)){
		   transactionTypeId = getArguments().getInt(ARG_TRANSACTIONTYPEID);
		}		
		transactionListFragment = TransactionListFragment.newInstance(transactionTypeId,false);					
	}
	
	
	@Override
	public void onFragmentCreateView(View rootView, Bundle savedInstanceState) {		
		super.onFragmentCreateView(rootView, savedInstanceState);
						
		if(getChildFragmentManager().findFragmentById(R.id.transaction_detail) == null){			
			if(rootView.findViewById(R.id.content_transaction_list)!=null){
				setFragment(R.id.content_transaction_list, transactionListFragment );
			}			
		}
		
		if (rootView.findViewById(R.id.content_transaction_detail) != null) {     
			mTwoPane = true;
        }									
	}	
	
	@Override
	public void onResume() {		
		super.onResume();
		
		if(mTwoPane){
			transactionListFragment.setActivateOnItemClick(true);
			if(selectedTransactionId!=0)
				onTransactionSelected(selectedTransactionId);
		}
	}
	
	@Override
	public void onAfterCreateOptionsMenu(Menu menu) {		
		SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    	SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        // Assumes current activity is the searchable activity
        if(null!=searchManager ) {   
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        
        menuItemActionEdit = menu.findItem(R.id.action_edit);
        menuItemActionDiscard = menu.findItem(R.id.action_discard);
        
        boolean visibleActionDetail = mTwoPane && selectedTransactionId != 0;
        setVisibleEditActionButtons(visibleActionDetail);
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch(item.getItemId())
	    	{
	    		case R.id.action_new:
	    			//waitUpdate = true;
	    			int transactionTypeNew = 0;    			
	    			if(transactionTypeId != 0){
	    				transactionTypeNew = transactionTypeId;
	    			}  
	    			else{
	    				transactionTypeNew = TransactionType.getDefaultTransactionType(getDataBase()).getTransactionTypeId();
	    			}
	    			if (mTwoPane) {
	    				
	    			}
	    			Intent editIntent = new Intent(this.getActivity(), TransactionEditActivity.class);
	    			editIntent.putExtra(TransactionEditActivity.EXTRA_TRANSACTIONTYPEID, transactionTypeNew );
	                startActivity(editIntent);
	                return true;
	    		case R.id.action_edit:
	    			
	    			Intent intent = new Intent(this.getContext(),TransactionEditActivity.class);
	    			intent.putExtra(TransactionEditActivity.EXTRA_TRANSACTIONID, selectedTransactionId);    			
	    			startActivity(intent);
	    			
	    			return true;
	    		case R.id.action_discard:    			
	    			transactionDetailFragment.beginDelete();    			
	    			return true;
	    	}
	    	
	    	return super.onOptionsItemSelected(item);
	    }
		
	
	private void setVisibleEditActionButtons(boolean visible){
    	menuItemActionEdit.setVisible(visible);
    	menuItemActionDiscard.setVisible(visible);
    }
    
    private void clearDetailContent(){
    	transactionDetailFragment = TransactionDetailFragment.newInstance(0);
    	
    	setFragment(R.id.content_transaction_detail, transactionDetailFragment);    	
    }   

	public void onDeleteSuccess(Transaction transaction) {
		selectedTransactionId = 0;
		transactionListFragment.loadTransactions(transactionTypeId);
		onTransactionSelected(selectedTransactionId);
		//refreshTransactions();		
	}

	/// TransactionListFragmentListener
	
	@Override
	public void onTransactionSelected(long id) {
		selectedTransactionId = id;        	
		
		if (mTwoPane) {
			setVisibleEditActionButtons( selectedTransactionId != 0 );
        	transactionDetailFragment = TransactionDetailFragment.newInstance(selectedTransactionId);         	
        	setFragment(R.id.content_transaction_detail, transactionDetailFragment);
        } else {
        	//waitUpdate = true;        	            
            startActivity(TransactionDetailActivity.newIntent(this.getActivity(), id) );            
            this.cancelAnimationTransition();
        }
	}	
	
	///End TransactionListFragmentListener
}
