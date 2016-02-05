package ivr.tables;

import java.io.Serializable;

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
@Table(name = "phonecodes")
public class PhoneCodesList extends BaseDatabaseObject implements Serializable {

	private static final long serialVersionUID = 8689987742223198899L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@DontPersistWhenSerializing
	private int id;

	@Column(name = "countrycodes")
	private String countrycodes;

	@Column(name = "areacodes")
	private String areacodes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getcountrycodes() {
		return countrycodes;
	}

	public void setcountrycode(String countrycodes) {
		this.countrycodes = countrycodes;
	}

	public String getareacodes() {
		return areacodes;
	}

	public void setareacodes(String areacodes) {
		this.areacodes = areacodes;
	}

	public static List<PhoneCodesList> getListofPhoneCodes() {
		String queryCond = "";

		try {
			System.out.println("The query condition is : " + queryCond);
			List<PhoneCodesList> sourceResult = (List<PhoneCodesList>) DBCommonOpertion
					.getDatabaseObjectsWithoutWhere(PhoneCodesList.class,
							queryCond);
System.out.println("phonelist  "+sourceResult);
			return sourceResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
