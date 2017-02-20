package ivr.utils;



import java.nio.ByteBuffer;
import java.util.Calendar;


public class GroupzKey {

	
	private int reserved = 0x0; ;
	private String ipaddress ;
	private short typeofsec ;
	private long docid ;
	private String did;
	private String sessionid ;
	private String encodedString ;
	private String date;

	private static final int  maxDecodedGroupzKey = 66  ;
	private static final int maxEncodedGroupzKey =  88;
	private static final char encryptionSequence[] = 
		{ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		  'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		  'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', 
		  '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 
		  'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 
		  'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
		  'Y', 'Z', '_', '-'} ;

	private int docIdLength = 24 ;
	private int sessionIdLength = 32; //8 
	private int ipAddressLength = 4 ;
	private int typeOfSecurityLength = 2 ;
	private int reservedBytesLength = 2 ;
	
	private int docIdOffset = 0 ;
	private int sessionIdOffset = docIdOffset+ docIdLength;
	private int ipAddressOffset = sessionIdOffset + sessionIdLength ;
	private int typeOfSecurityOffset = ipAddressOffset + ipAddressLength  ;
	private int reservedOffset = typeOfSecurityOffset + typeOfSecurityLength ;
	
	public final static int o0 = 0x1;
	public final static int o1 = 0x2;
	public final static int o2 = 0x4;
	public final static int o3 = 0x8;
	
	
	
	public GroupzKey( String ipAddress, String sessionId, short typeofsecurity, String docId){
		
		ipaddress=ipAddress;
		sessionid = sessionId ;
		typeofsec = typeofsecurity ;
		/*docid = docId ;
		GroupzDocId gd = new GroupzDocId(docid);
		gd.setTimestamp(Calendar.getInstance()) ;*/
		did = docId ;
		//encode() ;
	}
	
	public GroupzKey( String encStr){
		
		encodedString = encStr ;
		//decode(encodedString) ;
	}

	
	
	
	public GroupzKey() {
		
	}
	
	public int findIndex( char value){
		int baseValue = -1 ;
		int offset = -1 ;
		if ( value >= 'a' && value <= 'z'){
			baseValue = 0 ;
			offset = value - 'a' ;
		}
		
		if( value >= '0' && value <= '9'){
			baseValue = 26 ; // no. of lower case alphabets
			offset = value - '0' ;
		}
		if ( value >= 'A' && value <= 'Z'){
			baseValue = 36 ; // added number of digits
			offset = value - 'A' ;
		}
		if( value == '_'){
			baseValue = 62 ;
			offset = 0 ;
		}
		if( value == '-'){
			baseValue = 63 ;
			offset = 0 ;
		}

		int returnValue = baseValue + offset ;
		return returnValue ;
	}
	
	

	
	// getters and setters
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getSessionId() {
		return sessionid;
	}

	public void setSessionId(String sessionId) {
		this.sessionid = sessionId;
	}

	public String getDocId() {
		return did;
	}

	public void setDocId(String docId) {
		this.did = docId;
	}

	public short getTypeOfSec() {
		return typeofsec;
	}

	public void setTypeOfSec(short typeofsec) {
		this.typeofsec = typeofsec;
	}

	public String getIPAddress() {
		
		return ipaddress;
	}

	public void setIPAddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	

	
	public int getReserved() {
		
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}


	
	

