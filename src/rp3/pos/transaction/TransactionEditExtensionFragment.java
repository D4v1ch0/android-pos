package rp3.pos.transaction;

import java.util.ArrayList;
import java.util.List;

import rp3.content.SimpleCursorLoader;
import rp3.pos.R;
import rp3.pos.adapter.TransactionEditQuickProductAdapter;
import rp3.pos.db.Contract;
import rp3.pos.loader.ProductSalesCategoryLoader;
import rp3.pos.model.Product;
import rp3.pos.model.ProductSalesCategory;
import rp3.pos.model.Transaction;
import rp3.pos.model.SalesCategory;
import rp3.util.Screen;
import android.app.Activity;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.app.LoaderManager.LoaderCallbacks;
import org.askerov.dynamicgid.DynamicGridView;
import org.askerov.dynamicgid.DynamicGridView.OnDropListener;

public class TransactionEditExtensionFragment extends rp3.app.BaseFragment  {

	private SimpleCursorAdapter salesCategoryAdapter;
	private TransactionEditQuickProductAdapter produtAdpater;
	private int salesCategoryId = 0;
	
	private LoaderProductSalesCategory loaderProductCategoryCallback;
	
	private final int LOADER_SALESCATEGORY = 0;
	private final int LOADER_PRODUCTBYCATEGORY = 1;
	private Transaction transaction;
	
	int numColums = 3;
	
	private List<ProductSalesCategory> productSalesCategoryList;
	
	private DynamicGridView gridViewProducts;
	
	private TransactionEditExtensionListener transactionEditExtensionCallback; 
	
	public interface TransactionEditExtensionListener{
		public void addProduct(Product p);
	}
	
	public TransactionEditExtensionFragment(){
		productSalesCategoryList = new ArrayList<ProductSalesCategory>();
	}
	
