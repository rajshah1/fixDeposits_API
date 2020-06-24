package systems.rajshah.model;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class FdInfo {
	public String id;
	public String comapnyName;
	public Date startDate;
	public int period;
	public Date maturityDate;
	public String firstName;
	public String secondName;
	public String thirdName;
	public String nomineeName;
	public Boolean newOrRenew;
	public Boolean checkOrCash;
	public int amount;
	public String scheme;
	public String chequeNo;
	public String drawnOfBank;
	public String branch;
	public int maturatyAmount;
	public String certificateNo;
	public String uid;
	
	public FdInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	public String toString() {
		return "FdInfo [id=" + id + ", comapnyName=" + comapnyName + ", startDate=" + startDate + ", period=" + period
				+ ", maturityDate=" + maturityDate + ", firstName=" + firstName + ", secondName=" + secondName
				+ ", thirdName=" + thirdName + ", nomineeName=" + nomineeName + ", newOrRenew=" + newOrRenew
				+ ", checkOrCash=" + checkOrCash + ", amount=" + amount + ", scheme=" + scheme + ", chequeNo="
				+ chequeNo + ", drawnOfBank=" + drawnOfBank + ", branch=" + branch + ", maturatyAmount="
				+ maturatyAmount + ", certificateNo=" + certificateNo + ", uid=" + uid + "]";
	}

	public FdInfo(String id, String comapnyName, Date startDate, int period, Date maturityDate, String firstName,
			String secondName, String thirdName, String nomineeName, Boolean newOrRenew, Boolean checkOrCash,
			int amount, String scheme, String chequeNo, String drawnOfBank, String branch, int maturatyAmount,
			String certificateNo, String uid) {
		super();
		this.id = id;
		this.comapnyName = comapnyName;
		this.startDate = startDate;
		this.period = period;
		this.maturityDate = maturityDate;
		this.firstName = firstName;
		this.secondName = secondName;
		this.thirdName = thirdName;
		this.nomineeName = nomineeName;
		this.newOrRenew = newOrRenew;
		this.checkOrCash = checkOrCash;
		this.amount = amount;
		this.scheme = scheme;
		this.chequeNo = chequeNo;
		this.drawnOfBank = drawnOfBank;
		this.branch = branch;
		this.maturatyAmount = maturatyAmount;
		this.certificateNo = certificateNo;
		this.uid = uid;
	}



	public String getUid() {
		return uid;
	}



	public void setUid(String uid) {
		this.uid = uid;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getComapnyName() {
		return comapnyName;
	}


	public void setComapnyName(String comapnyName) {
		this.comapnyName = comapnyName;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public int getPeriod() {
		return period;
	}


	public void setPeriod(int period) {
		this.period = period;
	}


	public Date getMaturityDate() {
		return maturityDate;
	}


	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getSecondName() {
		return secondName;
	}


	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}


	public String getThirdName() {
		return thirdName;
	}


	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}


	public String getNomineeName() {
		return nomineeName;
	}


	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}


	public Boolean getNewOrRenew() {
		return newOrRenew;
	}


	public void setNewOrRenew(Boolean newOrRenew) {
		this.newOrRenew = newOrRenew;
	}


	public Boolean getCheckOrCash() {
		return checkOrCash;
	}


	public void setCheckOrCash(Boolean checkOrCash) {
		this.checkOrCash = checkOrCash;
	}


	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}


	public String getScheme() {
		return scheme;
	}


	public void setScheme(String scheme) {
		this.scheme = scheme;
	}


	public String getChequeNo() {
		return chequeNo;
	}


	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}


	public String getDrawnOfBank() {
		return drawnOfBank;
	}


	public void setDrawnOfBank(String drawnOfBank) {
		this.drawnOfBank = drawnOfBank;
	}


	public String getBranch() {
		return branch;
	}


	public void setBranch(String branch) {
		this.branch = branch;
	}


	public int getMaturatyAmount() {
		return maturatyAmount;
	}


	public void setMaturatyAmount(int maturatyAmount) {
		this.maturatyAmount = maturatyAmount;
	}


	public String getCertificateNo() {
		return certificateNo;
	}


	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	
	
}
