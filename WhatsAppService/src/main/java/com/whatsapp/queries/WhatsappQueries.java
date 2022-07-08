package com.whatsapp.queries;

public interface WhatsappQueries {
	String SELECT_NUMBERS_FOR_MESSAGE = "select  id , number , message from whatsappmessages where sentFlag = false and id % ? = ? ";
	String UPDATE_SENT_FLAG = "update whatsappmessages set sentFlag =? where id=?";

}
