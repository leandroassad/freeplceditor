package com.freeplc.editor.project.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import com.freeplc.editor.project.FreePLCProject;

public class ExamineIfClosed extends AbstractLadderComponent {
	public ExamineIfClosed() {
		setWidth(30);
		setHeight(30);
		setCommandWidth(FreePLCProject.LADDER_COMPONENT_WIDTH);
		setCommandHeight(FreePLCProject.LADDER_COMPONENT_HEIGHT);
	}
	
	@Override
	public String getId() {
		return "XIC";
	}

	@Override
	public void drawMe(int x, int y, Graphics2D g2d) {
		setX(x);
		setY(y);
		
		// Tamamho da linha do lado esquerdo e do lado direito do componente
		lineSize = 10;	//(commandWidth - width)/2;

		Line2D.Double d1 = new Line2D.Double(x, y, x+lineSize, y);
		Line2D.Double d2 = new Line2D.Double(x+lineSize+width+1, y, x+lineSize*2+width, y);
		
		drawEvaluateAnchorLines(g2d, d1, d2);
//		// Desenha a linha do lado esquerdo
//		g2d.draw(new Line2D.Double(x, y, x+lineSize, y));
//		// Desenha a linha do lado direito
//		g2d.draw(new Line2D.Double(x+lineSize+width+1, y, x+lineSize*2+width, y));
	
		// Desenha o componente
		g2d.draw(new Line2D.Double(x+lineSize, y-height/2, x+lineSize, y+height/2));
		g2d.draw(new Line2D.Double(x+lineSize+width, y-height/2, x+lineSize+width, y+height/2));
		
		g2d.setColor(Color.WHITE);
		
		g2d.fillRect(x+lineSize+1, y-height/2+1, width-2, height-2);
				
		g2d.setColor(Color.BLACK);
		
	}
	
	public boolean evaluate() {
		return (bitValue==1) ? true : false;
	}

}
