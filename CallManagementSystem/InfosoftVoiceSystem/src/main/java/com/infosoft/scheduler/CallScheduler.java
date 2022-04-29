package com.infosoft.scheduler;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.infosoft.connections.InfosoftConnection;
import com.infosoft.service.CallService;

@Configuration
@EnableScheduling

public class CallScheduler {
	@Autowired
	CallService cs;
	@Autowired
	InfosoftConnection ic;

	@Scheduled(fixedRate = 1000L)
	void initiateNewCall() {
		Connection con = ic.dataBaseConnection();

		cs.initiateCall(con);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
