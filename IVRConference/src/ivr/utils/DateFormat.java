package ivr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateFormat
{
	static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static  Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        try
        {
            dateToReturn = (Date) dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return dateToReturn;
    }

    public  Date getLastSynchTime()
    {
        SimpleDateFormat f = new SimpleDateFormat(DATEFORMAT);
        String utcTime = f.format(new Date());
        Date lastSynch = StringDateToDate(utcTime);
        return lastSynch;
    }

    // get latest synch time
    public static  String getLatestSynchTime()
    {
        SimpleDateFormat f = new SimpleDateFormat(DATEFORMAT);
        String utcTime = f.format(new Date());
        return utcTime.trim();
    }
    
    public  String getFormattedDateStr(Date date)
    {
        SimpleDateFormat f = new SimpleDateFormat(DATEFORMAT);
        String strDate = null;
        if (date != null)
        {
            strDate = f.format(date);
            strDate.trim();
        }
        return strDate.trim();
    }
}
