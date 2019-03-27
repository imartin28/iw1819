package es.ucm.fdi.iw.transfer;

import java.util.Date;

import es.ucm.fdi.iw.util.DateUtil;

public class UserTransfer {
	
	private long id;
	
	private String nickname;
	
	private String name;
	
	private String lastName;
	
	private String email;
	
	private String password;
	
	private String samePassword;
	
	private String oldPassword;
	
	private Date birthdate;
	
	private String birthdateStr;
	
	private String description;
	
	private String type;
	
	public UserTransfer() {
		
	}

	public UserTransfer(String email, String nickname, String name, String lastName, Date birthdate, String description) {
		this.email = email;
		this.nickname = nickname;
		this.name = name;
		this.lastName = lastName;
		this.birthdate = birthdate;
		if(this.birthdate != null)
			this.birthdateStr = DateUtil.horaMostrarString(this.birthdate);
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSamePassword() {
		return samePassword;
	}

	public void setSamePassword(String samePassword) {
		this.samePassword = samePassword;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	
	public String getBirthdateStr() {
		return birthdateStr;
	}

	public void setBirthdateStr(String birthdateStr) {
		this.birthdateStr = birthdateStr;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}