package com.freeplc.editor.project.component;

public class MessageTo extends AbstractLadderComponent {
	public MessageTo() {
		
	}
	
	@Override
	public String getId() {
		return "MSG";
	}

	@Override
	public boolean isOutputComponent() {
		return true;
	}
}
