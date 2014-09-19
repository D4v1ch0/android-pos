package rp3.pos;

import rp3.app.BaseFragment;
import rp3.pos.R;
import rp3.pos.model.TransactionDetail;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TransactionDetailItemFragment extends BaseFragment {
	
	public static final String ARG_TRANSACTION_DETAIL_ID = "transactionDetailId";
	
	private TransactionDetail transactionDetail;
	private long transactionDetailId = 0;	
	private boolean isDialog = false;
	
	
	public static TransactionDetailItemFragment newInstance(long transactionDetailId,boolean asDialog){
		Bundle arguments = new Bundle();
		arguments.putLong(TransactionDetailItemFragment.ARG_TRANSACTION_DETAIL_ID, transactionDetailId);
		TransactionDetailItemFragment fragment = new TransactionDetailItemFragment();
		fragment.setArguments(arguments);
		fragment.isDialog = asDialog;			
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//super.setDataBaseParams(DbOpenHelper.class);
		
		setContentView(R.layout.fragment_transaction_detail_item);
		if(getParentFragment()==null)
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
	}
	
	@Override
	public void onFragmentCreateView(View rootView, Bundle savedInstanceState) {		
		
		if(isDialog)
			this.getDialog().setTitle(R.string.label_detail);
									
		if(savedInstanceState==null){											
			setTextViewText(R.id.textView_transactiondetail_product_description, transactionDetail.getProduct().getDescription());		
			setTextViewCurrencyText(R.id.textView_transactiondetail_price, transactionDetail.getPrice());
			setTextViewCurrencyText(R.id.textView_transactiondetail_subtotal, transactionDetail.getSubtotal());		
			setTextViewCurrencyText(R.id.textView_transactiondetail_before_taxes, transactionDetail.getSubtotalBeforeTax());
			setTextViewCurrencyText(R.id.textView_transactiondetail_taxes, transactionDetail.getTax());
			setTextViewCurrencyText(R.id.textView_transactiondetail_total, transactionDetail.getTotal());								
			setTextViewCurrencyText(R.id.textView_transactiondetail_discount, transactionDetail.getDiscount());
			setTextViewNumberText(R.id.textView_transactiondetail_quantity, transactionDetail.getQuantity());					
		}	
		
		setViewVisibility(R.id.textView_transactiondetail_quantity, View.VISIBLE);
		setViewVisibility(R.id.textView_transactiondetail_discount, View.VISIBLE);
		
		if(getRootView().findViewById(R.id.editText_transactiondetail_quantity)!=null)
			setViewVisibility(R.id.editText_transactiondetail_quantity, View.GONE);		
		
		if(getRootView().findViewById(R.id.editText_transactiondetail_discount)!=null)
			setViewVisibility(R.id.editText_transactiondetail_discount, View.GONE);		
	}	
		
}
