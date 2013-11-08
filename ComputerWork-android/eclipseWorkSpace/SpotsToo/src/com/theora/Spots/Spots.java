
package com.theora.Spots;

import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.theora.M.*;

public class Spots extends MapActivity {
	/*------------------------------------------------------------*/
	private String apiDevKey = "0A3OP0pX5ZL23lxbww7vuu-8ZV6eFSlL0SAlpug";
	private String apiDeployKey = "0A3OP0pX5ZL2o6h7RubTlGFnfqI0jqbtvArFFBA";
	private String apiKey = apiDevKey;
	private Mcontroller m = null;
	private SpotsUtils utils = null; 
	private MapView map = null;
	private MapController controller = null;
	private LocationManager locationManager = null;
    private MyLocationOverlay myLocationOverlay = null;

	@SuppressWarnings("unused")
	private static boolean spotsLayerIsDirty = true;
	private static String spotLabel = null;
	private static int spotLatitude = 0;
	private static int spotLongitude = 0;
	private static int spotZoomLevel = 0;
	private static boolean spotIsDirty = false;
	private static boolean isCompass = false;
	private static boolean isSatellite = true;
	private static boolean isTraffic = true;
	private static boolean comeHereRequested = false;
	private static int zoomLevelRequested = 0;
	private static int adjustOnNextTap = 0;
	private static int fromHereRequested = 0;
	private List<Overlay> overlays = null;
	private Drawable marker = null;
	private Drawable car = null;
	private Drawable person = null;

