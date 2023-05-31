package com.flexical.controller;

public class ApiResponse<T> {
		private int statusCode;
	    private T data;
	    
	    public ApiResponse(int statusCode, T data) {
	        this.statusCode = statusCode;
	        this.data = data;
	    }

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}
	    
}
