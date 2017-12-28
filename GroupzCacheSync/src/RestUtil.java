import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RestUtil {
	
	
	// checking null
	public static boolean isEmpty(String test) {
		if (test == null || test.trim().isEmpty() == true|| test.equalsIgnoreCase("[]") || test == "") {
			return false;
		}
		return true;
	}

	public static Date getLastSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		System.out.println("String date:" + utcTime);
		Date lastSynch = StringDateToDate(utcTime);
		System.out.println("Date :" + lastSynch);
		return lastSynch;
	}
	public static Date StringDateToDate(String StrDate) {
		String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return dateToReturn;
	}
}
