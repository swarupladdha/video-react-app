package ivr.tables;

import java.io.Serializable;

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
@Table(name = "ivrgroupz")
public class IvrGroupzMapping extends BaseDatabaseObject implements Serializable {

	private static final long serialVersionUID = 8689987742223198899L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@DontPersistWhenSerializing
	private int id;

	@Column(name = "groupzCode")
	private String groupzCode;

	@Column(name = "welcomeNotes")
	private String welcomeNotes;

	@Column(name = "audioWelcomeUrl")
	private String audioWelcomeUrl;

	@Column(name = "selectionlist")
	private String selectionlist;

	@Column(name = "groupzNameUrl")
	private String groupzNameUrl;
	
	@Column(name = "recordSubMenuDisplayList")
	private String recordSubMenuDisplayList;	
	
	@Column(name = "recordSubMenuSelectionList")
	private String recordSubMenuSelectionList;
	
	@Column(name = "moreRecordSubmenuOptionsNotes")
	private String moreRecordSubmenuOptionsNotes;

	@Column(name = "moreRecordSubmenuOptionsUrl")
	private String moreRecordSubmenuOptionsUrl;	
	
	@Column(name = "generalinquirySelectionList")
	private String generalinquirySelectionList;	

	@Column(name = "generalinqwelcomeNotes")
	private String generalinqwelcomeNotes;

	@Column(name = "audiogeneralinqWelcomeUrl")
	private String audiogeneralinqWelcomeUrl;
	
	@Column(name = "endDate")
	private Date endDate;
	
	@Column(name = "address")
	private String address;

	@Column(name = "smstext")
	private String smstext;
	
	@Column(name = "multiLanguageFlag")
	private boolean multiLanguageFlag;
	
	@Column(name = "recmultilanguageSelectionList")
	private String recmultilanguageSelectionList;

	@Column(name = "recmultilanguageSelectionWelcomeURL")
	private String recmultilanguageSelectionWelcomeURL;

	@Column(name = "recordOptionsText")
	private String recordOptionsText;

	@Column(name = "recordOptionsUrl")
	private String recordOptionsUrl;
	
	@Column(name = "recordInstructionNote")
	private String recordInstructionNote;
	
	@Column(name = "recordInstructionUrl")
	private String recordInstructionUrl;
	
	@Column(name = "recordMsginMoreLangInstructionUrl")
	private String recordMsginMoreLangInstructionUrl;
	
	@Column(name = "recordMsginMoreLangInstructionNotes")
	private String recordMsginMoreLangInstructionNotes;
	
	
	public String getrecmultilanguageSelectionWelcomeURL() {
		return recmultilanguageSelectionWelcomeURL;
	}

	public void setrecmultilanguageSelectionWelcomeURL(
			String recmultilanguageSelectionWelcomeURL) {
		this.recmultilanguageSelectionWelcomeURL = recmultilanguageSelectionWelcomeURL;
	}

	public String getrecmultilanguageSelectionList() {
		return recmultilanguageSelectionList;
	}

	public void setrecmultilanguageSelectionList(
			String recmultilanguageSelectionList) {
		this.recmultilanguageSelectionList = recmultilanguageSelectionList;
	}

	
	public boolean getmultiLanguageFlag() {
		return multiLanguageFlag;
	}

	public void setmultiLanguageFlag(boolean multiLanguageFlag) {
		this.multiLanguageFlag = multiLanguageFlag;
	}
	

	public String getsmstext() {
		return smstext;
	}

	public void setsmstext(String smstext) {
		this.smstext = smstext;
	}

	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public Date getendDate() {
		return endDate;
	}

	public void setendDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getgeneralinquirySelectionList() {
		return generalinquirySelectionList;
	}

	public void setgeneralinquirySelectionList(String generalinquirySelectionList) {
		this.generalinquirySelectionList = generalinquirySelectionList;
	}

	
	
	public void setgeneralinqwelcomeNotes(String generalinqwelcomeNotes) {
		this.generalinqwelcomeNotes = generalinqwelcomeNotes;
	}

	public String getgeneralinqwelcomeNotes() {
		return generalinqwelcomeNotes;
	}

	public void setaudiogeneralinqWelcomeUrl(String audiogeneralinqWelcomeUrl) {
		this.audiogeneralinqWelcomeUrl = audiogeneralinqWelcomeUrl;
	}

