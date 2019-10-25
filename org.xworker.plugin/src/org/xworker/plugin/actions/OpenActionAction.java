package org.xworker.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xworker.plugin.Activator;

public class OpenActionAction implements IWorkbenchWindowActionDelegate {
	Shell shell = null;

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		shell = window.getShell();
		
		//ExplorerContext必须有shell变量，以便打开事物等时使用
        //
        if(Activator.explorerActionContext.get("shell") == null){
        	Activator.explorerActionContext.getScope(0).put("shell", shell);
        }
	}

	@Override
	public void run(IAction action) {
		if(Activator.explorerActionContext.get("shell") == null){
			return ;
		}
		
		World world = World.getInstance();

		Thing scriptThing = world.getThing("xworker.ide.worldExplorer.eclipse.Scripts");
		scriptThing.doAction("openActionDiag", Activator.explorerActionContext);

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
