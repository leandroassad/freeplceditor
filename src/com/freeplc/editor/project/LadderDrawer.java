package com.freeplc.editor.project;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import com.freeplc.editor.components.LadderCanvas;
import com.freeplc.editor.project.component.LadderComponent;

public class LadderDrawer {

	public LadderDrawer() {
		
	}
	
	public void draw(FreePLCProject project, LadderCanvas canvas, Graphics2D g2d) {
		ArrayList<Rung> rungList = project.getRungList();
		
		int width= canvas.getWidth();
		int height = canvas.getHeight();
//		System.out.println("Janela: [Largura,Altura] = [" + width + "," + height + "]");
//		System.out.println("Canvas: [Largura,Altura] = [" + canvas.getCanvasPanel().getWidth() + "," + canvas.getCanvasPanel().getHeight() + "]");
		
		
		// Configura a largura padrao da linha e detalhes de rendering
		g2d.setStroke(new BasicStroke(2));
		g2d.setPaint(Color.BLACK);
		
		RenderingHints rh =
	            new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
	            RenderingHints.VALUE_ANTIALIAS_ON);

		rh.put(RenderingHints.KEY_RENDERING,
	               RenderingHints.VALUE_RENDER_QUALITY);

		g2d.setRenderingHints(rh);
	        
		// Inicio do primeio Rung
		int startX = FreePLCProject.LADDER_START_X;
		int startY = FreePLCProject.LADDER_START_Y;
		int lineNumber = 0;
		int rungEndY = 0;
		for (int rungNumber=0; rungNumber<rungList.size(); rungNumber++) {
			Rung rung = rungList.get(rungNumber);
			
			lineNumber = 0;
						
			// Posicao Y do inicio da linha
			int lineStartY = startY + rungEndY; 	//previousRungLineHeight*rungNumber;
			
			// Desenha o numero do Rung			
			g2d.setPaint(Color.RED);
			String strNumber = Integer.toString(rungNumber);
			g2d.drawString(strNumber, startX-15, lineStartY + (FreePLCProject.LADDER_COMPONENT_HEIGHT/2));
			
			/*
			 * Desenha o Rung
			 */
			int rungLineHeight = FreePLCProject.LADDER_COMPONENT_HEIGHT;
			if (rung.hasBranch()) {
				rungLineHeight *= 2;
			}
			
			rungEndY += rungLineHeight;
			
			g2d.setPaint(Color.BLACK);
			//Desenha a primeira linha vertical, inicio do RungLine
			g2d.draw(new Line2D.Double(startX, lineStartY ,startX, lineStartY + rungLineHeight));
			// Desenha a ultima linha vertical, final do RungLine
			g2d.draw(new Line2D.Double(width-2*startX, lineStartY, width-2*startX, lineStartY + rungLineHeight));
			// Desenha linha do ladder dos componentes
			g2d.draw(new Line2D.Double(startX, lineStartY + (FreePLCProject.LADDER_COMPONENT_HEIGHT/2),	width-2*startX, lineStartY + (FreePLCProject.LADDER_COMPONENT_HEIGHT/2)));
			
			rung.setStartX(startX);
			rung.setStartY(lineStartY);
			rung.setHeight(rungLineHeight);
			rung.setWidth(width - 2*startX);
			rung.setIndex(lineNumber);
			lineNumber++;
			
			// Desenha os components do rung
			ArrayList<LadderComponent> list = rung.getComponentList();
			int componentX = startX;
			int componentY = lineStartY + (FreePLCProject.LADDER_COMPONENT_HEIGHT/2);
			for (LadderComponent ladderComponent : list) {
				if (ladderComponent.isOutputComponent()) {
					int newX = startX + rung.getWidth() - ladderComponent.getCommandWidth() - 2*ladderComponent.getLineSize();
					ladderComponent.drawMe(newX, componentY, g2d);
				}
				else {
					ladderComponent.drawMe(componentX, componentY, g2d);
					componentX += ladderComponent.getCommandWidth();
				}
				if (ladderComponent.isComputed() && !ladderComponent.isBranch()) {
					ladderComponent.drawVariableName(g2d);
					ladderComponent.drawValue(g2d);
				}
				
			}
			
			rung.setIndex(rungNumber);
			
			rungList.set(rungNumber, rung);			
		}
		
		if (rungList.size()*FreePLCProject.LADDER_COMPONENT_HEIGHT > canvas.getCanvasPanel().getHeight()) {
			canvas.getCanvasPanel().setPreferredSize(new Dimension(canvas.getCanvasPanel().getWidth(), canvas.getCanvasPanel().getHeight() + FreePLCProject.LADDER_COMPONENT_HEIGHT*2));
			canvas.getScrollPane().setPreferredSize(new Dimension(width, height));
		}

		// Se houver Rung selecionado, desenha a linha vermelha do lado esquerdo
		if (canvas.getSelectedRung() != LadderCanvas.UNSELECTED_RUNG) {
	        Rung rung = project.getRungList().get(canvas.getSelectedRung());
	        
	        startX = rung.getStartX();
	        startY = rung.getStartY();
	        width = rung.getWidth();
	        height = rung.getHeight();
	        
	        // Desenha a linha vermelha do Rung (do lado esquerdo), para indicar que está selecionado
	        g2d.setStroke(new BasicStroke(2));
	        g2d.setColor(Color.RED);
	        g2d.draw(new Line2D.Double(startX-3, startY ,startX-3, startY+height));
	        g2d.setColor(Color.BLACK);
	        
	        // Se houver algum componente selecionado dentro o Rung, desenha o destaque dele
	        LadderComponent comp = canvas.getSelectedLadderComponent();
	        if (comp != null) {// && !comp.isBranch()) {
	        	//System.out.println("Selected Component: " + comp.getId());
	        	comp.drawSelected(g2d);
	        }
//			g2d.draw(new Line2D.Double(startX, startY, startX+width, startY));
//			g2d.draw(new Line2D.Double(startX, startY+height, startX+width, startY+height));
		}
		
		g2d.dispose();
	}
	
}
