package com.freeplc.editor.project.component;

import java.awt.Graphics2D;

public interface LadderComponent extends Cloneable {
	public void setX(int x);
	public void setY(int y);
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public int getCommandWidth();
	public int getCommandHeight();
	public int getLineSize();		// Linha de ligacao entre os componentes
	
	public int getIntValue();
	public void setIntValue(int newValue);
	
	public double getDoubleValue();
	public void setDoubleValue(double newValue);
	
	public int getBitValue();
	public void setBitValue(int newValue);

	public String getVariableName();
	public void setVariableName(String varName);
	
	public String getId();
	public LadderComponent clone();
	
	public boolean isDelimiter();
	public boolean isComputed();
	public boolean isOutputComponent();
	public boolean isBranch();
	public boolean isUnderBranch();
	public void setUnderBranch(boolean value);

	public void drawMe(int x, int y, Graphics2D g2d);
	public void drawVariableName(Graphics2D g2d);
	public void drawValue(Graphics2D g2d);
	public void drawSelected(Graphics2D g2d);
	
	public boolean isVisible();
	public void setVisible(boolean visible);
	
	public Branch getMyBranch();
	public void setMyBranch(Branch branch);
	
	public boolean evaluate();
	
	public boolean isCompileError();
	public void setCompileError(boolean value);
		
}
