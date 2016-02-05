package ivr.tables;

import java.io.Serializable;

import java.util.Date;
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
@Table(name = "context")
public class ContextMapping extends BaseDatabaseObject implements Serializable {

	private static final long serialVersionUID = 3899144825604005236L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@DontPersistWhenSerializing
	private int id;

	@Column(name = "callSessionId")
	private String callSessionId;
	
	
	@Column(name = "url")
	private String url;



	@Column(name = "recordselectedList")
	private String recordselectedList;
	
	@Column(name = "ipAddress")
	private String ipAddress;

	@Column(name = "recordMainMenudisplayList")
	private String recordMainMenudisplayList;

	@Column(name = "contextdisplayList")
	private String contextdisplayList;

	@Column(name = "mainMenuSelection")
	private String mainMenuSelection;

	@Column(name = "mainMenuFlag")
	private boolean mainMenuFlag;

	@Column(name = "subMenuFlag")
	private boolean subMenuFlag;

	@Column(name = "recordFlag")
	private boolean recordFlag;





	@Column(name = "selectedrecordlang")
	private String selectedrecordlang;

	@Column(name = "isRecMultiLangSelectionFlag")
	private boolean isRecMultiLangSelectionFlag;

	@Column(name = "publishUrlList")
	private String publishUrlList;

	@Column(name = "contextselectionList")
	private String contextselectionList;

	@Column(name = "multiMembSelectlist")
	private String multiMembSelectlist;

	@Column(name = "multigrpzselectlist")
	private String multigrpzselectlist;

	@Column(name = "multimembWelcomeNotes")
	private String multimembWelcomeNotes;

	@Column(name = "multigrpzWelcomeNotes")
	private String multigrpzWelcomeNotes;

	@Column(name = "newInquiryselectionList")
	private String newInquiryselectionList;

	@Column(name = "newInquiryDisplayList")
	private String newInquiryDisplayList;

	@Column(name = "ivrNumber")
	private String ivrNumber;

	@Column(name = "callerId")
	private String callerId;

	@Column(name = "memberId")
	private int memberId;
	
	@Column(name = "memberCode")
	private String memberCode;

	@Column(name = "lastupdatetime")
	private Date lastupdatetime;

	@Column(name = "groupzCode")
	private String groupzCode;

	@Column(name = "groupzId")
	private String groupzId;
	
	@Column(name = "recordreferenceIdList")
	private String recordreferenceIdList;
	
	

	public String getrecordreferenceIdList() {
		return recordreferenceIdList;
	}

	public void setrecordreferenceIdList(String recordreferenceIdList) {
		this.recordreferenceIdList = recordreferenceIdList;
	}

	@Column(name = "languageSelected")
	private String languageSelected;

	@Column(name = "globalcategorywelcomeNotes")
	private String globalcategorywelcomeNotes;

	@Column(name = "multiGrpzFlag")
	private boolean multiGrpzFlag;

	@Column(name = "multiMemberFlag")
	private boolean multiMemberFlag;

	@Column(name = "globalFlag")
	private boolean globalFlag;
	
	@Column(name = "endOfMsgs")
	private boolean endOfMsgs;

	public boolean isEndOfMsgs() {
		return endOfMsgs;
	}

	public void setEndOfMsgs(boolean endOfMsgs) {
		this.endOfMsgs = endOfMsgs;
	}

	@Column(name = "contactFlag")
	private boolean contactFlag;

	@Column(name = "selectedinqOption")
	private String selectedinqOption;

	@Column(name = "regionalLanguageFlag")
	private boolean regionalLanguageFlag;

	@Column(name = "multiLanguageFlag")
	private boolean multiLanguageFlag;

	@Column(name = "detailedInqFlag")
	private boolean detailedInqFlag;	

	@Column(name = "moreSubMenuFlag")
	private boolean moreSubMenuFlag;
	
	@Column(name = "multisubMenulevelFlag")
	private boolean multisubMenulevelFlag;
	
	@Column(name = "messgSummaryFlag")
	private boolean messgSummaryFlag;
	
	@Column(name = "messgTraverseFlag")
	private boolean messgTraverseFlag;	
	

	public boolean isMessgSummaryFlag() {
		return messgSummaryFlag;
	}

