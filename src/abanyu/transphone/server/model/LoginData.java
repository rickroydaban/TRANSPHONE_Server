package abanyu.transphone.server.model;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginData {
	private String [] companyNames, plateNumbers, driverNames;
	private ArrayList<HashMap<String, String>> companyList, taxiList, driverList;
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
  
	public void setTaxiList(ArrayList<HashMap<String, String>> pTaxiList){
		taxiList = pTaxiList;
	}
	
	public void setPlateNumbers(String [] pPlateNumbers){
		plateNumbers = pPlateNumbers;
	}
	
	public String[] getPlateNumbers(){
		return plateNumbers;
	}
	
	public ArrayList<HashMap<String, String>> getTaxiList(){
		return taxiList;
	}
	
	public void setDriverList(ArrayList<HashMap<String, String>> pDriverList){
		driverList = pDriverList;
	}
	
	public void setDriverNames(String [] pDriverNames){
		driverNames = pDriverNames;
	}
	
	public String[] getDriverNames(){
		return driverNames;
	}
	
	public ArrayList<HashMap<String, String>> getDriverList(){
		return driverList;
	}


}
