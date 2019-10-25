package org.xworker.plugin.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xworker.plugin.Activator;

public class ThingPackageViewer extends  EditorPart {
	public static final String ID  = "org.xworker.plugin.editors.thingPackageViewer";
	ThingEditorInput thingEditorInput;
	private static Composite packageViewer;
	private static Composite parentComposite;	
	private static ActionContext ac = new ActionContext();
	private static ThingPackageViewerOutlinePage outlinePage;
	
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.setSite(site);
		super.setInput(input);
		
		if(input instanceof ThingEditorInput){
			thingEditorInput = (ThingEditorInput) input;
		}		
	}

	@Override
	public boolean isDirty() {
		return false;
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if(outlinePage == null){
				outlinePage = new ThingPackageViewerOutlinePage(this);
			}
			return outlinePage;
		}

		return super.getAdapter(adapter);
	}
	
	public ActionContext getActionContext(){
		return ac;
	}
	
	public static void init(){		
		if(parentComposite == null || parentComposite.isDisposed()){
			return;
		}
		
		World world = World.getInstance();
		Thing packageViewerThing = world.getThing("xworker.ide.worldExplorer.swt.util.PackageViewer/@shell/@packageCompoiste");
		if(packageViewerThing == null){
			return;
		}
		
		if(packageViewer != null){
			packageViewer.dispose();
		}
		
		ac.put("explorerActions", Activator.explorerActionContext.get("actions"));
		ac.put("explorerContext", Activator.explorerActionContext);
		ac.put("tree",  Activator.explorerActionContext.get("projectTree"));
		ac.put("projectTree",  Activator.explorerActionContext.get("projectTree"));
		ac.put("parent", parentComposite);
		packageViewer = (Composite) packageViewerThing.doAction("create", ac);
				
		//ExplorerContext必须有shell变量，以便打开事物等时使用
		//
        if(Activator.explorerActionContext.get("shell") == null){
        	Activator.explorerActionContext.put("shell", parentComposite.getShell());
        }
	}
	
	@Override
	public void createPartControl(Composite parent) {
		parentComposite = parent;
		
		init();
	}

	@Override
	public void setFocus() {
		if(packageViewer != null){
			packageViewer.setFocus();
		}
	}
}
