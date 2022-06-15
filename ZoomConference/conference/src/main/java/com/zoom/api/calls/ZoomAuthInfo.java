package com.zoom.api.calls;

public class ZoomAuthInfo {
	private String zoomClientId;
	private String zoomClientSecrete;
	private String zoomVerificationToken;
	
	
	ZoomAuthInfo(String zoomClientId, String zoomClienSecrete, String zoomVerificationToken) {
		this.zoomClientId = zoomClientId;
		this.zoomClientSecrete = zoomClienSecrete;
		this.zoomVerificationToken = zoomVerificationToken;
	}

	public String getZoomClientId() {
		return zoomClientId;
	}


	public String getZoomClientSecrete() {
		return zoomClientSecrete;
	}

	public String getZoomVerificationToken() {
		return zoomVerificationToken;
	}


	
	
}
