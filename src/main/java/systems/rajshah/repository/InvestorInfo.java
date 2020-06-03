package systems.rajshah.repository;

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

	
	
}
