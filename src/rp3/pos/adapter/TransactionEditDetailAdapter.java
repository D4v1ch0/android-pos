package rp3.pos.adapter;

import java.util.List;

import rp3.pos.R;
import rp3.pos.model.TransactionDetail;
import rp3.util.Format;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TransactionEditDetailAdapter extends BaseAdapter {

	public TransactionEditDetailAdapter(Context c, List<TransactionDetail> data, int layoutId) {
		details = data;
		this.layoutId = layoutId;
		inflater = LayoutInflater.from(c);
	}

	private LayoutInflater inflater = null;
	private int layoutId;
	List<TransactionDetail> details;

	static class ViewHolder {
		TextView textView_product;
		TextView textView_discount;
		TextView textView_total;
		TextView textView_quantity;
		TextView textView_price;
		TextView textView_discount_symbol;
	}

	@Override
	public int getCount() {
		return details.size();
	}

	@Override
	public Object getItem(int position) {
		return details.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((TransactionDetail)getItem(position)).getTransactionDetailId();
	}

	public void remove(int position){
		details.remove(position);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(layoutId, null);			

			holder = new ViewHolder();			
			holder.textView_product = (TextView)convertView.findViewById(R.id.textView_transactiondetail_product_description);
			holder.textView_quantity = (TextView)convertView.findViewById(R.id.textView_transactiondetail_quantity);
			holder.textView_price = (TextView)convertView.findViewById(R.id.textView_transactiondetail_price);
			holder.textView_discount = (TextView)convertView.findViewById(R.id.textView_transactiondetail_discount);
			holder.textView_total = (TextView)convertView.findViewById(R.id.textView_transactiondetail_total);
			holder.textView_discount_symbol = (TextView)convertView.findViewById(R.id.textView_discount_symbol);					
			
			convertView.setTag(holder);

		} else
			holder = (ViewHolder) convertView.getTag();

		TransactionDetail detail = (TransactionDetail)getItem(position);
		if(detail!=null)
		{
			holder.textView_product.setText(detail.getProduct().getDescription());
			holder.textView_quantity.setText(Format.getDefaultNumberFormat(detail.getQuantity()));
			holder.textView_price.setText(Format.getDefaultCurrencyFormat(detail.getPrice()));
			holder.textView_discount.setText(Format.getDefaultCurrencyFormat(detail.getDiscount()));
			holder.textView_total.setText(Format.getDefaultCurrencyFormat(detail.getSubtotalBeforeTax()));
			
			if(detail.getDiscount() == 0)
			{
				holder.textView_discount.setVisibility(View.INVISIBLE);
				holder.textView_discount_symbol.setVisibility(View.INVISIBLE);
			}
			else
			{
				holder.textView_discount.setVisibility(View.VISIBLE);
				holder.textView_discount_symbol.setVisibility(View.VISIBLE);
			}
		}		
				
		return convertView;
	}
	

	@Override
	public int getViewTypeCount() {
		return 1;
	}

}
