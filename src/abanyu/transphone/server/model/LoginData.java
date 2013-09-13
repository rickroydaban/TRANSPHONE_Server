package abanyu.transphone.server.model;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginData {
	private String [] companyNames;
	private ArrayList<HashMap<String, String>> companyList;
	
	public LoginData(){
		
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
}
