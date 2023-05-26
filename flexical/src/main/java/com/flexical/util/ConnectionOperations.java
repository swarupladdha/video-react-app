package com.flexical.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionOperations {
	public synchronized void closeStatement(Statement statement) 
	{
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void closePreparedStatement(PreparedStatement statement) 
	{
		try 
		{
			if (statement != null) 
			{
				statement.close();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public synchronized void closeResultset(ResultSet resultset) 
	{
		try 
		{
			if (resultset != null) 
			{
				resultset.close();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}


}
