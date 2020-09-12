package systems.rajshah.model;

import org.springframework.stereotype.Component;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.firebase.database.annotations.NotNull;

@Component
public class UserInfo {
	@NotNull
	@PropertyName(value="address1")
	public String addressOne;
	@NotNull
	@PropertyName(value="address2")
	public String addressTwo;
	@NotNull
	@PropertyName(value="address3")
	public String addressThree;
	@NotNull
	public String mobileNo;
	@NotNull
	public String name;
	@NotNull
	@DocumentId
	public String email;
	@NotNull
	public String role;
	@NotNull
	public String uid;
	
	
	public UserInfo(){
		
	}


	public String getaddressOne() {
		return addressOne;
	}


	public void setaddressOne(String addressOne) {
		this.addressOne = addressOne;
	}


	public String getaddressTwo() {
		return addressTwo;
	}


	public void setaddressTwo(String addressTwo) {
		this.addressTwo = addressTwo;
	}


	public String getaddressThree() {
		return addressThree;
	}


	public void setaddressThree(String addressThree) {
		this.addressThree = addressThree;
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


	@Override
	public String toString() {
		return "UserInfo [addressOne=" + addressOne + ", addressTwo=" + addressTwo + ", addressThree=" + addressThree + ", mobileNo="
				+ mobileNo + ", name=" + name + ", email=" + email + ", role=" + role + ", uid=" + uid + "]";
	}
	
	
	
}
