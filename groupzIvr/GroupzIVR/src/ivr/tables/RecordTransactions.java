package ivr.tables;



import java.io.Serializable;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import com.gpzhibernate.BaseDatabaseObject;
import com.gpzhibernate.DBCommonOpertion;
import com.gpzhibernate.DontPersistWhenSerializing;


@Entity
@Table(name = "ivrrecorddata")
public class RecordTransactions extends BaseDatabaseObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3679060962303828571L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@DontPersistWhenSerializing
	private int id;

	@Column(name = "kookoourls")
	private String kookoourls;
	
	@Column(name = "groupzurls")
	private String groupzurls;	
	
	@Column(name = "selectiondata")
	private String selectiondata;
	
	@Column(name = "publishJSON")
	private String publishJSON;

	@Column(name = "groupzCode")
	private String groupzCode;

	@Column(name = "retryCounter")
	private int retryCounter;
	
	@Column(name = "downloadFlag")
	private boolean downloadFlag;
	
	@Column(name = "publishedFlag")
	private boolean publishedFlag;

	@Column(name = "datetime")
	private Date datetime;
	
	@Column(name = "endDateString")
	private String endDateString;	

	@Column(name = "wavdata",columnDefinition = "LONGBLOB")
    @Lob()
    @Basic(fetch=FetchType.LAZY)
    private byte[] wavdata;

	public  byte[] getwavdata() {
		return wavdata;
	}

	public void setwavdata(byte[] wavdata) {
		this.wavdata = wavdata;
	}
	


	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	

	
	public int getRetryCounter() {
		return retryCounter;
	}


	public void setRetryCounter(int retryCounter) {
		this.retryCounter = retryCounter;
	}
	
	
	public boolean getdownloadFlag(){
		return downloadFlag;
	}
	
	public void setdownloadFlag(boolean downloadFlag) {
		this.downloadFlag = downloadFlag;
	}
	
	public boolean getpublishedFlag(){
		return publishedFlag;
	}
	
	public void setpublishedFlag(boolean publishedFlag) {
		this.publishedFlag = publishedFlag;
	}
	
	
	public void setkookoourls(String kookoourls) {
		this.kookoourls = kookoourls;
	}

	public String getkookoourls() {
		return kookoourls;
	}
	
	public void setgroupzurls(String groupzurls) {
		this.groupzurls = groupzurls;
	}

	public String getgroupzurls() {
		return groupzurls;
	}
	
	public void setselectiondata(String selectiondata) {
		this.selectiondata = selectiondata;
	}

	public String getselectiondata() {
		return selectiondata;
	}
	
	public void setpublishJSON(String publishJSON) {
		this.publishJSON = publishJSON;
	}

	public String getpublishJSON() {
		return publishJSON;
	}
	
	
	public void setdatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Date getdatetime() {
		return datetime;
	}
	
	
	public void setgroupzCode(String groupzCode) {
		this.groupzCode = groupzCode;
	}

	public String getgroupzCode() {
		return groupzCode;
	}
	

	
	public void setendDateString(String endDateString) {
		this.endDateString = endDateString;
	}

	public String getendDateString() {
		return endDateString;
	}
	
	
	
	
	

	public static RecordTransactions getSingleContext(int id) {
		String queryCond = "  ";
		
		queryCond = " id = "+id;
		try {
			System.out.println("The query condition is : " + queryCond);
			RecordTransactions contxtResult = (RecordTransactions) DBCommonOpertion
					.getSingleDatabaseObject(RecordTransactions.class, queryCond);

			return contxtResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<RecordTransactions> listdownloadWav(int id,String modVal) {
		String queryCond = "  ";

		try {

			queryCond = " downloadFlag = false  and Id % "+ modVal + "=" + id ;

			

			List<RecordTransactions> statusResult = (List<RecordTransactions>) DBCommonOpertion
					.getDatabaseObjects(RecordTransactions.class, queryCond);
			
			
			return statusResult;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<RecordTransactions>();
		}
	}


}
