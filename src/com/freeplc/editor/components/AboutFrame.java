package com.freeplc.editor.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class AboutFrame extends JFrame implements ActionListener {

	JFrame parent;
	public AboutFrame(JFrame parent) {
		this.parent = parent;
		
		createUserInterface();
	}
	
	private void createUserInterface() {
		setTitle("Sobre");
		
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
