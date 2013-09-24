package abanyu.transphone.server.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import abanyu.transphone.server.model.LoginData;
import abanyu.transphone.server.model.MappingData;
import abanyu.transphone.server.model.ServerData;

public class MultiInputDialog{
	ServerFrame frame;
	LoginData lData;
	MappingData mData;
	
	private DialogPassword newPasswordField;
	private DialogPassword confirmPasswordField;
	private DialogField newDescriptionField;
	private DialogField newBodyField;
	private DialogField newContactField;
	private DialogField plateNumField;
	private DialogField bodyNumField;
	private DialogField descField;
	private DialogPassword passwordField;
	private DialogField licenseField;
	private DialogField nameField;
	private JComboBox taxiList;
	private JComboBox driverList;

	public MultiInputDialog(ServerFrame pFrame, ServerData data){
		frame = pFrame;
		lData = data.getLoginData();
		mData = data.getMappingData();
	}

  public void displayAddTaxiGUI() {
  	int action = JOptionPane.showConfirmDialog(frame,
        				 															 getTaxiPanel(),
        				 															 "Add Taxi",
        				 															 JOptionPane.OK_CANCEL_OPTION,
        				 															 JOptionPane.PLAIN_MESSAGE);

  	if(action==JOptionPane.OK_OPTION){
  		String result = jsonParse(mData.getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=addTaxi&arg1="+lData.getSelectedCompany()+"&arg2="+String.valueOf(plateNumField.getText())+"&arg3="+String.valueOf(bodyNumField.getText())+"arg4"+String.valueOf(descField.getText()));
			if(result.equals("1"))
				result = "Success!";
  		JOptionPane.showMessageDialog(frame, result);
  	}  	
  }
  
  public void displayAddDriverGUI(){
  	int action = JOptionPane.showConfirmDialog(frame,
        																			 getDriverPanel(),
        																			 "Add Driver",
        																			 JOptionPane.OK_CANCEL_OPTION,
        																			 JOptionPane.PLAIN_MESSAGE);

  	if(action==JOptionPane.OK_OPTION){
  		String result = jsonParse(mData.getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=addTaxi&arg1="+lData.getSelectedCompany()+"&arg2="+String.valueOf(nameField.getText())+"&arg3="+String.valueOf(licenseField.getText()));
			if(result.equals("1"))
				result = "Success!";
  		JOptionPane.showMessageDialog(frame, result);
  	}  	
}

  public void displayChangePasswordGUI(){
  	int action = JOptionPane.showConfirmDialog(frame,
        				 															 getChangePasswordPanel(),
        				 															 "Change Password",
        				 															 JOptionPane.OK_CANCEL_OPTION,
        				 															 JOptionPane.PLAIN_MESSAGE);
  	if(action==JOptionPane.OK_OPTION){
  		if((String.valueOf(newPasswordField.getPassword())).equals(String.valueOf(confirmPasswordField.getPassword()))){
  			String result = jsonParse(mData.getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=changeCompanyPassword&arg1="+lData.getSelectedCompany()+"&arg2="+String.valueOf(newPasswordField.getPassword()));
  			if(result.equals("1"))
  				result = "Success!";
  			JOptionPane.showMessageDialog(frame, result);
  		}else{
  			JOptionPane.showMessageDialog(frame, "Password do not match");
  		}
  	}
  }

  public void displayChangeContactGUI(){
  	int action = JOptionPane.showConfirmDialog(frame,
        				 															 getChangeContactPanel(),
        				 															 "Change Contact",
        				 															 JOptionPane.OK_CANCEL_OPTION,
        				 															 JOptionPane.PLAIN_MESSAGE);
  	
  	if(action==JOptionPane.OK_OPTION){
  		String result = jsonParse(mData.getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=changeCompanyContact&arg1="+lData.getSelectedCompany()+"&arg2="+String.valueOf(newContactField.getText()));
			if(result.equals("1"))
				result = "Success!";
  		JOptionPane.showMessageDialog(frame, result);
  	}
  }
  
  public void displayChangeBodyGUI(){
  	int action = JOptionPane.showConfirmDialog(frame,
        																			 getChangeBodyPanel(),
        																			 "Change Body",
        																			 JOptionPane.OK_CANCEL_OPTION,
        																			 JOptionPane.PLAIN_MESSAGE);

  	if(action==JOptionPane.OK_OPTION){
  		String result = jsonParse(mData.getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=updateTaxiBodyNo&arg1="+lData.getSelectedCompany()+"&arg2="+String.valueOf(taxiList.getSelectedItem())+"&arg3="+bodyNumField.getText());
			if(result.equals("1"))
				result = "Success!";
  		JOptionPane.showMessageDialog(frame, result);
  	}
  }

  public void displayChangeDescriptionGUI(){
  	int action = JOptionPane.showConfirmDialog(frame,
        																			 getChangeDescriptionPanel(),
        																			 "Change Description",
        																			 JOptionPane.OK_CANCEL_OPTION,
        																			 JOptionPane.PLAIN_MESSAGE);
  	
  	if(action==JOptionPane.OK_OPTION){
  		String result = jsonParse(mData.getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=updateTaxiDesc&arg1="+lData.getSelectedCompany()+"&arg2="+String.valueOf(taxiList.getSelectedItem())+"&arg3="+bodyNumField.getText());
			if(result.equals("1"))
				result = "Success!";
  		JOptionPane.showMessageDialog(frame, result);
  	}  	
  }

  public void displayRemoveTaxiGUI(){
  	int action = JOptionPane.showConfirmDialog(frame,
        																			 getRemoveTaxiPanel(),
        																			 "Remove Taxi",
        																			 JOptionPane.OK_CANCEL_OPTION,
        																			 JOptionPane.PLAIN_MESSAGE);
  	
//  	if(action==JOptionPane.OK_OPTION){
//  		String result = jsonParse(mData.getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=updateTaxiDesc&arg1="+data.getSelectedCompany()+"&arg2="+String.valueOf(taxiList.getSelectedItem())+"&arg3="+bodyNumField.getText());
//		if(result.equals("1"))
//			result = "Success!";
//  		JOptionPane.showMessageDialog(frame, result);
//  	}  	
  }

  public void displayRemoveDriverGUI(){
  	int action = JOptionPane.showConfirmDialog(frame,
        																			 getRemoveDriverPanel(),
        																			 "Change Description",
        																			 JOptionPane.OK_CANCEL_OPTION,
        																			 JOptionPane.PLAIN_MESSAGE);
  	
//  	if(action==JOptionPane.OK_OPTION){
//  		String result = jsonParse(mData.getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=updateTaxiDesc&arg1="+data.getSelectedCompany()+"&arg2="+String.valueOf(taxiList.getSelectedItem())+"&arg3="+bodyNumField.getText());
//		if(result.equals("1"))
//			result = "Success!";
//  		JOptionPane.showMessageDialog(frame, result);
//  	}  	
  }

  private JPanel getRemoveTaxiPanel(){
  	JPanel removeTaxiPanel = new JPanel();
		removeTaxiPanel.setLayout(new BoxLayout(removeTaxiPanel, BoxLayout.Y_AXIS));
  	
  	JPanel newBodyPanel = new JPanel();
  	DialogLabel newDescriptionLabel = new DialogLabel("Description: ");
  	newDescriptionField = new DialogField();
  	newBodyPanel.add(newDescriptionLabel);
  	newBodyPanel.add(newDescriptionField);

  	removeTaxiPanel.add(newBodyPanel);
  	return removeTaxiPanel;  	
  } 
  
  private JPanel getRemoveDriverPanel(){
  	JPanel changeDescriptionPanel = new JPanel();
		changeDescriptionPanel.setLayout(new BoxLayout(changeDescriptionPanel, BoxLayout.Y_AXIS));
  	
  	JPanel newBodyPanel = new JPanel();
  	DialogLabel newDescriptionLabel = new DialogLabel("Description: ");
  	newDescriptionField = new DialogField();
  	newBodyPanel.add(newDescriptionLabel);
  	newBodyPanel.add(newDescriptionField);

  	changeDescriptionPanel.add(newBodyPanel);
  	return changeDescriptionPanel;  	
  } 
  
  private JPanel getChangeDescriptionPanel(){
  	JPanel changeDescriptionPanel = new JPanel();
		changeDescriptionPanel.setLayout(new BoxLayout(changeDescriptionPanel, BoxLayout.Y_AXIS));
  	
  	JPanel newBodyPanel = new JPanel();
  	DialogLabel newDescriptionLabel = new DialogLabel("Description: ");
  	newDescriptionField = new DialogField();
  	newBodyPanel.add(newDescriptionLabel);
  	newBodyPanel.add(newDescriptionField);

  	changeDescriptionPanel.add(newBodyPanel);
  	return changeDescriptionPanel;  	
  } 
  
  private JPanel getChangeBodyPanel(){
  	JPanel changeBodyPanel = new JPanel();
		changeBodyPanel.setLayout(new BoxLayout(changeBodyPanel, BoxLayout.Y_AXIS));
  	
  	JPanel newBodyPanel = new JPanel();
  	DialogLabel newBodyLabel = new DialogLabel("Body No: ");
  	newBodyField = new DialogField();
  	newBodyPanel.add(newBodyLabel);
  	newBodyPanel.add(newBodyField);

  	changeBodyPanel.add(newBodyPanel);
  	return changeBodyPanel;  	
  } 

  private JPanel getChangeContactPanel(){
  	JPanel changeContactPanel = new JPanel();
		changeContactPanel.setLayout(new BoxLayout(changeContactPanel, BoxLayout.Y_AXIS));
  	
  	JPanel newContactPanel = new JPanel();
  	DialogLabel newContactLabel = new DialogLabel("Contact No: ");
  	newContactField = new DialogField();
  	newContactPanel.add(newContactLabel);
  	newContactPanel.add(newContactField);

  	changeContactPanel.add(newContactPanel);
  	
  	return changeContactPanel;  	
  }
  
  private JPanel getChangePasswordPanel(){
  	JPanel changePasswordPanel = new JPanel();
		changePasswordPanel.setLayout(new BoxLayout(changePasswordPanel, BoxLayout.Y_AXIS));
  	
  	JPanel newPasswordPanel = new JPanel();
  	DialogLabel newPasswordLabel = new DialogLabel("New Password: ");
  	newPasswordField = new DialogPassword();
  	newPasswordPanel.add(newPasswordLabel);
  	newPasswordPanel.add(newPasswordField);

  	JPanel confirmPasswordPanel = new JPanel();
  	DialogLabel confirmPasswordLabel = new DialogLabel("Confirm: ");
  	confirmPasswordField = new DialogPassword();  	
  	confirmPasswordPanel.add(confirmPasswordLabel);
  	confirmPasswordPanel.add(confirmPasswordField);
  	
  	changePasswordPanel.add(newPasswordPanel);
  	changePasswordPanel.add(confirmPasswordPanel);
  	
  	return changePasswordPanel;	
  }
  
  
  private JPanel getTaxiPanel(){
  	JPanel taxiPanel = new JPanel();
		taxiPanel.setLayout(new BoxLayout(taxiPanel, BoxLayout.Y_AXIS));
  	
  	JPanel plateNumPanel = new JPanel();
  	DialogLabel plateNumLabel = new DialogLabel("Plate No: ");
  	plateNumField = new DialogField();
  	plateNumPanel.add(plateNumLabel);
  	plateNumPanel.add(plateNumField);

  	JPanel bodyNumPanel = new JPanel();
  	DialogLabel bodyNumLabel = new DialogLabel("Body No: ");
  	bodyNumField = new DialogField();
  	bodyNumPanel.add(bodyNumLabel);
  	bodyNumPanel.add(bodyNumField);

  	JPanel descPanel = new JPanel();
  	DialogLabel descLabel = new DialogLabel("Description: ");
  	descField = new DialogField();  	
  	descPanel.add(descLabel);
  	descPanel.add(descField);
  	
  	taxiPanel.add(plateNumPanel);
  	taxiPanel.add(bodyNumPanel);
  	taxiPanel.add(descPanel);
  	
  	return taxiPanel;
  }
  

  private JPanel getDriverPanel(){
  	JPanel driverPanel = new JPanel();
		driverPanel.setLayout(new BoxLayout(driverPanel, BoxLayout.Y_AXIS));
  	
  	JPanel namePanel = new JPanel();
  	DialogLabel nameLabel = new DialogLabel("Name: ");
  	nameField = new DialogField();
  	namePanel.add(nameLabel);
  	namePanel.add(nameField);

  	JPanel licensePanel = new JPanel();
  	DialogLabel licenseLabel = new DialogLabel("License: ");
  	licenseField = new DialogField();
  	licensePanel.add(licenseLabel);
  	licensePanel.add(licenseField);

  	JPanel passwordPanel = new JPanel();
  	DialogLabel passwordLabel = new DialogLabel("Password: ");
  	passwordField = new DialogPassword();  	
  	passwordPanel.add(passwordLabel);
  	passwordPanel.add(passwordField);
  	
  	driverPanel.add(namePanel);
  	driverPanel.add(licensePanel);
  	driverPanel.add(passwordPanel);
  	
  	return driverPanel;
  }

	public String jsonParse(String url){	  
		try {
			InputStream inputStream = new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
      return readAll(rd);
      
		}catch(Exception e){
			System.out.println("Exception: "+e.getMessage());
		}
		return null;
	}
	
  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

}