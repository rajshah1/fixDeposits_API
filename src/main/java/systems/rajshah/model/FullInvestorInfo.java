package systems.rajshah.model;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FullInvestorInfo {
	public InvestorInfo investor;	
	public List<FdInfo> fdInfo;
	
	public FullInvestorInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvestorInfo getInvestor() {
		return investor;
	}

	public void setInvestor(InvestorInfo investor) {
		this.investor = investor;
	}

	public List<FdInfo> getFdInfo() {
		return fdInfo;
	}

	public void setFdInfo(List<FdInfo> fdInfo) {
		this.fdInfo = fdInfo;
	}

	@Override
	public String toString() {
		return "FullInvestorInfo [investor=" + investor + ", fdInfo=" + fdInfo + "]";
	}
	
	
	
	
	
	
	
}	