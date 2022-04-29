package com.infosoft.queries;

public interface CallQueries {
	String ADD_CALLDETAILS = "insert into calldetails (fromNumber,toNumber,callStatus,timeLimit,createdTime,callBackUrl,callId) values(?,?,?,?,now(),?,?)";
	String GET_NEW_NUMERS = " select * from calldetails where callStatus ='initiated' and callSid is null and callStartTime is null and trialCount < ? ";
	String UPDATE_AGTER_CALL_INITIATE = "update calldetails set callStatus = ? , callSid = ?, callStartTime =? , updatedTime= now() ,trialCount=? where id = ? ";
	String UPDATE_AFTER_CALLBACK_RESPONSE = "update calldetails set callStatus =? , callStartTime =?, callEndTime =? ,updatedTime =now() where callSid =? and id >0";
	String GET_DETAILS_FOR_UPDATING_CALLBACK_RESPONSE = "select * from calldetails where callSid=?";
	String GET_DETAILS_FOR_UPDATING_CALLBACK_RESPONSE2 = "select * from calldetails where id=?";

}
