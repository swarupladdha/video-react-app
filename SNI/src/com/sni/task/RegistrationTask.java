package com.sni.task;


import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONObject;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.sni.utils.InterfaceKeys;
import com.sni.utils.PropertiesUtil;
import com.sni.utils.RestUtils;

public class RegistrationTask  {
		String registrationResponse = ""; 
		private List mClientIDs ;
	    private String mAudience;
	    private GoogleIdTokenVerifier mVerifier ;
	    private JsonFactory mJFactory;
	    private String mProblem = "Verification failed. (Time-out?)";
	 
	public RegistrationTask(String[] clientIDs, String audience) {
		   mClientIDs = Arrays.asList(clientIDs);
	       mAudience = audience;
	       NetHttpTransport transport = new NetHttpTransport();
	       mJFactory = new GsonFactory();
	       mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
	}


	public String GoogleSignUpProfile(String serviceType, String functionType,JSONObject data, JSONObject json,String idTokenString) {
		/*Connection connection = null;
		
		ConnectionPooling connectionPooling = ConnectionPooling.getInstance();*/
	
		try{
			String email = RestUtils.getJSONStringValue(data, InterfaceKeys.email);
			registrationResponse = RestUtils.emailIsValid(email);
			if (registrationResponse!=null) {
				return registrationResponse;
			}
			
			String name=RestUtils.getJSONStringValue(data, InterfaceKeys.name);
			if (RestUtils.isEmpty(name) == false) {
				registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.empty_name_code),PropertiesUtil.getProperty(InterfaceKeys.empty_name_message));
				return registrationResponse;
			}
			
			String atoken=RestUtils.getJSONStringValue(data, InterfaceKeys.token);
			if (RestUtils.isEmpty(atoken) == false) {
				registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.empty_token_code),PropertiesUtil.getProperty(InterfaceKeys.empty_token_message));
				return registrationResponse;
			}
			GoogleIdToken.Payload payload = null;
	    	
            GoogleIdToken token = GoogleIdToken.parse(mJFactory, "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE0MzY4NWY3YzQ2NmYwZWVlZGJmYjIwNWE4MmY2N2U0M2Y2YjE1YjgifQ.eyJhenAiOiI4MTU5NjUyMTYxMDYtaG0yZjVpcHZzcnYwczdnMWRraWFlZGRvZTgyYWkyZnEuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI4MTU5NjUyMTYxMDYtanNuZTFqMmVzZGlwaWZraWFoaGJlZjRrczJzNGd0bjIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDU0MzA3Njc2MzgxODA5NTMzNDYiLCJlbWFpbCI6InBheWFzd2luaWJoYXRAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImlzcyI6Imh0dHBzOi8vYWNjb3VudHMuZ29vZ2xlLmNvbSIsImlhdCI6MTQ5Mjc3MDExMSwiZXhwIjoxNDkyNzczNzExLCJuYW1lIjoiUGF5YXN3aW5pIEhlZ2RlIiwicGljdHVyZSI6Imh0dHBzOi8vbGg2Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tbWNGaG5YLUZaNXcvQUFBQUFBQUFBQUkvQUFBQUFBQUFDd2cvOWc5b0hycFMyNk0vczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IlBheWFzd2luaSIsImZhbWlseV9uYW1lIjoiSGVnZGUiLCJsb2NhbGUiOiJlbi1HQiJ9.fdJpiEUGkCuMZtcFcbsvb91xlVIEorsjsHR5MWRjL1KOcUt5vn26POs67Xg8zpUdyGj9ORjUI5MEVb2OrcAolJ2PMqUuZzo03tNCnmXiZD5Is48sgZmh60lNNKQ32kU8bFTf3vyEBUg2LHCIXtyAMH3uZ46KN2U32Vgo4HtjAlVvDwI2ODk35E2Sx97-Oo-G_V_-2spyLbif3t8YvvMVQV12VsQBq37U_g5XgslActB3u1c-PshN0tzQ5ZKx2aZ4OQfpAwxS7CIHbjTUgL29uSFoTVFWV8OkDoiwdUSZgBQhMQcfYl8L15KJ6Ud-bdb6V1n-lcDwEVp2HKAE1sBmVQ");
            System.out.println(token);
            GoogleIdToken.Payload t = token.getPayload();
            System.out.println(t.getEmail());
            System.out.println(t.getAudience());
            System.out.println(t.getIssuee());
            System.out.println(t.getUserId());
            System.out.println(mVerifier.verify(token));
            
            JSONObject jobj = new JSONObject();
            jobj.put(InterfaceKeys.email, t.getEmail());
            if(email.equals(t.getEmail())){
	            registrationResponse = RestUtils.processSucess(serviceType, functionType, jobj, "");
	            return registrationResponse;
            }
         //   jobj.put(InterfaceKeys.userid, t.getUserId());
            
            
           /* if (mVerifier.verify(token)) {
                GoogleIdToken.Payload tempPayload = token.getPayload();
                if (!tempPayload.getAudience().equals(mAudience))
                    mProblem = "Audience mismatch";
                else if (!mClientIDs.contains(tempPayload.getIssuee()))
                    mProblem = "Client ID mismatch";
                else
                    payload = tempPayload;
                System.out.println("==========================>      "+mProblem);
            }*/

			
		}
		catch(IllegalStateException e){
			registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.connection_timeout_code),PropertiesUtil.getProperty(InterfaceKeys.connection_timeouts_message));
			return registrationResponse;
		}
		catch(Exception e)	{
			e.printStackTrace();
			registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_code),PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_message));
			return registrationResponse;
		}
		/*finally	{
			if(connection!=null){
				connectionPooling.close(connection);
			}
		}*/
		registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_code),PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_message));
		return registrationResponse;
		}

		

	
}


