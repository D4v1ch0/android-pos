package rp3.pos;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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
	public static final int NAV_CLOSE_SESSION = -4;

	public static final String AUTHORITY = "rp3.pos.provider";
	public static final String ACCOUNT = "default_account";
	public static final String ACCOUNT_TYPE = "rp3.pos";


	public static Intent newIntent(Context c, boolean clearTop){
		Intent i = new Intent(c,MainActivity.class);
		if(clearTop)
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		showNavHeader(true);
		setNavHeaderIcon(R.drawable.ic_action_person);
		setNavHeaderTitle(Session.getUser().getLogonName());
        
	}

	@Override
	public void onNavItemSelected(NavItem item) {
		super.onNavItemSelected(item);

		switch (item.getId()) {
		case NAV_HOME:
			break;
		case NAV_CLIENT:
			break;
		case NAV_CLOSE_SESSION:
			Session.logOut();
			startActivity( new Intent(this, StartActivity.class));
			finish();
			break;
		case NAV_SYNC:	
			showDialogProgress(R.string.message_title_synchronizing, R.string.message_please_wait);
			new AsyncTask<Object, Integer, Boolean>() {
				@Override
				protected Boolean doInBackground(Object... params) {				
					SystemClock.sleep(3000);
					return true;
				}
				@Override
				protected void onPostExecute(Boolean result) {								
					closeDialogProgress();
				}				
			}.execute();
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

	//NavItem home = new NavItem(NAV_HOME, "Inicio", R.drawable.ic_menu_home);
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
		settingGroup.addChildItem(new NavItem(NAV_SYNC, "Sincronizar",R.drawable.ic_action_refresh, NavItem.TYPE_ACTION));
		
		NavItem logOut = new NavItem(NAV_CLOSE_SESSION, R.string.action_logout, 0, NavItem.TYPE_ACTION);
		settingGroup.addChildItem(logOut);
		
		//navItems.add(home);
		navItems.add(documentGroup);
		//navItems.add(clientGroup);
		navItems.add(settingGroup);		
	}

}
