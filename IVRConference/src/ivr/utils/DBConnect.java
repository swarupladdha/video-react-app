package ivr.utils;

public class DBConnect
{

	public boolean establishConnection()
	{
		boolean connectionStatus = false;	
		try
		{
			System.setProperty("Hibernate-Url", DBConnect.class.getResource("/hibernate.cfg.xml").toString());
			System.out.println("Hibernate Initialization : " + System.getProperty("Hibernate-Url"));
			System.out.println(System.getProperties());
			connectionStatus=true;
			return connectionStatus;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			connectionStatus=false;
		}
		return connectionStatus;
	}

}
