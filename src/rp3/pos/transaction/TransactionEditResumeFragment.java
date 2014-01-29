package rp3.pos.transaction;

import rp3.pos.R;
import rp3.pos.model.Transaction;
import rp3.util.ViewUtils;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TransactionEditResumeFragment extends Fragment {

	public TransactionEditResumeFragment()
	{		
	}
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
			
	}
	
	View rootView;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_transaction_edit_resume, container, false);
		
		return rootView;
	}
	
	public void updateTransactionResume(Transaction t)
	{
		ViewUtils.setTextViewCurrencyText(rootView, R.id.textView_transaction_subtotal, t.getSubtotal());
		ViewUtils.setTextViewCurrencyText(rootView, R.id.textView_transaction_tax, t.getTaxes());
		ViewUtils.setTextViewCurrencyText(rootView, R.id.textView_transaction_discount, t.getDiscount());
		ViewUtils.setTextViewCurrencyText(rootView, R.id.textView_transaction_total, t.getTotal());
	}
}
