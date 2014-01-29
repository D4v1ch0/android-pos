package rp3.pos.transaction;

import rp3.pos.R;
import rp3.pos.TransactionListActivity;
import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.Product;
import rp3.pos.model.Transaction;
import rp3.pos.model.TransactionDetail;
import rp3.pos.model.TransactionType;
import rp3.util.Screen;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TransactionEditActivity extends rp3.app.BaseActivity implements TransactionEditMainFragment.TransactionEditListener,
	TransactionEditItemFragment.TransactionEditItemListener,
	TransactionEditExtensionFragment.TransactionEditExtensionListener {

	public static final String EXTRA_TRANSACTIONID = "rp3.pos.transactionId";
	public static final String EXTRA_TRANSACTIONTYPEID = "rp3.pos.transactionTypeId";
	static final String STATE_TRANSACTIONID = "transactionId";
	static final String STATE_TRANSACTIONTYPEID = "transactionTypeId";
	
	static final int REQUEST_CODE_DETAIL_EDIT  = 1;
	
	private TransactionEditMainFragment transactionEditMainFragment;
	private TransactionEditResumeFragment transactionEditResumeFragment;
	private TransactionEditExtensionFragment transactionEditExtensionFragment;
	
	private boolean useTransactionResume= false;	
	private boolean useTransactionExtension = false;	
	
	private long transactionId;	
	private int transactionTypeId;
	private Transaction transaction;	
	private boolean cancelUpdate = false;
		
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
		
		transactionEditResumeFragment = ((TransactionEditResumeFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_edit_resume));
		
		useTransactionResume = transactionEditResumeFragment != null;
						
		if(savedInstanceState!=null){
			transactionId = savedInstanceState.getLong(STATE_TRANSACTIONID);
			transactionTypeId = savedInstanceState.getInt(STATE_TRANSACTIONTYPEID);			
			
			transactionEditMainFragment = ((TransactionEditMainFragment) getFragmentManager()
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
			/*Main Fragment*/			
			transactionEditMainFragment = TransactionEditMainFragment.newInstance(transactionId, transactionTypeId);
			
			FragmentTransaction ft = getFragmentManager().beginTransaction();					
			ft.add(R.id.content_transaction_edit_main, transactionEditMainFragment);							
						
			/*Extension Fragment*/
			if(findViewById(R.id.content_transaction_edit_extension)!=null){
				transactionEditExtensionFragment = TransactionEditExtensionFragment.newInstance();				
				
				ft.add(R.id.content_transaction_edit_extension, transactionEditExtensionFragment);
				
				useTransactionExtension = true;				
			}
			
			ft.commit();
						
			//content_transaction_edit_extension
			
			//if(useTransactionEditResumeFragment)
			//	transactionEditResumeFragment.updateTransactionResume(new Transaction());					
		}
		useTransactionExtension = transactionEditExtensionFragment != null;
		this.setTitle(TransactionType.getTransactionType(getDataBase(), transactionTypeId).getName());			
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_transaction_edit, menu);
		
		MenuItem item = menu.findItem(R.id.action_change_product_insert_mode);
		
		if(transaction.getTransactionType().getProductoInsertModePreference() == TransactionType.PREFERENCE_PRODUCT_INSERT_ACTION ){			
			item.setTitle(R.string.action_change_product_insert_inline);
		}else{			
			item.setTitle(R.string.action_change_product_insert_action);				
		}
		
		return true;
	}	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {				
		if(useTransactionResume)
		{
			FragmentManager fm = getFragmentManager();
	        FragmentTransaction ft = fm.beginTransaction();
	        ft.remove(transactionEditResumeFragment);
	        ft.commit();
		}
		
		outState.putLong(STATE_TRANSACTIONID, transactionId);
		outState.putInt(STATE_TRANSACTIONTYPEID, transactionTypeId);
				
        super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {		
		case android.R.id.home:						
			startActivity(new Intent(this,TransactionListActivity.class));
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
			short newMode;
			if(transaction.getTransactionType().getProductoInsertModePreference() == TransactionType.PREFERENCE_PRODUCT_INSERT_ACTION ){
				newMode = TransactionType.PREFERENCE_PRODUCT_INSERT_INLINE;
				item.setTitle(R.string.action_change_product_insert_action);	
			}else{
				newMode = TransactionType.PREFERENCE_PRODUCT_INSERT_ACTION;
				item.setTitle(R.string.action_change_product_insert_inline);				
			}
			
			transaction.getTransactionType().setProductoInsertModePreference(newMode);
			TransactionType.updateProductInsertModePreference(getDataBase(),transaction.getTransactionTypeId(),newMode);
			transactionEditMainFragment.setInsertProductMode(newMode);
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@Override
	public void onPositiveConfirmation(int id) {		
		super.onPositiveConfirmation(id);
		cancelUpdate = true;
		Transaction.delete(getDataBase(), transactionId);
		finish();
	}
	
	private void insertOrUpdateTransaction(){					
		Transaction.insertOrUpdate(getDataBase(), transaction);
		this.transactionId = transaction.getTransactionId();		
	}
	
	@Override
	protected void onStart() {		
		super.onStart();
		cancelUpdate = false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(!cancelUpdate){
			insertOrUpdateTransaction();
		}
		cancelUpdate = false;				
	}
		
	
	@Override
	public void onTransactionChange(Transaction t) {		
		if(useTransactionResume)
			transactionEditResumeFragment.updateTransactionResume(t);			
	}

	@Override
	public void setTransaction(Transaction t) {		
		transaction = t;
		if(useTransactionExtension && t!=null)
			transactionEditExtensionFragment.setTransactionValues(t);
	}	
	
	@Override
	public Transaction getTransaction() {
		return transaction;
	}
	
		
	private void ShowDetailDialog(long transactionDetailId){
		TransactionEditItemFragment fragment = TransactionEditItemFragment.newInstance(transactionDetailId,true);		
		showDialogFragment(fragment,"detailDialog");
	}
	
	private void startDetailEditActivity(long transactionDetailId){
		Intent intent = new Intent(this,TransactionEditItemActivity.class);
		intent.putExtra(TransactionEditItemActivity.EXTRA_TRANSACTION_DETAIL_ID, transactionDetailId);
		
		startActivityForResult(intent, REQUEST_CODE_DETAIL_EDIT);
	}

	@Override
	public void onAddDetail(TransactionDetail detail) {
		insertOrUpdateTransaction();
		onTransactionDetailItemSelected(detail.getTransactionDetailId());
	}
	
	@Override
	public void onTransactionDetailItemSelected(long transactionDetailId) {
		cancelUpdate = true;
		if(Screen.isMinLageLayoutSize(this))
			ShowDetailDialog(transactionDetailId);
		else
			startDetailEditActivity(transactionDetailId);			
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		
		switch (requestCode){
			case REQUEST_CODE_DETAIL_EDIT:
				if(resultCode == RESULT_OK)
					refreshDetail();
				break;
		}		
	}
	
	private void refreshDetail(){
		transactionEditMainFragment.requireRefreshDataTransaction();
	}

	@Override
	public void addProduct(Product p) {
		transactionEditMainFragment.addProduct(p.getProductId(), p.getSku(), p.getDescription(), p.getPrice(), 1);
	}
}
