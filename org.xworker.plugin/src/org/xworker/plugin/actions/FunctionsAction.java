package org.xworker.plugin.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xworker.plugin.Activator;

public class FunctionsAction implements IWorkbenchWindowActionDelegate {
	Shell shell = null;
	
	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		shell = window.getShell();
		
        if(Activator.explorerActionContext.get("shell") == null){
        	Activator.explorerActionContext.getScope(0).put("shell", shell);
        }
	}

	@Override
	public void run(IAction action) {
		if(Activator.explorerActionContext.get("shell") == null){
			return;
		}
		
		World world = World.getInstance();
		
		Thing scriptThing = world.getThing("xworker.ide.worldExplorer.eclipse.Scripts");
		Map<String, Object> context = new HashMap<String, Object>();
		Thing thingFlowComposite = world.getThing("xworker.ui.topic.TopicShell/@mainComposite");
		context.put("compositeThing", thingFlowComposite);
		context.put("title", thingFlowComposite.getMetadata().getLabel());
		context.put("path", thingFlowComposite.getMetadata().getPath());
		
		try{
			scriptThing.doAction("openThingComposite", Activator.explorerActionContext, context);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {	
	}
}