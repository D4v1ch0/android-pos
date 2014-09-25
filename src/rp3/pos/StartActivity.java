package rp3.pos;

import rp3.configuration.Configuration;
import rp3.content.SimpleCallback;
import rp3.data.MessageCollection;
import rp3.pos.accounts.MyServerAuthenticate;
import rp3.pos.db.DbOpenHelper;
import rp3.pos.sync.SyncAdapter;
import rp3.sync.SyncAudit;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;


public class StartActivity  extends rp3.app.StartActivity {
	
	public StartActivity(){
		//rp3.accounts.Authenticator.setServerAuthenticate(new MyServerAuthenticate());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		Configuration.TryInitializeConfiguration(this, DbOpenHelper.class);	
	}
	
	@Override
	public void onContinue() {		
		super.onContinue();
		
		Long days = SyncAudit.getDaysOfLastSync(SyncAdapter.SYNC_TYPE_GENERAL, SyncAdapter.SYNC_EVENT_SUCCESS);
		
		if(days == null || days > 0){
			Bundle bundle = new Bundle();
			bundle.putString(SyncAdapter.ARG_SYNC_TYPE, SyncAdapter.SYNC_TYPE_GENERAL);
			requestSync(bundle);
		}else{
			callNextActivity();
		}
	}
	
	public void onSyncComplete(Bundle data, final MessageCollection messages) {
		if(messages.getCuount()>0)
			showDialogMessage(messages, new SimpleCallback() {				
				@Override
				public void onExecute(Object... params) {
					if(!messages.hasErrorMessage())
						callNextActivity();
					else
						finish();
				}
			});
		else
			callNextActivity();
	}
	
	
	private void callNextActivity(){
		startActivity(MainActivity.newIntent(this,false));
		finish();
	}
	
}
