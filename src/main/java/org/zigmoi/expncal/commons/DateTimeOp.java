package org.zigmoi.expncal.commons;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;

public class DateTimeOp {

	public final static String DATE_FORMAT_CCYYMMDD = "yyyyMMdd";
	public final static String DATE_FORMAT_YYMMDD = "yyMMdd";
	public final static String TIME_FORMAT_HHMMSS = "Hmmss";
	public final static String DATE_FORMAT_CCYYMMDDHHMMSS = "yyyyMMddHHmmss";

	public final static String TZ_TIMEZONE = "Asia/Kolkata";
	public final static String TZ_LOCALE = "IST";

	public static
		boolean isLegalDate(String s, String pattern, boolean lenient) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setLenient(lenient);
		return sdf.parse(s, new ParsePosition(0)) != null;
	}

	public static
		String getSysDateCCYYMMDD() {
		return new SimpleDateFormat(DATE_FORMAT_CCYYMMDD).format(new Date());
	}

	public static
		String getSysDateYYMMDD() {
		return new SimpleDateFormat(DATE_FORMAT_YYMMDD).format(new Date());
	}

	public static
		String getSysTimeHHMMSSMS() {
		String ms = new SimpleDateFormat("S").format(new Date());
		return StringUtils.leftPad(new SimpleDateFormat(TIME_FORMAT_HHMMSS).format(new Date())
			+ StringUtils.leftPad(ms, 3, "0"), 9, "0");
	}

	public static
		String getCCYYMMDDHHMMSS() {
		return getSysDateCCYYMMDD() + getSysTimeHHMMSSMS().substring(0, 6);
	}

	public static
		int getActivityTime() {

		return Integer.parseInt(String.valueOf(
			new StringBuffer(
				new StringBuffer(String.valueOf(new Date().getTime()))
				.reverse()
				.substring(3)
			).reverse()
		));
	}

	public static
		String convActivityToCCYYMMDDHHMMSS(int activityTime) {

		Long time = Long.parseLong(String.valueOf(activityTime) + "000");
		Date date = new Date(time);
		return DateTimeOp.dateFormat(DATE_FORMAT_CCYYMMDDHHMMSS, date, TZ_TIMEZONE);
	}

	public static
		int convCCYYMMDDHHMMSSToActivity(String dateCCYYMMDDHHMMSS) throws ParseException {

		String str = dateCCYYMMDDHHMMSS + "000 " + TZ_LOCALE;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmSSS zzz");
		Date date = df.parse(str);
		return Integer.parseInt(
			String.valueOf(
				new StringBuffer(
					new StringBuffer(
						String.valueOf(date.getTime())
					).reverse().substring(3)
				).reverse()
			)
		);
	}

	public static String dateFormat(String formatStr, Date date, String timeZone) {

		DateFormat format = new SimpleDateFormat(formatStr);
		format.setTimeZone(TimeZone.getTimeZone(timeZone));
		return format.format(date);
	}

	public static void main(String[] args) throws ParseException {

		System.out.println(DateTimeOp.getActivityTime());
		
		System.out.println(
			DateTimeOp.convActivityToCCYYMMDDHHMMSS(
				DateTimeOp.getActivityTime()
			)
		);

		System.out.println(
			DateTimeOp.convCCYYMMDDHHMMSSToActivity(DateTimeOp.getCCYYMMDDHHMMSS())
		);

	}
}
