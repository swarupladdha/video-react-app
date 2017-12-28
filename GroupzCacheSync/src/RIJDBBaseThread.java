


import com.mongodb.client.MongoDatabase;


abstract class RIJDBBaseThread implements Runnable {
    private String message;
    int threadid ;
    private MongoDatabase  session = null ;
    public RIJDBBaseThread(int s, MongoDatabase db){
	threadid = s ;
	try{
		session = db;
	}
	catch(Exception e){
		e.printStackTrace() ;
	}
        this.message=""+s;
    }
 

    public void run() {
	while( true ) {
        	processmessage();
		try{
			Thread.sleep(2000) ;
		}
		catch(Exception e){
			e.printStackTrace(); 
		}
	}
    }
 
    public void processmessage() {
        try {
		  process(session) ;
  	} 
	catch (Exception e) { 	
		e.printStackTrace(); 
	}
    }

	abstract void process(MongoDatabase session )  ;
}

