package ivr.tables;



import java.io.Serializable;
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
@Table(name = "recordcontext")
public class RecordContext extends BaseDatabaseObject implements Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 3679060962303828571L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@DontPersistWhenSerializing
	private int id;

	@Column(name = "url")
	private String url;

	@Column(name = "groupzCode")
	private String groupzCode;
	
	@Column(name = "ivrnumber")
	private String ivrnumber;
	

	@Column(name = "selectedList")
	private String selectedList;
	

	@Column(name = "selectionList")
	private String selectionList;
	
	@Column(name = "displayList")
	private String displayList;
	

	@Column(name = "sessionid")
	private String sessionid;
	
	@Column(name = "mainMenuSelection")
	private String mainMenuSelection;
	
	@Column(name = "callerID")
	private String callerID;
	
	@Column(name = "mainMenuFlag")
	private boolean mainMenuFlag;
	
	@Column(name = "subMenuFlag")
	private boolean subMenuFlag;
	
	@Column(name = "groupzSelectFlag")
	private boolean groupzSelectFlag;
	
	@Column(name = "recordFlag")
	private boolean recordFlag;
	
	@Column(name = "publishxml")
	private String publishxml;
	
	
	@Column(name = "multiLanguageFlag")
	private boolean multiLanguageFlag;
	
	@Column(name = "languageFlag")
	private boolean languageFlag;
	
	
	@Column(name = "selectedlangCode")
	private String selectedlangCode;
	
	@Column(name = "isMultiLangSelectionFlag")
	private boolean isMultiLangSelectionFlag;
	
	@Column(name = "publishUrlList")
	private String publishUrlList;
	
	
	public boolean getmainMenuFlag(){
		return mainMenuFlag;
	}
	
	public void setmainMenuFlag(boolean mainMenuFlag) {
		this.mainMenuFlag = mainMenuFlag;
	}
	
	
	public boolean getisMultiLangSelectionFlag(){
		return isMultiLangSelectionFlag;
	}
	
	public void setisMultiLangSelectionFlag(boolean isMultiLangSelectionFlag) {
		this.isMultiLangSelectionFlag = isMultiLangSelectionFlag;
	}
	
	
	public boolean getsubMenuFlag(){
		return subMenuFlag;
	}
	
	public void setsubMenuFlag(boolean subMenuFlag) {
		this.subMenuFlag = subMenuFlag;
	}
	
	public boolean getgroupzSelectFlag(){
		return groupzSelectFlag;
	}
	
	public void setgroupzSelectFlag(boolean groupzSelectFlag) {
		this.groupzSelectFlag = groupzSelectFlag;
	}
	
	public boolean getrecordFlag(){
		return recordFlag;
	}
	
	public void setrecordFlag(boolean recordFlag) {
		this.recordFlag = recordFlag;
	}
	

	public String getselectedList(){
		return selectedList;
	}
	
	public void setselectedList(String selectedList) {
		this.selectedList = selectedList;
	}
	
	public String getpublishxml(){
		return publishxml;
	}
	
	public void setpublishxml(String publishxml) {
		this.publishxml = publishxml;
	}
	
	public String getpublishUrlList(){
		return publishUrlList;
	}
	
	public void setpublishUrlList(String publishUrlList) {
		this.publishUrlList = publishUrlList;
	}
	
	
	
	public String getcallerID(){
		return callerID;
	}
	
	public void setcallerID(String callerID) {
		this.callerID = callerID;
	}
	
	public String getmainMenuSelection(){
		return mainMenuSelection;
	}
	
	public void setmainMenuSelection(String mainMenuSelection) {
		this.mainMenuSelection = mainMenuSelection;
	}
	
	public String getdisplayList(){
		return displayList;
	}
	
	public void setdisplayList(String displayList) {
		this.displayList = displayList;
	}
	
	public String getselectionList(){
		return selectionList;
	}
	
	public void setselectionList(String selectionList) {
		this.selectionList = selectionList;
	}


	public String getsessionid(){
		return sessionid;
	}
	
	public void setsessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	

	public boolean getmultiLanguageFlag() {
		return multiLanguageFlag;
	}

	public void setmultiLanguageFlag(boolean multiLanguageFlag) {
		this.multiLanguageFlag = multiLanguageFlag;
	}
	
	public boolean getlanguageFlag() {
		return languageFlag;
	}

	public void setlanguageFlag(boolean languageFlag) {
		this.languageFlag = languageFlag;
	}

	
	
	public String getselectedlangCode(){
		return selectedlangCode;
	}
	
	public void setselectedlangCode(String selectedlangCode) {
		this.selectedlangCode = selectedlangCode;
	}
	


	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	public String getivrnumber(){
		return ivrnumber;
	}
	
	public void setivrnumber(String ivrnumber) {
		this.ivrnumber = ivrnumber;
	}
	
	
	public void seturl(String url) {
		this.url = url;
	}

	public String geturl() {
		return url;
	}
	
	public void setgroupzCode(String groupzCode) {
		this.groupzCode = groupzCode;
	}

	public String getgroupzCode() {
		return groupzCode;
	}
	
	

	public static RecordContext getSingleContext(String sessid) {
		String queryCond = "  ";
		if(sessid.equals(null)||sessid.isEmpty()){
			return null;
		}
		queryCond = " sessionid = '"+sessid+"'";
		try {
			System.out.println("The query condition is : " + queryCond);
			RecordContext contxtResult = (RecordContext) DBCommonOpertion
					.getSingleDatabaseObject(RecordContext.class, queryCond);

			return contxtResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}




}
