package com.freeplc.editor.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import com.freeplc.editor.config.Command;
import com.freeplc.editor.main.FreePLCEditor;
import com.freeplc.editor.project.FreePLCProject;
import com.freeplc.editor.project.LadderDrawer;
import com.freeplc.editor.project.Rung;
import com.freeplc.editor.project.component.Branch;
import com.freeplc.editor.project.component.Delimiter;
import com.freeplc.editor.project.component.LadderComponent;

import lombok.Getter;

@SuppressWarnings("serial")
public class LadderCanvas extends JInternalFrame {
	static public final int UNSELECTED_RUNG = -1;
	
	FreePLCEditor editor;
	@Getter JScrollPane scrollPane;
	@Getter DrawingCanvas canvasPanel;
	@Getter int canvasWidth;
	@Getter int canvasHeight;
	@Getter int selectedRung = UNSELECTED_RUNG;
	@Getter LadderComponent selectedLadderComponent = null;
	JPopupMenu popupMenu = null;
	LadderComponent lastDelimiterVisible = null;
	
	
	public LadderCanvas(FreePLCEditor editor) {
		super("LADDER", true, false, false, true);
		this.editor = editor;
		
		createUserInterface();
	}
	
	private void createUserInterface() {
		canvasWidth = editor.getWidth()-300;
		canvasHeight = editor.getHeight()-200;
		
		System.out.println("Criando LadderCanvas com " + canvasWidth + "x" + canvasHeight);
		
		Dimension size = new Dimension(canvasWidth, canvasHeight);
		setSize(size);
		setLocation(260,0);
		
		Dimension minimumSize = new Dimension(editor.getWidth()/2, editor.getHeight()/2);
		setMinimumSize(minimumSize);
		
		canvasPanel = new DrawingCanvas(editor.getCurrentProject(), this);
		canvasPanel.setSize(canvasWidth, canvasHeight);
		//canvasPanel.setPreferredSize(new Dimension(canvasWidth, canvasHeight*3));
		canvasPanel.setBackground(Color.WHITE);
		canvasPanel.setForeground(Color.BLACK);
		
		scrollPane = new JScrollPane(canvasPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
						
		add(scrollPane);
		
//		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
//		Dimension mySize = new Dimension((int)size.getWidth(), (int)(size.getHeight()-100));
//		setMinimumSize(mySize);
		
		setVisible(true);
		
	}

	
	public class DrawingCanvas extends JPanel implements MouseListener, MouseMotionListener {
		
		int lastMousePressedX = -1;
		int lastMousePressedY = -1;
		
		LadderCanvas canvas;
		LadderDrawer ladderDrawer;
		FreePLCProject project;
		public DrawingCanvas(FreePLCProject project, LadderCanvas canvas) {
			
			this.canvas = canvas;
			this.project = project;
			
			ladderDrawer = new LadderDrawer();
			
			popupMenu = new JPopupMenu();
			JMenuItem insertRung = new JMenuItem("Insert Rung");
			insertRung.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) { 
					  if (selectedRung != UNSELECTED_RUNG) {
						  project.getRungList().add(selectedRung, new Rung());
					  }
					  else {
						  project.getRungList().add(new Rung());
					  }
				      repaint();
				}
			});

