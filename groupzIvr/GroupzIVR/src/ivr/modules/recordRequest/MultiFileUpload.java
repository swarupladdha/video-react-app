package ivr.modules.recordRequest;

import ivr.utils.GroupzKey;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

public class MultiFileUpload {
	private final String boundary;
	private static final String LINE_FEED = "\r\n";
	private HttpURLConnection httpConn;	
	private OutputStream outputStream;
	private PrintWriter writer;

	/**
	 * This constructor initializes a new HTTP POST request with content type is
	 * set to multipart/form-data
	 * 
	 * @param requestURL
	 * @param charset
	 * @throws IOException
	 */
	public MultiFileUpload(String requestURL)
			throws IOException {
		String charset = "UTF-8";

		// creates a unique boundary based on time stamp
		boundary = "===" + System.currentTimeMillis() + "===";
		System.out.println("finalurl = " + requestURL);
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true); 

		httpConn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);

		outputStream = httpConn.getOutputStream();

		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
				true);

		InetAddress iP = InetAddress.getLocalHost();

		String ipaddress = iP.getHostAddress();
		
		//Change groupzkey ipaddress value with above while uploading to code system.

		//GroupzKey gt = new GroupzKey("223.180.81.152", "1234", (short)1, 127);

		//String encodedString = gt.encode();
		
		String encodedString = "test";

		System.out.println("Encoded String : " + encodedString);

		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"groupzkey\"")
				.append(LINE_FEED);
		writer.append("Content-Type: text/plain; charset=" + charset).append(
				LINE_FEED);
		writer.append(LINE_FEED);
		writer.append(encodedString).append(LINE_FEED);
		writer.flush();

	}

	public void addFilePart(String fieldName, InputStream inputStream,
			String fileName) throws IOException {

		writer.append("--" + boundary).append(LINE_FEED);
		writer.append(
				"Content-Disposition: form-data; name=\"document\"; filename=\""
						+ fileName + "\"").append(LINE_FEED);
		writer.append(
				"Content-Type: "
						+ URLConnection.guessContentTypeFromName(fileName))
				.append(LINE_FEED);
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.flush();

		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();

		writer.append(LINE_FEED);
		writer.flush();
	}

	public String finish() throws IOException {
		
		String resultString = null;

		writer.append(LINE_FEED).flush();
		writer.append("--" + boundary + "--").append(LINE_FEED);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			StringBuffer res = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				res.append(inputLine);
			}

			resultString = res.toString();
			in.close();

			httpConn.disconnect();
		} else {
			throw new IOException("Server returned non-OK status: " + status);
		}

		return resultString;
	}
}