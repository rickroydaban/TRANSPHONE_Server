package abanyu.transphone.server.model;

public class ServerData {
	LoginData loginData;
	MappingData mappingData;
	
	public ServerData(){
		loginData = new LoginData();
		mappingData = new MappingData();
	}
	
	public LoginData getLoginData(){
		return loginData;
	}
	
	public MappingData getMappingData(){
		return mappingData;
	}
}
