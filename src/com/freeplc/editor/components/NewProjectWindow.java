package com.freeplc.editor.components;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.freeplc.editor.main.FreePLCEditor;
import com.freeplc.editor.tools.PLCDiscoverWindow;

@SuppressWarnings("serial")
public class NewProjectWindow extends JFrame implements ActionListener {

	public static final String OK_OPTION = "OK";
	public static final String CANCEL_OPTION = "CANCEL";
	
	FreePLCEditor editor;
	JTextField projectName;
	JComboBox<String> plcType;
	public NewProjectWindow(FreePLCEditor editor) {
		this.editor = editor;
		
		createUserInterface();
	}
	
	private void createUserInterface() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Novo Projeto");
		setLocationByPlatform(true);
		setPreferredSize(new Dimension(400, 100));
//		setLocationRelativeTo(editor);
		
//		Vector<String> plcTypeVector = new Vector<String>();
//		plcTypeVector.add("FreePLC Local Simulator");
//		plcTypeVector.add("FreePLC Remote Discovery");
		
		GridLayout layout = new GridLayout(2, 2);
		layout.setHgap(5);
		layout.setVgap(5);
		setLayout(layout);
		
		setResizable(true );
		
		
		add(new JLabel("Nome: "));
		projectName = new JTextField();
		projectName.addActionListener(this);
		projectName.putClientProperty("ITEMID", OK_OPTION);
		add(projectName);
		
//		add(new JLabel("Tipo PLC:"));
//		plcType = new JComboBox<String>(plcTypeVector);
//		add(plcType);
		
		JButton okButton = new JButton("Confirmar");
		JButton cancelButton = new JButton("Cancelar");
		
		add(okButton);
		add(cancelButton);
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		okButton.putClientProperty("ITEMID", OK_OPTION);
		cancelButton.putClientProperty("ITEMID", CANCEL_OPTION);
		
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String item;
		if (e.getSource() instanceof JTextField) {
			item = (String)((JTextField)e.getSource()).getClientProperty("ITEMID");
		}
		else {
			item = (String)((JButton)e.getSource()).getClientProperty("ITEMID");
		}
		
		switch (item) {
		case OK_OPTION:
			if (projectName.getText().trim().length() == 0) {
				JOptionPane.showMessageDialog(this, "Nome do projeto não pode ser vazio", "Novo Projeto", JOptionPane.ERROR_MESSAGE);
			}
			else {
//				switch (plcType.getSelectedIndex()) {
//				case Constants.PLC_TYPE_LOCAL_SIMULATOR:
//					System.out.println("Iniciando um simulador local");
//					FreePLCProject project = new FreePLCProject();
//					project.setProjectName(projectName.getText().trim());
//					project.setPlcType(Constants.PLC_TYPE_LOCAL_SIMULATOR);
//					
//					editor.setFreePLCProject(project);
//					editor.createProjectExplorerWindow();
//					editor.createDrawWindow();
//					break;
//				case Constants.PLC_TYPE_REMOTE_DISCOVERY:
					// Aqui, chama o PLCDiscovery
					PLCDiscoverWindow discover = new PLCDiscoverWindow(editor, projectName.getText().trim());
					editor.getDesktop().add(discover);
					
					System.out.println("Executando discovery para localizar os PLCs disponiveis na rede");
//					break;
//				}
				dispose();				
			}
			
			break;
		case CANCEL_OPTION:
			dispose();
			break;
		}
	}
}
