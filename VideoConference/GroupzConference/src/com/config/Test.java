package com.config;

import java.net.URLEncoder;

import com.utils.ConnectionUtils;

import net.sf.json.JSONObject;

public class Test {

//	public static void main(String[] args) {
//		String videoUrl = "https://s3.amazonaws.com/tokbox.com.archive2/46107292/4700fc3a-ae25-45c4-bdf9-deb16514eca7/archive.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20190410T070319Z&X-Amz-SignedHeaders=host&X-Amz-Expires=600&X-Amz-Credential=AKIAI6LQCPIXYVWCQV6Q%2F20190410%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=0dc5528ae0cccc7fa97d0ca26d5471930ecfae42d9b6665a50e2e940c4bfdc23";
//		Test t = new Test();
//		t.connectAndGet(videoUrl);
//	}
	
	
	public void connectAndGet(String videoUrl) {
		try {
			String url ="http://qa1.jobztop.com:8080/GroupzFS/uploadvideobyurl?request=";
			JSONObject obj = new JSONObject();
			obj.put("url", videoUrl);
			ConnectionUtils utils = new ConnectionUtils();
			String response = utils.ConnectandRecieve(url+URLEncoder.encode(obj.toString(),"UTF-8"));
			System.out.println(response);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
