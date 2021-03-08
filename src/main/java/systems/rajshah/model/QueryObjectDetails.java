package systems.rajshah.model;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class QueryObjectDetails {
	private String searchField;
	private Date initialDate;
	private Date lastDate;
	
	
	public QueryObjectDetails() {
		super();
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


	@Override
	public String toString() {
		return "QueryObjectDetails [searchField=" + searchField + ", initialDate=" + initialDate + ", lastDate="
				+ lastDate + "]";
	}
	
	
}
