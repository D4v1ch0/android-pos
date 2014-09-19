package rp3.pos.transaction;

import rp3.configuration.PreferenceManager;
import rp3.pos.Constants;
import rp3.pos.R;
import rp3.pos.adapter.TransactionEditDetailAdapter;
import rp3.pos.client.SearchClientActivity;
import rp3.pos.db.Contract;
import rp3.pos.model.Client;
import rp3.pos.model.Product;
import rp3.pos.model.Transaction;
import rp3.pos.model.TransactionDetail;
import rp3.pos.model.TransactionType;
import rp3.pos.product.ProductSearchActivity;
import rp3.util.CursorUtils;
import rp3.util.DateTime;
import rp3.util.Screen;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import rp3.widget.listview.EnhancedListView;
import rp3.widget.listview.EnhancedListView.Undoable;

public class TransactionEditMainFragment extends rp3.app.BaseFragment 
	implements TransactionEditItemFragment.TransactionEditItemListener {
	 
	public static final String ARG_TRANSACTIONID = "transactionId";
	public static final String ARG_TRANSACTIONTYPEID = "transactionTypeId";
			
	private EnhancedListView listView_detail;
	private TransactionEditDetailAdapter detail_adapter;
	private View viewEmptyDetail;	
	private Transaction transaction;
	
	private Button button_client;
	
	private final int REQUEST_CODE_CLIENT_SEARCH = 1;
	private final int REQUEST_CODE_PRODUCT_SEARCH = 2;
	private final int REQUEST_CODE_DETAIL_EDIT  = 3;
	private boolean cancelOnPauseUpdate = false;
	private long pendingInsertProductId = 0;
	
	AutoCompleteTextView textEditProduct;
	
	public TransactionEditMainFragment()
	{
	}		
	
	public static TransactionEditMainFragment newInstance(long transactionId, int transactionTypeId){
		TransactionEditMainFragment fragment = new TransactionEditMainFragment();
		
		Bundle arguments = new Bundle();
		arguments.putLong(TransactionEditMainFragment.ARG_TRANSACTIONID, transactionId);
		arguments.putInt(TransactionEditMainFragment.ARG_TRANSACTIONTYPEID, transactionTypeId);
		
		fragment.setArguments(arguments);
		return fragment;
	}
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		setContentView(R.layout.fragment_transaction_edit_main);				
		
		if(savedInstanceState==null){
			Bundle arguments = getArguments();
			long transactionId = 0;
			int transactionTypeId = 0;
			
			if(arguments.containsKey(ARG_TRANSACTIONID))
				transactionId = arguments.getLong(ARG_TRANSACTIONID);
			if(arguments.containsKey(ARG_TRANSACTIONTYPEID))
				transactionTypeId = arguments.getInt(ARG_TRANSACTIONTYPEID);			
			
			if(transactionId!=0){
				transaction = Transaction.getTransaction(getDataBase(), transactionId, true);				
			}
			else{
				transaction = new Transaction();
				transaction.setTransactionTypeId(transactionTypeId);
				transaction.setTransactionDate(DateTime.getCurrentDateTime());
				transaction.setState(Transaction.STATE_DEFAULT);
			}
			
			TransactionType transactionType = TransactionType.getTransactionType(getDataBase(), transaction.getTransactionTypeId());			
			transaction.setTransactionType(transactionType);						
		}				
	}
	
	@Override
	public void onAfterCreateOptionsMenu(Menu menu) {		
		super.onAfterCreateOptionsMenu(menu);
		
		MenuItem item = menu.findItem(R.id.action_change_product_insert_mode);
		String pref = PreferenceManager.getString(Constants.PREF_POS_PRODUCT_INSERT_MODE, Constants.PREF_VALUE_POS_PRODUCT_INSERT_WINDOW);
				
		if(pref.equals(Constants.PREF_VALUE_POS_PRODUCT_INSERT_WINDOW)){			
			item.setTitle(R.string.action_change_product_insert_inline);
		}else{			
			item.setTitle(R.string.action_change_product_insert_action);				
		}		
	}	
	
	@Override
	public void onPause() {
		super.onPause();
		listView_detail.discardUndo();
		if(!cancelOnPauseUpdate) insertOrUpdateTransaction();
		setCancelOnPauseUpdate(false);
	}
	
	@Override
	public void onFragmentCreateView(View rootView, Bundle savedInstanceState){
				
		String[] product_fields = {
		         Contract.Product.FIELD_SKU,
		         Contract.Product.FIELD_NAME
		         };
		
		int[] product_toViews = {
		    		 R.id.textView_product_code, 
		    		 R.id.textView_product_name};
		     
		SimpleCursorAdapter product_adapter = new SimpleCursorAdapter(this.getActivity(), 
   	        R.layout.rowlist_transaction_product_search, null, product_fields, product_toViews,0)
		{
			@Override
			public Cursor runQueryOnBackgroundThread(CharSequence constraint) {				
				return Product.getProductSearchCursor(getDataBase(), String.valueOf(constraint));
			}
			
			@Override
			public CharSequence convertToString(Cursor cursor) {			
				return cursor.getString(
						cursor.getColumnIndex(Contract.Product.FIELD_NAME));
			}
		};	

		detail_adapter = newInstanceDetailAdpater();
		
		setUpListView();								
		
		textEditProduct = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView_product_insert);
		textEditProduct.setAdapter(product_adapter);
		
		textEditProduct.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position,
					long id) {
				// Get the cursor, positioned to the corresponding row in the result set
	            Cursor cursor = (Cursor) listView.getItemAtPosition(position);
	 	            
	            String description = CursorUtils.getString( cursor, Contract.Product.FIELD_NAME);
	            String sku = CursorUtils.getString( cursor, Contract.Product.FIELD_SKU);
	            Double price = CursorUtils.getDouble( cursor, Contract.Product.FIELD_PRICE);
	            textEditProduct.setText("");
	            // Update the parent class's TextView
	            addProduct(id, sku, description, price, 1);
			}
			
		});
						
		button_client = (Button)rootView.findViewById(R.id.button_client_search);
		
		if(!TextUtils.isEmpty(transaction.getClientFullName())){
			button_client.setText(transaction.getClientFullName());
		}
		
		button_client.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent =  new Intent(TransactionEditMainFragment.this.getActivity(),SearchClientActivity.class);
				startActivityForResult(intent, REQUEST_CODE_CLIENT_SEARCH);
			}
		});
						
		setButtonClickListener(R.id.button_product_insert, new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				Intent intent =  new Intent(TransactionEditMainFragment.this.getActivity(),ProductSearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_PRODUCT_SEARCH);
			}
		});
		
		setInsertProductMode( transaction.getTransactionType().getProductoInsertModePreference() );
				
		verifyListEmpty();
		notifyTransactionChange();
	}	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {		
		
		case android.R.id.home:			
			finish();
			return true;
		case R.id.action_accept:	
			//Save in onPause			
			finish();
			return true;
		case R.id.action_discard:
			
			showDialogConfirmation(0, R.string.message_confirmation_transaction_delete, 
					R.string.message_confirmation_transaction_delete);						
			
			return true;
		case R.id.action_change_product_insert_mode:
			
			String newMode;
			
			if(PreferenceManager.getString(Constants.PREF_POS_PRODUCT_INSERT_MODE, 
					Constants.PREF_VALUE_POS_PRODUCT_INSERT_WINDOW).equals(Constants.PREF_VALUE_POS_PRODUCT_INSERT_WINDOW)){
				newMode = Constants.PREF_VALUE_POS_PRODUCT_INSERT_INLINE;
				item.setTitle(R.string.action_change_product_insert_action);
			}else{
				newMode = Constants.PREF_VALUE_POS_PRODUCT_INSERT_WINDOW;
				item.setTitle(R.string.action_change_product_insert_inline);				
			}
			PreferenceManager.setValue(Constants.PREF_POS_PRODUCT_INSERT_MODE, newMode);
			
			//transaction.getTransactionType().setProductoInsertModePreference(newMode);
			//TransactionType.updateProductInsertModePreference(getDataBase(),transaction.getTransactionTypeId(),newMode);
			
			//setInsertProductMode(newMode);
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
	public void onPositiveConfirmation(int id) {		
		super.onPositiveConfirmation(id);		
		Transaction.delete(getDataBase(), transaction);
		finish();
	}
	
	private void insertOrUpdateTransaction(){					
		Transaction.insertOrUpdate(getDataBase(), transaction);				
	}
	
	private void setCancelOnPauseUpdate(boolean cancel){
		cancelOnPauseUpdate = cancel;
	}
	
	public void setInsertProductMode(short mode) {		
		if(mode == TransactionType.PREFERENCE_PRODUCT_INSERT_INLINE){
			setViewVisibility(R.id.viewGroup_inline_productInsert, View.VISIBLE);
			setViewVisibility(R.id.viewGroup_action_productInsert, View.GONE);
		}
		else
		{
			setViewVisibility(R.id.viewGroup_inline_productInsert,View.GONE);
			setViewVisibility(R.id.viewGroup_action_productInsert,View.VISIBLE);			
		}
	}	
			
	private void notifyTransactionChange(){		
		updateTransactionResume();
	}
	
	private void showDetailDialog(long transactionDetailId){
		TransactionEditItemFragment fragment = TransactionEditItemFragment.newInstance(transactionDetailId);		
		showDialogFragment(fragment,"detailDialog");
	}
	
	
	
	private void startDetailEditActivity(long transactionDetailId){				
		startActivityForResult( TransactionEditItemActivity.newIntent(getContext(), transactionDetailId), 
				REQUEST_CODE_DETAIL_EDIT);		
	}
	
	private void onAddDetail(TransactionDetail detail) {
		setCancelOnPauseUpdate(true);
		insertOrUpdateTransaction();		
		onTransactionDetailItemSelected(detail.getID());
	}
	
	public void onTransactionDetailItemSelected(long transactionDetailId) {		
		if(Screen.isMinLargeLayoutSize(this.getContext()))
			showDetailDialog(transactionDetailId);
		else
			startDetailEditActivity(transactionDetailId);			
	}
	
	private void setUpListView(){
		
		listView_detail = (EnhancedListView)getRootView().findViewById(R.id.listView_products);
		
		viewEmptyDetail = getRootView().findViewById(android.R.id.empty);
		
		listView_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
		      	if(id==0){
		  			onAddDetail(transaction.getTransactionDetails().get(position));		  		
		  		}
		  		else{									
		  			onTransactionDetailItemSelected(id);			    	  
		      }
			}		
		});
		
		listView_detail.setDismissCallback(new EnhancedListView.OnDismissCallback() {
			
			@Override
			public Undoable onDismiss(EnhancedListView listView, final int position) {				
				
				// Store the item for later undo
			    final TransactionDetail item = (TransactionDetail) detail_adapter.getItem(position);
			    
			    // Remove the item from the adapter
			    transaction.getTransactionDetails().remove(position);			
			    updateTransactionDetail();
			    
			    return new EnhancedListView.Undoable() {
			      // Reinsert the item to the adapter
			      @Override public void undo() {			    	  
			    	  transaction.getTransactionDetails().add(position, item);
			    	  updateTransactionDetail();			    	  			    	 
			      }

			      // Return a string for your item
			      @Override public String getTitle() {
			        return getText(R.string.label_deleted) + " '" + item.getProduct().getDescription() + "'";
			      }

			      // Delete item completely from your persistent storage
			      @Override public void discard() {
			    	  TransactionDetail.delete( getDataBase(), item.getID() );
			    	  updateTransactionDetail();
			      }
			    };
			}
		});
		
		listView_detail.enableSwipeToDismiss();
		listView_detail.setRequireTouchBeforeDismiss(false);

		listView_detail.setAdapter(detail_adapter);
	}		
	
	
	private void updateTransactionDetail(){
				
		verifyListEmpty();
		transaction.calculate();
		
		detail_adapter.notifyDataSetChanged();		
		notifyTransactionChange();
	}
	
	private void updateClient(){
		if(!TextUtils.isEmpty(transaction.getClientFullName()))
			button_client.setText(transaction.getClientFullName());
		else
			button_client.setText(null);
	}
	
	private void verifyListEmpty(){
		if(transaction.getTransactionDetails().size() > 0)
			viewEmptyDetail.setVisibility(View.GONE);
		else
			viewEmptyDetail.setVisibility(View.VISIBLE);
	}
	
	
	public TransactionDetail addProduct(long id,String sku, String description, double price, double quantity)
	{				
		Product product = new Product();
		product.setDescription(description);
		product.setProductId(id);
		product.setSku(sku);	
		
		TransactionDetail detail = new TransactionDetail();
		detail.setProduct(product);
		detail.setQuantity(quantity);
		detail.setPrice(price);
		detail.calculate();
		
		transaction.addDetail(detail);		
		
		updateTransactionDetail();
		updateClient();
		
		return detail;
	}	
	
	
	public void requireRefreshDataTransaction(){
		
		if(transaction.getID()!=0)
		{			
			transaction.setTransactionDetails( TransactionDetail.getTransactionDetails(getDataBase(), transaction.getID()) );
		
			detail_adapter = null;
			detail_adapter = newInstanceDetailAdpater();
			
			listView_detail.setAdapter(detail_adapter);
			detail_adapter.notifyDataSetChanged();
			
			transaction.calculate();
		}		
		notifyTransactionChange();
	}	
	
	private TransactionEditDetailAdapter newInstanceDetailAdpater(){
		return new TransactionEditDetailAdapter(this.getActivity(), 
				this.transaction.getTransactionDetails(),
				R.layout.rowlist_transaction_edit_detail);
	}
		
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_CODE_DETAIL_EDIT:
			if(resultCode == Activity.RESULT_OK)
				refreshDetail();
			break;
		case REQUEST_CODE_CLIENT_SEARCH:
			if(resultCode == Activity.RESULT_OK)
			{
				long client_id = data.getLongExtra(SearchClientActivity.RESULT_ARG_ID, 0);
				Client client = Client.getClientById(getDataBase(), client_id);
				if(client!=null){
					transaction.setClient(client);
					updateClient();
				}
			}
			break;
		case REQUEST_CODE_PRODUCT_SEARCH:
			if(resultCode == Activity.RESULT_OK) {				
				
				final long productId = data.getLongExtra(ProductSearchActivity.RESULT_ARG_ID, 0);	
				if(Screen.isMinLargeLayoutSize(this.getContext())){
					new AsyncTask<Object, Integer, Boolean>() {
						@Override
						protected Boolean doInBackground(Object... params) {				
							SystemClock.sleep(500);
							return true;
						}
						@Override
						protected void onPostExecute(Boolean result) {								
							insertProductFromSearch(productId);
						}				
					}.execute();
				}else{
					insertProductFromSearch(productId);
				}
										
			}
			break;		
		}
	}	
	
	private void insertProductFromSearch(long productId){
		Product product = Product.getProduct(getDataBase(), productId);				
		TransactionDetail detail = addProduct(productId, product.getSku(), product.getDescription(), product.getPrice(), 0);
		onAddDetail(detail);
	}
	
	@Override
	public void onSaveInstanceState(Bundle arg0) {		
		super.onSaveInstanceState(arg0);				
	}	
	
	private void updateTransactionResume()
	{
		setTextViewCurrencyText(R.id.textView_transaction_subtotal, transaction.getSubtotal());
		setTextViewCurrencyText(R.id.textView_transaction_tax, transaction.getTaxes());
		setTextViewCurrencyText(R.id.textView_transaction_discount, transaction.getDiscount());
		setTextViewCurrencyText(R.id.textView_transaction_total, transaction.getTotal());
	}

	
	private void refreshDetail(){
		requireRefreshDataTransaction();
	}

	@Override
	public void onItemEditAcceptAction(TransactionDetail transactionDetail) {
		refreshDetail();		
	}

	@Override
	public void onItemEditCancelAction(TransactionDetail transactionDetail) {		
	}
	
	@Override
	public void onItemEditDiscardAction(TransactionDetail transactionDetail) {		
		refreshDetail();		
	}
}
