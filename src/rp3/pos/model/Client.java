package rp3.pos.model;

import rp3.db.sqlite.DataBase;
import rp3.pos.db.Contract;
import android.database.Cursor;

public class Client {

	private int id;
	private String cardId;
	private String names;
	private String lastNames;
	private String address;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getLastNames() {
		return lastNames;
	}
	public void setLastNames(String lastNames) {
		this.lastNames = lastNames;
	}
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names;
	}	
	
	public String getFullName() {
		return String.format("%s %s", this.getNames(), this.getLastNames());
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public static Cursor getClientSearchCursor(DataBase db, String termSearch)
	{		
		String query = Contract.Client.QUERY_CLIENT_SEARCH;
		
		Cursor c = db.rawQuery(query, "*" + termSearch + "*" );
		return c;
	}
	
	public static Client getClientById(DataBase db,int id){
		String query = Contract.Client.QUERY_CLIENT_BY_ID;
		
		Cursor c = db.rawQuery(query, id );
		c.moveToFirst();
		
		Client client = new Client();
		client.setCardId( c.getString( c.getColumnIndex(Contract.Client.FIELD_CARDID) ) );
		client.setId( c.getInt( c.getColumnIndex(Contract.Client._ID) ) );
		client.setLastNames( c.getString( c.getColumnIndex(Contract.Client.FIELD_LASTNAMES) ) );
		client.setNames( c.getString( c.getColumnIndex(Contract.Client.FIELD_NAMES) ) );
		client.setAddress( c.getString( c.getColumnIndex(Contract.Client.FIELD_ADDRESS) ) );
		
		return client;
	}

}