			JMenuItem appendRung = new JMenuItem("Append Rung");
			appendRung.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) { 
					  if (selectedRung != UNSELECTED_RUNG) {
						  project.getRungList().add(selectedRung+1, new Rung());
					  }
					  else {
						  project.getRungList().add(new Rung());
					  }
				      repaint();
				}
			});
			
			JMenuItem removeRung = new JMenuItem("Remove Rung");
			removeRung.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedRung != UNSELECTED_RUNG) {
						project.getRungList().remove(selectedRung);
						repaint();
					}
				}
				
			});
			
			JMenuItem removeComponent = new JMenuItem("Remove Component");
			removeComponent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedLadderComponent != null) {
						if (selectedLadderComponent.isComputed()) {
							Rung rung = project.getRungList().get(selectedRung);
							int index = 0;
							for (LadderComponent component : rung.getComponentList()) {
								if (component.equals(selectedLadderComponent)) {
									break;
								}
								index++;
							}
							rung.getComponentList().remove(index);
							if (!selectedLadderComponent.isOutputComponent())
								rung.getComponentList().remove(index);	// Remove o delimitador
							System.out.println("Removeu componente..." + selectedLadderComponent.getId());
							
							repaint();
						}
					}
					
				}
				
			});
			
			popupMenu.add(insertRung);
			popupMenu.add(appendRung);
			popupMenu.add(removeRung);
			popupMenu.add(new JSeparator());
			popupMenu.add(removeComponent);
			
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			draw(g);
		}
		
		private void draw(Graphics g)
		 {
			 Graphics2D g2d = (Graphics2D)g;
			 
			 ladderDrawer.draw(project,  canvas, g2d);
		 }

		/*
		 * Verifica se ha um Rung selecionado
		 */
		public int getSelectedRung(int x, int y) {
			int rungIndex = UNSELECTED_RUNG;
			ArrayList<Rung> rungList = project.getRungList();
			for (Rung rung : rungList) {
				if (x >= rung.getStartX() && x <= (rung.getStartX() + rung.getWidth())
					&& y >= rung.getStartY() && y <= (rung.getStartY() + rung.getHeight())) {
						//System.out.println("RungLine selecionada: " + rungLine.getIndex() + " do Rung: " + rung.getIndex());
						rungIndex = rung.getIndex();
						break;
				}
			}
			return rungIndex;
		}

		/*
		 * Verifica se ha um componente selecionado dentro do Rung
		 */
		public LadderComponent getSelectedLadderComponentInRung(int rungIndex, int x, int y) {
			LadderComponent component = null;
			
			Rung rung = project.getRungList().get(rungIndex);
			int componentIndex = 1;
			for(LadderComponent comp : rung.getComponentList()) {
				if (x >= comp.getX() && x<= (comp.getX() + comp.getCommandWidth())
					&& y >= comp.getY()-comp.getCommandHeight()/2 && y <= (comp.getY() + comp.getCommandHeight()/2)) {							
					component = comp;
					break;
				}					
				componentIndex++;
			}
			
			return component;
		}
		
		/*
		 * Verifica se ha um componente selecionado no Ladder
		 */
		public LadderComponent getSelectedLadderComponent(int x, int y) {
			int rungIndex = getSelectedRung(x, y);
			if (rungIndex != UNSELECTED_RUNG) {
				return getSelectedLadderComponentInRung(rungIndex, x, y);
			}
			
			return null;
		}

		
		@Override
		public void mouseDragged(MouseEvent e) {
//			System.out.println("Dragged: [" + e.getX() + ", " + e.getY());	
//			if (selectedLadderComponent != null) {
//				
//			}
//			footer.setMousePosition(e.getX(), e.getY());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
			LadderComponent component;

			if (lastDelimiterVisible != null) {
				lastDelimiterVisible.setVisible(false);
			}

			int selectedRung = getSelectedRung(x, y);
			component = null;
			if (selectedRung != UNSELECTED_RUNG) {				
				// Procura pelo componente selecionado
				Rung rung = project.getRungList().get(selectedRung);
				int componentIndex = 1;
				for(LadderComponent comp : rung.getComponentList()) {
					if (!comp.isBranch()) {
						if (x >= comp.getX() && x<= (comp.getX() + comp.getCommandWidth())
							&& y >= comp.getY()-comp.getCommandHeight()/2 && y <= (comp.getY() + comp.getCommandHeight()/2)) {							
							component = comp;
							break;
						}					
						componentIndex++;
					}
					else {
						Branch theBranch = (Branch)comp;
						component = theBranch.verifySelectedComponent(x, y);
						if (component != null) break;
					}
				}
			}
			
			if (component != null) {
				if (component.isDelimiter()) {
					lastDelimiterVisible = component;
					component.setVisible(true);
					repaint();
				}
				else {
					if (lastDelimiterVisible != null) {
						lastDelimiterVisible.setVisible(false);
						lastDelimiterVisible = null;
						repaint();
					}
				}
			}
			else {
				if (lastDelimiterVisible != null) {
					lastDelimiterVisible.setVisible(false);
					lastDelimiterVisible = null;
					repaint();
				}
			}

			//System.out.println("Moved: [" + e.getX() + ", " + e.getY() + "] - SelectedRung = [" + (rungIndex!=UNSELECTED_RUNG ? rungIndex : "No RUNG") + "]");	
//			footer.setMousePosition(e.getX(), e.getY());
		}		
		
		// Verifica se há na lista (Ou RungLine) um componente de Output, pois "Só pode haver um"...
		protected boolean hasOutputComponent(List<LadderComponent> componentList) {
			boolean hasComponent = false;
			
			for (LadderComponent ladderComponent : componentList) {
				if (ladderComponent.isOutputComponent()) {
					hasComponent = true;
					break;
				}
			}
			
			return hasComponent;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
//			System.out.println("Mouse clicado em [" + x + " ," + y + "]");
			// Busca para verificar qual RungLine foi clicado			
			if (e.getButton() == MouseEvent.BUTTON3) {
				popupMenu.show(this, e.getX(), e.getY());
			}
			else {
				selectedRung = getSelectedRung(x, y);
				System.out.println("Rung Selecionado: " + selectedRung);
				selectedLadderComponent = null;
				if (selectedRung != UNSELECTED_RUNG) {
					
					// Procura pelo componente selecionado dentro do Rung
					Rung rung = project.getRungList().get(selectedRung);
					int componentIndex = 1;
					for(LadderComponent comp : rung.getComponentList()) {
						int y1Component = comp.getY() - comp.getCommandHeight()/2;
						int y2Component = comp.getY() + comp.getCommandHeight()/2;
						
						if (comp.isBranch()) {
							y1Component = comp.getY();
							y2Component = comp.getY() + comp.getCommandHeight();							
						}
						if (x >= comp.getX() && x<= (comp.getX() + comp.getCommandWidth())
							&& y >= y1Component && y <= y2Component) {
							if (comp.isBranch()) {	// Busca dentro do Branch
								System.out.println("Componente selecionado eh um Branch");
								Branch theBranch = (Branch)comp;
								selectedLadderComponent = theBranch.verifySelectedComponent(x,  y);
								if (selectedLadderComponent != null) {
									System.out.println("Componente selecionado esta dentro de um Branch. Componente: " + selectedLadderComponent.getId() + " na lista: " + theBranch.getSelectedListNumber());								
									break;
								}
								else
									selectedLadderComponent = comp;		// Marca o Branch todo mesmo.
							}
							else {
								selectedLadderComponent = comp;
								System.out.println("Componente selecionado no rung principal "+ comp.getId());
								break;
							}
						}
						componentIndex++;
					}

					// Se houver componente selecionado 
					Command selectedCommand = editor.getSelectedCommand();
					if (selectedLadderComponent != null) {
						// Componente eh um Delimitador e ainda há componente selecionado da TAB de comandos
						// Cria o componente e adiciona no Rung
						if (selectedLadderComponent.isDelimiter()==true) {
							
							if (selectedCommand != null) {
								String classname = selectedCommand.getClassname();	// Classe que implementa o Componente
								
								LadderComponent newComponent;
								try {
									newComponent = (LadderComponent)Class.forName(classname).newInstance();	// Instancia o componente
									if (newComponent.isOutputComponent()) {
										if (hasOutputComponent(rung.getComponentList())) {
											JOptionPane.showMessageDialog(null, "Já existe um Output no Rung", "Aviso", JOptionPane.INFORMATION_MESSAGE);
										}
										else {
											rung.getComponentList().add(newComponent);									
										}
									}
									else {
										if (selectedLadderComponent.isUnderBranch()) {		// Component é um delimitador e esta dentro de um branch
											System.out.println("Delimitador dentro do Branch...");
											Branch branchComp = selectedLadderComponent.getMyBranch();
											newComponent.setUnderBranch(true);
											newComponent.setMyBranch(branchComp);
											
											branchComp.getSelectedList().add(newComponent);
											
											Delimiter del1 = new Delimiter();
											del1.setUnderBranch(true);
											del1.setMyBranch(branchComp);
											branchComp.getSelectedList().add(del1);
										}
										else {
											System.out.println("Adicionando um novo componente no rung principal...");
											rung.getComponentList().add(componentIndex, newComponent);
											rung.getComponentList().add(componentIndex+1, new Delimiter());
											if (newComponent.isBranch())
												editor.unselectLastSelectedCommand();
										}
									}
								} catch (InstantiationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IllegalAccessException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (ClassNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
							}
						}
						else if (e.getClickCount() >= 2 && selectedLadderComponent.isComputed() && !selectedLadderComponent.isBranch()) {	 
//							// Mostra janela para definir o nome da variavel
							String newVarName = JOptionPane.showInputDialog("Nome da Variável", selectedLadderComponent.getVariableName() != null ? selectedLadderComponent.getVariableName() : "");
							if (newVarName != null) {
								selectedLadderComponent.setVariableName(newVarName);
							}
						}
					}
				}
				
				repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			//System.out.println("Entered: [" + e.getX() + ", " + e.getY());
		}

		@Override
		public void mouseExited(MouseEvent e) {
			//System.out.println("Exited: [" + e.getX() + ", " + e.getY());
		}

		@Override
		public void mousePressed(MouseEvent e) {
//			System.out.println("Pressed: [" + e.getX() + ", " + e.getY());
//			if (e.getButton() == MouseEvent.BUTTON1) {
//				lastMousePressedX = e.getX();
//				lastMousePressedY = e.getY();
//				
//				selectedLadderComponent = getSelectedLadderComponent(e.getX(), e.getY());				
//				if (selectedLadderComponent != null && selectedLadderComponent.isComputed())	// Nao eh um delimitador?
//					selectedLadderComponent = null;
//			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//System.out.println("Released: [" + e.getX() + ", " + e.getY());		
//			if (e.getButton() == MouseEvent.BUTTON1) {
//				lastMousePressedX = -1;
//				lastMousePressedY = -1;
//			}
		}		
	}	

}
