package com.infosoft.queries;

public interface CallQueries {
	String ADD_CALLDETAILS = "insert into calldetails (fromNumber,toNumber,callStatus,timeLimit,createdTime,callBackUrl,callId) values(?,?,?,?,now(),?,?)";
	String GET_NEW_NUMBERS = " select * from calldetails where callStatus ='initiated' and callSid is null and callStartTime is null and trialCount < ? ";
	String GET_NEW_DETAILS= "select callSid from calldetails where callStatus = 'in-progress' ";
	String UPDATE_AFTER_CALL_INITIATE = "update calldetails set callStatus = ? , callSid = ?, callStartTime =? , updatedTime= now() ,trialCount=? where id = ? ";
	String UPDATE_AFTER_CALLBACK_RESPONSE = "update calldetails set callStatus =? , callStartTime =?, callEndTime =? ,updatedTime =now() where callSid =? and id >0";
	String UPDATE_AFTER_CALLBACK_RESPONSE2 = "update calldetails set callStatus =?,callEndTime =? where callSid =?";
	String GET_DETAILS_FOR_UPDATING_CALLBACK_RESPONSE = "select * from calldetails where callSid=?";
	String GET_DETAILS_FOR_UPDATING_CALLBACK_RESPONSE2 = "select * from calldetails where id=?";


}
