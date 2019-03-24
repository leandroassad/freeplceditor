package com.freeplc.editor.project.component;

import java.awt.Graphics2D;

public class ExamineIfOpen extends ExamineIfClosed {
	public ExamineIfOpen() {
		super();
	}
	
	@Override
	public String getId() {
		return "XIO";
	}

	@Override
	public void drawMe(int x, int y, Graphics2D g2d) {
		super.drawMe(x, y, g2d);		
		
		// Desenha a Barra 
		g2d.drawLine(x+lineSize+3, y+height/2-3, x+lineSize+width-3, y-height/2+3);		
	}

	public boolean evaluate() {
		return (bitValue==0) ? true : false;
	}

}
