package org.xworker.plugin.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.xmeta.ActionContext;
import org.xworker.plugin.Activator;

public class ThingComposite extends EditorPart {
	public static final String ID  = "org.xworker.plugin.thingComposite";
	
	ThingEditorInput thingEditorInput = null;
	Composite composite = null;
	ActionContext actionContext = null;
	ThingOutlinePage outlinePage = null;
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		thingEditorInput.thing.doAction("doSave", actionContext);
	}

	@Override
	public void doSaveAs() {
		thingEditorInput.thing.doAction("doSaveAs", actionContext);
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.setSite(site);
		super.setInput(input);
		
		if(input instanceof ThingEditorInput){
			this.thingEditorInput = (ThingEditorInput) input;
			this.setPartName(thingEditorInput.getThing().getMetadata().getLabel());
		}
	}

	@Override
	public boolean isDirty() {
		Object obj = thingEditorInput.thing.doAction("isDirty", actionContext);
		if(obj == null){
			return false;
		}else if (obj.equals(true)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		Object obj = thingEditorInput.thing.doAction("isSaveAsAllowed", actionContext);
		if(obj == null){
			return false;
		}else if (obj.equals(true)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		if(thingEditorInput != null){
			actionContext = new ActionContext();
			actionContext.put("explorerContext", Activator.explorerActionContext);
			actionContext.put("explorerActions", Activator.explorerActionContext.get("actions"));
			actionContext.put("utilBrowser", Activator.explorerActionContext.get("utilBrowser"));
			actionContext.put("parent", parent);
			
			//创建Outline
			outlinePage = new ThingOutlinePage(this, ThingOutlinePage.BROWSER_COMPOSITE);
			
			thingEditorInput.thing.doAction("create", actionContext);	
		}
		
		//ExplorerContext必须有shell变量，以便打开事物等时使用
        if(Activator.explorerActionContext.get("shell") == null){
        	Activator.explorerActionContext.put("shell", parent.getShell());
        }
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			try{
				thingEditorInput.thing.doAction("initOutline", actionContext);
			}catch(Exception e){				
			}
			
			return outlinePage;
		}

		return super.getAdapter(adapter);
	}

	@Override
	public void setFocus() {
		if(composite != null){
			composite.setFocus();
		}
	}
}
