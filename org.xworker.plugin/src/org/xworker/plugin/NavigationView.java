package org.xworker.plugin;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.xmeta.Thing;
import org.xmeta.World;

public class NavigationView extends ViewPart {
	public static final String ID = "org.xworker.plugin.navigationView";
	private static Tree packageTree;
	private static Composite parentComposite;
	private static NavigationView view;
	
	public static void init(){
		if(view == null || parentComposite == null){
			return;
		}
		
		World world = World.getInstance();

		Thing packageTreeThing = world.getThing(Constants.PACKAGE_TREE_THING);
		if(packageTreeThing == null){
			return;
		}
		
		if(packageTree != null){
			packageTree.dispose();
		}
		
		Activator.explorerActionContext.put("parent", parentComposite);
		packageTree = (Tree) packageTreeThing.doAction("create", Activator.explorerActionContext);
		
		//初始化第一级节点
		Thing scripts = World.getInstance().getThing("xworker.ide.worldExplorer.eclipse.Scripts");
        scripts.doAction("initPackageTree", Activator.explorerActionContext);
        
        parentComposite.layout();
        
        //ExplorerContext必须有shell变量，以便打开事物等时使用
        //
        if(Activator.explorerActionContext.get("shell") == null){
        	Activator.explorerActionContext.getScope(0).put("shell", parentComposite.getShell());
        }
	}
	
	/**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
	public void createPartControl(Composite parent) {
		NavigationView.view = this;
		NavigationView.parentComposite = parent;
		
		init();		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		if(packageTree != null){
			packageTree.setFocus();
		}
	}
}