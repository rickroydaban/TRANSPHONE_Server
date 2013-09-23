package abanyu.transphone.server.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class DialogButton extends JButton{
	public DialogButton(String value){
		setText(value);
		setPreferredSize(new Dimension(180,30));
		setFont(new Font("Calibri",Font.PLAIN,15));
		setMargin(new Insets(0,20,0,20));
		setBorder(BorderFactory.createCompoundBorder( this.getBorder(), 
				 																					BorderFactory.createEmptyBorder(2,2,2,2)) );

	}
}
