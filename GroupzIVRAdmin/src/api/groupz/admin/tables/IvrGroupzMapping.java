package api.groupz.admin.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gpzhibernate.BaseDatabaseObject;
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

	@Column(name = "ivrnumber")
	private String ivrnumber;
	
	@Column(name = "groupzCode")
	private String groupzCode;

	@Column(name = "welcomeNotes")
	private String welcomeNotes;

	@Column(name = "audioWelcomeUrl")
	private String audioWelcomeUrl;

	@Column(name = "selectionlist")
	private String selectionlist;
	
	@Column(name = "selectionlistUrl")
	private String selectionlistUrl;

	@Column(name = "groupzNameUrl")
	private String groupzNameUrl;
	
	@Column(name = "multiLanguageFlag")
	private boolean multiLanguageFlag;
	
	@Column(name = "recmultilanguageSelectionList")
	private String recmultilanguageSelectionList;

	@Column(name = "recmultilanguageSelectionWelcomeURL")
	private String recmultilanguageSelectionWelcomeURL;
	
	@Column(name = "endDate")
	private Date endDate;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "groupzBase")
	private String groupzBase;

	

	public String getGroupzBase() {
		return groupzBase;
	}

	public void setGroupzBase(String groupzBase) {
		this.groupzBase = groupzBase;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getIvrnumber() {
		return ivrnumber;
	}

	public void setIvrnumber(String ivrnumber) {
		this.ivrnumber = ivrnumber;
	}

	public String getgroupzCode() {
		return groupzCode;
	}

	public void setgroupzCode(String groupzCode) {
		this.groupzCode = groupzCode;
	}
	public String getwelcomeNotes() {
		return welcomeNotes;
	}

	public void setwelcomeNotes(String welcomeNotes) {
		this.welcomeNotes = welcomeNotes;
	}

	public String getaudioWelcomeUrl() {
		return audioWelcomeUrl;
	}

	public void setaudioWelcomeUrl(String audioWelcomeUrl) {
		this.audioWelcomeUrl = audioWelcomeUrl;
	}

	public String getselectionlist() {
		return selectionlist;
	}

	public void setselectionlist(String selectionlist) {
		this.selectionlist = selectionlist;
	}

	public String getselectionlistUrl() {
		return selectionlistUrl;
	}

	public void setselectionlistUrl(String selectionlistUrl) {
		this.selectionlistUrl = selectionlistUrl;
	}

	public String getgroupzNameUrl() {
		return groupzNameUrl;
	}

	public void setgroupzNameUrl(String groupzNameUrl) {
		this.groupzNameUrl = groupzNameUrl;
	}

	public boolean getmultiLanguageFlag() {
		return multiLanguageFlag;
	}

	public void setmultiLanguageFlag(boolean multiLanguageFlag) {
		this.multiLanguageFlag = multiLanguageFlag;
	}

	public String getrecmultilanguageSelectionList() {
		return recmultilanguageSelectionList;
	}

	public void setrecmultilanguageSelectionList(
			String recmultilanguageSelectionList) {
		this.recmultilanguageSelectionList = recmultilanguageSelectionList;
	}

	public String getrecmultilanguageSelectionWelcomeURL() {
		return recmultilanguageSelectionWelcomeURL;
	}

	public void setrecmultilanguageSelectionWelcomeURL(
			String recmultilanguageSelectionWelcomeURL) {
		this.recmultilanguageSelectionWelcomeURL = recmultilanguageSelectionWelcomeURL;
	}

	public Date getendDate() {
		return endDate;
	}

	public void setendDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getaddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}