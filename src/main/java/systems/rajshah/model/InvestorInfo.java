package systems.rajshah.model;

import org.springframework.stereotype.Component;

@Component
public class InvestorInfo {
	public String id;
	public String firstName;
	public String middleName;
	public String lastName;
	public String familyHead;
	public String address;
	public String familyCode;
	@Override
	public String toString() {
		return "InvestorInfo [id=" + id + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName="
				+ lastName + ", familyHead=" + familyHead + ", address=" + address + ", familyCode=" + familyCode + "]";
	}
	public InvestorInfo() {
		super();
	}
	public InvestorInfo(String id, String firstName, String middleName, String lastName, String familyHead,
			String address, String familyCode) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.familyHead = familyHead;
		this.address = address;
		this.familyCode = familyCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFamilyHead() {
		return familyHead;
	}
	public void setFamilyHead(String familyHead) {
		this.familyHead = familyHead;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFamilyCode() {
		return familyCode;
	}
	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	
	
	
}
