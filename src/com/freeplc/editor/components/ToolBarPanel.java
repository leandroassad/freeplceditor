package com.freeplc.editor.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

import com.freeplc.editor.config.Command;
import com.freeplc.editor.config.Folders;
import com.freeplc.editor.config.FreePLCConfig;
import com.freeplc.editor.global.GlobalProperties;
import com.freeplc.editor.main.FreePLCEditor;

@SuppressWarnings("serial")
public class ToolBarPanel extends JToolBar implements MouseListener {
	JLabel selectedCommandIcon = null;
	Command selectedCommand = null;
	FreePLCEditor editor;
	public ToolBarPanel(FreePLCEditor editor, String title) {
		super(title);
		
		this.editor = editor;
		
		createUserInterface();
	}
	
	private void createUserInterface() {
//		setPreferredSize(new Dimension((int)screenSize.getWidth(), 50));

		createTabs();
		
		editor.getContentPane().add(this, BorderLayout.PAGE_START);

	}
		
	public Command getSelectedCommand() {		
		return selectedCommand;
	}
	
	public void unselectLastSelectedCommand() {
		if (selectedCommand != null) {
			selectedCommandIcon.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    		selectedCommandIcon = null;
    		selectedCommand = null;
		}
	}
	
	private void createTabs() {
		setRollover(true);
		JTabbedPane tabPanel = new JTabbedPane();
		
		FreePLCConfig cfg = GlobalProperties.getInstance().getFreePLCConfig();
		
		Folders [] folders = cfg.getLadderConf().getFolders();
		for (Folders folder : folders) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			
			Command[] commands = folder.getCommand();
			for (Command command : commands) {
				JLabel commandLabel = new JLabel();
				if (command.getImage() != null) {
					commandLabel.setIcon(new ImageIcon(command.getImage()));
				}
				else commandLabel.setText(command.getName());
				commandLabel.setToolTipText(command.getDescription());
				if (command.getClassname() != null) {
					commandLabel.putClientProperty("COMMAND", command);
				}
				else
					commandLabel.setEnabled(false);
				
				commandLabel.addMouseListener(this);
				commandLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
				commandLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				commandLabel.setFocusable(true);
				
				panel.add(commandLabel);
				
			}
			
			tabPanel.addTab(folder.getName(), panel);
		}
				
		add(tabPanel);		
	}
	
	public void mousePressed(MouseEvent me) {
        JComponent comp = (JComponent) me.getSource();
        if (!comp.isEnabled()) return;
        
        // Pressionou o botao de selecao de componentes (Botao da Esquerda)
        if (me.getButton() == MouseEvent.BUTTON1) {        
        	/*
        	 * Se nenhum componentes estiver selecionado, seleciona
        	 * Senao, se for o mesmo componente, de-seleciona ele ou, se for outro, 
        	 * de-seleciona o ultimo e seleciona o novo componente.
        	 */
	        if (selectedCommandIcon == null) {
	        	comp.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED, Color.BLUE, Color.BLUE));
	        	selectedCommandIcon = (JLabel)comp;
	        	selectedCommand = (Command)selectedCommandIcon.getClientProperty("COMMAND");
	    		System.out.println("Selected ClassName: " + selectedCommand.getClassname());
	        }
	        else {
	        	if (selectedCommandIcon == comp) {
	            	comp.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
	        		selectedCommandIcon = null;
	        		selectedCommand = null;
	        	}
	        	else {
	        		selectedCommandIcon.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
	        		comp.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED, Color.BLUE, Color.BLUE));
	        		selectedCommandIcon = (JLabel)comp;
	        		selectedCommand = (Command)selectedCommandIcon.getClientProperty("COMMAND");
		    		System.out.println("Selected ClassName: " + selectedCommand.getClassname());
	        	}	        	
	        }
	        // Avisa o editor qual o componente atualmente selecionado
	        editor.setSelectedCommand(selectedCommand);
        }
      }
	
      public void mouseEntered(MouseEvent me) {
        JComponent comp = (JComponent) me.getSource();
        if (!comp.isEnabled()) return;

        if (comp != selectedCommandIcon)
        	comp.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));
      }
      
      public void mouseExited(MouseEvent me) {
  	    JComponent comp = (JComponent) me.getSource();
        if (!comp.isEnabled()) return;

        if (comp != selectedCommandIcon)
        	comp.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));	    	  
	  }

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
}
