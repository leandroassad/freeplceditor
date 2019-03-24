package com.freeplc.editor.project.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import com.freeplc.editor.project.FreePLCProject;

import lombok.Getter;

/*
 * Este componente ajuda na montagem do bloco "OU"
 * O formato do comando é BST List<LadderComponent> NXB List<LadderComponent> BND
 */
public class Branch extends AbstractLadderComponent {
	@Getter ArrayList<LadderComponent> firstComponentList = new ArrayList<LadderComponent>();
	@Getter ArrayList<LadderComponent> secondComponentList = new ArrayList<LadderComponent>();
	
	@Getter ArrayList<LadderComponent> selectedList = null;
	@Getter int selectedListNumber = 0;

	public Branch() {
		setWidth(30);
		setHeight(30);
		setCommandWidth(FreePLCProject.LADDER_COMPONENT_WIDTH);
		setCommandHeight(FreePLCProject.LADDER_COMPONENT_HEIGHT);
		
		Delimiter del1 = new Delimiter();
		del1.setUnderBranch(true);
		del1.setMyBranch(this);

		Delimiter del2 = new Delimiter();
		del2.setUnderBranch(true);
		del2.setMyBranch(this);
		
		firstComponentList.add(del1);
		secondComponentList.add(del2);
	}
	
	@Override
	public String getId() {
		return "BST";
	}

	@Override
	public void drawMe(int x, int y, Graphics2D g2d) {
		setX(x);
		setY(y);
		
		// Tamamho da linha do lado esquerdo e do lado direito do componente
		lineSize = 10;	//(commandWidth - width)/2;

		// Desenha a linha do lado esquerdo, no rungline
		g2d.draw(new Line2D.Double(x, y, x+lineSize, y));
	
		// Desenha o componente
		// Linha Inicial Vertical
		g2d.draw(new Line2D.Double(x+lineSize, y, x+lineSize, y+commandHeight));
		
		// Componentes Ladder do Branch
		
		// Desenha a linha de cima
		int componentX = x+lineSize;
		int componentY = y;
		int firstCommandWidth= 0;	// Tamanho do Branch comeca em 0 e depende do numero de componentes
		for (LadderComponent ladderComponent : firstComponentList) {
			//System.out.println("BRANCH 1: Desenhando componente + " + ladderComponent.getId() + " na posicao: " + componentX);
			ladderComponent.drawMe(componentX, componentY, g2d);
			componentX += ladderComponent.getCommandWidth();
			
			if (ladderComponent.isComputed() && !ladderComponent.isBranch()) {
				ladderComponent.drawVariableName(g2d);
				ladderComponent.drawValue(g2d);;
			}
			
		}
		firstCommandWidth = componentX - (x+lineSize);
		
		componentX = x+lineSize;
		componentY = y + commandHeight;// + (FreePLCProject.LADDER_COMPONENT_HEIGHT/2);
		int secondCommandWidth = 0;
		LadderComponent lastComponent = null;
		for (LadderComponent ladderComponent : secondComponentList) {
			//System.out.println("BRANCH 2: Desenhando componente + " + ladderComponent.getId() + " na posicao: " + componentX);
			ladderComponent.drawMe(componentX, componentY, g2d);
			componentX += ladderComponent.getCommandWidth();
			
			if (ladderComponent.isComputed() && !ladderComponent.isBranch()) {
				ladderComponent.drawVariableName(g2d);
				ladderComponent.drawValue(g2d);
			}
			lastComponent = ladderComponent;
		}

		secondCommandWidth = componentX - (x+lineSize);
		commandWidth = Integer.max(firstCommandWidth, secondCommandWidth);
		//System.out.println("BRANCH: CommandWidth: " + commandWidth);

		// Desenha o restante da linha de baixo, se houver
		g2d.draw(new Line2D.Double(componentX, y+commandHeight, x+lineSize+commandWidth, y+commandHeight));		

		
		// Linha Final Vertical
		g2d.draw(new Line2D.Double(x+lineSize+commandWidth, y+commandHeight, x+lineSize+commandWidth, y));
		
		// Desenha a linha do lado direito, no rungline
		g2d.draw(new Line2D.Double(x+lineSize+commandWidth+1, y, x+lineSize*2+commandWidth, y));
		commandWidth += 2*lineSize;

	}
	
	@Override
	public void drawSelected(Graphics2D g2d) {
		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
        g2d.setStroke(dashed);
                
        g2d.drawRect(x, y, commandWidth, commandHeight);
	}

	
	public boolean isBranch() {
		return true;
	}
	

	public LadderComponent verifySelectedComponent(ArrayList<LadderComponent> list, int listNumber, int x, int y) {
		LadderComponent component = null;
		selectedList = null;
		selectedListNumber = 0;
		
		for(LadderComponent comp : list) {
			if (x >= comp.getX() && x<= (comp.getX() + comp.getCommandWidth())
				&& y >= comp.getY()-comp.getCommandHeight()/2 && y <= (comp.getY() + comp.getCommandHeight()/2)) {							
				component = comp;
				selectedList = list;
				selectedListNumber = listNumber;
				break;
			}					
		}
		
		return component;
		
	}
	
	public LadderComponent verifySelectedComponent(int x, int y) {
		// Busca o componente na lista 1
		LadderComponent component = verifySelectedComponent(firstComponentList, 1, x, y);
		
		// Busca o componente na lista 2
		if (component == null) {			
			component = verifySelectedComponent(secondComponentList, 2, x, y);
		}		
		
		return component;
	}
	

}
