package org.xworker.plugin;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class XWorkerPatternMatchListener implements IPatternMatchListener{
	TextConsole console;
	ExplorerHelper helper;
	
	public XWorkerPatternMatchListener(ExplorerHelper helper){
		this.helper = helper;
	}
	
	@Override
	public int getCompilerFlags() {
		return 0;
	}

	@Override
	public String getLineQualifier() {
		return "at";
	}

	@Override
	public String getPattern() {	
		return "\\sat\\s.*.\\.p.*.\\.p.*\\).*";
	}

	@Override
	public void connect(TextConsole console) {
		this.console = console;
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		try {
			String line = console.getDocument().get(event.getOffset(), event.getLength());
			int classStartOffset = line.indexOf("at") + 3;
			int dotIndex = line.indexOf(".", classStartOffset);
			dotIndex = line.indexOf(".", dotIndex + 1);
			dotIndex = line.indexOf(".", dotIndex + 1);
			dotIndex = line.indexOf(".", dotIndex + 1);
			int classStartIndex = line.indexOf("(", dotIndex) + 1;
			int lineOffset = line.indexOf(":", dotIndex) + 1;
			int lineEndOffset = line.indexOf(")", lineOffset);
			int lineIndex = 0;
			if(lineOffset != 0 && lineEndOffset != -1){
				lineIndex = Integer.parseInt(line.substring(lineOffset, lineEndOffset));
			}
			
			String className = line.substring(classStartOffset, dotIndex);
			if(className.indexOf("$") != -1){
				className = className.substring(0, className.indexOf("$"));
			}
			console.addHyperlink(new XWorkerClassHyperlink(helper, className, lineIndex), event.getOffset() + classStartIndex, lineEndOffset - classStartIndex);
		} catch (BadLocationException e) {		
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		System.out.println("	at ui.p1947850322.p197597892.init$_run_closure1.doCall(init.groovy".matches(".*at.*.\\.p.*.\\.p.*\\).*"));
	}
}