	private SpotsOverlay spotsOverlay = null;
	private SpotsOverlay carOverlay = null;
	private SpotsOverlay sentLocationOverlay = null;

	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new SpotsUtils(this);
		// un-suppress unused warning
		if ( apiKey == apiDeployKey && apiKey !=  apiDevKey )
			m.utils.logInfo("Starting");
		Resources res = this.getResources();
		marker = res.getDrawable(R.drawable.yellow_pushpin_64);
		car = res.getDrawable(R.drawable.vw64);
		person = res.getDrawable(R.drawable.person64);
		locationManager  = (LocationManager)getSystemService(LOCATION_SERVICE);
		initMapView();
		overlays = map.getOverlays();
	    utils.createTable();
		initMyLocation();
	    initCenter();
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		resume();
	}
	/*------------------------------------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		utils.onCreateOptionsMenu(menu);
		return(true);
	}
	/*------------------------------*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = utils.onOptionsItemSelected(item);
		if ( ret )
			resume();
		return(ret);
	}
	/*------------------------------------------------------------*/
	@Override
	public void onPause() {
		super.onPause();
		saveLastViewed();
	}
	/*------------------------------------------------------------*/
	public void saveLastViewed() {
		String label = SpotsUtils.lastLabel;
		m.model.sql(String.format("delete from spots where label = '%s'", label));
		int zl = map.getZoomLevel();
		GeoPoint geo = map.getMapCenter();
		utils.insert(SpotsUtils.lastLabel, geo.getLatitudeE6(), geo.getLongitudeE6(), zl,
				isCompass,  isSatellite,  isTraffic);
	}
	/*------------------------------------------------------------*/
	/**
	 * this is called from onResume and
	 * every time we come back from a successful menu call
	 */
	public void resume() {
		setCompass(isCompass);
		map.setSatellite(isSatellite);
		map.setTraffic(isTraffic);
		// The activity doesn't always die when quitting it.
		// but on resume, the layer would not be drawn if the spotsLayerIsDirty flag
		// is not set, and retained from previous run, so don't 'if' re-layering
		relayer();
		if ( spotIsDirty )
			reposition();
		if ( comeHereRequested )
			comeHere();
		if ( fromHereRequested != 0 )
			fromHere(fromHereRequested);
		if ( zoomLevelRequested != 0 ) {
	        controller.setZoom(zoomLevelRequested);
			zoomLevelRequested = 0;
		}
		if ( adjustOnNextTap > 0 ) {
			String label = utils.label(adjustOnNextTap);
			String msg = "Tap to set new location for " + label;
			m.view.msg(msg);
			setTitle(msg);
		}
			
	}
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private String intString(int n, String singular) {
		return(intString(n, singular, null));
	}
	/*------------------------------------*/
	private String intString(double d, String singular) {
		return(intString((int)d, singular, null));
	}
	/*------------------------------------*/
	private String intString(double d, String singular, String plural) {
		return(intString((int)d, singular, plural));
	}
	/*------------------------------------*/
	private String intString(int n, String singular, String plural) {
		if ( n == 1 )
			return("1 " + singular);
		else if ( plural != null )
			return( n + " " + plural );
		else
			return(n + " " + singular + "s");
	}
	/*------------------------------------*/
	private String metersString(double meters) {
		if ( meters < 3000 )
			return(intString(meters, "meter"));
		else 
			return(intString(meters / 1000, "kilometer"));
	}
	/*------------------------------------*/
	private String milesString(double miles) {
		if ( miles < 1 )
			return(intString(miles * 5280, "foot", "feet"));
		else if ( miles < 3 )
			return(
					intString(miles, "mile") + " " +
					intString((miles - (int)miles)*5280, "foot", "feet")
				);
		else
			return(intString(miles, "mile"));
	}
	/*------------------------------------*/
	private void fromHere(int rowId) {
		if ( locationManager == null ) {
			m.view.msg("No Location Manager Service");
			fromHereRequested = 0;
			utils.goTo(rowId);
			resume();
			return;
		}
		Location here = locationManager.getLastKnownLocation("network");
		if ( here == null ) {
			m.view.msg("Last location unknown");
			fromHereRequested = 0;
			utils.goTo(rowId);
			resume();
			return;
		}
		int hereLat = (int)(here.getLatitude() * 1000000);
		int hereLon = (int)(here.getLongitude() * 1000000);
		MmodelRow row = m.model.getById("spots", rowId);
		int thereLat = row.getInt("latitude");
		int thereLon = row.getInt("longitude");
		int latSpan = Math.abs(hereLat - thereLat);
		int lonSpan = Math.abs(hereLon - thereLon);
		controller.zoomToSpan(latSpan, lonSpan);
		controller.zoomOut();
		
		int centerLat = (hereLat + thereLat) / 2;
		int centerLon = (hereLon + thereLon) / 2;
		GeoPoint hereGeo = new GeoPoint(hereLat, hereLon);
		GeoPoint thereGeo = new GeoPoint(thereLat, thereLon);
		double meters = utils.distanceBetween(hereGeo, thereGeo);
		double miles = meters * 0.0006213712;
		String metric = metersString(meters);
		String us = milesString(miles);
		String country = Locale.getDefault().getCountry();
		String msg;
		if ( country.compareTo("US") == 0 )
			 msg = us + " (" + metric + ")";
		else
			 msg = metric + " (" + us + ")";
		m.view.msg(msg);
		String title = utils.label(rowId) + ": " + msg;
		String address;
		if ( (address = utils.address(thereLat/1000000.0, thereLon/1000000.0)) != null )
		title += " - " + address;
		setTitle(title);
		controller.animateTo(new GeoPoint(centerLat, centerLon));
		isCompass = true;
		setCompass(isCompass);
		fromHereRequested = 0;
	}
	/*------------------------------------------------------------*/
	private void relayer() {
		overlays.remove(spotsOverlay);
		overlays.remove(carOverlay);
		overlays.remove(sentLocationOverlay);
		spotsOverlay = new SpotsOverlay(this, marker);
		carOverlay = new SpotsOverlay(this, car);
		sentLocationOverlay = new SpotsOverlay(this, person);
		MmodelRow rows[] = utils.spotsRows();
		for ( MmodelRow row : rows  )
			addOverlayItem(row);
		// google code  crashes consistently when trying to handle events
		// with empty overlays
		// so don't tell it there is an overlay, unless there is something in it.
		if ( spotsOverlay.size() > 0 )
			overlays.add(spotsOverlay);
		if ( carOverlay.size() > 0 )
			overlays.add(carOverlay);
		if ( sentLocationOverlay.size() > 0 )
			overlays.add(sentLocationOverlay);
		spotsLayerIsDirty = false;
		// */
	}
	/*-----------------------------------------*/
	private void addOverlayItem(MmodelRow row) {
		String label = row.getValue("label");
		if ( label.compareTo("Car") == 0 ) {
			placeCar(row);
			return;
		}
		if ( label.compareTo(SpotsUtils.sentLocation) == 0 ) {
			placeSentLocation(row);
			return;
		}
		addOverlayItem(
				row.getInt("latitude"),
				row.getInt("longitude"),
				label,
				row.getValue("created")
				);
	}
	/*-----------------------------------------*/
	private void addOverlayItem(int lat, int lon, String label, String snippet) {
		GeoPoint geo = new GeoPoint(lat, lon);
		OverlayItem overlayItem = new OverlayItem(geo, label, snippet);
		spotsOverlay.addOverlayItem(overlayItem);
	}
	/*------------------------------------------------------------*/
	private OverlayItem carOrPersonItem(MmodelRow row) {
		String latitude = row.getValue("latitude");
		String longitude = row.getValue("longitude");
		int lat = Integer.parseInt(latitude);
		int lon = Integer.parseInt(longitude);
		GeoPoint geo = new GeoPoint(lat, lon);
		String latDegrees = utils.degreeString(lat);
		String lonDegrees = utils.degreeString(lon);
		String snippet = latDegrees + "," + lonDegrees ;
		String label = utils.timeFmt(row.getValue("created"));
		return(new OverlayItem(geo, label, snippet));
	}
	/*------------------------------------------------------------*/
	private void placeCar(MmodelRow row) {
		OverlayItem overlayItem = carOrPersonItem(row);
		carOverlay.addOverlayItem(overlayItem);
	}
	/*------------------------------------------------------------*/
	private void placeSentLocation(MmodelRow row) {
		OverlayItem overlayItem = carOrPersonItem(row);
		sentLocationOverlay.addOverlayItem(overlayItem);
	}
	/*------------------------------------------------------------*/
	// compass does not work in emulator
	// onResume does the actual setting/clearing of the compass
	/*----------------------*/
	public static void toggleCompass() {
		isCompass = isCompass ? false : true ;
	}
	/*----------------------*/
	private void setCompass(boolean onOff) {
		if ( onOff )
			myLocationOverlay.enableCompass();
		else
			myLocationOverlay.disableCompass();
	}
	/*------------------------------------------------------------*/
	private void initMyLocation() {
		try {
			myLocationOverlay = new MyLocationOverlay(this, map);
			myLocationOverlay.enableMyLocation();
			overlays.add(myLocationOverlay);
		} catch (Exception e) {
			m.utils.logError("Failed");
		}
	}
	/*------------------------------------------------------------*/
	private void initCenter() {
		if ( controller == null ) {
			m.utils.logError("controller is null");
			return;
		}
		int intentSpot;
		if ( (intentSpot = utils.fromIntent(getIntent())) > 0 ) {
			utils.goTo(intentSpot);
			return;
		}
		MmodelRow lastSpot = m.model.getRow("select * from spots order by created desc limit 1");
		if ( lastSpot != null ) {
			utils.goTo(lastSpot);
			return;
		}
		// not reached except with fresh install
		// when not even the last view spot is not listed in the database.
		comeHere();
	}
	/*------------------------------------------------------------*/
	private void reposition() {
		GeoPoint geo = new GeoPoint(spotLatitude, spotLongitude);
        controller.animateTo(geo);
        controller.setZoom(spotZoomLevel);
		setCompass(isCompass);
		map.setSatellite(isSatellite);
		map.setTraffic(isTraffic);

        String  title = spotLabel;
        String address;
        if ( (address = utils.address(spotLatitude / 1000000.0, spotLongitude/ 1000000.0)) != null)
        	title = title + " - " + address ;
        setLabelInTitle(title);
		spotIsDirty = false;
	}
	/*------------------------------------------------------------*/
	private void initMapView() {
		map = new MapView(this, apiKey);
		map.setClickable(true); // not even zoom controls will work without this
		setContentView(map);
		controller = map.getController();
		map.setBuiltInZoomControls(true);
		// this one is vital to keep, even though its unused!
		OnClickListener mapClicked = new OnClickListener() {
			@Override
			public void onClick(View v) {
				log("initMapView: OnClickListener: Clicked");
			}
		};
		map.setOnClickListener(mapClicked);
	}
	/*------------------------------------------------------------*/
    private void comeHere() {
    	Runnable myLocationFixer = new Runnable() {
            public void run() {
            	GeoPoint my = myLocationOverlay.getMyLocation();
                if ( my == null ) {
                        m.view.msg("Current Location Unknown");
                        return;
                }
	            controller.animateTo(my);
	   			controller.setZoom(16);
            }
		};
		if ( myLocationOverlay.runOnFirstFix(myLocationFixer) )
            setLabelInTitle("You Are Here"); // Exception if called from runnable
		else
	        controller.setZoom(8);
        comeHereRequested = false;
  }

