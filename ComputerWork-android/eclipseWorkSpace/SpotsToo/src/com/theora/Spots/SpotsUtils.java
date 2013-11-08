package com.theora.Spots;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import com.google.android.maps.GeoPoint;
import com.theora.M.Mcontroller;
import com.theora.M.MmodelRow;
import com.theora.M.Mtime;
import com.theora.M.Mutils;
import com.theora.M.AppsByOhad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class SpotsUtils {
	private Activity a;
	private Mcontroller m;
	public static final String lastLabel = "Last Viewed";
	public static final String sentLocation = "From SMS";
	/*------------------------------------------------------------*/
	public SpotsUtils(Activity a) {
		this.a = a;
		m = new Mcontroller(a, "mdb");
	}
	/*------------------------------------------------------------*/
	public  boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = a.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return(true);
	}
	/*------------------------------*/
	public  boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.comeHere:
				m.utils.logInfo("comeHere");
				Spots.comeHereRequest();
			return(true);
			case R.id.goToCar:
				m.utils.logInfo("goToCar");
				goToCar();
			return(true);
			case R.id.smsMyLocation:
				smsMyLocation();
			return(true);
			case R.id.toggleCompass:
				m.utils.logInfo("toggleCompass");
				Spots.toggleCompass();
			return(true);
			case R.id.zoom:
				m.utils.logInfo("zoom");
				zoom();
			return(true);
			case R.id.listSpots:
				m.utils.logInfo("listSpots");
				a.startActivity(new Intent(a, ListSpots.class));
			return(true);
			case R.id.satellite:
				m.utils.logInfo("satellite");
				Spots.satellite();
			return(true);
			case R.id.traffic:
				m.utils.logInfo("traffic");
				Spots.traffic();
			return(true);
			case R.id.otherApps:
				m.utils.logInfo("AppsByOhad");
				a.startActivity(new Intent(a, AppsByOhad.class));
			return(true);
		}
		return false;
	}
	/*------------------------------------------------------------*/
	private void zoom() {
		Intent i = new Intent(a, SelectZoom.class);
		a.startActivity(i);
	}
	/*------------------------------------------------------------*/
	public void goTo(int rowId) {
		MmodelRow row = m.model.getById("spots", rowId);
		if ( row != null )
			goTo(row);
	}
	/*------------------------------------------------------------*/
	public void goTo(MmodelRow row) {
		String label = row.getValue("label");
		String latitude = row.getValue("latitude");
		String longitude = row.getValue("longitude");
		String zoomLevel = row.getValue("zoomLevel");
		boolean isCompass = row.getBoolean("isCompass");
		boolean isSatellite = row.getBoolean("isSatellite");
		boolean isTraffic = row.getBoolean("isTraffic");
		int lon = Integer.parseInt(longitude);
		int lat = Integer.parseInt(latitude);
		int zl = Integer.parseInt(zoomLevel);
		Spots.setSpot(label, lon, lat, zl, isCompass, isSatellite, isTraffic); 
	}
	/*------------------------------------------------------------*/
	private void goToCar() {
		int rowId = m.model.getInt("select id from spots where label = 'Car'");
		if ( rowId > 0 )
			Spots.fromHereReqeuest(rowId);
		else
			m.view.msg("Car location unknown");
	}
	/*------------------------------------------------------------*/
	public void createTable() {
		// m.model.sql("drop table if exists spots");
		String sql = "create table if not exists spots (" +
			"id integer primary key autoincrement" +
			", label char(80) collate nocase" +
			", latitude int" +
			", longitude int" +
			", zoomLevel int" + 
			", isCompass char(8)" + 
			", isSatellite char(8)" + 
			", isTraffic char(8)" + 
			", created datetime" +
			", updated datetime" +
			", unique (label)" +
			" )";
		m.model.sql(sql);
	}
	/*------------------------------------------------------------*/
	public String label(int rowId) {
		return(m.model.getString("select label from spots where id = " + rowId));
	}
	/*------------------------------------------------------------*/
	public String makeNewLabel(String label) {
		return(makeNewLabel(label, null));
	}
	/*------------------------------------------------------------*/
	public String makeNewLabel(String label, String oldLabel) {
		String tryLabel = label;
		for(int i=2;;i++) {
			if ( ! isLabel(tryLabel, oldLabel) )
				return(tryLabel);
			tryLabel = label + " #" + i;
		}
	}
	/*-------------------------------*/
	// is this label only in the database
	// except 
	private boolean isLabel(String label, String except) {
		String exceptCond;
		if ( except != null )
			exceptCond = String.format("and label != '%s'", m.model.str(except));
		else
			exceptCond = "";
		String sql = String.format("select count(*) from spots where label = '%s' %s", m.model.str(label), exceptCond);
		return(m.model.getInt(sql) > 0);
	}
	/*------------------------------------------------------------*/
	public int insert(String label, int latitude, int longitude, int zoomLevel,
							boolean isCompass, boolean isSatellite, boolean isTraffic) {
		createTable();
		String nv[][] = {
				{ "label", label }
				, { "latitude", "" + latitude }
				, { "longitude", "" + longitude }
				, { "zoomLevel", "" + zoomLevel }
				, { "isCompass", "" + new Boolean(isCompass).toString() }
				, { "isSatellite", "" + new Boolean(isSatellite).toString() }
				, { "isTraffic", "" + new Boolean(isTraffic).toString() }
				, { "created", Mtime.dateTimeNow() }
				, { "updated", Mtime.dateTimeNow() }
		};
		int id = m.model.insert("spots", nv);
		return(id);		
	}
	/*------------------------------------------------------------*/
	public void update(int rowId, GeoPoint geo, int zoomLevel, boolean isCompass, boolean isSatellite, boolean isTraffic) {
		int lat = geo.getLatitudeE6();
		int lon = geo.getLongitudeE6();
		String nv[][] = {
				{ "latitude", "" + lat }
				, { "longitude", "" + lon }
				, { "updated", Mtime.dateTimeNow() }
				, { "isCompass", "" + new Boolean(isCompass).toString() }
				, { "isSatellite", "" + new Boolean(isSatellite).toString() }
				, { "isTraffic", "" + new Boolean(isTraffic).toString() }
		};
		m.model.update("spots", rowId, nv);	
		if ( zoomLevel > 0 ) {
			String zl[][] = {{ "zoomLevel", "" + zoomLevel }};
			m.model.update("spots", rowId, zl);
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * degree representation of  lat/lon e6 value
	 */
	public String degreeString(int e6) {
		double d = e6 / 1000000.0 ;
		int degrees = (int)d;
		double fraction = d - degrees;
		double minutesAndSeconds = fraction * 60 ;
		int minutes = (int)minutesAndSeconds;
		double seconds = (minutesAndSeconds - minutes) * 60;
		int iSeconds = (int)seconds;
		String ret = String.format("%02d°%02d'%02d\"", degrees, minutes, iSeconds);
		return(ret);
		
	}
	/*------------------------------------------------------------*/
	/**
	 * the list of spots, not including the last viewed
	 */
	public MmodelRow[] spotsRows() {
		String sql = String.format("select * from spots where label != '%s' order by label", lastLabel);
		MmodelRow rows[] = m.model.getRows(sql);
		return(rows);
	}
	/*------------------------------------------------------------*/
	public void log(String str) {
		Log.d("Spots", str);
	}
	/*------------------------------------------------------------*/
	public String timeFmt(String value) {
		String datetime[] = value.split(" ");
		String ymd[] = datetime[0].split("-");
		String hmsf[] = datetime[1].split("\\.");
		String hms[] = hmsf[0].split(":");
		int d = Integer.parseInt(ymd[2]);
		int m = Integer.parseInt(ymd[1]);
		int hh = Integer.parseInt(hms[0]);
		String ret = "" + m + "/" + d + " " + hh + ":" + hms[1];
		return(ret);
	}
	/*------------------------------------------------------------*/
	public void delete(int rowId) {
		m.model.delete("spots", rowId);
		Spots.spotsLayerIsDirty();
	}
	/*------------------------------------------------------------*/
	public void setBackground() {
		LinearLayout ll = m.view.initView();
		View v = new View(a);
		Drawable map =  m.utils.getDrawable("map");
		v.setBackgroundDrawable(map);
		ll.addView(v);
	}
	/*------------------------------------------------------------*/
	public double distanceBetween(GeoPoint a, GeoPoint b) {
		float results[] = new float[1];
		double startLatitude = a.getLatitudeE6() / 1000000.0 ;
		double startLongitude = a.getLongitudeE6() / 1000000.0 ;
		double endLatitude = b.getLatitudeE6() / 1000000.0 ;
		double endLongitude = b.getLongitudeE6() / 1000000.0 ;
		Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
		return(results[0]);
	}
	/*------------------------------------------------------------*/
	public void edit(int rowId) {
		Intent i = new Intent(a, Edit.class);
		i.putExtra("Spots.rowId", rowId);
		a.startActivity(i);
	}
	/*------------------------------------------------------------*/
	public String address(double lat, double lon) {
		Geocoder coder = new Geocoder(a);
		try {
			List<Address> results = coder.getFromLocation(lat, lon, 2);
			 Address address = results.get(0);
			 String ret = address.getAddressLine(0);
			 if ( ret == null )
				 return(null); // this is not an error
			 try {
				 String address2 = address.getAddressLine(1);
				 if ( address2 != null )
					 ret += ", " + address2;
			} catch (Exception e) {
			}
			 try {
				 String address3 = address.getAddressLine(2);
				 if ( address3 != null )
					 ret += ", " + address3;
			} catch (Exception e) {
			}
			return(ret);
		} catch (IOException e) {
			 m.utils.logError("Fail1");
			return(null);
		} catch ( Exception e ) {
			 m.utils.logError("Fail2");
			return(null);
		}
	}
	/*------------------------------------------------------------*/
	/*
	 * return 0 if intent is not relevant
	 * let it throw if an error occurs
	 * so error is reported by the calling method
	 * other args in the query may be present from theora redirects
	 */
	private int tryFromIntent(Intent intent) throws Exception {
		String action = intent.getAction();
		if ( action.compareTo(Intent.ACTION_VIEW) != 0)
			return(0);
		Uri uri = intent.getData();
		String query = uri.getQuery();
		if ( query == null )
			return(0);
		String nv[][] = {
				{ "geo", null },
				{ "label", null },
				{ "zoomLevel", null },
				{ "isCompass", null },
				{ "isSatellite", null },
				{ "isTraffic", null },
		};
		for(int i=0;i<6;i++)
			nv[i][1] = uri.getQueryParameter(nv[i][0]);
		String latLon[] = nv[0][1].split(",");
		String label;
		try {
			label = URLDecoder.decode(nv[1][1], "UTF-8");
		} catch (Exception e) {
			label = nv[1][1].replace('+', ' ');
		}
		if ( label.compareTo(sentLocation) == 0 )
			m.model.sql("delete from spots where label = '" + sentLocation +"'");
		else
			label = makeNewLabel(label);
		int rowId = insert(
				label,
				(int)((new Double(latLon[0])).doubleValue() * 1000000), // lat
				(int)((new Double(latLon[1])).doubleValue() * 1000000), //lon
				(new Integer(nv[2][1])).intValue(), // zoomLevel
				(new Boolean(nv[3][1])).booleanValue(), // isCompass
				(new Boolean(nv[4][1])).booleanValue(), // isSattelite
				(new Boolean(nv[5][1])).booleanValue() // isTraffic
		);
		return(rowId);
	}
	/*------------------------------------------------------------*/
	public int fromIntent(Intent i) {
		try {
			int intentRow = tryFromIntent(i);
			if ( intentRow == 0 )
				return(0);
			String label = label(intentRow);
			if ( label.compareTo(sentLocation) == 0 )
				m.utils.logInfo("Sent Location");
			else
				m.utils.logInfo("Sent Spot");
			return(intentRow);
		} catch (Exception e) {
			m.utils.logError("Fail");
			return(0);
		}
	}
	/*------------------------------------------------------------*/
	public GeoPoint rowGeo(int rowId) {
		return(rowGeo(m.model.getById("spots", rowId)));
	}
	/*------------------------------------------------------------*/
	public GeoPoint rowGeo(MmodelRow row) {
		String latitude = row.getValue("latitude");
		String longitude = row.getValue("longitude");
		int lat = Integer.parseInt(latitude);
		int lon = Integer.parseInt(longitude);
		GeoPoint geo = new GeoPoint(lat, lon);
		return(geo);
	}
	/*------------------------------------------------------------*/
	public void sms(int rowId) {
		m.utils.logInfo();
		MmodelRow row = m.model.getById("spots", rowId);
		double lat = row.getInt("latitude") / 1000000.0;
		double lon = row.getInt("longitude")/ 1000000.0;
		String fields[] = { "geo", "label", "zoomLevel", "isCompass", "isSatellite", "isTraffic" };
		String query = "geo=" + Mutils.format(lat, 6) + "," + Mutils.format(lon, 6);
		for(int i=1;i<6;i++)
			query += "&" + fields[i] + "=" + row.getValue(fields[i]);
		
		String url = "http://spots.theora.com/spots.php?" + query;
		Intent intent = new Intent(Intent.ACTION_VIEW);         
		intent.setData(Uri.parse("sms:"));
		intent.putExtra("sms_body", url);
		a.startActivity(intent);
	}
	/*------------------------------------------------------------*/
	public void smsMyLocation() {
		m.utils.logInfo();
		LocationManager locationManager  = (LocationManager)a.getSystemService(Context.LOCATION_SERVICE);
        if ( locationManager == null ) { 
            m.view.msg("No location manger");
            return;
        }
        Location here = locationManager.getLastKnownLocation("network");
        if ( here == null ) { 
            m.view.msg("Current location unknown");
            return;
        }
        double lat = here.getLatitude();
        double lon = here.getLongitude();
        String label = null;
        try {
			label = URLEncoder.encode(sentLocation, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			label = sentLocation.replace(' ', '+');
		}
		String query = "geo=" + Mutils.format(lat, 6) + "," + Mutils.format(lon, 6) +
			"&label=" +  label +
			"&zoomLevel=18&isCompass=true&isSatellite=true&isTraffic=false";
		
		String url = "http://spots.theora.com/spots.php?" + query;
		Intent intent = new Intent(Intent.ACTION_VIEW);         
		intent.setData(Uri.parse("sms:"));
		intent.putExtra("sms_body", url);
		a.startActivity(intent);
	}
	/*------------------------------------------------------------*/
}
