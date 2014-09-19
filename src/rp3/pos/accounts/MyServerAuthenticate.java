package rp3.pos.accounts;


import rp3.accounts.ServerAuthenticate;
import android.accounts.AccountManager;

import android.os.Bundle;


public class MyServerAuthenticate implements ServerAuthenticate {
	
	public Bundle signUp(final String name, final String email, final String pass, String authType){
		return null;
	}
	
    public Bundle signIn(final String user, final String pass, String authType) {
    	Bundle bundle = new Bundle();
    		
		bundle.putString(ServerAuthenticate.KEY_ERROR_MESSAGE, "");
		bundle.putString(AccountManager.KEY_AUTHTOKEN, "OK");
		bundle.putBoolean(ServerAuthenticate.KEY_SUCCESS, true);	    		    	
		
    	return bundle;	       	    	
    }

	@Override
	public boolean requestSignIn() {		
		return false;
	}
}