	public void setMessgSummaryFlag(boolean messgSummaryFlag) {
		this.messgSummaryFlag = messgSummaryFlag;
	}

	public boolean isMessgTraverseFlag() {
		return messgTraverseFlag;
	}

	public void setMessgTraverseFlag(boolean messgTraverseFlag) {
		this.messgTraverseFlag = messgTraverseFlag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public int getmemberId() {
		return memberId;
	}

	public void setmemberId(int memberId) {
		this.memberId = memberId;
	}
	
	public String getmemberCode() {
		return memberCode;
	}

	public void setmemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getgroupzId() {
		return groupzId;
	}

	public void setgroupzId(String groupzId) {
		this.groupzId = groupzId;
	}
	
	public String getipAddress() {
		return ipAddress;
	}

	public void setipAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIvrNumber() {
		return ivrNumber;
	}

	public void setIvrNumber(String ivrNumber) {
		this.ivrNumber = ivrNumber;
	}

	public String getlanguageSelected() {
		return languageSelected;
	}

	public void setlanguageSelected(String languageSelected) {
		this.languageSelected = languageSelected;
	}

	public String getselectedgeneralinqOption() {
		return selectedinqOption;
	}

	public void setselectedgeneralinqOption(String selectedinqOption) {
		this.selectedinqOption = selectedinqOption;
	}

	public String getgroupzCode() {
		return groupzCode;
	}

	public void setgroupzCode(String groupzCode) {
		this.groupzCode = groupzCode;
	}

	public String getmultimembWelcomeNotes() {
		return multimembWelcomeNotes;
	}

	public void setmultimembWelcomeNotes(String multimembWelcomeNotes) {
		this.multimembWelcomeNotes = multimembWelcomeNotes;
	}

	public String getmultigrpzWelcomeNotes() {
		return multigrpzWelcomeNotes;
	}

	public void setmultigrpzWelcomeNotes(String multigrpzWelcomeNotes) {
		this.multigrpzWelcomeNotes = multigrpzWelcomeNotes;
	}

	public String getCallSessionId() {
		return callSessionId;
	}

	public void setCallSessionId(String callSessionId) {
		this.callSessionId = callSessionId;
	}

	public String getcontextselectionList() {
		return contextselectionList;
	}

	public void setcontextselectionList(String contextselectionList) {
		this.contextselectionList = contextselectionList;
	}

	public String getcontextdisplayList() {
		return contextdisplayList;
	}

	public void setcontextdisplayList(String contextdisplayList) {
		this.contextdisplayList = contextdisplayList;
	}

	public String getmultiMembSelectlist() {
		return multiMembSelectlist;
	}

	public void setmultiMembSelectlist(String multiMembSelectlist) {
		this.multiMembSelectlist = multiMembSelectlist;
	}

	public String getmultigrpzselectlist() {
		return multigrpzselectlist;
	}

	public void setmultigrpzselectlist(String multigrpzselectlist) {
		this.multigrpzselectlist = multigrpzselectlist;
	}

	public String getCallerId() {
		return callerId;
	}

	public void setCallerId(String callerId) {
		this.callerId = callerId;
	}

	public boolean getmultiGrpzFlag() {
		return multiGrpzFlag;
	}

	public void setmultiGrpzFlag(boolean multiGrpzFlag) {
		this.multiGrpzFlag = multiGrpzFlag;
	}

	public boolean getmultiMemberFlag() {
		return multiMemberFlag;
	}

	public void setmultiMemberFlag(boolean multiMemberFlag) {
		this.multiMemberFlag = multiMemberFlag;
	}

	public boolean getregionalLanguageFlag() {
		return regionalLanguageFlag;
	}

	public void setregionalLanguageFlag(boolean regionalLanguageFlag) {
		this.regionalLanguageFlag = regionalLanguageFlag;
	}

	public boolean getmultiLanguageFlag() {
		return multiLanguageFlag;
	}

	public void setmultiLanguageFlag(boolean multiLanguageFlag) {
		this.multiLanguageFlag = multiLanguageFlag;
	}

	public Date getLastupdatetime() {
		return lastupdatetime;
	}

	public void setLastupdatetime(Date lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}

	public boolean getglobalFlag() {
		return globalFlag;
	}

	public void setglobalFlag(boolean globalFlag) {
		this.globalFlag = globalFlag;
	}

	public boolean getcontactFlag() {
		return contactFlag;
	}

	public void setcontactFlag(boolean contactFlag) {
		this.contactFlag = contactFlag;
	}

	public boolean getdetailedInqFlag() {
		return detailedInqFlag;
	}

	public void setdetailedInqFlag(boolean detailedInqFlag) {
		this.detailedInqFlag = detailedInqFlag;
	}

	public String getglobalcategorywelcomeNotes() {
		return globalcategorywelcomeNotes;
	}

	public void setglobalcategorywelcomeNotes(String globalcategorywelcomeNotes) {
		this.globalcategorywelcomeNotes = globalcategorywelcomeNotes;
	}

	public boolean getmainMenuFlag() {
		return mainMenuFlag;
	}

	public void setmainMenuFlag(boolean mainMenuFlag) {
		this.mainMenuFlag = mainMenuFlag;
	}

	public boolean getisRecMultiLangSelectionFlag() {
		return isRecMultiLangSelectionFlag;
	}

	public void setisRecMultiLangSelectionFlag(
			boolean isRecMultiLangSelectionFlag) {
		this.isRecMultiLangSelectionFlag = isRecMultiLangSelectionFlag;
	}

	public boolean getsubMenuFlag() {
		return subMenuFlag;
	}

	public void setsubMenuFlag(boolean subMenuFlag) {
		this.subMenuFlag = subMenuFlag;
	}


	public boolean getrecordFlag() {
		return recordFlag;
	}

	public void setrecordFlag(boolean recordFlag) {
		this.recordFlag = recordFlag;
	}

	public String getrecordselectedList() {
		return recordselectedList;
	}

	public void setrecordselectedList(String recordselectedList) {
		this.recordselectedList = recordselectedList;
	}

	public String getpublishUrlList() {
		return publishUrlList;
	}

	public void setpublishUrlList(String publishUrlList) {
		this.publishUrlList = publishUrlList;
	}

	public String getmainMenuSelection() {
		return mainMenuSelection;
	}

	public void setmainMenuSelection(String mainMenuSelection) {
		this.mainMenuSelection = mainMenuSelection;
	}

	public String getrecordMainMenudisplayList() {
		return recordMainMenudisplayList;
	}

	public void setrecordMainMenudisplayList(String recordMainMenudisplayList) {
		this.recordMainMenudisplayList = recordMainMenudisplayList;
	}




	public boolean getmoreSubMenuFlag() {
		return moreSubMenuFlag;

	}
	
	public void setmoreSubMenuFlag(boolean moreSubMenuFlag) {
		this.moreSubMenuFlag = moreSubMenuFlag;

	}


	
	public void setmultisubMenulevelFlag(boolean multisubMenulevelFlag) {
		this.multisubMenulevelFlag = multisubMenulevelFlag;

	}

	public boolean getmultisubMenulevelFlag() {
		return multisubMenulevelFlag;

	}

	public String getselectedrecordlang() {
		return selectedrecordlang;
	}

	public void setselectedrecordlang(String selectedrecordlang) {
		this.selectedrecordlang = selectedrecordlang;
	}

	public void seturl(String url) {
		this.url = url;
	}

	public String geturl() {
		return url;
	}

	public String getnewInquiryDisplayList() {
		return newInquiryDisplayList;
	}

	public void setnewInquiryDisplayList(String newInquiryDisplayList) {
		this.newInquiryDisplayList = newInquiryDisplayList;
	}

	public String getnewInquiryselectionList() {
		return newInquiryselectionList;
	}

	public void setnewInquiryselectionList(String newInquiryselectionList) {
		this.newInquiryselectionList = newInquiryselectionList;
	}

	public static ContextMapping getSingleContext(String callSessionId) {
		String queryCond = "  ";
		if (callSessionId == null) {
			return null;
		}
		queryCond = " callSessionId = '" + callSessionId + "'";
		try {
			System.out.println("The query condition is : " + queryCond);
			ContextMapping contxtResult = (ContextMapping) DBCommonOpertion
					.getSingleDatabaseObject(ContextMapping.class, queryCond);

			return contxtResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
