package abanyu.transphone.server.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import abanyu.transphone.server.model.LoginData;
import abanyu.transphone.server.model.ServerData;
import abanyu.transphone.server.view.LoginPanel;
import abanyu.transphone.server.view.NoConnectionView;
import abanyu.transphone.server.view.ServerFrame;

public class LoginController implements ActionListener{
	ServerFrame serverFrame;
	ServerData serverData;
	LoginData loginData;
	LoginPanel loginView;
	boolean hasConnection = false;
//	private JDialog dlg;
	
	public LoginController(ServerData pServerData, ServerFrame pServerFrame){
		loginView = new LoginPanel();
		loginView.getLoginButton().addActionListener(this);
		loginView.getClearButton().addActionListener(this);
		serverFrame = pServerFrame;
		loginData = pServerData.getLoginData();
		serverData = pServerData;
		
// 		dlg = new JDialog(serverFrame, "Getting Server Information...", true);
// 		JProgressBar dpb = new JProgressBar(0, 500);
// 		dlg.add(BorderLayout.NORTH, new JLabel("Progress..."));
// 		dlg.add(BorderLayout.CENTER, dpb);
// 		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
// 		dlg.setSize(300, 75);
// 		dlg.setLocationRelativeTo(serverFrame);	
// 		dlg.setVisible(true);
	}
	
	public void operate(){
		serverFrame.setContentPane(loginView.getLoginPanel());
		saveDBCompanyList();
		serverFrame.invalidate();
		serverFrame.validate();
	}
	
	public void saveDBCompanyList(){
		InputStream inputStream = null;
	  List<String> companyNameList = new ArrayList<String>();
	  
		try {
			inputStream = new URL(serverData.getMappingData().getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=getCompanies").openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
      JSONArray companies = new JSONArray(readAll(rd));
      hasConnection = true;
      
      ArrayList<HashMap<String, String>> companyList = new ArrayList<HashMap<String, String>>();
	  	for (int i = 0; i < companies.length(); i++) {
	  		Map<String, String> company = new HashMap<String, String>();
	  		JSONObject jo = companies.getJSONObject(i);
      
	  		//RETRIEVE EACH JSON OBJECT'S FIELDS
	  		company.put("id", String.valueOf(jo.getString("id")));
	  		company.put("name", jo.getString("name"));
	  		company.put("password", jo.getString("password"));
	  		company.put("contact", jo.getString("contact"));
	  		company.put("ip", jo.getString("serverip"));
      	  		
				companyNameList.add(company.get("name"));
	  		companyList.add((HashMap<String, String>) company);
	  	}
	  	
	  	
			loginData.setCompanyNames(companyNameList.toArray(new String[companyNameList.size()]));
			loginData.setCompanyList(companyList);
			
		} catch (IOException e) {
			System.out.println("io exception: "+e.getMessage());
			serverFrame.setContentPane(new NoConnectionView(serverFrame, serverData).getErrorPanel());
			serverFrame.invalidate();
			serverFrame.validate();
		} catch (JSONException e) {
			System.out.println("json exception: "+e.getMessage());
		}     

	}
	
  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == loginView.getLoginButton()){
			String companyName = (String)loginView.getUsernameField().getText().toString();
			String password = String.valueOf(loginView.getPasswordField().getPassword());
			for(HashMap<String, String> map: loginData.getCompanyList()) {
				if(map.get("name").equals(companyName)){
					if(map.get("password").equals(password)){
						loginData.setSelectedCompany(loginData.getCompanyID(String.valueOf(companyName)));
		      	new MappingController(serverData, serverFrame).operate();
					}else{
						loginView.getMessagePanel().setVisible(true);
					}		
				}
			}
		}
		
		if(e.getSource() == loginView.getClearButton()){
			loginView.getPasswordField().setText("");
			loginView.getMessagePanel().setVisible(false);
		}
	}

}
