package systems.rajshah.model;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ReportGenObject {

	private String familyCode;
	private String familyHeadAddress;
	private String familyHeadName;
	//private String fdUserName;
	private List<FdInfo> fdInfo;
	
	public ReportGenObject() {
		super();
	}
	public String getFamilyCode() {
		return familyCode;
	}
	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}
	public String getFamilyHeadAddress() {
		return familyHeadAddress;
	}
	public void setFamilyHeadAddress(String familyHeadAddress) {
		this.familyHeadAddress = familyHeadAddress;
	}
	public String getFamilyHeadName() {
		return familyHeadName;
	}
	public void setFamilyHeadName(String familyHeadName) {
		this.familyHeadName = familyHeadName;
	}
	/*
	 * public String getFdUserName() { return fdUserName; } public void
	 * setFdUserName(String fdUserName) { this.fdUserName = fdUserName; }
	 */	
	

	public List<FdInfo> getFdInfo() {
		return fdInfo;
	}
	public void setFdInfo(List<FdInfo> fdInfo) {
		this.fdInfo = fdInfo;
	}
	@Override
	public String toString() {
		return "ReportGenObject [familyCode=" + familyCode + ", familyHeadAddress=" + familyHeadAddress
				+ ", familyHeadName=" + familyHeadName + ", fdInfo=" + fdInfo.toString() + "]";
	}
	
	
	
	
	
	
	
	

}
