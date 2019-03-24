package com.freeplc.editor.project.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractLadderComponent implements LadderComponent {

	@Getter @Setter int x;
	@Getter @Setter int y;
	
	@Getter @Setter int width, height, commandWidth, commandHeight;
	
	@Getter @Setter int intValue=0, bitValue=0;
	@Getter @Setter double doubleValue;
	@Setter String variableName;
	
	@Setter boolean underBranch = false;
	
	@Getter @Setter Branch myBranch = null;
	
	boolean visible = true;
	
	@Getter int lineSize = 10;
	
	@Getter @Setter boolean compileError = false;
	
	public LadderComponent clone() {
		try {
			return (LadderComponent)super.clone();
		} catch (CloneNotSupportedException e) {
			//log.info("Clone not supported exception");
			return this;
		}
	}
		
	// Diz se o componente deve ser considerado na execucao do ladder
	// Normalmente sim, mas deve ser false para componentes especiais de desenho
	public boolean isComputed() {
		return true;
	}
	
	// Diz se o componente é apenas para saída, isso significa que deve ficar no final do ladder.
	public boolean isOutputComponent() {
		return false;
	}
	
	@Override
	public void drawMe(int x, int y, Graphics2D g2d) {
		// Cada LadderComponent deve fazer o seu...
	}
	
	public void drawVariableName(Graphics2D g2d) {
		if (isCompileError()) {
			g2d.setColor(Color.RED);
		}
		g2d.drawString(variableName==null?"VAR?":variableName, x+15, y-commandHeight/2 + 15);
		
		if (isCompileError()) {
			g2d.setColor(Color.BLACK);
		}
	}
	
	public void drawValue(Graphics2D g2d) {
		String strValue = Integer.toString(bitValue);
		g2d.drawString(strValue, x+commandWidth/2-1, y+commandHeight/2-15);
	}
	
	@Override
	public void drawSelected(Graphics2D g2d) {
		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
        g2d.setStroke(dashed);
                
        g2d.drawRect(x, y-commandHeight/2, commandWidth, commandHeight);
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isBranch() {
		return false;
	}
	
	public boolean isUnderBranch() {
		return underBranch;
	}
	
	public boolean isDelimiter() {
		return false;
	}
	
	public String getVariableName() {
		return variableName!=null ? variableName : "VAR?";
	}
	
	public boolean evaluate() {
		return false;
	}
	
	public void drawEvaluateAnchorLines(Graphics2D g2d, Line2D.Double d1, Line2D.Double d2) {
		// Desenha as linhas em verde, se estiver TRUE 
		if (evaluate() == true) {
			g2d.setStroke(new BasicStroke(8));
			g2d.setPaint(Color.GREEN);
			// Desenha a linha do lado esquerdo
			g2d.draw(d1);
			// Desenha a linha do lado direito
			g2d.draw(d2);
			g2d.setStroke(new BasicStroke(2));
			g2d.setPaint(Color.BLACK);
		}
		// Desenha a linha do lado esquerdo
		g2d.draw(d1);
		// Desenha a linha do lado direito
		g2d.draw(d2);
	}
}
