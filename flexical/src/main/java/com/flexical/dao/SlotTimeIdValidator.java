package com.flexical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.flexical.model.ValidSlotTimeId;
import com.flexical.util.ConnectionPooling;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SlotTimeIdValidator implements ConstraintValidator<ValidSlotTimeId, Integer> {
	public static final Logger logger = Logger.getLogger(SlotTimeIdValidator.class);
    
    @Override
    public boolean isValid(Integer slotTimeId, ConstraintValidatorContext context) {
    		String sql = "Select * from slottime where id = ?";
    		ConnectionPooling connectionPooling = ConnectionPooling.getInstance();
    		Connection dbConnection = connectionPooling.getConnection();
    		PreparedStatement ps = null;
    		ResultSet rs = null;
    		try {
    		
    			ps = dbConnection.prepareStatement(sql);
    			ps.setInt(1, slotTimeId);
    			rs = ps.executeQuery();
    			if (rs.next()) {
    				return true;
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			try {
    				if (rs != null)
    					rs.close();
    				if (ps != null)
    					ps.close();
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    		return false;    	
    }
}