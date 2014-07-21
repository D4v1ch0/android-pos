package rp3.pos;

import java.util.List;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import rp3.app.NavActivity;
import rp3.app.nav.NavItem;
import rp3.core.R;
import rp3.pos.db.DbOpenHelper;
import rp3.pos.model.TransactionType;
import rp3.runtime.Session;

public class MainActivity extends NavActivity {

	public static final int NAV_HOME = -1;
	public static final int NAV_CLIENT = -2;
	public static final int NAV_SYNC = -3;

	public static final String AUTHORITY = "rp3.pos.provider";
	public static final String ACCOUNT = "default_account";
	public static final String ACCOUNT_TYPE = "rp3.pos";


	public static Intent newIntent(Context c){
		Intent i = new Intent(c,MainActivity.class);
		return i;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		
		Session.Start(this);
		rp3.configuration.Configuration.TryInitializeConfiguration(this, DbOpenHelper.class);
        
		if(savedInstanceState == null){
			
			TransactionType transactionType = TransactionType.getDefaultTransactionType(getDataBase());			
			setNavigationSelection(transactionType.getTransactionTypeId());  
			
		}					    
        
	}

	@Override
	public void onNavItemSelected(NavItem item) {
		super.onNavItemSelected(item);

		switch (item.getId()) {
		case NAV_HOME:
			break;
		case NAV_CLIENT:
			break;
		case NAV_SYNC:		
			break;
		default:
			setNavFragment(TransactionFragment.newInstance(item.getId()),
					item.getTitle());
			break;
		}
	}

	@Override
	public void navConfig(List<NavItem> navItems, NavActivity currentActivity) {
		super.navConfig(navItems, currentActivity);

		NavItem home = new NavItem(NAV_HOME, "Inicio", R.drawable.ic_menu_home);
		NavItem documentGroup = new NavItem(0, "DOCUMENTOS", 0, NavItem.TYPE_CATEGORY);

		List<TransactionType> transactionTypes = TransactionType
				.getActiveTransactionTypes(getDataBase(), false);

		for (TransactionType type : transactionTypes) {
			documentGroup.addChildItem(new NavItem(type.getTransactionTypeId(),
					type.getName(), R.drawable.ic_action_collection));
		}

		NavItem clientGroup = new NavItem(0, "OTROS", 0, NavItem.TYPE_CATEGORY);
		clientGroup.addChildItem(new NavItem(NAV_CLIENT, "Clientes",
				R.drawable.ic_action_person));

		NavItem settingGroup = new NavItem(0, "CONFIGURACION", 0, NavItem.TYPE_CATEGORY);
		settingGroup.addChildItem(new NavItem(NAV_SYNC, "Sincronizar", R.drawable.ic_action_refresh));
		
		navItems.add(home);
		navItems.add(documentGroup);
		navItems.add(clientGroup);
		navItems.add(settingGroup);
	}

}