	private String encodeThreeBytes( byte value[]) {
		
		//System.out.println("Size of value passed : " + value.length) ;

		/*System.out.println("First Value : " + " : " + ((value[0]  & 0xFF) >>> 2 )) ;
		System.out.println("Second Value : " + (((value[0] & 0x03) << 4) | ((value[1] & 0xFF)>>>4))) ;
		System.out.println("Third Value : "  + (((value[1] & 0x0F) << 2) | ((value[2] & 0xC0) >>> 6))) ;
		System.out.println("Fourth Value : "  + ( value[2] & 0x3F) );*/

		char firstCharacter = encryptionSequence[ (value[0]  & 0xFF) >>> 2 ] ;
		char secondCharacter = encryptionSequence[((value[0] & 0x03) << 4) | ((value[1] & 0xFF)>>>4)] ;
		char thirdCharacter = encryptionSequence[(((value[1] & 0x0F) << 2) | ((value[2] & 0xC0) >>> 6))] ;
		char fourthCharacter = encryptionSequence[((value[2] & 0x3F))] ;
		StringBuffer encodedString = new StringBuffer() ;
		encodedString.append( firstCharacter ) ;
		encodedString.append( secondCharacter ) ;
		encodedString.append( thirdCharacter ) ;
		encodedString.append( fourthCharacter ) ;
		//System.out.println("Encoded string : " + encodedString.toString()) ;
		return encodedString.toString() ;
	}
	
	private byte[] decodeToThreeBytes( char value[]) {
		
//		System.out.println("Size of value passed : " + value.length) ;

		int firstOffset = findIndex( value[0]) ;
		int secondOffset = findIndex( value[1]) ;
		int thirdOffset = findIndex( value[2]) ;
		int fourthOffset = findIndex( value[3]) ;
		
		byte decodedBytes[] = new byte[3] ;
		decodedBytes[0] = (byte)(((firstOffset & 0x3F) << 2) | ((secondOffset & 0x30) >>> 4)); 
		//System.out.println("First : " + firstOffset + "(" + value[0] + ")" + " Second : " + secondOffset + "(" + value[1] + ")"+" Third : " + thirdOffset + "(" + value[2] + ")"+ " Fourth : " + fourthOffset +"(" + value[3] + ")") ;
		decodedBytes[1] = (byte)(((secondOffset & 0x0F) << 4 ) | ((thirdOffset & 0x3F) >>> 2)); 
		decodedBytes[2] = (byte)(((thirdOffset & 0x03) << 6 ) | ((fourthOffset & 0x3F))); 				
		return decodedBytes ;
	}

	

	public String encode(){
		ByteBuffer byteBufferDecoded = ByteBuffer.allocate( maxDecodedGroupzKey );
		StringBuffer encodedString = new StringBuffer() ;
		
		byte[] srcc = did.getBytes() ;
		for( int i=0 ; i < did.length() ; i++){
			byteBufferDecoded.put(docIdOffset+i, srcc[i]) ;
		}
		
		//byteBufferDecoded.putLong(docIdOffset, docid) ;
	   
		//byteBufferDecoded.putInt(ipAddressOffset, ipadd) ;
		byteBufferDecoded.putShort(typeOfSecurityOffset, typeofsec) ;
		//byteBufferDecoded.putLong(sessionIdOffset, sessionid) ;
			byte[] src = sessionid.getBytes() ;
			for( int i=0 ; i < sessionid.length() ; i++){
				byteBufferDecoded.put(sessionIdOffset+i, src[i]) ;
			}
			
			String[] ipa = ipaddress.split("\\.");
			
			for( int i=0,j=0 ; j < ipa.length ; i++,j++){
				if(ipa[j].equals("*")==false){
					byteBufferDecoded.put(ipAddressOffset+i,  (byte)Integer.parseInt(ipa[j])) ;
					//int j = ipAddressOffset+i;
					//System.out.println(j+" = "+byteBufferDecoded.get(ipAddressOffset+i));
				}
				else{
					if(j==0){
						reserved = reserved | o0;
					}
					if(j==1){
						reserved = reserved | o1;
					}
					if(j==2){
						reserved = reserved | o2;
					}
					if(j==3){
						reserved = reserved | o3;
					}
					
				}
				}
			
			byteBufferDecoded.put(reservedOffset, (byte)reserved) ;
			
		
		
		
		
		
		byte[] tmpBytes = new byte[3]  ;
		for( int start=0 ; start < maxDecodedGroupzKey ; start=start+3){
			tmpBytes[0] = byteBufferDecoded.get( start ) ;
			tmpBytes[1] = byteBufferDecoded.get( start+1 ) ;
			tmpBytes[2] = byteBufferDecoded.get( start+2 ) ;
			
			encodedString.append( encodeThreeBytes(tmpBytes)) ;
		}
		System.out.println( "The Encoded String is : " + encodedString) ;		
		return encodedString.toString() ;
	}
	
