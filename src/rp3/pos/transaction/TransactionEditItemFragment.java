package rp3.pos.transaction;

import rp3.app.BaseFragment;
import rp3.pos.R;
import rp3.pos.model.TransactionDetail;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TransactionEditItemFragment extends BaseFragment {
	
	public static final String ARG_TRANSACTION_DETAIL_ID = "transactionDetailId";
	
	private TransactionDetail transactionDetail;
	private long transactionDetailId = 0;
	private TransactionEditItemListener callback;	
	
	public interface TransactionEditItemListener{
		
		void onItemEditAcceptAction(TransactionDetail transactionDetail);
		void onItemEditCancelAction(TransactionDetail transactionDetail);
		void onItemEditDiscardAction(TransactionDetail transactionDetail);
		
	}
	
	
	public static TransactionEditItemFragment newInstance(long transactionDetailId){
		Bundle arguments = new Bundle();
		arguments.putLong(TransactionEditItemFragment.ARG_TRANSACTION_DETAIL_ID, transactionDetailId);
		TransactionEditItemFragment fragment = new TransactionEditItemFragment();
		fragment.setArguments(arguments);		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_transaction_edit_item);
		setRetainInstance(true);
		
		Bundle arguments = getArguments();
		if(arguments!=null && arguments.containsKey(ARG_TRANSACTION_DETAIL_ID)){
			transactionDetailId = arguments.getLong(ARG_TRANSACTION_DETAIL_ID,0);
		}
		
		if(transactionDetailId!=0){
			transactionDetail = TransactionDetail.getById(getDataBase(), transactionDetailId);
		}else{
			transactionDetail =  new TransactionDetail();
		}			
	}
	
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);
		if(getParentFragment() != null){
			if(!(getParentFragment() instanceof TransactionEditItemListener)) {
				throw new IllegalStateException("Parent Fragment must implement fragment's TransactionEditItemListener.");				
			}
			callback = (TransactionEditItemListener)getParentFragment();
		}
		else if (!(activity instanceof TransactionEditItemListener)) {
			throw new IllegalStateException("Activity must implement fragment's TransactionEditItemListener.");
        }		
		callback = (TransactionEditItemListener)activity;
	}
	
	@Override
	public void onFragmentCreateView(View rootView, Bundle savedInstanceState) {		
		
		if(isDialog())
			this.getDialog().setTitle(R.string.label_detail);
			
		setImageButtonClickListener(R.id.button_accept, new View.OnClickListener() {
			
			public void onClick(View v) {				
				updateTransactionDetail();				
				callback.onItemEditAcceptAction(transactionDetail);
				finish();
			}
		});
				
		setImageButtonClickListener(R.id.button_cancel, new View.OnClickListener() {
			
			public void onClick(View v) {				
				callback.onItemEditCancelAction(transactionDetail);
				finish();
			}
		});
		
		setImageButtonClickListener(R.id.button_discard, new View.OnClickListener() {
			
			public void onClick(View v) {				
				beginDeleteDetail();				
			}
		});
					
		if(isDialog())
			setViewVisibility(R.id.button_discard, View.VISIBLE);
		else
			setViewVisibility(R.id.button_discard, View.GONE);
						
				
		if(savedInstanceState==null){											
			setTextViewText(R.id.textView_transactiondetail_product_description, transactionDetail.getProduct().getDescription());		
			setTextViewCurrencyText(R.id.textView_transactiondetail_price, transactionDetail.getPrice());
			setTextViewCurrencyText(R.id.textView_transactiondetail_subtotal, transactionDetail.getSubtotal());		
			setTextViewCurrencyText(R.id.textView_transactiondetail_before_taxes, transactionDetail.getSubtotalBeforeTax());
			setTextViewCurrencyText(R.id.textView_transactiondetail_taxes, transactionDetail.getTax());
			setTextViewCurrencyText(R.id.textView_transactiondetail_total, transactionDetail.getTotal());
			setTextViewCurrencyText(R.id.editText_transactiondetail_discount, transactionDetail.getDiscount());
			setTextViewNumberText(R.id.editText_transactiondetail_quantity, transactionDetail.getQuantity());
		}	
		
		if(transactionDetail.getQuantity() == 0){
			EditText textViewQuantity = (EditText)getRootView().findViewById(R.id.editText_transactiondetail_quantity);
			textViewQuantity.requestFocus();
			textViewQuantity.selectAll();
		}
	}
	
	public void beginDeleteDetail(){
		super.showDialogConfirmation(0,R.string.message_confirmation_transaction_detail_delete);
	}

	@Override
	public void onPositiveConfirmation(int id) {		
		super.onPositiveConfirmation(id);
		deleteDetail();
	}
		
	
	private void deleteDetail(){				
		TransactionDetail.delete(getDataBase(), transactionDetailId);
		callback.onItemEditDiscardAction(transactionDetail);
		finish();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {		
		super.onSaveInstanceState(outState);
		setTransactionDetailFromViewValues();
	}
	
	private void setTransactionDetailFromViewValues(){
		double quantity = getTextViewDouble(R.id.editText_transactiondetail_quantity);
		double discount = getTextViewDouble(R.id.editText_transactiondetail_discount);
		
		transactionDetail.setQuantity(quantity);
		transactionDetail.setDiscount(discount);
		transactionDetail.calculate();
	}
	
	private void updateTransactionDetail(){
		
		setTransactionDetailFromViewValues();
		
		TransactionDetail.update(getDataBase(), transactionDetail);
	}
	
}
