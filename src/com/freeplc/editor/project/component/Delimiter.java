package com.freeplc.editor.project.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import com.freeplc.editor.project.FreePLCProject;

/**
 * Delimiter
 * Este componente serve somente para orientar a colocacao dos outros componentes
 * e nao ficara aparecendo no ladder final.
 * @author leandro
 *
 */
public class Delimiter extends AbstractLadderComponent {
	
	public Delimiter() {
		setVisible(false);
		setWidth(10);
		setHeight(10);
		setCommandWidth(30);
		setCommandHeight(30);
	}
	
	@Override
	public String getId() {
		return "DLM";
	}

	@Override
	public boolean isComputed() {
		return false;
	}
	
	public boolean isDelimiter() {
		return true;
	}
	
	@Override
	public void drawMe(int x, int y, Graphics2D g2d) {
		setX(x);
		setY(y);
		
		// Linha do lado esquerdo e do lado direito do componente
		lineSize = 10; //(commandWidth - width)/2;
		
		// Desenha a linha do lado esquerdo 
		g2d.draw(new Line2D.Double(x, y, x+lineSize, y));
		// Desenha a linha do lado direito
		g2d.draw(new Line2D.Double(x+lineSize+width+1, y, x+lineSize*2+width, y));
		
		
		if (isVisible()) {
			g2d.setStroke(new BasicStroke(1));
			Rectangle rect = new Rectangle(x+lineSize, y-height/2, width, height); 
			g2d.draw(rect);
			
			g2d.setColor(Color.WHITE);
			
			g2d.fillRect(x+lineSize+1, y-height/2+1, width-1, height-1);
	
			g2d.setColor(Color.BLACK);
	
			// Desenha o X dentro do delimitador
	//		g2d.drawLine(x+lineSize+2, y-height/2+2, x+lineSize+width-2, y+height/2-2);
	//		g2d.drawLine(x+lineSize+2, y+height/2-2, x+lineSize+width-2, y-height/2+2);
			g2d.drawLine(x+lineSize, y-height/2, x+lineSize+width, y+height/2);
			g2d.drawLine(x+lineSize, y+height/2, x+lineSize+width, y-height/2);

			g2d.setStroke(new BasicStroke(2));		
		}
		else {
			//Desenha somente a linha
			g2d.setColor(Color.WHITE);
			g2d.fillRect(x+lineSize+1, y-height/2+1, width-1, height-1);
			g2d.setColor(Color.BLACK);
			g2d.drawLine(x+lineSize, y, x+lineSize+width, y);
			
		}
		
	}
	
	@Override
	public void drawSelected(Graphics2D g2d) {
//		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
//        g2d.setStroke(dashed);
//                
//        g2d.drawRect(x, y-commandHeight/2, commandWidth, commandHeight);
		// Linha do lado esquerdo e do lado direito do componente
		if (isVisible()) {
			int lineSize = 10;	//(commandWidth - width)/2;
	
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Color.RED);
	
			// Desenha o X dentro do delimitador
			g2d.drawLine(x+lineSize+2, y-height/2+2, x+lineSize+width-2, y+height/2-2);
			g2d.drawLine(x+lineSize+2, y+height/2-2, x+lineSize+width-2, y-height/2+2);
			
			g2d.setStroke(new BasicStroke(2));
		}

	}
}
