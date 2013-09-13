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

public class ManageCompany{
  private JComboBox companyListBox;
	private JPasswordField passwordField;
	private JButton loginButton, clearButton;
	private JPanel mainPanel, messagePanel;
	
	public ManageCompany(){
  	mainPanel = new JPanel(new GridBagLayout());  	
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));    
    mainPanel.setBackground(Color.white);
  		
    /**/JPanel loginPanel = new JPanel();
				loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
				loginPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.decode("#ABABAB")), 
																														 		 BorderFactory.createEmptyBorder(20,20,20,20)));

    /**//**/JPanel companyPanel = new JPanel();
  	/**//**//**/JLabel companyLabel = new JLabel("Company");
  							companyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
  							companyLabel.setFont(new Font("Calibri",Font.BOLD,16));
  							companyLabel.setForeground(Color.decode("#343434"));
  	
  							companyListBox = new JComboBox();
  							companyListBox.setPreferredSize(new Dimension(200, 35));
  							companyListBox.setFont(new Font("Calibri",Font.PLAIN,15));
  							companyListBox.setBorder(BorderFactory.createCompoundBorder( companyListBox.getBorder(), 
  																	 																				 BorderFactory.createEmptyBorder(5, 5, 5, 5)));
  	  	
  	/**//**/JPanel passwordPanel = new JPanel();
  	/**//**//**/JLabel passwordLabel = new JLabel("Password");
  	   		  		passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
  	   		  		passwordLabel.setFont(new Font("Calibri",Font.BOLD,16));
  	   		  		passwordLabel.setForeground(Color.decode("#343434"));
       
  	/**//**//**/passwordField = new JPasswordField();
  	   		  		passwordField.setPreferredSize(new Dimension(200, 35));
  	   		  		passwordField.setFont(new Font("Calibri",Font.PLAIN,15));
  	   		  		passwordField.setBorder(BorderFactory.createCompoundBorder( passwordField.getBorder(), 
    															  																				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
    /**/messagePanel = new JPanel();
				messagePanel.setVisible(false);

	  /**//**/JLabel messageLabel = new JLabel("Password is incorrect");
    				messageLabel.setForeground(Color.RED);
    				messageLabel.setPreferredSize(new Dimension(280,30));
    				messageLabel.setHorizontalAlignment(JLabel.CENTER);
    				messageLabel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.decode("#8B0000")), 
								 																											 BorderFactory.createEmptyBorder(20,20,20,20)) );
  	
        
	  /**//**/JPanel loginButtonPanel = new JPanel();
		/**//**//**/loginButton = new JButton("Login");
		    				loginButton.setPreferredSize(new Dimension(80,35));
		    				loginButton.setFont(new Font("Calibri",Font.PLAIN,15));
    
		    				clearButton = new JButton("Clear");
		    				clearButton.setPreferredSize(new Dimension(80,35));
		    				clearButton.setFont(new Font("Calibri",Font.PLAIN,15));
		    				
    mainPanel.add(loginPanel);
    /**/loginPanel.add(companyPanel);
    /**//**/companyPanel.add(companyLabel);
    /**//**/companyPanel.add(companyListBox);
    /**/loginPanel.add(passwordPanel);
    /**//**/passwordPanel.add(passwordLabel);
    /**//**/passwordPanel.add(passwordField);
    /**/loginPanel.add(messagePanel);
    /**//**/messagePanel.add(messageLabel);
    /**/loginPanel.add(loginButtonPanel);
    /**//**/loginButtonPanel.add(loginButton);
    /**//**/loginButtonPanel.add(clearButton);
	}

  public JComboBox getCompanyListBox(){
  	return companyListBox;
  }
  
	public JPasswordField getPasswordField(){
		return passwordField;
	}
	
	public JButton getLoginButton(){
		return loginButton;
	}

	public JComponent getLoginPanel() {
    return mainPanel;
  }	
	
	public JPanel getMessagePanel(){
		return messagePanel;
	}
	
	public JButton getClearButton(){
		return clearButton;
	}
}