	public String getaudiogeneralinqWelcomeUrl() {
		return audiogeneralinqWelcomeUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public void setWelcomeNotes(String welcomeNotes) {
		this.welcomeNotes = welcomeNotes;
	}

	public String getWelcomeNotes() {
		return welcomeNotes;
	}

	public void setgroupzNameUrl(String groupzNameUrl) {
		this.groupzNameUrl = groupzNameUrl;
	}

	public String getgroupzNameUrl() {
		return groupzNameUrl;
	}

	public String getAudioWelcomeUrl() {
		return audioWelcomeUrl;
	}

	public void setAudioWelcomeUrl(String audioWelcomeUrl) {
		this.audioWelcomeUrl = audioWelcomeUrl;
	}

	public String getselectionlist() {
		return selectionlist;
	}

	public void setselectionlist(String selectionlist) {
		this.selectionlist = selectionlist;
	}
	
	
	public String getrecordSubMenuDisplayList() {
		return recordSubMenuDisplayList;
	}
	
	public void setrecordSubMenuDisplayList(String recordSubMenuDisplayList) {
		this.recordSubMenuDisplayList = recordSubMenuDisplayList;
	}

	public String getrecordSubMenuSelectionList() {
		return recordSubMenuSelectionList;
	}
	
	public void setrecordSubMenuSelectionList(String recordSubMenuSelectionList) {
		this.recordSubMenuSelectionList = recordSubMenuSelectionList;
	}
	
	public String getmoreRecordSubmenuOptionsNotes() {
		return moreRecordSubmenuOptionsNotes;
	}
	
	public void setmoreRecordSubmenuOptionsNotes(String moreRecordSubmenuOptionsNotes) {
		this.moreRecordSubmenuOptionsNotes = moreRecordSubmenuOptionsNotes;
	}
	
	public String getmoreRecordSubmenuOptionsUrl() {
		return moreRecordSubmenuOptionsUrl;
	}
	
	public void setmoreRecordSubmenuOptionsUrl(String moreRecordSubmenuOptionsUrl) {
		this.moreRecordSubmenuOptionsUrl = moreRecordSubmenuOptionsUrl;
	}

	public String getGroupzCode() {
		return groupzCode;
	}

	public void setGroupzCode(String groupzCode) {
		this.groupzCode = groupzCode;
	}
	
	
	public String getrecordOptionsText() {
		return recordOptionsText;
	}
	
	public void setrecordOptionsText(String recordOptionsText) {
		this.recordOptionsText = recordOptionsText;
	}
	
	public String getrecordOptionsUrl() {
		return recordOptionsUrl;
	}
	
	public void setrecordOptionsUrl(String recordOptionsUrl) {
		this.recordOptionsUrl = recordOptionsUrl;
	}
	
	public String getrecordInstructionNote() {
		return recordInstructionNote;
	}
	
	public void setrecordInstructionNote(String recordInstructionNote) {
		this.recordInstructionNote = recordInstructionNote;
	}
	
	public String getrecordInstructionUrl() {
		return recordInstructionUrl;
	}
	
	public void setrecordInstructionUrl(String recordInstructionUrl) {
		this.recordInstructionUrl = recordInstructionUrl;
	}
	
	public String getrecordMsginMoreLangInstructionNotes() {
		return recordMsginMoreLangInstructionNotes;
	}
	
	public void setrecordMsginMoreLangInstructionNotes(String recordMsginMoreLangInstructionNotes) {
		this.recordMsginMoreLangInstructionNotes = recordMsginMoreLangInstructionNotes;
	}
	
	public String getrecordMsginMoreLangInstructionUrl() {
		return recordMsginMoreLangInstructionUrl;
	}
	
	public void setrecordMsginMoreLangInstructionUrl(String recordMsginMoreLangInstructionUrl) {
		this.recordMsginMoreLangInstructionUrl = recordMsginMoreLangInstructionUrl;
	}
	
	public static List<IvrGroupzMapping> getListivrSourceMap(String ivrNubmer) {
		
		String queryCond = "  ";
		
		if (ivrNubmer == null) {
			return null;
		}
		
		queryCond = " ivrNumber = '" + ivrNubmer + "'";
		
		try {
			
			System.out.println("The query condition is : " + queryCond);
			List<IvrGroupzMapping> sourceResult = (List<IvrGroupzMapping>) DBCommonOpertion
					.getDatabaseObjects(IvrGroupzMapping.class, queryCond);

			return sourceResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static IvrGroupzMapping getSourcewithgrpzCode(String groupzCode) {
		String queryCond = "  ";
		if (groupzCode == null) {
			return null;
		}
		queryCond = " groupZCode = '" + groupzCode + "'";
		try {
			System.out.println("The query condition is : " + queryCond);
			IvrGroupzMapping sourceResult = (IvrGroupzMapping) DBCommonOpertion
					.getSingleDatabaseObject(IvrGroupzMapping.class, queryCond);

			return sourceResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static IvrGroupzMapping getSingleivrSourceMapwithGroupzCode(
			String ivrNubmer, String grpzCode) {
		String queryCond = "  ";
		if (ivrNubmer == null && grpzCode == null) {
			return null;
		}
		queryCond = " ivrNumber = '" + ivrNubmer + "' and groupZCode='"
				+ grpzCode + "'";
		try {
			System.out.println("The query condition is : " + queryCond);
			IvrGroupzMapping sourceResult = (IvrGroupzMapping) DBCommonOpertion
					.getSingleDatabaseObject(IvrGroupzMapping.class, queryCond);

			return sourceResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<IvrGroupzMapping> getListofAllsourceMap() {
		String queryCond = "";

		try {
			System.out.println("The query condition is : " + queryCond);
			List<IvrGroupzMapping> sourceResult = (List<IvrGroupzMapping>) DBCommonOpertion
					.getDatabaseObjectsWithoutWhere(IvrGroupzMapping.class,
							queryCond);
System.out.println("phonelist  "+sourceResult);
			return sourceResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
