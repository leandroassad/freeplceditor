package com.freeplc.editor.project.component;

public class OutputLatch extends AbstractLadderComponent {
	public OutputLatch() {
		
	}
	
	@Override
	public String getId() {
		return "OTL";
	}

	@Override
	public boolean isOutputComponent() {
		return true;
	}
}
