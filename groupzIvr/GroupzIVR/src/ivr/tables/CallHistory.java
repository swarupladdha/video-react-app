package ivr.tables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.gpzhibernate.BaseDatabaseObject;
import com.gpzhibernate.DBCommonOpertion;

import com.gpzhibernate.DontPersistWhenSerializing;
 
@Entity
@Table(name = "callhistory")
public class CallHistory extends BaseDatabaseObject implements
		Serializable {

	private static final long serialVersionUID = -1972848324964001530L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@DontPersistWhenSerializing
	private int id;

	@Column(name = "selection")
	private String selection;

	@Column(name = "ivrnumber")
	private String ivrnumber;

	@Column(name = "contactNumber")
	private String contactNumber;
	
	@Column(name = "groupzCode")
	private String groupzCode;

	@Column(name = "migrationStatus")
	 private Boolean migrationStatus;
	
	@Column(name = "callStatus")
	 private Boolean callStatus;
	
	
	@Column(name = "datetime")
	private Date datetime;
	
	@Column(name = "contactFlag")
	private boolean contactFlag;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean getcontactFlag(){
		return contactFlag;
	}
	
	public void setcontactFlag(boolean contactFlag) {
		this.contactFlag = contactFlag;
	}
	
	public String getselection() {
		return selection;
	}

	public void setselection(String selection) {
		this.selection = selection;
	}
	
	public Boolean getmigrationStatus() {
		return migrationStatus;
	}


	public void setmigrationStatus(Boolean migrationStatus) {
		this.migrationStatus = migrationStatus;
	}
	
	public Boolean getcallStatus() {
		return callStatus;
	}


	public void setcallStatus(Boolean callStatus) {
		this.callStatus = callStatus;
	}
	
	
	
	public String getgroupzCode() {
		return groupzCode;
	}


	public void setgroupzCode(String groupzCode) {
		this.groupzCode = groupzCode;
	}
	
	public Date getdatetime() {
		return datetime;
	}


	public void setdatetime(Date datetime) {
		this.datetime = datetime;
	}


	public String getivrnumber() {
		return ivrnumber;
	}

	public void setivrnumber(String ivrnumber) {
		this.ivrnumber = ivrnumber;
	}

	public String getcontactNumb() {
		return contactNumber;
	}

	public void setcontactNumb(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public static List<CallHistory> listofMigrationRecords(boolean status,int start,int noRecs) {
		
		
		String queryCond = "  ";

		try {

			queryCond = " migrationStatus = " + status + " LIMIT "+start + ","+noRecs;

			System.out.println("The query condition is : " + queryCond);

			List<CallHistory> statusResult = (List<CallHistory>) DBCommonOpertion
					.getDatabaseObjects(CallHistory.class, queryCond);
			System.out.println("The return list is : " + statusResult);
			return statusResult;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<CallHistory>();
		}
	}

	
	public static List<CallHistory> lisofrecordsforUpdate(String listStr) {
		
		
		String queryCond = "  ";

		try {

			queryCond = " id in (" + listStr + ")";

			System.out.println("The query condition is : " + queryCond);

			List<CallHistory> statusResult = (List<CallHistory>) DBCommonOpertion
					.getDatabaseObjects(CallHistory.class, queryCond);
			System.out.println("The return list is : " + statusResult);
			return statusResult;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<CallHistory>();
		}
	}
	
	
	public static CallHistory getSingleCallHistory(int idval) {
		String queryCond = "  ";
		
		queryCond = " id = " + idval;
		try {
			System.out.println("The query condition is : " + queryCond);
			CallHistory result = (CallHistory) DBCommonOpertion
					.getSingleDatabaseObject(CallHistory.class, queryCond);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
