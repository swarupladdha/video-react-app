package com.zoom.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties("app")
public class ApplicationProperties {
	private String jwtApiKey;
	private String jwtApiSecrete;
	private String oauthClientId;
	private String oauthClientSecrete;
	private String oauthAccountId;
	private String jwt_Token;
	private String oauthAccessToken;
	
	
	public String getJwtApiKey() {
		return jwtApiKey;
	}
	public void setJwtApiKey(String jwtApiKey) {
		this.jwtApiKey = jwtApiKey;
	}
	public String getJwtApiSecrete() {
		return jwtApiSecrete;
	}
	public void setJwtApiSecrete(String jwtApiSecrete) {
		this.jwtApiSecrete = jwtApiSecrete;
	}
	public String getOauthClientId() {
		return oauthClientId;
	}
	public void setOauthClientId(String oauthClientId) {
		this.oauthClientId = oauthClientId;
	}
	public String getOauthClientSecrete() {
		return oauthClientSecrete;
	}
	public void setOauthClientSecrete(String oauthClientSecrete) {
		this.oauthClientSecrete = oauthClientSecrete;
	}
	public String getOauthAccountId() {
		return oauthAccountId;
	}
	public void setOauthAccountId(String oauthAccountId) {
		this.oauthAccountId = oauthAccountId;
	}
	public String getJwt_Token() {
		return jwt_Token;
	}
	public void setJwt_Token(String jwt_Token) {
		this.jwt_Token = jwt_Token;
	}
	public String getOauthAccessToken() {
		return oauthAccessToken;
	}
	public void setOauthAccessToken(String oauthAccessToken) {
		this.oauthAccessToken = oauthAccessToken;
	}
	
}