	@SuppressWarnings("deprecation")
	public boolean decode(String triepon){
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(maxDecodedGroupzKey) ;
		int decodeByteOffset = 0 ;
		char[] tmpChars = new char[4] ;
		byte[] byteDecoded = null ;
		for( int len=0 ; len < triepon.length() ; len = len+4 ){
			tmpChars[0] = triepon.charAt(len) ;
			tmpChars[1] = triepon.charAt(len+1) ;
			tmpChars[2] = triepon.charAt(len+2) ;
			tmpChars[3] = triepon.charAt(len+3) ;
			byteDecoded = decodeToThreeBytes(tmpChars) ;
//			System.out.println("Decoded byte offset : " + decodeByteOffset + " Byte: " + byteDecoded[0]) ;
			byteBuffer.put( decodeByteOffset++ , byteDecoded[0]) ;
//			System.out.println("Decoded byte offset : " + decodeByteOffset + " Byte: " + byteDecoded[1]) ;
			byteBuffer.put( decodeByteOffset++ , byteDecoded[1]) ;
//			System.out.println("Decoded byte offset : " + decodeByteOffset + " Byte: " + byteDecoded[2]) ;
			byteBuffer.put( decodeByteOffset++ , byteDecoded[2]) ;
		}

	
		//docid = byteBuffer.getLong(docIdOffset) ; 
		
		String ttss="";
		for(int i = docIdOffset;i<docIdOffset+docIdLength;i++){
			
				ttss = ttss+(char)byteBuffer.get(i);
			
		}
		
		did=ttss;
		/*GroupzDocId tg = new GroupzDocId() ;
		tg.decode( did ) ;
		docid = tg.getDocId();
		date = tg.getDate();*/
		
		String ts="";
		for(int i = sessionIdOffset;i<sessionIdOffset+sessionIdLength;i++){
			
				ts = ts+(char)byteBuffer.get(i);
			
		}
		
		sessionid=ts;
		
		reserved = byteBuffer.get(reservedOffset) ;
		
		String ips=""; boolean f=true,a=false;
		for(int j=0,i = ipAddressOffset;j<4;i++,j++){
			a=false;
			
			
			if(j==0){
				if(o0 == (reserved & o0)){
					a=true;
				}
			}
			if(j==1){
				if(o1 == (reserved & o1)){
					a=true;
				}	
			}
			if(j==2){
				if(o2 == (reserved & o2)){
					a=true;
				}
			}
			if(j==3){
				if(o3 == (reserved & o3)){
					a=true;
				}
			}
			String tss = "";
			if(a==false){
				int ti = byteBuffer.get(i);
				if(ti<0){
					ti = ti + 256;
				}
				tss = Integer.toString(ti);
			}
			else{
				tss = "*";
			}
				if(f==false){
					ips = ips+"."+tss;
					
				}
				else{
					ips = ips+tss;
					f=false;
				}
			
		}
		
		ipaddress=ips.trim();
		
		reserved = byteBuffer.get(reservedOffset) ;
		typeofsec = byteBuffer.getShort(typeOfSecurityOffset) ;
		

		System.out.println("Doc ID : " + did ) ;
		System.out.println("IP address : " + ipaddress ) ;
		System.out.println("type of security : " + typeofsec ) ;
		System.out.println("sessionid : " + sessionid ) ;
		System.out.println( "Reserved : " + reserved) ;
		return false ;
	}

	public static void main( String[] args){
		GroupzKey gt = new GroupzKey("221.134.196.74","1234",(short)1,"aaaaaaaaaaddPvAj66aaauEa") ;
		
		String encodedString = gt.encode() ;
		System.out.println("Encoded String : " + encodedString ) ;
		
		GroupzKey tg = new GroupzKey() ;
		tg.decode( encodedString ) ;
	

	
}
}