//	private boolean comeHere() {
//		if ( locationManager == null ) {
//			m.view.msg("No location manger");
//			return(false);
//		}
//		Location here = locationManager.getLastKnownLocation("gps");
//		if ( here == null ) {
//			m.view.msg("Current location unknown");
//			return(false);
//		}
//		if ( controller == null ) {
//			m.utils.logError("controller is null");
//			return(false);
//		}
//		int hereLat = (int)(here.getLatitude() * 1000000);
//		int hereLon = (int)(here.getLongitude() * 1000000);
//        GeoPoint hereGeo = new GeoPoint(hereLat, hereLon);
//        controller.animateTo(hereGeo);
//        setLabelInTitle("You Are Here");
//	    comeHereRequested = false;
//	    return(true);
//	}
	/*------------------------------------------------------------*/
	private void clearTitle() {
		setLabelInTitle(null);
	}
	/*------------------------------------------------------------*/
	private void setLabelInTitle(String title) {
			if ( title == null)
				setTitle("Spots");
			else
				setTitle("Spots - " + title);
	}
	/*------------------------------------------------------------*/
   private void log(String msg) {
	   utils.log(msg);
   }
   /*------------------------------------------------------------*/
   private void clicked(GeoPoint geo) {
	   if ( adjustOnNextTap > 0 ) {
		   m.utils.logInfo("Adjusting");
		   int rowId = adjustOnNextTap;
		   utils.update(rowId, geo, map.getZoomLevel(), isCompass, isSatellite, isTraffic);
		   adjustOnNextTap = 0;
		   utils.goTo(rowId);
		   resume();
		   controller.zoomIn();
		   return;
	   }
	   m.utils.logInfo("NewSpot");
	   Intent i = new Intent(this, NewSpot.class);
       int longitude = geo.getLongitudeE6();
       int latitude = geo.getLatitudeE6();
	   int zoomLevel = map.getZoomLevel();
	   i.putExtra("latitude", latitude);
	   i.putExtra("longitude", longitude);
	   i.putExtra("zoomLevel", zoomLevel);
	   i.putExtra("isCompass", isCompass);
	   i.putExtra("isSatellite", isSatellite);
	   i.putExtra("isTraffic", isTraffic);
	   startActivity(i);
   }
   /*----------------------------------*/
   // if super is called, it might execute a pushpin tap.
   @Override
   public boolean dispatchTouchEvent(MotionEvent ev) {
	   clearTitle();
	   // never return false from here without super(), or things will stop working
	  if ( handleTouchEvent(ev) )
		  return(true);
	  else {
		  try {
			  // crashed during some testing
		      boolean ret = super.dispatchTouchEvent(ev);
		      return(ret);
		  } catch ( Exception e) {
			  log("dispatchTouchEvent: super failed:" + ev.toString());
			  return(true); // lets say this counts as handled
		  }
	  }
   }
   /*----------------------------------*/
   // might this touched spot be a spot, so let it fall through to a tap
   // on the pushpin rather than responding to a new map touch
   
   private int isSpot(MotionEvent ev) {
	   int thisX = (int)ev.getX();
	   int thisY = (int)ev.getY();
	   Projection proj = map.getProjection();
	   MmodelRow rows[] = utils.spotsRows();
	   for( MmodelRow row : rows ) {	    
			String latitude = row.getValue("latitude");
			String longitude = row.getValue("longitude");
			int lat = Integer.parseInt(latitude);
			int lon = Integer.parseInt(longitude);
		    GeoPoint geo = new GeoPoint(lat, lon);
		    Point p = proj.toPixels(geo, null);
		    
		    // if max is too high, then neither Google's pushpin tap nor this
		    // logic will respond
		    // if it is too low, this will respond,
		    // and the Google tap will never be seen
		    int max = 40;
		    
		    // for debugging
//		    String label = row.getValue("label");
//		    int zl = map.getZoomLevel();
//		    if ( Mutil.stristr(label, "well") )
//		    	utils.log(String.format("isSpot touch=%d,%d '%s'=%d,%d zl=%d",
//		    			thisX, thisY, label, p.x, p.y, zl));
		    
		    if ( Math.abs(thisX - p.x) < max && Math.abs(thisY - p.y) < max )
		    	return(row.id());
	   }
	   return(0);
   }
   /*----------------------------------*/
   // if this is a pushpin tap, return false from here
   // onTap is called by the overlay class
   private double lastDownX = 0.0;
   private double lastDownY = 0.0;
   private boolean handleTouchEvent(MotionEvent ev) {
	   int fingers = ev.getPointerCount();
	   if ( fingers > 1 )
	   	return(false);
	   int action = ev.getAction();
	   if (action == MotionEvent.ACTION_DOWN ) {
		   lastDownX = ev.getX();
		   lastDownY = ev.getY();
	   } else if ( action == MotionEvent.ACTION_UP ) {
		   double upX = ev.getX();
		   double upY = ev.getY();
		   if ( upX == lastDownX && upY == lastDownY ) {
			   // prevent responding to zoom controls clicks
			   Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

			   // if rotation is a sideways rotation,
			   // then the height reported is that of the width
			   // int rotation = display.getRotation();
			   // int width = display.getWidth();
			   int height = display.getHeight();
			   double percentFromBottom = 100.0 * (height - upY) / height ;
			   
			   if ( adjustOnNextTap == 0 && percentFromBottom < 15 )
				   return(false);
			   
			   // the overlay class calls onTap if a spot is tapped
			   if ( adjustOnNextTap == 0 && (isSpot(ev)) > 0 ) {
			       	return(false);
			   }
			   
			   Projection p = map.getProjection();
			   int x = (int)upX;
			   int y = (int)upY;
			   int yCorrection = -50;
			   int xCorrection = -5;
			   x += xCorrection;
			   y += yCorrection;
		       GeoPoint geo = p.fromPixels(x, y);
		       clicked(geo);
			   return(true);
		   }
	   }
	   return(false);
   }
   /*------------------------------------------------------------*/
   @Override
   protected boolean isRouteDisplayed() {
      // Required by MapActivity
      return false;
   }
   /*------------------------------------------------------------*/
   public static void spotsLayerIsDirty() {
	   spotsLayerIsDirty = true;
   }
   /*------------------------------------------------------------*/
   public static void zoomLevelRequest(int zl) {
	   zoomLevelRequested = zl;
   }
   /*------------------------------------------------------------*/
   public static void adjustOnNextTap(int rowId) {
	   adjustOnNextTap = rowId;
   }
   /*------------------------------------------------------------*/
   public static void comeHereRequest() {
	   comeHereRequested = true;
   }
   /*------------------------------------------------------------*/
   public static void fromHereReqeuest(int rowId) {
	   fromHereRequested = rowId;
   }
   /*------------------------------------------------------------*/
   public static void setSpot(String label, int lon, int lat, int zoomLevel,
		   			boolean isCompass, boolean isSatellite, boolean isTraffic) {
	   spotLabel = label;
	   spotLongitude = lon;
	   spotLatitude = lat;
	   spotZoomLevel = zoomLevel;
	   Spots.isCompass = isCompass;
	   Spots.isSatellite = isSatellite;
	   Spots.isTraffic = isTraffic;
	   
	   spotIsDirty = true;
   }
   /*------------------------------------------------------------*/
   public static boolean isSattelite() {
	   return(isSatellite);
   }
   /*------------------------------------------------------------*/
   public static void satellite() {
	   isSatellite = isSatellite ? false : true ;
   }
   /*------------------------------------------------------------*/
   public static void traffic() {
	   isTraffic = isTraffic ? false : true ;
   }
   /*------------------------------------------------------------*/
   
}
/*------------------------------------------------------------*/
