package abanyu.transphone.server.view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DialogField extends JTextField{
	public DialogField(){
		setPreferredSize(new Dimension(220,30));
		setFont(new Font("Calibri",Font.PLAIN,15));
		setBorder(BorderFactory.createCompoundBorder( this.getBorder(), 
				 																					BorderFactory.createEmptyBorder(3,5,3,2)) );

	}
}
