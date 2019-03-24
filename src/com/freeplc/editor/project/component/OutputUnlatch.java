package com.freeplc.editor.project.component;

public class OutputUnlatch extends AbstractLadderComponent {
	public OutputUnlatch() {
		
	}
	
	@Override
	public String getId() {
		return "OTU";
	}

	@Override
	public boolean isOutputComponent() {
		return true;
	}
}
