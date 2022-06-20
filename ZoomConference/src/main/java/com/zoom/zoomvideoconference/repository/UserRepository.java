package com.zoom.zoomvideoconference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zoom.zoomvideoconference.dbhelper.UserDao;

public interface UserRepository extends JpaRepository<UserDao, Integer>{
	
}
