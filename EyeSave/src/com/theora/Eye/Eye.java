package com.theora.Eye;

import java.util.List;

import com.theora.M.Mcontroller;
import com.theora.M.MmodelRow;
import com.theora.M.Mtime;
import com.theora.M.Mview;
import com.theora.M.Mview.RowClickListener;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
an error to test eclipse closed projects
public class Eye extends Activity implements LocationListener, RowClickListener {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private LocationManager mgr = null;
	private String showingProvider = null; // if null, showing providers table
	private String best = null;
	/*---------------------------------*/
	private static final String[] A = { "invalid", "n/a", "fine", "coarse" };
	private static final String[] P = { "invalid", "n/a", "low", "medium", "high" };
	private static final String[] S = { "out of service", "temporarily unavailable", "available" };
	/*------------------------------------------------------------*/
	public Eye() {
		m = new Mcontroller(this, "mdb");
	}
	/*------------------------------------------------------------*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    mgr = (LocationManager) getSystemService(LOCATION_SERVICE); 
        createTables();
    }
	/*------------------------------------------------------------*/
    private void createTables() {
    	m.model.sql("drop table if exists providers");
    	String providers = "create table providers ( " +
			"id integer primary key autoincrement" +
			", name char(255)" +
			", enabled char(4)" +
			", status char(255)" +
			", best char(12)" +
			", lastUpdate date" +
			" )";
    	if ( ! m.model.sql(providers) )
    		m.view.msg("Failed to create table providers");
    	m.model.sql("drop table if exists providerDetails");
    	String providerDetails = "create table providerDetails ( " +
			"id integer primary key autoincrement" +
			", providerId int" +
			", description char(255)" +
			", value char(255)" +
			" )";
    	if ( ! m.model.sql(providerDetails) )
    		m.view.msg("Failed to create table providerDetails");
    }
	/*------------------------------------------------------------*/
    @Override
    protected void onResume() {
       super.onResume();
       Criteria criteria = new Criteria(); 
       best = mgr.getBestProvider(criteria, true);
       log("Best provider is: " + best);
       refreshProviders();
       mgr.requestLocationUpdates(best, 15000, 1, this);
       Location location = mgr.getLastKnownLocation(best); 
       dumpLocation(location);
       if ( showingProvider != null ) {
    	   // mgr.requestLocationUpdates(showingProvider, 15000, 1, this);
    	   showProvider();
       } else {
    	   showProviders();
       }
    }
	/*------------------------------------------------------------*/
    private boolean isBest(String provider) {
        return(best.compareTo(provider) == 0);
    }
	/*----------------------------*/
    private String isBestStr(String provider) {
        return(isBest(provider) ? "*" : "");
    }
	/*------------------------------------------------------------*/
    @Override
    protected void onPause() {
       super.onPause();
       // Stop updates to save power while app paused
       mgr.removeUpdates(this);
    }
	/*------------------------------------------------------------*/
    public void onLocationChanged(Location location) {
       dumpLocation(location);
    }
	/*------------------------------------------------------------*/
    public void onProviderDisabled(String provider) {
    	log("Provider disabled: " + provider);
    	updateEnabled(provider, false);
    }
	/*-------------------------------------*/
    public void onProviderEnabled(String provider) {
    	log("Provider Enabled: " + provider);
    	updateEnabled(provider, false);
    }
	/*-------------------------------------*/
    public void updateEnabled(String provider, boolean enabled) {
    	int id = providerId(provider);
    	String sql = String.format("update providers set enabled = '%s' where id = %d",
    			enabled ? "*" : "",
    			id);
		m.model.sql(sql);
    	 if ( showingProvider == null )
    		 showProviders();
    	 else if ( showingProvider.compareTo(provider) == 0 )
    		showProvider();
    	 else
    		 m.view.msg(provider + " now " + (enabled ? "enabled" : "disabled"));
    }
	/*------------------------------------------------------------*/
    private String now() {
    	return(Mtime.fmt(Mtime.now()));
    }
	/*------------------------------------------------------------*/
    public int newProvider(String provider) {
	    int id = providerId(provider);
	    if ( id > 0 )
	    	m.model.sql("delete from providers where id = " + id);
    	String sql = String.format(
    			"insert into providers ( name, best, lastUpdate) values ( '%s', '%s', '%s' )",
    			provider, isBestStr(provider), now());
    	id = m.model.insert(sql);
    	return(id);
    	
    }
	/*------------------------------------------------------------*/
	private void addProviderDetail(int providerId, String description, String value) {
		String sql = "insert into providerDetails ( providerId, description, value ) values ( " + 
			String.format("%d, '%s', '%s' )", providerId, description, value);
		m.model.insert(sql);
	}
	/*------------------------------------------------------------*/
	private void addProviderDetail(int providerId, String description, boolean value) {
		addProviderDetail(providerId, description, value ? "*" : "");
	}
	/*------------------------------------------------------------*/
    public int updateProviderDetails() {
    	int providerId = providerId(showingProvider);
    	if ( providerId == 0 ) {
    		log("updateProviderDetails: Eh: " + showingProvider);
    		return(0);
    	} else {
	    	m.model.sql("delete from providerDetails where providerId = " + providerId);    		
    	}
		m.model.sql("delete from providerDetails where id = " + providerId);
	    LocationProvider info = mgr.getProvider(showingProvider);
		addProviderDetail(providerId, "name", info.getName());
		addProviderDetail(providerId, "enabled", mgr.isProviderEnabled(showingProvider));
		addProviderDetail(providerId, "accuracy", A[info.getAccuracy() + 1]);
		addProviderDetail(providerId, "powerRequirement", P[info.getPowerRequirement() + 1]);
		addProviderDetail(providerId, "hasMonetaryCost", info.hasMonetaryCost());
		addProviderDetail(providerId, "requiresCell", info.requiresCell());
		addProviderDetail(providerId, "requiresNetwork", info.requiresNetwork());
		addProviderDetail(providerId, "requiresSatellite", info.requiresSatellite());
		addProviderDetail(providerId, "supportsAltitude", info.supportsAltitude());
		addProviderDetail(providerId, "supportsBearing", info.supportsBearing());
		addProviderDetail(providerId, "supportsSpeed", info.supportsSpeed());
		return(providerId);
    }
	/*------------------------------------------------------------*/
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	String description = S[status];
       log("Provider status changed: " + provider + ", status="
             + description + ", extras=" + extras);
       String sql = String.format(
    		   	"update providers set status = '%s', lastUpdate = '%s' where id = %d",
    		   	description,
    		   	now(),
    		   	providerId(provider)
    	);
       m.model.sql(sql);
       if ( showingProvider.compareTo(provider) == 0)
    	   showProvider();
       else if ( showingProvider == null )
    	   showProviders();
       else
    	   m.view.msg(provider + " now " + description);
    }
