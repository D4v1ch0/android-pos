package rp3.pos.adapter;


import java.util.List;

import org.askerov.dynamicgid.BaseDynamicGridAdapter;

import rp3.pos.R;
import rp3.pos.model.ProductSalesCategory;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class TransactionEditQuickProductAdapter extends BaseDynamicGridAdapter {
	 	
	public interface OnActionChangeListener{
		void onChange(ProductSalesCategory p);
	}
	
	public TransactionEditQuickProductAdapter(Context c, List<ProductSalesCategory> data, int columnCount,
			boolean isFavoriteCategory, OnActionChangeListener l)
	{					
		super(c, data, columnCount);
		details = data;		
		inflater = LayoutInflater.from(c);
		this.isFavoriteCategory = isFavoriteCategory; 
		listener = l;
	}

	private OnActionChangeListener listener; 
	private LayoutInflater inflater = null;	
	boolean isFavoriteCategory = false;
	List<ProductSalesCategory> details;
	
	public void isFavoriteList(boolean isFavorite){
		isFavoriteCategory = isFavorite;
	}
	
	static class ViewHolder {
		ImageButton button_action;
		TextView textView_product;
	}

			
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.griviewcell_product_action, null);			

			holder = new ViewHolder();			
			holder.textView_product = (TextView)convertView.findViewById(R.id.textView_product_name);
			holder.button_action = (ImageButton)convertView.findViewById(R.id.button_quick_product_action);
			holder.button_action.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						ProductSalesCategory d = (ProductSalesCategory)v.getTag();
						if(d!=null){
							if(isFavoriteCategory){
								d.setFavorite(false);
								remove(d);
							}
							else{
								ImageButton vb = (ImageButton)v;
								d.setFavorite(!d.isFavorite());
								
								if(d.isFavorite())
									vb.setImageResource(R.drawable.ic_action_important);
								else
									vb.setImageResource(R.drawable.ic_action_not_important);
							}							
							listener.onChange(d);
						}
					}
				});			
			
			convertView.setTag(holder);

		} else
			holder = (ViewHolder) convertView.getTag();

		ProductSalesCategory detail = (ProductSalesCategory)getItem(position);
		if(detail!=null)
		{
			holder.textView_product.setText( detail.getProduct().getDescription() );
			holder.button_action.setTag(detail);
			
			if(isFavoriteCategory)
				holder.button_action.setImageResource(R.drawable.ic_action_discard);
			else if(detail.isFavorite())
				holder.button_action.setImageResource(R.drawable.ic_action_important);
			else
				holder.button_action.setImageResource(R.drawable.ic_action_not_important);
		}		
				
		return convertView;
	}	

}
