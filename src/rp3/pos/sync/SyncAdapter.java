package rp3.pos.sync;

import org.ksoap2.serialization.SoapObject;

import rp3.configuration.Configuration;
import rp3.connection.WebService;
import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class SyncAdapter extends rp3.content.SyncAdapter {

	
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);		
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {		
		Configuration.TryInitializeConfiguration(this.getContext());		
		
		android.os.Debug.waitForDebugger();
		WebService service = new WebService("tran_soap_maestro", "GetTransactions");

		//service.invokeWebService();
		
		SoapObject response = service.getSoapObjectResponse();
		
	}
	
}
