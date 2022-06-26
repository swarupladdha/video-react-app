package authentication;

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

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.sf.json.JSONObject;


public class TokenGenerator {

	
	private org.apache.log4j.Logger l = org.apache.log4j.Logger.getLogger(TokenGenerator.class);
	
	private String JWT_Token;
	private String AccessToken;
	private final String oauthTokenUri = "https://zoom.us/oauth/token?grant_type=account_credentials&account_id=%s";
	
	
	//oauth 2.0
	private String access_token;
	private String refresh_token;
	
	public String getEncodedString(String clientId, String clientSecrete) {
//		String plainCredentials = this.env.getProperty("app.oauthClientId") + ":"
//				+ this.env.getProperty("app.oauthClientSecrete");
		String plainCredentials = clientId + ":" + clientSecrete;
		String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
		System.out.println(base64Credentials);
		return base64Credentials;
	}

//	public void getJwtToken() {
//		String id = UUID.randomUUID().toString().replace("-", "");
//		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//		Date creation = new Date(System.currentTimeMillis());
//		Date expiry = new Date(System.currentTimeMillis() + (1000 * 60));
//		SecretKey key = Keys.hmacShaKeyFor(this.env.getProperty("app.jwtApiSecrete").getBytes(StandardCharsets.UTF_8));
//		String token = Jwts.builder().setId(id).setIssuer(this.env.getProperty("app.jwtApiKey")).setIssuedAt(creation).setSubject("")
//				.setExpiration(expiry).signWith(key, signatureAlgorithm).compact();
//		this.JWT_Token = token;
//		System.out.println(JWT_Token);
//
//	}

	public String getTokenOauth(String accountID, String clientId, String clientSecrete) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(String.format(this.oauthTokenUri, accountID)))
				.POST(BodyPublishers.ofString("body")).setHeader("Authorization", "Basic " + this.getEncodedString(clientId, clientSecrete))
				.build();

		HttpResponse<String> res = client.send(request, BodyHandlers.ofString());
		JSONObject obj = JSONObject.fromObject(res.body());
		this.AccessToken = String.valueOf(obj.get("access_token"));
		return this.AccessToken;
	}
	
	public String getoauthToken(String clientId, String clientSecrete, String code) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(String.format("https://zoom.us/oauth/token?grant_type=authorization_code&code=%s&redirect_uri=http://localhost:8080/meeting/", code)))
			.setHeader("authorization", "Basic "+getEncodedString(clientId, clientSecrete))
			.setHeader("Content-Type", "application/x-www-form-urlencoded")
			.POST(BodyPublishers.ofString("body")).build();
		
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		JSONObject obj = JSONObject.fromObject(res.body());
		this.access_token = String.valueOf(obj.get("access_token"));
		this.refresh_token = String.valueOf(obj.getString("refresh_token"));
		l.info("oauth 2.0 "+this.access_token);
		l.info("oauth 2.0 "+this.refresh_token);
		return res.body();
	}
	
	public String getAccessToken(String clientId, String clientSecrete) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create("https://zoom.us/oauth/token?grant_type=refresh_token&refresh_token="+this.refresh_token))
				.setHeader("authorization", "Basic "+getEncodedString(clientId, clientSecrete))
				.setHeader("Content-Type", "application/x-www-form-urlencoded")
				.POST(BodyPublishers.ofString("body")).build();
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		JSONObject obj = JSONObject.fromObject(res.body());
		this.access_token = String.valueOf(obj.get("access_token"));
		this.access_token = String.valueOf(obj.get("refresh_token"));
		l.info("this is refresh object "+obj);
		l.info("generated access and referesh token -----access token "+this.access_token +" Referesh"+this.refresh_token);
		return this.access_token;
	}
	
	
}
