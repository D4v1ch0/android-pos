package rp3.pos;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;


import rp3.content.SimpleCursorLoader;
import rp3.pos.db.Contract;
import rp3.pos.model.Transaction;

public class TransactionListFragment extends rp3.app.BaseListFragment {
		    
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String STATE_SEARCH = "searchmode";
    
    public static final String ARG_TRANSACTIONTYPEID = "transactionTypeId";
    public static final String ARG_SEARCHMODE = "searchMode";
    
    private static final int LOADER_MODE_SEARCH_TEXT = 1;
    private static final int LOADER_MODE_SEARCH_TRANSACTIONTYPE = 2;
    
    private static final String LOADER_ARG_SEARCH_MODE = "LOADER_ARG_SEARCH_MODE";
    private static final String LOADER_ARG_SEARCH_TEXT = "LOADER_ARG_SEARCH_TEXT";
    private static final String LOADER_ARG_SEARCH_TRANSACTIONTYPEID = "LOADER_ARG_SEARCH_TRANSACTIONTYPEID";
    
    private TransactionListFragmentListener transactionListFragmentCallback;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private SimpleCursorAdapter adapter;  
    private int currentTransactionTypeId;
    private boolean useSearchMode = false;
    
    
    public static TransactionListFragment newInstance(int transactionTypeId, boolean useSearchMode) {
    	TransactionListFragment fragment = new TransactionListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TRANSACTIONTYPEID, transactionTypeId);
		args.putBoolean(ARG_SEARCHMODE, useSearchMode);
		fragment.setArguments(args);
		return fragment;
    }
    
    public interface TransactionListFragmentListener {
        public void onTransactionSelected(long id);
    }

    public TransactionListFragment() {
    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          
     currentTransactionTypeId = getArguments().getInt(ARG_TRANSACTIONTYPEID);
     useSearchMode = getArguments().getBoolean(ARG_SEARCHMODE,false);
     
     String[] fields = {
         Contract.Transaction.FIELD_TRANSACTIONDATE,
         Contract.Transaction.FIELD_TOTAL,
         Contract.Transaction.FIELD_TRANSACTION_CODE,
         Contract.Transaction.FIELD_TRANSACTIONTYPE_DESCRIPTION,
         Contract.Transaction.FIELD_CLIENT_NAMES,
         Contract.Transaction.FIELD_DETAIL_RESUME
         };

     int[] toViews = {
    		 R.id.textView_transaction_date, 
    		 R.id.textView_transaction_value,
    		 R.id.textView_transaction_id,
    		 R.id.textView_transaction_typeName,
    		 R.id.textView_transaction_client,
    		 R.id.textView_transaction_detailPreview};
                        
     adapter = new SimpleCursorAdapter(this.getActivity(), 
    	        R.layout.rowlist_transaction_item, null, fields, toViews,0);        
          
     adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
    	    public boolean setViewValue(View view, Cursor cursor, int column) {
    	        if( cursor.getColumnName(column).equals(Contract.Transaction.COLUMN_TRANSACTIONDATE)){ 
    	            TextView tv = (TextView) view;
    	            String dateStr = cursor.getString(cursor.getColumnIndex(Contract.Transaction.COLUMN_TRANSACTIONDATE));    	            
    	           
					tv.setText(rp3.util.Format.getMediumDateFormat(TransactionListFragment.this.getActivity(),dateStr));
					
    	            return true;
    	        }else if( cursor.getColumnName(column).equals(Contract.Transaction.COLUMN_TOTAL)){ 
    	        	TextView tv2 = (TextView) view;
    	            double value = cursor.getDouble(cursor.getColumnIndex(Contract.Transaction.COLUMN_TOTAL));    	                	                	            
					tv2.setText(rp3.util.Format.getDefaultCurrencyFormat(value));
					return true;
    	        }
    	        return false;
    	    }
    	});
     
     setListAdapter(adapter);
          
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {    	
    	super.onActivityCreated(savedInstanceState);
    	
//    	Bundle b = new Bundle();
//    	b.putInt(LOADER_ARG_SEARCH_MODE, LOADER_MODE_SEARCH_TRANSACTIONTYPE);
//    	b.putInt(LOADER_ARG_SEARCH_TRANSACTIONTYPEID, currentTransactionTypeId);
//    	
//    	executeLoader(0, b, this);
    	
    }
        
    
    @Override
    public void onStart() {    	
    	super.onStart();    	    	
    }
         
    
    public void searchTransactions(String termSearch){
    	Bundle b = new Bundle();
    	b.putInt(LOADER_ARG_SEARCH_MODE, LOADER_MODE_SEARCH_TEXT);
    	b.putString(LOADER_ARG_SEARCH_TEXT, termSearch);
    	
    	executeLoader(0, b, this);
    }      
    
    @Override
    public void onResume() {    	
    	super.onResume();
    	
    	if(!useSearchMode){
    		loadTransactions(currentTransactionTypeId);
    	}
    }
        
    
    public void loadTransactions(int transactionType)
    {
    	Bundle b = new Bundle();
    	b.putInt(LOADER_ARG_SEARCH_MODE, LOADER_MODE_SEARCH_TRANSACTIONTYPE);
    	b.putInt(LOADER_ARG_SEARCH_TRANSACTIONTYPEID, transactionType);
    	
    	executeLoader(0, b, this);
    }        
    
	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

	
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        if(getParentFragment()!=null){        	
        	transactionListFragmentCallback = (TransactionListFragmentListener)getParentFragment();
        }else{
        	transactionListFragmentCallback = (TransactionListFragmentListener) activity;
        }
//
//        // Activities containing this fragment must implement its callbacks.
//        if (!(activity instanceof Callbacks)) {
//            throw new IllegalStateException("Activity must implement fragment's callbacks.");
//        }
//
//        mCallbacks = (Callbacks) activity;
    }   

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        transactionListFragmentCallback.onTransactionSelected(id);        
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);     
            outState.putBoolean(STATE_SEARCH, useSearchMode);
        }
    }
    
    

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        	    
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
    
    public void clearActivatedPosition()
    {    	
    	getListView().setItemChecked(ListView.INVALID_POSITION, true);    	
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
		super.onCreateLoader(id, args);
		
		int mode = args.getInt(LOADER_ARG_SEARCH_MODE);								
		
		SimpleCursorLoader loader;
		if(mode == LOADER_MODE_SEARCH_TEXT){
			final String termSearch = args.getString(LOADER_ARG_SEARCH_TEXT);
			loader = new SimpleCursorLoader(this.getActivity()){
				@Override
				public Cursor loadInBackground() {																	
					return Transaction.getTransactionSearchCursor(getDataBase(), termSearch);
				}
			};
		}
		else{
			final int transactionTypeId = args.getInt(LOADER_ARG_SEARCH_TRANSACTIONTYPEID);
			loader = new SimpleCursorLoader(this.getActivity()){
				@Override
				public Cursor loadInBackground() {								
					return Transaction.getTransactionCursor(getDataBase(), transactionTypeId);	
					}									
			};				
		}
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {		
		super.onLoadFinished(loader, c);
		adapter.swapCursor(c);
		notifyListChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {		
		adapter.swapCursor(null);
		notifyListChanged();
	}

	
	
}
