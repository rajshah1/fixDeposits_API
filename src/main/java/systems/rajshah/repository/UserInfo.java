package systems.rajshah.repository;

import org.springframework.stereotype.Component;

@Component
public class UserInfo {
	
	public String Address;
	public Double MobileNo;
	public String Name;
	public String email;
	public String role;
	public String UID;
	
	
	
	@Override
	public String toString() {
		return "UserInfo [Address=" + Address + ", MobileNo=" + MobileNo + ", Name=" + Name + ", email=" + email
				+ ", role=" + role + ", UID=" + UID + "]";
	}
	
	
	public UserInfo(String address, Double mobileNo, String name, String email, String role, String uID) {
		super();
		Address = address;
		MobileNo = mobileNo;
		Name = name;
		this.email = email;
		this.role = role;
		UID = uID;
	}


	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public Double getMobileNo() {
		return MobileNo;
	}
	public void setMobileNo(Double mobileNo) {
		MobileNo = mobileNo;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	
	
}
