package rp3.pos;

import pe.com.maestro.commercial.StartActivity;
import pe.com.maestro.commercial.StoreSelectorActivity;
import pe.com.maestro.commercial.db.DbOpenHelper;
import rp3.configuration.Configuration;
import android.os.Bundle;


public class StartActivity  extends rp3.app.StartActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		Configuration.TryInitializeConfiguration(this, DbOpenHelper.class);	
	}
	
	@Override
	public void onContinue() {		
		super.onContinue();
		
		
	}
	
	private void callNextActivity(){
		startActivity();
		finish();
	}
}
