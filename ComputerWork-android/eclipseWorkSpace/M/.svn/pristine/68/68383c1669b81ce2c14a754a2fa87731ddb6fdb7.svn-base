package com.theora.M;

import java.util.Calendar;

/*------------------------------------------------------------*/
/**
  * time integers
  *
  * time as integer, for example 1330 means 1:30 pm
  *
  * @package com.theora.M

  */
/*------------------------------------------------------------*/
public class Mtime {
	/*------------------------------*/
	/**
	 * the time right now
	 *
	 * @return int
	 */
	public static int now() {
		Calendar c = Calendar.getInstance();
		return(now(c));
	}
	/*------------------------------*/
	/**
	 * now as a String with milliseconds
	 * 2011-05-21 09:49:56.835
	 */
	public static String dateTimeNow() {
		Calendar c = Calendar.getInstance();
		int hh = c.get(Calendar.HOUR_OF_DAY);
		int mm = c.get(Calendar.MINUTE);
		int ss = c.get(Calendar.SECOND);
		int millis = c.get(Calendar.MILLISECOND);
		String time = String.format("%02d:%02d:%02d.%03d", hh, mm, ss, millis);
		String date = Mdate.dash(Mdate.today());
		String ret = date + " " + time;
		return(ret);
		/*------------------------------------------------------------*/

	}
	/*------------------------------*/
	public static int now(Calendar c) {
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		return(h*100+m);
	}
	/*------------------------------*/
	/**
	 * scan or convert to this integer format
	 *
	 * @param string
	 * @return int
	 *
	 * '13:22:11' -> 1322 (13:22)
	 * '13:22' -> 1322 (13:22)
	 * '1322' -> 1322 (13:22)
	 * '13' -> 1300 (13:00)
	 * '24' -> 24 (12:24 am)
	 * '0:13' -> 13 (12:13 am)
	 * -1 - error
	 */
	public static int scan(String s) {
		int ret;
		try {
			String hms[] = s.split(":");
			int cnt = hms.length;
			if (cnt == 2 || cnt == 3)
				ret = Integer.parseInt(hms[0]) * 100 + Integer.parseInt(hms[1]);
			else {
				ret = Integer.parseInt(s);
				if (ret < 0)
					return (0);
				if (ret < 24)
					ret *= 100;
			}
			return (ret);
		} catch (Exception e) {
			return(-1);
		}
	}
	/*------------------------------*/
	/**
	 * number of minutes since midnight
	 *
	 * minute(324) = 3*60 + 24 = 204
	 *
	 * @param integer|string
	 * @return int
	 */
	public static int minutes(int t) {
		int m = t % 100 ;
		int h = (t - m)/100;
		return(h * 60 + m);
	}
	/*------------------------------*/
	/**
	 * number of minutes between two time values
	 *
	 * @param integer|string the later time in the day
	 * @param integer|string the earlier
	 * @return int
	 */

	public static int minuteDiff(int t1, int t2) {
		return(minutes(t1) - minutes(t2));
	}
	/*------------------------------*/
	/**
	 * number of hours between two time values
	 *
	 * @param integer|string the later time in the day
	 * @param integer|string the earlier
	 * @return int
	 */
	 
	public static int hourDiff(int t1, int t2) {
		return(minuteDiff(t1, t2)/60);
	}
	/*------------------------------*/
	/**
	 * compose time integer from hours and minutes
	 */
	public static int compose(int h, int m) {
		return(h*100+m);
	}
	/*------------------------------*/
	/**
	 * separate time integer into hours and minutes
	 */
	public static int[] separate(int t) {
		int m = t % 100 ;
		int h = (t - m)/100;
		int ret[] = {h, m};
		return(ret);
	}
	/*------------------------------*/
	/**
	 * format a time value as usual
	 *
	 * @param integer|string
	 * @return string
	 */
	 
	public static String fmt(int t) {
		int m = t % 100 ;
		int h = (t - m)/100;
		return(String.format("%d:%02d", h, m));
	}
	/*------------------------------*/
	/**
	 * format a time value for the database
	 *
	 * @param integer|string
	 * @return string
	 */
	 
	public static String dbFmt(int t) {
		int m = t % 100 ;
		int h = (t - m)/100;
		return(String.format("%02d:%02d:00.000", h, m));
	}
	/*------------------------------*/
}
/*------------------------------------------------------------*/
