package dbhelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "participant_table")
public class Participant {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer Id;

	@Column(nullable = false)
	private String participantEmail;

	@Column(nullable = false)
	private String participantPassword;

	@Column(nullable = false)
	private String isHost;

	private String mobileNo;

	private String otp;

	@Column(name = "registrants_id", nullable = false)
	private String registrantsId;

	@Column(name = "meeting_id", nullable = false)
	private String meetingId;

	@Column(name = "join_url", nullable = false)
	private String joinUrl;

	public Participant(String guestEmail, String guestPassword, String isHost, String mobileNo, String registrantsId,
			String meetingId, String join_url) {
		this.participantEmail = guestEmail;
		this.participantPassword = guestPassword;
		this.isHost = isHost;
		this.mobileNo = mobileNo;
		this.registrantsId = registrantsId;
		this.meetingId = meetingId;
		this.joinUrl = join_url;
	}

	public String getGuestEmail() {
		return participantEmail;
	}

	public void setGuestEmail(String guestEmail) {
		this.participantEmail = guestEmail;
	}

	public String getGuestPassword() {
		return participantPassword;
	}

	public void setGuestPassword(String guestPassword) {
		this.participantPassword = guestPassword;
	}

	public String getIsHost() {
		return isHost;
	}

	public void setIsHost(String isHost) {
		this.isHost = isHost;
	}

	public void setMobile(String mobile) {
		this.mobileNo = mobile;
	}

	public String getMobile() {
		return this.mobileNo;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

}
