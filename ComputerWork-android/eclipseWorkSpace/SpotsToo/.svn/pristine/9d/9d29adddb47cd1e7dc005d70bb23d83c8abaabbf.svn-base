package com.theora.Spots;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.theora.M.McallbackWithString;
import com.theora.M.Mcontroller;
/*------------------------------------------------------------*/
@SuppressWarnings("rawtypes")
public class SpotsOverlay extends ItemizedOverlay {
	/*------------------------------------------------------------*/
	private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
	@SuppressWarnings("unused")
	private Context ctx = null;
	Activity a = null;
	Spots spots = null;
	private Mcontroller m = null;
	SpotsUtils utils = null;
	private int textSize = 20;
	private int white = 0xffffffff;
	private int black = 0xff000000;
	/*------------------------------------------------------------
	public SpotsOverlay(Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
		}
	/*------------------------------------------------------------*/
	// !!! without boundCenterBottom, the drawable doesn't show at all. not sure why.
	public SpotsOverlay(Spots spots, Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
		  this.spots = spots;
		  a = spots;
		  ctx = spots;
		  m = new Mcontroller(a, "mdb");
		  utils = new SpotsUtils(a);
		}
	/*------------------------------------------------------------*/
	public void addOverlayItem(OverlayItem overlayItem) {
	    overlayItems.add(overlayItem);
	    populate();
	}
	/*------------------------------------------------------------*/
	@Override
	protected OverlayItem createItem(int i) {
	  return overlayItems.get(i);
	}
	/*------------------------------------------------------------
	@Override
	protected MyMapItem createItem(int i) {
		OverlayItem item = overlayItems.get(i);
		GeoPoint geo = item.getPoint();
		String title = item.getTitle();
		String snippet = item.getSnippet();
		MyMapItem myMapItem = new MyMapItem(geo, title, snippet);
	  return(myMapItem);
	}
	/*------------------------------------------------------------*/
	@Override
	public int size() {
	  return overlayItems.size();
	}
	/*------------------------------------------------------------*/
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = overlayItems.get(index);
	  String title = item.getTitle();
	  String snippet = item.getSnippet();
	  String sql = String.format("select id from spots where label = '%s'", m.model.str(title));
	  int rowId = m.model.getInt(sql);
	  if ( rowId > 0 )
		  actionsMenu(title, snippet, rowId);
	  else
		  m.view.alert(title, snippet);
	  return true;
	}
	/*------------------------------------------------------------*/
	private void actionsMenu(String title, String snippet, final int rowId) {
		String[] actions = { "Go To", "From Here", "Edit", "Adjust", "SMS", "Delete" };
		final String label = utils.label(rowId);
		m.view.select(label, actions, new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null )
					return;
				else if (s.compareTo("Go To") == 0 ) {
					m.utils.logInfo("Go To");
					utils.goTo(rowId);
				} else if ( s.compareTo("Edit") == 0 ) {
					m.utils.logInfo("Edit");
					utils.edit(rowId);
				} else if ( s.compareTo("From Here") == 0 ) {
					m.utils.logInfo("From Here");
					Spots.fromHereReqeuest(rowId);
				} else if ( s.compareTo("Adjust") == 0 ) {
					m.utils.logInfo("Adjust");
					Spots.adjustOnNextTap(rowId);
				} else if ( s.compareTo("SMS") == 0 ) {
					m.utils.logInfo("SMS");
					utils.sms(rowId);
				} else if ( s.compareTo("Delete") == 0 ) {
					m.utils.logInfo("Delete");
					utils.delete(rowId);
					Spots.spotsLayerIsDirty();
				}
				spots.resume();
			}
		});
	}
	/*------------------------------------------------------------*/

    @Override
    public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow)
    {
        super.draw(canvas, mapView, shadow);

        if (shadow == false)
        {
            //cycle through all overlays
            for (int index = 0; index < overlayItems.size(); index++)
            {
                OverlayItem item = overlayItems.get(index);

                // Converts lat/lng-Point to coordinates on the screen
                GeoPoint point = item.getPoint();
                Point ptScreenCoord = new Point() ;
                mapView.getProjection().toPixels(point, ptScreenCoord);

                //Paint
                Paint paint = new Paint();
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(textSize);
                int color = ( Spots.isSattelite() ) ? white : black ;
                paint.setColor(color);

                //show text to the right of the icon
                canvas.drawText(item.getTitle(), ptScreenCoord.x, ptScreenCoord.y+textSize, paint);
            }
        }
    }
	/*------------------------------------------------------------*/

}
