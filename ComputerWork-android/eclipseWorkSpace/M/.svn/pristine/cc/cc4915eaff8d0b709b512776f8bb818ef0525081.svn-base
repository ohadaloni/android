package com.theora.M;

import java.util.Calendar;
import java.util.GregorianCalendar;
/*------------------------------------------------------------*/
/**
  * Mdate - date utilities
  *
  * methods have no instantiation dependencies and can be called with Mdate::methodName(...) 
  *
  * int dates are in the form 20090929
  *
  * @package com.theora.M
  */
/*------------------------------------------------------------*/
public class Mdate {
	/*------------------------------*/
	/**
	 * compose a date from its three components
	 *
	 * @param int
	 * @param int
	 * @param int
	 * @return int
	 */
	public static int compose(int year, int month, int day) {
		return(year * 10000 + month * 100 + day);
	}
	/*------------------------------*/
	/**
	  * separate a date to its components
	  *
	  * @param int|string the date
	  * @return array array with 3 components in this order: year, month, day
	  */
	public static int[] separate(int date) {
		int d = date % 100 ;
		int m = ( (int)(date/100) ) % 100 ;
		int y = (int)(date / 10000);
		int ret[] = {y, m, d};
		return(ret);
	}
	/*------------------------------*/
	private static int fromGc(GregorianCalendar gc) {
		int y = gc.get(Calendar.YEAR);
		int m = gc.get(Calendar.MONTH)+1;
		int d = gc.get(Calendar.DAY_OF_MONTH);
		return(compose(y, m, d));
	}
	/*------------------------------*/
	private static Calendar toC(int date) {
		int ymd[] = separate(date);
		Calendar c = Calendar.getInstance();
		c.set(ymd[0], ymd[1], ymd[2]);
		return(c);
	}
	/*------------------------------*/
	public static int fromC(Calendar c) {
		int y = c.get(Calendar.YEAR);
		int m = c.get(Calendar.MONTH)+1;
		int d = c.get(Calendar.DAY_OF_MONTH);
		return(compose(y, m, d));
	}
	/*------------------------------*/
	private static GregorianCalendar toGc(int date) {
		int ymd[] = separate(date);
		GregorianCalendar gc = new GregorianCalendar(ymd[0], ymd[1], ymd[2]);
		return(gc);
	}
	/*------------------------------*/
	/**
	 * add 1 or more days to a date
	 *
	 *	@param int|string
	 *	@param int
	 *	@return int
	 */
	public static int addDays(int date, int days) {
		GregorianCalendar gc = toGc(date);
		gc.add(Calendar.DAY_OF_YEAR, days);
		return(fromGc(gc));
	}
	/*--------------------*/
	/**
	 * subtract 1 or more days from a date
	 *
	 *	@param int|string
	 *	@param int
	 *	@return int
	 */
	public static int subtractDays(int date, int days) {
		return(addDays(date, days * -1));
	}
	/*--------------------*/
	/**
	 * add 1 or more weeks to a date
	 *
	 *	@param int|string
	 *	@param int
	 *	@return int
	 */
	public static int addWeeks(int date, int weeks) {
		return(addDays(date, weeks * 7));
	}
	/*--------------------*/
	/**
	 * subtract 1 or more weeks from a date
	 *
	 *	@param int|string
	 *	@param int
	 *	@return int
	 */
	public static int subtractWeeks(int date, int weeks) {
		return(addWeeks(date, weeks * -1));
	}
	/*--------------------*/
	/**
	 * add 1 or more years to a date
	 *
	 *	@param int|string
	 *	@param int
	 *	@return int
	 */
	public static int addYears(int date, int years) {
		int ymd[] = separate(date);
		ymd[0] += years;
		return(compose(ymd[0], ymd[1], ymd[2]));
	}
	/*--------------------*/
	/**
	 * subtract 1 or more years from a date
	 *
	 *	@param int|string
	 *	@param int
	 *	@return int
	 */
	public static int subtractYears(int date, int years) {
		return(addYears(date, years * -1));
	}
	/*--------------------*/
	private static int addMonth(int date) {
		int ymd[] = separate(date);
		if ( ymd[1] < 12 )
			ymd[1]++;
		else {
			ymd[0]++;
			ymd[1] = 1 ;
		}
		return(compose(ymd[0], ymd[1], ymd[2]));
	}
	/*--------------------*/
	private static int subtractMonth(int date) {
		int ymd[] = separate(date);
		if ( ymd[1] > 1 )
			ymd[1]--;
		else {
			ymd[0]--;
			ymd[1] = 12 ;
		}
		return(compose(ymd[0], ymd[1], ymd[2]));
	}
	/*--------------------*/
	/**
	 * subtract 1 or more months from a date
	 *
	 *	@param int|string
	 *	@param int
	 *	@return int
	 */
	public static int subtractMonths(int date, int months) {
		for(int i=0;i<months;i++)
			date = subtractMonth(date);
		return(date);
	}
	/*--------------------*/
	/**
	 * add 1 or more months to a date
	 *
	 *	@param int|string
	 *	@param int
	 *	@return int
	 */
	public static int addMonths(int date, int months) {
		if ( months < 0 )
			return(subtractMonths(date, -months));
		for(int i=0;i<months;i++)
			date = addMonth(date);
		return(date);
	}
	/*------------------------------*/
	/**
	 * remove any dashes from a database formatted date
	 * '2009-09-29' -> 20090929
	 * 20090929 -> 20090929
	 *
	 * @param string
	 * @return int
	 */
	public static int undash(String str) {
		String ret = str.replace("-", "");
		return(Integer.parseInt(ret));
	}
	/*------------------------------*/
	/**
	 * format a date separated by dashes for the database
	 *
	 * @param int|string
	 * @return string
	 */
	public static String dash(int date) {
		int ymd[] = separate(date);
		return(String.format("%04d-%02d-%02d",ymd[0],ymd[1],ymd[2]));
	}
	/*------------------------------------------------------------*/
	/**
	 * old style Unix time stamp
	 */
	public static int time() {
		return (int) (System.currentTimeMillis() / 1000L);		
	}
	/*------------------------------*/
	/**
	 * what is the unix epoch of a date
	 */
	public static int datetimeToUnixTime(String datetime) {
		String dt[] = datetime.split(" ");
		if ( dt.length != 2 )
			return(0);
		int date = undash(dt[0]);
		int ymd[] = separate(date);
		if ( ymd.length != 3 )
			return(0);
		String hmsEtc[] = dt[1].split("\\.");
		String hms[] = hmsEtc[0].split(":");
		int hours = new Integer(hms[0]).intValue();
		int minutes = new Integer(hms[1]).intValue();
		int seconds;
		if ( hms.length == 3 )
			seconds = new Integer(hms[2]).intValue();
		else
			seconds = 0;
		int milliseconds;
		if ( hmsEtc.length == 2 )
			milliseconds = new Integer(hmsEtc[1]).intValue();
		else
			milliseconds = 0;
		if ( milliseconds >= 500 )
			seconds++;
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(ymd[0], ymd[1]-1, ymd[2], hours, minutes, seconds);
		long millis = cal.getTimeInMillis();
		int ret = (int)(millis / 1000);
		return(ret);
	}
	/*------------------------------*/
	/**
	 * convert unixTime to date
	 * 
	 * @param int
	 * @return int
	 */
	public static int fromUnixTime(int unixTime) {
		Calendar cal = GregorianCalendar.getInstance();
		long millis = unixTime * 1000L;
		cal.setTimeInMillis(millis);
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		return(compose(y, m, d));
	}
	/*------------------------------------------------------------*/
	/**
	 * number of days between two dates (d1 - d2)
	 *
	 */
	public static int diff(int d1, int d2) {
		GregorianCalendar gc1 = toGc(d1);
		java.util.Date dt1 = gc1.getTime();
		GregorianCalendar gc2 = toGc(d2);
		java.util.Date dt2 = gc2.getTime();
		long millis1 = dt1.getTime();
		long millis2 = dt2.getTime();
		long diffmillis = millis1 - millis2;
		long diffDays = diffmillis / (1000 * 60 * 60 * 24);
		return((int)diffDays);
	}
	/*------------------------------------------------------------*/
	/**
	 * format a date
	 *
	 */
	public static String fmt(int date) {
		return(fmt(date, false));
	}
	/*------------------------------------------------------------*/
	/**
	 * format a date
	 *
	 */
	public static String fmt(int date, boolean withWeekDay) {
		String ret;
		if (  date <= 0 )
			return("");
		int ymd[] = separate(date);
		
		if ( withWeekDay )
			ret = String.format("%s, %s %d %d",
					wdayName(wday(date)),
					monthName(ymd[1]),
					ymd[2],
					ymd[0]
			);
		else
			ret = String.format("%s %d %d",
					monthName(ymd[1]),
					ymd[2],
					ymd[0]
			);
		return(ret);
	}
	/*------------------------------------------------------------*/
	/**
	 * Will we ever use scan for dates
	 *
	 * try to figure out what date is denoted by str
	 *
	 * 1. a Unix timestamp is accepted - no longer
	 * 2. 19831107 and 20111107 are accepted
	 * 3. "t" is today's date
	 * 4. 7 means the 7th of the current month
	 * 5. 7/11 and "7 11" means November 7th of this year
	 * 6. "7 11 2011" is 20111107
	 * 7. "7 11 1983" is 19831107
	 * 8. "7 11 11" is 20111107
	 * 9. "7 11 83" is 19831107
	 *
	 * @param string some description of a date
	 * @return int the implied date or null
	 * 
	 *
	public static int scan(String str) {
		// if M identifies a database format of the data...
		if ( Mmodel.typeByValue(str).compareTo(Mmodel.type_date) == 0 )
			return(undash(str));

		if ( str == "t" )
			return(today());

		int today[] = separate(today());
		int thisYear = today[0];
		int thisMonth = today[1];
		if ( str == "t" )
			return(today());
		if ( Mutils.isNumeric(str) ) {
			int i = Integer.parseInt(str);
			if ( i > 1900 * 10000 && i < 2100 * 10000 )
				return(i);
		}
		String spaced = str.replace("/", " ");
		String parts[] = spaced.split(" ");
		int cnt = parts.length;
		if ( cnt == 1 && Mutils.isNumeric(parts[0]) ) {
			int d = Integer.parseInt(parts[0]);
			if ( d < 1 || d > 31 )
				return(-1);
			return(thisYear * 10000 + thisMonth * 100 + d);
		}
		if ( cnt == 2 && Mutils.isNumeric(parts[0]) && Mutils.isNumeric(parts[1]) ) {
			int m = Integer.parseInt(parts[0]);
			int d = Integer.parseInt(parts[1]);
			if ( d < 1 || d > 31 )
				return(-1);
			if ( m < 1 || m > 12 )
				return(-1);
			return(thisYear * 10000 + m * 100 + d);
		}
		if ( cnt == 3 && Mutils.isNumeric(parts[0]) && Mutils.isNumeric(parts[1]) && Mutils.isNumeric(parts[2])  ) {
			int m = Integer.parseInt(parts[0]);
			int d = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			if ( d < 1 || d > 31 )
				return(-1);
			if ( m < 1 || m > 12 )
				return(-1);
			if ( y < 25 )
				y += 2000 ;
			else if ( y < 100 )
				y += 1900 ;
			return(y * 10000 + m * 100 + d);
		}
		return(-1);
	}
	/*----------------------------------------*/
	/**
	 * @param int
	 * @return boolean
	 */
	public static boolean isLeap(int year) {
		return( year % 4 == 0 && year % 100 != 0 || year % 400 == 0 );
	}
	/*------------------------------*/
	/**
	  * how many days are in a month
	  *
	  * @param int
	  * @param int if not given then monthLength(2) always returns 28.
	  * @return int
	  */
	public static int monthLength(int m, int y) {
		int monthlens[] = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		return(monthlens[m] + ( ( m == 2 && isLeap(y)) ? 1 : 0 ));
	}
	/*------------------------------*/
	/**
	  * Short month name - monthStr(9) == "Sep"
	  *
	  * @param int
	  * @return string
	  */
	public static String monthName(int m) {
		String monthNames[] = {
			"",
			"Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
		};
		return(monthNames[m]);
	}
	/*------------------------------*/
	public static String wdayName(int wday) {
		String wdayNames[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		return(wdayNames[wday]);
	}
	/*----------------------------------------*/
	/**
	  * Long month name - monthLname(9) == "September"
	  *
	  * @param int
	  * @return string
	  */
	public String monthLname(int m) {
		// better speed and less memory usage without reuse of the above
		String monthlnames[] = {
			"",
			"January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December"
		};
		return(monthlnames[m]);
	}
	/*----------------------------------------*/
	/**
	  * the week day of a date 
	  *
	  * @param int|string date
	  * @return int 0 if date is on a Sunday, 6 if Saturday
	  */
	public static int wday(int date) {
		// TODO - Mdate.wday not always giving correct results
//		GregorianCalendar gc = toGc(date);
//		int gcwday = gc.get(Calendar.DAY_OF_WEEK);
//		return((gcwday+3)%7);
		Calendar c = toC(date);
		int cwday = c.get(Calendar.DAY_OF_WEEK);
		return((cwday+3)%7);
	}
	/*----------------------------------------*/
	/**
	  * today's date
	  *
	  * @return int
	  */
	public static int today() {
		return(fromGc(new GregorianCalendar()));
	}
	/*------------------------------*/
	/**
	  * create a monthly calendar for the given month
	  *
	  * return a 2 dimensional array of weeks, matching an ordinary visual layout:<br >
	  * the returned array has 4-6 entries.
	  * (4 if its a non-leap February starting on a Sunday).<br />
	  * In each week keys 0-7 (Sunday-Saturday) have the day of month for the week.<br >
	  * Empty entries have a null value, so each week is an array with exactly 7 entries.
	  *
	  * @param int year
	  * @param int month
	  * @return array
	  */
	  public static int[][] cal(int year, int month) {
		int mdays = monthLength(month, year);
		int zeroDay = year * 10000 + month * 100;
		int wd = wday(zeroDay+1);
		int day, w;
		int cal[][] = new int[7][7];
		for(int i=0;i<7;i++)
			for(int j=0;j<7;j++)
				cal[i][j] = 0;
		for(day=1,w=0;day<=mdays;day++) {
			if ( day != 1 && wd == 0 )
				w++;
			cal[w][wd] = day ;
			wd = (wd+1) % 7 ;
		}
		int ret[][] = new int[w][7];
		for(int i=0;i<w;i++)
			for(int j=0;j<7;j++)
				ret[i][j] = cal[i][j];
		return(ret);
	  }
	/*----------------------------------------*/
	/**
	 * list dates between two dates
	 *
	 * @param int|string = returned[0]
	 * @param int|string the last entry in the returned array
	 * @return array
	 */
	public static int[] dateList(int starting, int ending) {
		int diff = diff(ending, starting);
		int ret[] = new int[diff+1];
		int date, i;
		for (date = starting, i=0; date <= ending ; date = addDays(date, 1), i++ )
			ret[i] = date;
		return(ret);
	}
	/*----------------------------------------*/
}
/*------------------------------------------------------------*/
