package abanyu.transphone.server.view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

@SuppressWarnings("serial")
public class DialogPassword extends JPasswordField{
	public DialogPassword(){
		setPreferredSize(new Dimension(220,30));
		setFont(new Font("Calibri",Font.PLAIN,15));
		setBorder(BorderFactory.createCompoundBorder( this.getBorder(), 
				 																					BorderFactory.createEmptyBorder(3,5,3,2)) );

	}
}
