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
@Table(name = "ivrgroupzbase")
public class IvrGroupzBaseMapping extends BaseDatabaseObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 793805160020555447L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@DontPersistWhenSerializing
	private int id;

	@Column(name = "grpzWelcomeNotes")
	private String grpzWelcomeNotes;

	@Column(name = "audioGrpzWelcomeUrl")
	private String audioGrpzWelcomeUrl;

	@Column(name = "selectionHangupNotes")
	private String selectionHangupNotes;

	@Column(name = "selectionEndNotes")
	private String selectionEndNotes;

	@Column(name = "selectionEndUrl")
	private String selectionEndUrl;

	@Column(name = "errorNotes")
	private String errorNotes;

	@Column(name = "audioSelectionHangupUrl")
	private String audioSelectionHangupUrl;

	@Column(name = "audioerrorUrl")
	private String audioerrorUrl;

	@Column(name = "audioMemberWelcomeUrl")
	private String audioMemberWelcomeUrl;

	@Column(name = "memberWelcomeNotes")
	private String memberWelcomeNotes;

	@Column(name = "notRegGroupzUrl")
	private String notRegGroupzUrl;

	@Column(name = "notRegGroupzNotes")
	private String notRegGroupzNotes;

	@Column(name = "maintenanceUrl")
	private String maintenanceUrl;

	@Column(name = "maintenanceNotes")
	private String maintenanceNotes;

	@Column(name = "generalHangupNotes")
	private String generalHangupNotes;

	@Column(name = "generalHangupUrl")
	private String generalHangupUrl;

	@Column(name = "numbersUrlList")
	private String numbersUrlList;

	@Column(name = "previousMenuSelectNotes")
	private String previousMenuSelectNotes;

	@Column(name = "previousMenuSelectUrl")
	private String previousMenuSelectUrl;

	@Column(name = "playspeed")
	private int playspeed;

	@Column(name = "settimeout")
	private int settimeout;

	@Column(name = "multiLanguageFlag")
	private boolean multiLanguageFlag;

	@Column(name = "languageSelectionList")
	private String languageSelectionList;

	@Column(name = "languageWelcomeURL")
	private String languageWelcomeURL;
	
	@Column(name = "enquiryflag")
	private boolean enquiryflag;



	public boolean getEnquiryflag() {
		return enquiryflag;
	}

	public void setEnquiryflag(boolean enquiryflag) {
		this.enquiryflag = enquiryflag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public String getlanguageWelcomeURL() {
		return languageWelcomeURL;
	}

	public void setlanguageWelcomeURL(String languageWelcomeURL) {
		this.languageWelcomeURL = languageWelcomeURL;
	}

	public String getlanguageSelectionList() {
		return languageSelectionList;
	}

	public void setlanguageSelectionList(String languageSelectionList) {
		this.languageSelectionList = languageSelectionList;
	}

	public boolean getmultiLanguageFlag() {
		return multiLanguageFlag;
	}

	public void setmultiLanguageFlag(boolean multiLanguageFlag) {
		this.multiLanguageFlag = multiLanguageFlag;
	}

	public int getsettimeout() {
		return settimeout;
	}

	public void setsettimeout(int settimeout) {
		this.settimeout = settimeout;
	}

	public void setplayspeed(int playspeed) {
		this.playspeed = playspeed;
	}

	public int getplayspeed() {
		return playspeed;
	}

	public String getgeneralHangupNotes() {
		return generalHangupNotes;
	}

	public void setgeneralHangupNotes(String generalHangupNotes) {
		this.generalHangupNotes = generalHangupNotes;
	}

	public String getaudiogeneralHangupUrl() {
		return generalHangupUrl;
	}

	public void setaudiogeneralHangupUrl(String generalHangupUrl) {
		this.generalHangupUrl = generalHangupUrl;
	}

	public void setgrpzWelcomeNotes(String grpzWelcomeNotes) {
		this.grpzWelcomeNotes = grpzWelcomeNotes;
	}

	public String getgrpzWelcomeNotes() {
		return grpzWelcomeNotes;
	}

	public String getmaintenanceNotes() {
		return maintenanceNotes;
	}

	public String getpreviousMenuSelectNotes() {
		return previousMenuSelectNotes;
	}

	public void setpreviousMenuSelectNotes(String previousMenuSelectNotes) {
		this.previousMenuSelectNotes = previousMenuSelectNotes;
	}

	public String getpreviousMenuSelectUrl() {
		return previousMenuSelectUrl;
	}

	public void setpreviousMenuSelectUrl(String previousMenuSelectUrl) {
		this.previousMenuSelectUrl = previousMenuSelectUrl;
	}

	public void setmaintenanceNotes(String maintenanceNotes) {
		this.maintenanceNotes = maintenanceNotes;
	}

	public String getmaintenanceUrl() {
		return maintenanceUrl;
	}

	public void setmaintenanceUrl(String maintenanceUrl) {
		this.maintenanceUrl = maintenanceUrl;
	}

	public String getnumbersUrlList() {
		return numbersUrlList;
	}

	public void setnumbersUrlList(String numbersUrlList) {
		this.numbersUrlList = numbersUrlList;
	}

	public String getselectionHangupNotes() {
		return selectionHangupNotes;
	}

	public void setselectionHangupNotes(String selectionHangupNotes) {
		this.selectionHangupNotes = selectionHangupNotes;
	}

	public String getaudioGrpzWelcomeUrl() {
		return audioGrpzWelcomeUrl;
	}

	public void setaudioGrpzWelcomeUrl(String audioGrpzWelcomeUrl) {
		this.audioGrpzWelcomeUrl = audioGrpzWelcomeUrl;
	}

	public String getaudioMemberWelcomeUrl() {
		return audioMemberWelcomeUrl;
	}

	public void setaudioMemberWelcomeUrl(String audioMemberWelcomeUrl) {
		this.audioMemberWelcomeUrl = audioMemberWelcomeUrl;
	}

	public String getselectionEndNotes() {
		return selectionEndNotes;
	}

	public void setselectionEndNotes(String selectionEndNotes) {
		this.selectionEndNotes = selectionEndNotes;
	}

	public String getselectionEndUrl() {
		return selectionEndUrl;
	}

	public void setselectionEndUrl(String selectionEndUrl) {
		this.selectionEndUrl = selectionEndUrl;
	}

	public String getaudioSelectionHangupUrl() {
		return audioSelectionHangupUrl;
	}

	public void setaudioSelectionHangupUrl(String audioSelectionHangupUrl) {
		this.audioSelectionHangupUrl = audioSelectionHangupUrl;
	}

	public String getmemberWelcomeNotes() {
		return memberWelcomeNotes;
	}

	public void setmemberWelcomeNotes(String memberWelcomeNotes) {
		this.memberWelcomeNotes = memberWelcomeNotes;
	}

	public String geterrorNotes() {
		return errorNotes;
	}

	public void seterrornotes(String errornotes) {
		this.errorNotes = errornotes;
	}

	public String getaudioerrorUrl() {
		return audioerrorUrl;
	}

	public void setaudioerrorUrl(String audioerrorUrl) {
		this.audioerrorUrl = audioerrorUrl;
	}


	public String getnotRegGroupzUrl() {
		return notRegGroupzUrl;
	}

	public void setnotRegGroupzUrl(String notRegGroupzUrl) {
		this.notRegGroupzUrl = notRegGroupzUrl;
	}

	public String getnotRegGroupzNotes() {
		return notRegGroupzNotes;
	}

	public void setnotRegGroupzNotes(String notRegGroupzNotes) {
		this.notRegGroupzNotes = notRegGroupzNotes;
	}



	public static IvrGroupzBaseMapping getSingleivrnumberMap(String ivrNubmer)
	{
		String queryCond = "  ";
		
		if (ivrNubmer == null)
		{
			return null;
		}
		
		queryCond = " ivrnumber = '" + ivrNubmer + "'";
		
		try
		{
			System.out.println("The query condition in ivrgroupzbase is : " + queryCond);
			IvrGroupzBaseMapping sourceResult = (IvrGroupzBaseMapping) DBCommonOpertion.getSingleDatabaseObject(IvrGroupzBaseMapping.class, queryCond); 
			// used to check ivrnumber with ivrgroupzbase table
			return sourceResult;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
