package rp3.pos;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import rp3.pos.adapter.TransactionEditDetailAdapter;
import rp3.pos.model.Transaction;
import rp3.pos.transaction.TransactionEditActivity;

public class TransactionDetailFragment extends rp3.app.BaseFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "rp3.pos.transactionid";
    public static final String ARG_PARENT_SOURCE = "rp3.pos.parentsource";

    public static final String PARENT_SOURCE_LIST = "LIST";
    public static final String PARENT_SOURCE_SEARCH = "SEARCH";
    
    public static final String STATE_TRANSACTIONID = "transactionid";
    
    private Transaction transaction;
    private long transactionId;
    
    private TransactionDetailListener transactionDetailCallback;
    
    
    public interface TransactionDetailListener{
    	public void onDeleteSuccess(Transaction transaction);
    }
            
    public TransactionDetailFragment() {
    }   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        if(getParentFragment()==null)
        	setRetainInstance(true);
        
        if (getArguments().containsKey(ARG_ITEM_ID)) {            
            transactionId = getArguments().getLong(ARG_ITEM_ID);            
        }else if(savedInstanceState!=null){
        	transactionId = savedInstanceState.getLong(STATE_TRANSACTIONID);
        }        
        
        if(transactionId != 0){        	
        	transaction = Transaction.getTransaction(getDataBase(), transactionId,true);
        }
        
        if(transaction==null){
        	super.setContentView(R.layout.base_content_no_selected_item);
        }        
    }

    @Override
    public void onAttach(Activity activity) {    	
    	super.onAttach(activity);
    	
    	setContentView(R.layout.fragment_transaction_detail);
    	
    	if(getParentFragment()!=null){
    		transactionDetailCallback = (TransactionDetailListener)getParentFragment();
    	}else{
    		transactionDetailCallback = (TransactionDetailListener)activity;
    	}
    	    
    }
    
    @Override
    public void onFragmentCreateView(View rootView, Bundle savedInstanceState) {    	
    	       
        if (transaction != null) {        	        
        	 
        	setTextViewText(R.id.textView_transaction_client, transaction.getClientFullName());
        	setTextViewText(R.id.textView_transaction_id, transaction.getTransactionCode());
        	setTextViewText(R.id.textView_transaction_typeName, transaction.getTransactionType().getName());
        	setTextViewCurrencyText(R.id.textView_transaction_total, transaction.getTotal());
        	setTextViewCurrencyText(R.id.textView_transaction_subtotal, transaction.getSubtotal());        	
        	setTextViewCurrencyText(R.id.textView_transaction_discount, transaction.getDiscount());
        	setTextViewCurrencyText(R.id.textView_transaction_tax, transaction.getTaxes());
        	setTextViewDateText(R.id.textView_transaction_date, transaction.getTransactionDate());        	       
        	setTextViewNumberText(R.id.textView_transaction_quantity, transaction.getQuantity());
        	setTextViewNumberText(R.id.textView_transaction_lines, transaction.getTransactionDetails().size());
        	
        	TransactionEditDetailAdapter adapter = new TransactionEditDetailAdapter(this.getActivity(), 
        			this.transaction.getTransactionDetails(), R.layout.rowlist_transaction_detail_item);    		
        	
//        	Cursor c = transaction.getTransactionDetailCursor();
//        	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getActivity(),
//        			R.layout.rowlist_transaction_product,c , 
//        			TransactionDetail.SIMPLEFROM_CURSOR, new int[]
//        					{
//        						R.id.textView_transactiondetail_quantity,
//        						R.id.textView_transactiondetail_product_description,
//        						R.id.textView_transactiondetail_discount,
//        						R.id.textView_transactiondetail_total
//        					},0);
        	
//        	adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//    			@Override
//        	    public boolean setViewValue(View view, Cursor cursor, int column) {
//    				String columnName = cursor.getColumnName(column);
//    				
//        	        if( columnName.equals(Contract.TransactionDetail.COLUMN_TOTAL)
//        	        		|| columnName.equals(Contract.TransactionDetail.COLUMN_DISCOUNT)){ 
//        	            TextView tv = (TextView) view;
//        	            double value = cursor.getDouble(cursor.getColumnIndex(columnName));    	            
//        	           
//    					tv.setText(rp3.util.Format.getDefaultCurrencyFormat(value));
//    					
//        	            return true;
//        	        }
//        	        return false;
//        	    }
//        	});
        	        
        	
        	setViewAdapter(R.id.listView_products,adapter);
        	
        	setViewOnItemClickListener(R.id.listView_products, new AdapterView.OnItemClickListener() {
    			@Override
    			public void onItemClick(AdapterView<?> arg0, View view, int position,
    					long id) {  
    				
    				if(rp3.util.Screen.isMinLargeLayoutSize(getActivity()))    				
    					showItemDialog(id);
    				else{
    					startActivity(TransactionDetailItemActivity.newIntent(getActivity(), id));
    				}    					
    			}
    		});
        	
        }       
    }
      
    private void showItemDialog(long id){
    	TransactionDetailItemFragment fragment = TransactionDetailItemFragment.newInstance(id, true);    	
    	showDialogFragment(fragment,"detailDialog");
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	outState.putLong(STATE_TRANSACTIONID, transactionId);    	
    }
    
    public void beginDelete(){
    	showDialogConfirmation(0,R.string.message_confirmation_transaction_delete);
    }
        
    @Override
    public void onPositiveConfirmation(int id) {
    	super.onPositiveConfirmation(id);
    	Transaction.delete(getDataBase(), transaction);
    	transactionDetailCallback.onDeleteSuccess(transaction);
    }
    
    public static TransactionDetailFragment newInstance(long transactionId)
    {
    	Bundle arguments = new Bundle();
        arguments.putLong(TransactionDetailFragment.ARG_ITEM_ID, transactionId);
        TransactionDetailFragment fragment = new TransactionDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {    	
    	super.onOptionsItemSelected(item);
    	
    	switch(item.getItemId())
    	{
    		case android.R.id.home:
    			this.getActivity().finish();
    			this.cancelAnimationTransition();
    			return true;
    		case R.id.action_edit:    			    			
    			startActivity(TransactionEditActivity.newIntent(this.getContext(), transactionId) );
    			this.finish();
    			this.cancelAnimationTransition();
    			return true;
    		case R.id.action_discard:    			
    			beginDelete();    			
    			return true;
    	}
    	
    	return true;
    }
    
    
}
