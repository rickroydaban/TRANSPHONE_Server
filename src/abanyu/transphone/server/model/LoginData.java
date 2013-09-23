package abanyu.transphone.server.model;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginData {
	private String [] companyNames;
	private ArrayList<HashMap<String, String>> companyList;
	private int selectedCompany;
	
	public LoginData(){
		
	}
	
	public void setSelectedCompany(int compID){
		selectedCompany = compID;
	}
	
	public int getSelectedCompany(){
		return selectedCompany;
	}
	
	public void setCompanyList(ArrayList<HashMap<String, String>> pCompanyList){
		companyList = pCompanyList;
	}
	
	public void setCompanyNames(String [] pCompanyNames){
		companyNames = pCompanyNames;
	}
	
	public String[] getCompanyNames(){
		return companyNames;
	}
	
	public ArrayList<HashMap<String, String>> getCompanyList(){
		return companyList;
	}
	
  public int getCompanyID(String selName){
  	for(HashMap<String, String> map: companyList) {
    	if(map.get("name").equals(selName))
    			return Integer.parseInt(map.get("id"));
    }
  	
  	return -1;
  }
}
