package alerts.email ;

import java.io.FileInputStream;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import alerts.utils.Constants;

/** The main entry point for the application which
 *  creates a pool of threads for probing the
 *  MessagesInTable, messages_sent_table and the
 *  retrytable and uses the Java 5 Executor service
 *  to run all the threads in the pool as parallel 
 *  tasks
 *
 *  @author Sunil Tuppale
 *  @date July-19-2010
 *  @version 1.0
 */
public class VinrEmailNotification {
    static final Logger logger = Logger.getLogger(VinrEmailNotification.class);

    public static void main(String[] args) {
        
        Properties logProperties = null;
        try {
            //settings for logging
            String fileName = System.getenv("LOG_PROPERTIES_FILE");
            if (fileName == null)
                fileName="vinralerts.properties" ;

            logProperties = new Properties(System.getProperties());
            logProperties.load(new FileInputStream(fileName));
            PropertyConfigurator.configure(logProperties);
            logger.debug("Logging initialized in VinrEmailNotification class ");

        } catch(Exception e) {
            e.printStackTrace();
        }

        /*
         * create a thread pool with four threads
         */
        int THREAD_POOL_SIZE = ConnectionPoolProvider.getInstance().getThreadPoolSize();
        ExecutorService messagesInTableTPExecSvc = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        //ExecutorService messagesSentTableTPExecSvc = Executors.newFixedThreadPool(Constants.THREAD_POOL_SIZE); 
        //ExecutorService retryTableTPExecSvc = Executors.newFixedThreadPool(Constants.THREAD_POOL_SIZE);

        /*
         * place four tasks in the work queue for the thread pool
         */
        for( int i = 0; i < THREAD_POOL_SIZE; i++ ) {
            messagesInTableTPExecSvc.execute(new MessagesInTableProbe(i));
            //messagesSentTableTPExecSvc.execute(new MessagesSentTableProbe(i));
            //retryTableTPExecSvc.execute(new RetryTableProbe(i));
        }

        /*
         * prevent other tasks from being added to the queue
         */
        messagesInTableTPExecSvc.shutdown();
        //messagesSentTableTPExecSvc.shutdown(); 
        //retryTableTPExecSvc.shutdown();
        //ConnectionPoolProvider.getInstance().getDataSource().release();
    }
}
