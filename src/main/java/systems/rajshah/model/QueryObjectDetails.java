package systems.rajshah.model;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class QueryObjectDetails {
	public String searchField;
	public Date initialDate;
	public Date lastDate;
	
	
	public QueryObjectDetails() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Date getInitialDate() {
		return initialDate;
	}


	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}


	public Date getLastDate() {
		return lastDate;
	}


	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}


	public String getSearchField() {
		return searchField;
	}


	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
	
	
	
	
	
	
}
