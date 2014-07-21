package rp3.pos.nav;

import java.util.List;

import android.content.Context;

import rp3.pos.R;
import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.TransactionType;


import rp3.app.NavActivity;
import rp3.app.nav.NavItem;
import rp3.db.sqlite.DataBase;
import rp3.db.sqlite.DataBaseServiceHelper;


public class Nav implements rp3.app.nav.NavSetting {

	public static final int HOME = -1;
	public static final int CLIENT = -2;	
	
	private Context context;
	//private NavActivity currentActivity;
	
	@Override
	public void navConfig(List<NavItem> navItems, NavActivity currentActivity) {
		//this.currentActivity =  currentActivity;
		this.context = currentActivity.getApplicationContext();
		DataBase db = DataBaseServiceHelper.getWritableDatabase(context, DbOpenHelper.class);
		
		NavItem home = new NavItem(HOME,"Inicio", R.drawable.ic_menu_home);
		NavItem documentGroup = new NavItem(0,"DOCUMENTOS", 0, NavItem.TYPE_CATEGORY);
		
		List<TransactionType> transactionTypes = TransactionType.getActiveTransactionTypes(db, false);
		
		for(TransactionType type : transactionTypes){
			documentGroup.addChildItem(new NavItem(type.getTransactionTypeId(), type.getName(), R.drawable.ic_action_collection));	
		}				
		
		NavItem clientGroup = new NavItem(0,"OTROS", 0, NavItem.TYPE_CATEGORY);		
		clientGroup.addChildItem(new NavItem(CLIENT,"Clientes", R.drawable.ic_action_person));
		
		navItems.add(home);
		navItems.add(documentGroup);
		navItems.add(clientGroup);
	}

	@Override
	public void onNavItemSelected(NavItem item) {
		
		switch (item.getId()) {
		case HOME:
			
			break;
		case CLIENT:
			
			break;

		default:
			
			//currentActivity.startActivity(TransactionListActivity.newIntent(context, id));			
			//currentActivity.finish();
			
			break;
		}
		
	}

}
