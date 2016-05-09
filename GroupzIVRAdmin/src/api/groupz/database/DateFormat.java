package api.groupz.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat
{
	public static Date StringDateToDate(String StrDate)
	{
		String DATEFORMAT ="YYYY-MM-dd-HH:mm:ss";
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try
		{
			if(StrDate != null && StrDate.length()>0)
			{
				dateToReturn =(Date) dateFormat.parse(StrDate);
			}
			else
			{
				dateToReturn = null;
			}
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		return dateToReturn;
	}
}