	public static TransactionEditExtensionFragment newInstance(){
		TransactionEditExtensionFragment fragment = new TransactionEditExtensionFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//super.setDataBaseParams(DbOpenHelper.class);
		setRetainInstance(true);
		
		setContentView(R.layout.fragment_transaction_edit_extension);
			
		salesCategoryAdapter = new SimpleCursorAdapter(this.getActivity(),
				R.layout.rowlist_transaction_edit_salescategory, 
				null, new String[] { Contract.SalesCategory.FIELD_NAME },
				new int[] { R.id.textView_name }, 0){
			
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View result = super.getView(position, convertView, parent);
				if(position == 0){					
					TextView t = (TextView)result.findViewById(R.id.textView_name);
					t.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, R.drawable.ic_action_important);	
					t.setPadding(5, 5, 5, 5);
				}
				return result;								
			}
		};		
		
		loaderProductCategoryCallback = new LoaderProductSalesCategory();
		
		produtAdpater = newProductAdapter(productSalesCategoryList);			
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(!(activity instanceof TransactionEditExtensionListener))
			throw new IllegalStateException("Activity must implements Fragment TransactionEditExtensionListener");
		transactionEditExtensionCallback = (TransactionEditExtensionListener)activity;
	}
	
	@Override
	public void onStart() {		
		super.onStart();		
	}
	
	private void stopProductEditMode(){
		if(gridViewProducts.isEditMode())
		{
			gridViewProducts.stopEditMode();
			gridViewProducts.clearDisappearingChildren();
		}
	}
	
	@Override
	public void onFragmentCreateView(View rootView, Bundle savedInstanceState) {		
		
		setListViewAdapter(R.id.listView_salesCategory, salesCategoryAdapter);
				
		if(Screen.isXLargeLayoutSize(this.getActivity()))
			numColums = 4;
		
		gridViewProducts = (DynamicGridView)rootView.findViewById(R.id.viewGroup_quickAddProduct);					
		
		gridViewProducts.setNumColumns(numColums);
		produtAdpater.setColumnCount(numColums);
		
		gridViewProducts.setAdapter(produtAdpater);		
		
		gridViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				
				Product product = new Product();
				ProductSalesCategory det = (ProductSalesCategory)produtAdpater.getItem(position);
				
				String description = det.getProduct().getDescription();
				double price = det.getProduct().getPrice();				
				int productId = det.getProductId();
						
				product.setDescription(description);
				product.setPrice(price);
				product.setProductId(productId);
				
				transactionEditExtensionCallback.addProduct(product);
			}
		});
		
			
		gridViewProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {				
				gridViewProducts.startEditMode();
                return false;
			}
		});
		
		gridViewProducts.setOnDropListener(new OnDropListener() {
			
			@Override
			public void onActionDrop() {				
				gridViewProducts.stopEditMode();
			}
		});
		
		if(transaction!=null){
			setTextViewDateText(R.id.textView_transaction_date, transaction.getTransactionDate());
			setTextViewText(R.id.textView_transaction_typeName, transaction.getTransactionTypeName());
			setTextViewText(R.id.textView_transaction_code, transaction.getTransactionCode());
		}
		
		if(salesCategoryId == 0){
			((ListView)rootView.findViewById(R.id.listView_salesCategory)).setItemChecked(0, true);
		}
		
		setListViewOnItemClickListener(R.id.listView_salesCategory, new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				
				stopProductEditMode();
				
				salesCategoryId = (int)id;
				
				Bundle args = new Bundle();
				args.putInt(LoaderProductSalesCategory.ARG_SALESCATEGORYID, salesCategoryId);
				getLoaderManager().restartLoader(LOADER_PRODUCTBYCATEGORY, args, 
						TransactionEditExtensionFragment.this.loaderProductCategoryCallback);
			}
		});
				
	}
	
	public void setTransactionValues(Transaction t){
		transaction = t;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		
		
		getLoaderManager().initLoader(LOADER_SALESCATEGORY, null, this);		
				
		Bundle args = new Bundle();
		args.putInt(LoaderProductSalesCategory.ARG_SALESCATEGORYID, salesCategoryId);
		getLoaderManager().initLoader(LOADER_PRODUCTBYCATEGORY, args, loaderProductCategoryCallback);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {		
		super.onCreateLoader(id, args);
		
		Loader<Cursor> loader = null;
		
		switch (id) {
		case LOADER_SALESCATEGORY :
			loader = new SimpleCursorLoader(this.getActivity()) {			
				@Override
				public Cursor loadInBackground() {				
					return SalesCategory.getSalesCategoryCursor(getDataBase());
				}
			};
			break;
		}
		
		return loader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {		
		super.onLoadFinished(loader, c);
		
		switch (loader.getId()) {
		case LOADER_SALESCATEGORY:
			
			salesCategoryAdapter.swapCursor(c);
			
			break;
		}	
		
	}	
	
	private TransactionEditQuickProductAdapter newProductAdapter(List<ProductSalesCategory> data){									
		return new TransactionEditQuickProductAdapter(this.getActivity(), data, 
				numColums, salesCategoryId == 0, new TransactionEditQuickProductAdapter.OnActionChangeListener() {
					@Override
					public void onChange(ProductSalesCategory p) {				
						ProductSalesCategory.update(getDataBase(), p);
					}
				});
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {		
		super.onLoaderReset(loader);
		switch (loader.getId()) {
		case LOADER_SALESCATEGORY:
			
			salesCategoryAdapter.swapCursor(null);
			
			break;			
		}				
	}
	
//	private void setProductButtons(Cursor c, int evaluateCategoryId){
//		
//		int columns = 0;
//		LinearLayout row = null;		
//		
//		int maxColumns = 0;
//		
//		if(Screen.isXLargeLayoutSize(getActivity()) && 
//				Screen.isXLargeLayoutSize(getActivity()))
//			maxColumns = 4;
//		else
//			maxColumns = 3;
//		
//		gridViewProducts.removeAllViews();		
//		
//		while(c.moveToNext()){
//			columns++;			
//			
//			if(row == null || columns == maxColumns + 1)			
//			{
//				row = new LinearLayout(getActivity());
//				row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,120));
//				gridViewProducts.addView(row);
//				columns = 1;
//			}
//			
//			String productName = CursorUtils.getString(c,Contract.ProductSalesCategory.FIELD_PRODUCT_DESCRIPTION);
//			int productId = CursorUtils.getInt(c,Contract.ProductSalesCategory.FIELD_PRODUCTID);
//			double price = CursorUtils.getInt(c,Contract.ProductSalesCategory.FIELD_PRODUCT_PRICE);
//			
//			boolean isFavorite = CursorUtils.getInt(c, Contract.ProductSalesCategory.FIELD_ISFAVORITE) == 1;
//			
//			Button button = new Button(getActivity());
//			button.setText(productName);
//			button.setTag(R.id.key_product_id,productId);
//			button.setTag(R.id.key_product_price,price);					
//			
//			button.setTop(0);
//			button.setLeft(0);
//			button.setRight(0);
//			button.setBottom(0);
//					 				
//			button.setLines(3);
//			button.setEllipsize(TruncateAt.END);
//			int dr = 0;
//			if(evaluateCategoryId == 0)
//				dr = R.drawable.ic_action_discard;
//			else
//			{
//				if(isFavorite)
//					dr = R.drawable.ic_action_important;
//				else
//					dr = R.drawable.ic_action_not_important;
//			}
//			
//			button.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, dr);
//			
//			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT,1);
//			lp.setMargins(0, 0, 0, 0);
//			
//			button.setLayoutParams(lp);
//			button.setGravity(Gravity.CENTER);
//			
//			button.setOnClickListener(this);
//			
//			row.addView(button);
//			
//		}
//		
//		for(int i = columns + 1; i <= maxColumns; i++){						
//			
//			Button layout = new Button(getActivity());
//			layout.setLayoutParams(new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT,1));
//			layout.setVisibility(View.INVISIBLE);
//			row.addView(layout);
//		}
//		
//		gridViewProducts.invalidate();
//		gridViewProducts.requestLayout();
//	}

//	@Override
//	public void onClick(View view) {
//		if(view instanceof Button){
//			Product product = new Product();
//			
//			String description = ((Button)view).getText().toString();
//			double price = (Double)view.getTag(R.id.key_product_price);
//			int productId = (Integer)view.getTag(R.id.key_product_id);
//					
//			product.setDescription(description);
//			product.setPrice(price);
//			product.setProductId(productId);
//			
//			transactionEditExtensionCallback.addProduct(product);
//		}
//	}
	
	public class LoaderProductSalesCategory implements LoaderCallbacks<List<ProductSalesCategory>>{
		
		public static final String ARG_SALESCATEGORYID = "salesCategoryId";
		private int mySalesCategory = 0;
		@Override
		public Loader<List<ProductSalesCategory>> onCreateLoader(int arg0,
				Bundle bundle) {				
			
			mySalesCategory = bundle.getInt(ARG_SALESCATEGORYID,0);			
			return new ProductSalesCategoryLoader(getActivity(),getDataBase(),salesCategoryId);
		}

		@Override
		public void onLoadFinished(Loader<List<ProductSalesCategory>> arg0,
				List<ProductSalesCategory> data)
		{								
			productSalesCategoryList.clear();
			productSalesCategoryList.addAll(data);
			produtAdpater.set(productSalesCategoryList);
			produtAdpater.isFavoriteList( mySalesCategory == 0 );
			produtAdpater.notifyDataSetChanged();
		}

		@Override
		public void onLoaderReset(Loader<List<ProductSalesCategory>> arg0) {			
			productSalesCategoryList.clear();
			produtAdpater.clear();			
			produtAdpater.set(productSalesCategoryList);
			produtAdpater.isFavoriteList( mySalesCategory == 0 );
			produtAdpater.notifyDataSetChanged();		
		}
		
	}
	
	
}
