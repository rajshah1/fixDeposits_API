package systems.rajshah.model;

import org.springframework.stereotype.Component;

@Component
public class UserInfo {
	
	public String address;
	public String mobileNo;
	public String name;
	public String email;
	public String role;
	public String uid;
	
	
	public UserInfo(){
		
	}
	@Override
	public String toString() {
		return "UserInfo [address=" + address + ", mobileNo=" + mobileNo + ", name=" + name + ", email=" + email
				+ ", role=" + role + ", uid=" + uid + "]";
	}
	public UserInfo(String address, String mobileNo, String name, String email, String role, String uid) {
		super();
		this.address = address;
		this.mobileNo = mobileNo;
		this.name = name;
		this.email = email;
		this.role = role;
		this.uid = uid;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
		
	
}
