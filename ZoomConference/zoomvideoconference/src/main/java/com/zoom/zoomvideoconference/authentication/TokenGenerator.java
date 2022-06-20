package com.zoom.zoomvideoconference.authentication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.sf.json.JSONObject;

public class TokenGenerator {

	private String JWT_Token;
	private String AccessToken;

	@Autowired
	private Environment env;

	public String getEncodedString() {
		String plainCredentials = this.env.getProperty("app.oauthClientId") + ":"
				+ this.env.getProperty("app.oauthClientSecrete");
		String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
		System.out.println(base64Credentials);
		return base64Credentials;
	}

	public void getJwtToken() {
		String id = UUID.randomUUID().toString().replace("-", "");
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Date creation = new Date(System.currentTimeMillis());
		Date expiry = new Date(System.currentTimeMillis() + (1000 * 60));
		SecretKey key = Keys.hmacShaKeyFor(this.env.getProperty("app.jwtApiSecrete").getBytes(StandardCharsets.UTF_8));
		String token = Jwts.builder().setId(id).setIssuer(this.env.getProperty("app.jwtApiKey")).setIssuedAt(creation).setSubject("")
				.setExpiration(expiry).signWith(key, signatureAlgorithm).compact();
		this.JWT_Token = token;
		System.out.println(JWT_Token);

	}

	public String getTokenOauth(String url) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://zoom.us/oauth/token?grant_type=account_credentials&account_id="
						+ this.env.getProperty("app.oauthAccountId")))
				.POST(BodyPublishers.ofString("body")).setHeader("Authorization", "Basic " + this.getEncodedString())
				.build();

		HttpResponse<String> res = client.send(request, BodyHandlers.ofString());
		JSONObject obj = JSONObject.fromObject(res.body());
		this.AccessToken = String.valueOf(obj.get("access_token"));
		return this.AccessToken;
	}
}
