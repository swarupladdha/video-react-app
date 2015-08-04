/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.groupz.sendsms.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;


public class StartValueLeafReport implements Runnable{

	public static Logger logger = Logger.getLogger("smscountryLogger");
	static Properties prop = new Properties();
	static String providerName = "smscountry";
	static int num = -1;

	 static SMPPSession session;
	


	static {

		try {

			System.setProperty("Hibernate-Url", StartValueLeafReport.class
					.getResource("/Hibernate.cfg.xml").toString());
			InputStream in = StartValueLeafReport.class
					.getResourceAsStream("/smppConf.properties");
			prop.load(in);

			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());

			InputStream login = StartValueLeafReport.class
					.getResourceAsStream("/log4j.properties");
			logProperties.load(login);

			String threadSleepTime = prop.getProperty("threadreportSleeptime");
			num = Integer.parseInt(threadSleepTime);
			
	

		} catch (Exception e) {
			logger.info("Exception occured in load property file.", e);
			e.printStackTrace();
		}
	}
	
	  public StartValueLeafReport()
	    {
	        session = new SMPPSession();
	     }

	public static void getInit() {
		try {
			
	
			
			
			
			final AtomicInteger counter = new AtomicInteger();

			BasicConfigurator.configure();

			String ipaddr = prop.getProperty("valueleaf_host");
			String port = prop.getProperty("valueleaf_port");
			int portaddr = Integer.parseInt(port);
			String usrname = prop.getProperty("valueleaf_username");
			String password = prop.getProperty("valueleaf_password");
		

			session.setEnquireLinkTimer(50000);
			
			SMPPSession session = new SMPPSession();
			session.connectAndBind(ipaddr, portaddr, new BindParameter(
					BindType.BIND_TRX, usrname, password, "cp",
					TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN,
					null));

			logger.info("session started");

			 
			

			// Set listener to receive deliver_sm
			session.setMessageReceiverListener(new MessageReceiverListener() {

				public void onAcceptDeliverSm(DeliverSm deliverSm)
						throws ProcessRequestException {
					if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm
							.getEsmClass())) {
						counter.incrementAndGet();
						// delivery receipt
						try {

							String report = new String(deliverSm
									.getShortMessage());

							logger.info(report);
							//System.out.println("Report : " + report);
							SaveDeliveryStatusReportManager delstat = new SaveDeliveryStatusReportManager();
							delstat.saveDeliveryReport(report, providerName);

						} catch (InvalidDeliveryReceiptException e) {
							System.err
									.println("Failed getting delivery receipt");
							e.printStackTrace();
							logger.error(
									"Exception occured in receiving sms report receipt - ValueLeaf.",
									e);
						}

						catch (Exception e) {
							e.printStackTrace();
							logger.info(
									"Exception occured in receiving sms report receipt - ValueLeaf.",
									e);
						}
					}
				}

				public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
						throws ProcessRequestException {
					// TODO Auto-generated method stub
					return null;
				}

				public void onAcceptAlertNotification(
						AlertNotification alertNotification) {
				}

			});

		} catch (IOException e) {
		
			logger.info(
					"Failed connect and bind to host to ValueLeaf report connection.",
					e);
			e.printStackTrace();
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			String s = writer.toString();
			String printString = s.substring(0, 300);			
			
			logger.info("Failed connect and bind to host to ValueLeaf report connection. -- " + printString);
			
		}
	}
	

	
	 public void run()
	    {
		 while(true){
			 
	            try
	            {	   	                
	                    SessionState sessionrec = session.getSessionState();
	                    if(!sessionrec.isReceivable() || sessionrec.equals(
								SessionState.CLOSED) || session.getSessionState() == SessionState.UNBOUND)
	                    {
	                    	System.out.println("inside session check");
	                        session.unbindAndClose();               
	                        getInit();
	                        
	                        
	                    }
	                    
	                  
	                    Thread.sleep(num);
	                    Thread.yield();
	                 
	                    
	                  
	                
	            }
	            catch(Exception e)
	            {
	            	
	            	
					Writer writer = new StringWriter();
					PrintWriter printWriter = new PrintWriter(writer);
					e.printStackTrace(printWriter);
					String s = writer.toString();
					String printString = s.substring(0, 300);			
					
					logger.info("Exception occured while trying rebind inside thread - ValueLeaf -- " + printString);
	               
	               
	            }
	        } 
	    }

	public static void main(String args[]) {
		try {
			StartValueLeafReport smscountryobj = new StartValueLeafReport();
			
		
				
						 getInit();
			
		
	            Thread newthrd = new Thread(smscountryobj);
	            newthrd.run();

		} catch (Exception e) {
			System.err.println("Exception in ValueLeaf main");
			logger.info(
					"Exception in ValueLeaf main.",
					e);
			e.printStackTrace();
		}
	}
	
		
	
}