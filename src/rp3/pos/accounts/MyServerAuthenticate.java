package rp3.pos.accounts;


import org.json.JSONObject;

import rp3.accounts.ServerAuthenticate;
import rp3.accounts.User;
import rp3.connection.WebService;
import rp3.runtime.Session;
import android.accounts.AccountManager;

import android.os.Bundle;


public class MyServerAuthenticate implements ServerAuthenticate {
	
	public Bundle signUp(final String name, final String email, final String pass, String authType){
		return null;
	}
	
    public Bundle signIn(final String user, final String pass, String authType) {
    	
    	WebService method = new WebService();
    	method.setConfigurationName("account", "signin");
    	method.setAuthTokenType(authType);
    	    	
    	
    	method.addParameter("LogonName", user);
    	method.addParameter("Password", pass);
    	
    	Bundle bundle = new Bundle();
    		
    	
    	try {
			method.invokeWebService();
						
			JSONObject response = method.getJSONObjectResponse();
			
			String authToken = response.getJSONObject("Data").getString("AuthToken");
			
			bundle.putString(ServerAuthenticate.KEY_ERROR_MESSAGE, "");
			bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			bundle.putBoolean(ServerAuthenticate.KEY_SUCCESS, response.getJSONObject("Data").getBoolean("IsValid"));			
    	}
    	catch(Exception e) {
    		bundle.putString(ServerAuthenticate.KEY_ERROR_MESSAGE, "ERROR");
    		bundle.putString(AccountManager.KEY_AUTHTOKEN, "");
			bundle.putBoolean(ServerAuthenticate.KEY_SUCCESS, false);
    	}
    	
    	
			    		    	
		
    	return bundle;	       	    	
    }

	@Override
	public boolean requestSignIn() {		
		Bundle data = signIn(Session.getUser().getLogonName(), Session.getUser().getPassword(), User.getAccountType());
		String token = data.getString(AccountManager.KEY_AUTHTOKEN);
		return token ==null || token.length() == 0;
	}
}
