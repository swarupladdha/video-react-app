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
	
	@Column(name = "endDate")
	private Date endDate;
	
	@Column(name = "address")
	private String address;

	@Column(name = "multiLanguageFlag")
	private boolean multiLanguageFlag;
	
	@Column(name = "recmultilanguageSelectionList")
	private String recmultilanguageSelectionList;

	@Column(name = "recmultilanguageSelectionWelcomeURL")
	private String recmultilanguageSelectionWelcomeURL;


	
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
	
	public String getGroupzCode() {
		return groupzCode;
	}

	public void setGroupzCode(String groupzCode) {
		this.groupzCode = groupzCode;
	}

	public static List<IvrGroupzMapping> getListivrSourceMap(String ivrNubmer)
	{
		String queryCond = "  ";
		
		if (ivrNubmer == null)
		{
			return null;
		}
		queryCond = " ivrNumber = '" + ivrNubmer + "'";
		
		try
		{	
			System.out.println("The query condition in ivrgroupz is : " + queryCond);
			List<IvrGroupzMapping> sourceResult = (List<IvrGroupzMapping>) DBCommonOpertion.getDatabaseObjects(IvrGroupzMapping.class, queryCond);
			System.out.println("sourceResult" + sourceResult);
			return sourceResult;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static IvrGroupzMapping getSourcewithgrpzCode(String groupzCode)
	{
		String queryCond = "  ";
		
		if (groupzCode == null)
		{
			return null;
		}
		queryCond = " groupZCode = '" + groupzCode + "'";
		
		try
		{
			System.out.println("The query condition in ivrgroupz is : " + queryCond);
			IvrGroupzMapping sourceResult = (IvrGroupzMapping) DBCommonOpertion.getSingleDatabaseObject(IvrGroupzMapping.class, queryCond);
			return sourceResult;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static IvrGroupzMapping getSingleivrSourceMapwithGroupzCode(String ivrNubmer, String grpzCode)
	{
		String queryCond = "  ";
		
		if (ivrNubmer == null && grpzCode == null)
		{
			return null;
		}
		queryCond = " ivrNumber = '" + ivrNubmer + "' and groupZCode='" + grpzCode + "'";
		
		try
		{
			System.out.println("The query condition in ivrgroupz is : " + queryCond);
			IvrGroupzMapping sourceResult = (IvrGroupzMapping) DBCommonOpertion	.getSingleDatabaseObject(IvrGroupzMapping.class, queryCond);
			return sourceResult;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<IvrGroupzMapping> getListofAllsourceMap()
	{
		String queryCond = "";

		try
		{
			System.out.println("The query condition in ivrgroupz is : " + queryCond);
			List<IvrGroupzMapping> sourceResult = (List<IvrGroupzMapping>) DBCommonOpertion.getDatabaseObjectsWithoutWhere(IvrGroupzMapping.class, queryCond);
			System.out.println("phonelist_ivrgroupz  " + sourceResult);
			return sourceResult;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
