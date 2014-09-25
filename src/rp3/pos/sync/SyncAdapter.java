package rp3.pos.sync;

import rp3.db.sqlite.DataBase;
import rp3.sync.SyncAudit;
import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends rp3.content.SyncAdapter {
	
	public static String SYNC_TYPE_GENERAL = "general";
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);		
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {		
		super.onPerformSync(account, extras, authority, provider, syncResult);	
		
		//android.os.Debug.waitForDebugger();
		String syncType = extras.getString(ARG_SYNC_TYPE);
		
		DataBase db = null;		
		int result = 0;
		
		try{
			db = DataBase.newDataBase(rp3.pos.db.DbOpenHelper.class);
			
			if(syncType == null || syncType.equals(SYNC_TYPE_GENERAL)){								
				db.beginTransaction();
				
				result = rp3.sync.GeopoliticalStructure.executeSync(db);				
				addDefaultMessage(result);
				
				if(result == SYNC_EVENT_SUCCESS){
					result = rp3.sync.GeneralValue.executeSync(db);
					addDefaultMessage(result);
				}
				
				if(result == SYNC_EVENT_SUCCESS){
					result = rp3.sync.IdentificationType.executeSync(db);
					addDefaultMessage(result);
				}
				
								
				db.commitTransaction();												
			}
			
		SyncAudit.insert(syncType, result);
				
		}catch (Exception e) {			
			Log.e(TAG, "E: " + e.getMessage());
			addDefaultMessage(SYNC_EVENT_ERROR);
			SyncAudit.insert(syncType, SYNC_EVENT_ERROR);
		} 
		finally{			
			db.endTransaction();
			db.close();
			
			notifySyncFinish();
		}								
	}
	
}