/*------------------------------------------------------------*/
    private void refreshProviders() {
        List<String> providersList = mgr.getAllProviders();
    	for ( String provider : providersList )
    		newProvider(provider);
    }
	/*------------------------------------------------------------*/
    private void showProviders() {
    	String sql = "select id, name, lastUpdate as tm, status, enabled from providers order by name";
    	MmodelRow providers[] = m.model.getRows(sql);
    	m.view.reset();
    	m.view.registerModifier("name", new Mview.Modifier() {
			@Override
			public String modify(String value) {
				if ( isBest(value) )
					return(value + "*");
				else
					return(value);
			}
		});
    	m.view.setRowClickListener(this);
    	m.view.showRows(providers, Mview.LARGE);
    }
	/*------------------------------------------------------------*/
    public void showProvider() {
		updateProviderDetails();
    	int id = providerId(showingProvider);
    	String sql = "select id, description, value from providerDetails where providerId = " + id + " order by id";
    	MmodelRow rows[] = m.model.getRows(sql);
    	m.view.reset();
    	m.view.setNoHeaders();
    	m.view.setRowClickListener(this);
    	m.view.showRows(rows, providerName(id), Mview.MEDIUM);
    }
	/*------------------------------------------------------------*/
    private String providerName(int id) {
		String provider = m.model.getString("select name from providers where id = " + id);
		return(provider);
    }
	/*------------------------------------------------------------*/
    private int providerId(String name) {
		String sql = String.format("select id from providers where name = '%s'", name);
		int  id = m.model.getInt(sql);
		if ( id <= 0 )
				return(0);
		return(id);
    	
    }
	/*------------------------------------------------------------*/
    private void log(String s) {
    	Log.d("Eye", s);
    }
	/*------------------------------------------------------------*/
	@Override
	public void rowClicked(int id) {
		if ( showingProvider == null ) {
			showingProvider = providerName(id);			
			showProvider();
		} else {
			showingProvider = null;
			showProviders();
		}
	}
	/*------------------------------------------------------------*/
	private void dumpLocation(Location location) {
		if (location == null)
			log("Location[unknown]");
		else
			log(location.toString());
		   }
	/*------------------------------------------------------------*/
}
