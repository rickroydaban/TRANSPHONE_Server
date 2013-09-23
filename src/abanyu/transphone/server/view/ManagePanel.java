package abanyu.transphone.server.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class ManagePanel{
	JPanel mainPanel;
	
	JButton changePassButton, changeContactButton, addTaxiButton, 
					addDriverButton, removeTaxiButton, removeDriverButton;
	
	public ManagePanel(){
  	mainPanel = new JPanel(new GridBagLayout());  	
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));    
    mainPanel.setBackground(Color.white);
  		
    /**/JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
				buttonPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.decode("#ABABAB")), 
																														 		 BorderFactory.createEmptyBorder(20,20,20,20)));

				changePassButton = new JButton("Change Password");
				changeContactButton = new JButton("Change Contact Number");
				addTaxiButton = new JButton("Add Taxi");
				addDriverButton = new JButton("Add Driver");
				removeTaxiButton = new JButton("Remove Taxi");
				removeDriverButton = new JButton("Remove Driver");
		    				
    mainPanel.add(buttonPanel);
    /**/buttonPanel.add(changePassButton);
    /**/buttonPanel.add(changeContactButton);
    /**/buttonPanel.add(addTaxiButton);
    /**/buttonPanel.add(addDriverButton);
    /**/buttonPanel.add(removeTaxiButton);
    /**/buttonPanel.add(removeDriverButton);
 }

  public JButton getChangePassButton(){
  	return changePassButton;
  }
  
  public JButton getChangeContactButton(){
  	return changeContactButton;
  }
  
  public JButton getAddTaxiButton(){
  	return addTaxiButton;
  }
  
  public JButton getAddDriverButton(){
  	return addDriverButton;
  }
  
  public JButton getRemoveTaxiButton(){
  	return removeTaxiButton;
  }
  
  public JButton getRemoveDriverButton(){
  	return removeDriverButton;
  }


}
